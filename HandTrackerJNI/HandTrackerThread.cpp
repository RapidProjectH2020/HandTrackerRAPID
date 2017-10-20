#include "HandTrackerThread.h"
#include <Acquisition/OpenNIGrabber/OpenNIGrabber.hpp>
#include <Rendering/RendererOGL/RendererOGL.hpp>
#include <boost/thread.hpp>
#include <boost/locking_queue.hpp>
#include <boost/variant.hpp>

namespace MBV { namespace HandTracking {

typedef HandTrackerOtherThread::HTPtr HTPtr;

struct HTRequestCreate {};
struct HTRequestSetupCameraView { HTPtr ht; Matrix4x4 mat; };
struct HTRequestSetupCameraProj { HTPtr ht; Matrix4x4 mat; };
struct HTRequestComputeBoundingBox { HTPtr ht; ParamVector x; UInt width, height; Single padding; };
struct HTRequestZoomCamera { HTPtr ht; Matrix4x4 proj; cv::Rect focus; UInt width, height; };
struct HTRequestPreprocessInput { HTPtr ht; cv::Mat rgb, depth; cv::Rect focus; };
struct HTRequestUploadObservations { HTPtr ht; cv::Mat labels, depths; };
struct HTRequestTrack { HTPtr ht; ParamVector x; };
struct HTRequestVisualize { HTPtr ht; cv::Mat rgb; Matrix4x4 view, projection;
                            ParamVector x; UInt edgeThickness; Single alpha; };
struct HTRequestLiveGrabber { };
struct HTRequestFileGrabber { String oni; UInt start; };

typedef boost::variant
<
  HTRequestCreate
, HTRequestSetupCameraView
, HTRequestSetupCameraProj
, HTRequestComputeBoundingBox
, HTRequestZoomCamera
, HTRequestPreprocessInput
, HTRequestUploadObservations
, HTRequestTrack
, HTRequestVisualize
, HTRequestLiveGrabber
, HTRequestFileGrabber
> HTRequest;

struct HTResultException { boost::exception_ptr ex; };
struct HTResultVoid {};
struct HTResultHT { HTPtr handTracker; };
struct HTResultRect { cv::Rect rect; };
struct HTResultMat { Matrix4x4 mat; };
struct HTResultPreprocessed { cv::Mat labels, depths; };
struct HTResultTrack { double score; ParamVector x; };
struct HTResultCvMat { cv::Mat mat; };
struct HTResultGrabber { Grabber::Ptr grabber; };

typedef boost::variant
<
  HTResultHT
, HTResultVoid
, HTResultRect
, HTResultMat
, HTResultPreprocessed
, HTResultTrack
, HTResultCvMat
, HTResultException
, HTResultGrabber
> HTResult;

boost::locking_queue<HTRequest> g_htRequests;
boost::locking_queue<HTResult> g_htResults;

struct HTDispatch
        : boost::static_visitor<HTResult>
{
    HTResult operator()(const HTRequestCreate&) const
    {
        HTResultHT ret = { boost::make_shared<HandTrackerLib>(640, 512) };

        ExposedRenderer::Ptr r = ret.handTracker->getRenderer();
        RendererOGLBase::Ptr rogl = boost::dynamic_pointer_cast<RendererOGLBase>(r->getDelegate());
        if (rogl)
        {
            rogl->setCulling(RendererOGLBase::CullFront);
        }
        return ret;
    }

    HTResult operator()(const HTRequestSetupCameraView &req) const
    {
        req.ht->setupVirtualCameraView(req.mat);
        return HTResultVoid();
    }

    HTResult operator()(const HTRequestSetupCameraProj &req) const
    {
        req.ht->setupVirtualCameraProjection(req.mat);
        return HTResultVoid();
    }

    HTResult operator()(const HTRequestComputeBoundingBox &r) const
    {
        HTResultRect res = { r.ht->step2_computeBoundingBox(r.x, r.width, r.height, r.padding) };
        return res;
    }

    HTResult operator()(const HTRequestZoomCamera &r) const
    {
        HTResultMat res = { r.ht->step3_zoomVirtualCamera(r.proj, r.focus, r.width, r.height) };
        return res;
    }

    HTResult operator()(const HTRequestPreprocessInput &r) const
    {
        HTResultPreprocessed res;
        boost::tie(res.labels, res.depths) = r.ht->step4_preprocessInput(r.rgb, r.depth, r.focus);
        return res;
    }

    HTResult operator()(const HTRequestUploadObservations &r) const
    {
        r.ht->step5_setObservations(r.labels, r.depths);
        return HTResultVoid();
    }

    HTResult operator()(const HTRequestTrack &r)
    {
        HTResultTrack res;
        res.score = r.ht->step6_track(r.x, res.x);
        return res;
    }

    HTResult operator()(const HTRequestVisualize &r) const
    {
        HTResultCvMat res = { r.ht->step7_visualize(r.rgb, r.view, r.projection, r.x, r.edgeThickness, r.alpha) };
        return res;
    }

    HTResult operator()(const HTRequestLiveGrabber &r) const
    {
        HTResultGrabber ret = { HandTrackerLib::generateDefaultGrabber() };
        return ret;
    }

    HTResult operator()(const HTRequestFileGrabber &r) const
    {
        OpenNIGrabber::Ptr grabber = boost::make_shared<OpenNIGrabber>(true, true, "media/openni.xml", r.oni, false);
        grabber->initialize();
        grabber->seek(r.start);
        HTResultGrabber ret = { grabber };
        return ret;
    }
} g_htDispatch;

struct VarPrintVisitor
    : boost::static_visitor<String>
{
    template < class T >
    String operator()(const T&) const
    {
        return typeid(T).name();
    }
};

template < class Var >
String GetType(const Var &var)
{
    return boost::apply_visitor(VarPrintVisitor(), var);
}

void HandTrackerThreadLoop()
{
    //LOG(info) << __FUNCTION__ << " : Started hand tracking thread";
    fprintf(stderr,"%s : Started hand tracking thread\n",__FUNCTION__);

    while (!boost::this_thread::interruption_requested())
    {
        //LOG(info) << __FUNCTION__ << " : Waiting on request";
        fprintf(stderr,"%s : Waiting on request\n",__FUNCTION__);
        HTRequest req = g_htRequests.pop(true);
        //LOG(info) << __FUNCTION__ << " : Request received, type = " << GetType(req);
        fprintf(stderr,"%s : Request received, type = %s\n",__FUNCTION__,GetType(req).c_str());
        HTResult res;
        try
        {
            res = boost::apply_visitor(g_htDispatch, req);
            //LOG(info) << __FUNCTION__ << " : Request computed, type = " << GetType(res);
            fprintf(stderr,"%s : Request computed, type = %s\n",__FUNCTION__,GetType(req).c_str());
        }
        catch(const Exception &ex_)
        {
            //LOG(info) << __FUNCTION__ << " : Something went wrong";
            fprintf(stderr,"%s : Something went wrong\n",__FUNCTION__);
            HTResultException ex = { boost::copy_exception(ex_) };
            res = ex;
        }
        catch(...)
        {
            //LOG(info) << __FUNCTION__ << " : Something went wrong";
            fprintf(stderr,"%s : Something went wrong\n",__FUNCTION__);
            HTResultException ex = { boost::current_exception() };
            res = ex;
        }
        //LOG(info) << __FUNCTION__ << " : Pushing result";
        fprintf(stderr,"%s : Pushing result\n",__FUNCTION__);
        g_htResults.push(res);
        //LOG(info) << __FUNCTION__ << " : Result pushed";
        fprintf(stderr,"%s : Result pushed\n",__FUNCTION__);
    }
}

template < class Ret >
Ret Call(const HTRequest &req)
{
    g_htRequests.push(req);
    HTResult res = g_htResults.pop(true);
    if (HTResultException *ex = boost::get<HTResultException>(&res))
    {
        boost::rethrow_exception(ex->ex);
    }
    else if (Ret *res_ = boost::get<Ret>(&res))
    {
        return *res_;
    }
    else
        MBV_EXCEPT_MSG("Invalid return type");
}

boost::thread& GetThread()
{
    fprintf(stderr,"%s : GetThread called \n",__FUNCTION__);
    static boost::thread htThread(HandTrackerThreadLoop);
    return htThread;
}

void HandTrackerOtherThread::startHTThread()
{
    GetThread();
}

void HandTrackerOtherThread::stopHTThread()
{
    fprintf(stderr,"%s : stopHTThread called \n",__FUNCTION__);
    boost::thread &thread = GetThread();
    thread.interrupt();
    thread.join();
}

HandTrackerOtherThread::HandTrackerOtherThread()
{
    GetThread();
    m_handTracker = Call<HTResultHT>(HTRequestCreate()).handTracker;
}

Decoder::Ptr HandTrackerOtherThread::getDecoder() const
{
    return m_handTracker->getDecoder(true);
}

ExposedRenderer::Ptr HandTrackerOtherThread::getRenderer() const
{
    return m_handTracker->getRenderer(true);
}

Grabber::Ptr HandTrackerOtherThread::generateLiveGrabber()
{
    HTRequestLiveGrabber req;
    return Call<HTResultGrabber>(req).grabber;
}

Grabber::Ptr HandTrackerOtherThread::generateFileGrabber(const String &oniFile, UInt start)
{
    HTRequestFileGrabber req = { oniFile, start };
    return Call<HTResultGrabber>(req).grabber;
}

void HandTrackerOtherThread::setupVirtualCameraView(const Matrix4x4& mat)
{
    HTRequestSetupCameraView req = { m_handTracker, mat };
    Call<HTResultVoid>(req);
}

void HandTrackerOtherThread::setupVirtualCameraProjection(const Matrix4x4& mat)
{
    HTRequestSetupCameraProj req = { m_handTracker, mat };
    Call<HTResultVoid>(req);
}

cv::Rect HandTrackerOtherThread::step2_computeBoundingBox(const ParamVector& x, UInt width, UInt height, Single padding)
{
    HTRequestComputeBoundingBox req = { m_handTracker, x, width, height, padding };
    return Call<HTResultRect>(req).rect;
}

Matrix4x4 HandTrackerOtherThread::step3_zoomVirtualCamera(const Matrix4x4& projection, const cv::Rect& focus, UInt width, UInt height)
{
    HTRequestZoomCamera req = { m_handTracker, projection, focus, width, height };
    return Call<HTResultMat>(req).mat;
}

std::pair<cv::Mat, cv::Mat> HandTrackerOtherThread::step4_preprocessInput(const cv::Mat& rgb, const cv::Mat& depth, const cv::Rect& focus)
{
    HTRequestPreprocessInput req = { m_handTracker, rgb, depth, focus };
    HTResultPreprocessed res = Call<HTResultPreprocessed>(req);
    return std::make_pair(res.labels, res.depths);
}

void HandTrackerOtherThread::step5_setObservations(const cv::Mat& labels, const cv::Mat& depths)
{
    HTRequestUploadObservations req = { m_handTracker, labels, depths };
    Call<HTResultVoid>(req);
}

double HandTrackerOtherThread::step6_track(const ParamVector& previousSolution, ParamVector& solution)
{
    HTRequestTrack req = { m_handTracker, previousSolution };
    HTResultTrack res = Call<HTResultTrack>(req);
    solution = res.x;
    return res.score;
}

cv::Mat HandTrackerOtherThread::step7_visualize(
        const cv::Mat& rgb, const Matrix4x4& view, const Matrix4x4& projection,
        const ParamVector& solution, UInt edgeThickness, float alpha)
{
    HTRequestVisualize req = { m_handTracker, rgb, view, projection, solution, edgeThickness, alpha };
    return Call<HTResultCvMat>(req).mat;
}

}}
