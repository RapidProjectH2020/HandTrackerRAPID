#include <eu_project_rapid_handtracker_HandTrackerJNI.h>
#include "JNIHelper.h"
#include <map>
#include <algorithm>
#include <Core/Exception/Exception.hpp>
#include <Core/Timers/Timers.hpp>
#include "HandTrackerThread.h"

using namespace MBV::HandTracking;

struct HTThreadManager
{
    HTThreadManager() { HandTrackerOtherThread::startHTThread(); }
    ~HTThreadManager() { HandTrackerOtherThread::stopHTThread(); }
} g_htThreadmanager;

typedef boost::shared_ptr<HandTrackerOtherThread> HandTrackerLibPtr;
struct Tracker
{
	HandTrackerLibPtr tracker;
	Grabber::Ptr grabber;
};

struct HandTrackerHelper
	: public JNI::JNIHelper
{
	HandTrackerHelper(JNIEnv *env, jobject obj)
		: JNI::JNIHelper(env, obj)
	{
		fprintf(stderr,"HandTrackerJNI/HandTracker.cpp : Hand Tracker Constructor called\n");
		try
            {
		       ht = getTracker();
            }
            catch(const std::exception &e)
            {
              fprintf(stderr,"HandTrackerJNI/HandTracker.cpp : Fatal error constructing constructor %s \n",e.what());
            }



		fprintf(stderr,"HandTrackerJNI/HandTracker.cpp : Ready to continue..\n");
		/*
		LOG(info) << "Hand tracker = " << ht.tracker;
		LOG(info) << "Grabber = " << ht.grabber;
		LOG(info) << "Decoder = " << ht.tracker->getDecoder();
		LOG(info) << "Renderer = " << ht.tracker->getRenderer();
		*/

		/* Force change of directory ?
        char * envPath = getenv("MBV_SDK");
        if (envPath!=0)
         {
           char finalPath[2048]={0};
           snprintf(finalPath,2048,"%s/bin",envPath);
		   LOG(info) << "Changing directory to= " << finalPath;
		   chdir(finalPath);
         }
       */
	}

    const Tracker& getTracker()
    {
		fprintf(stderr,"HandTrackerJNI/HandTracker.cpp : Requesting a tracker\n");
        static boost::shared_ptr<Tracker> tracker;

        if(!tracker)
        {
            HandTrackerLibPtr ht;
            Grabber::Ptr grabber;

            try
            {
                ht = boost::make_shared<HandTrackerOtherThread>();
            }
            catch(const std::exception &e)
            {
                LOG(fatal) << e.what();
            }

            try
            {
                grabber = HandTrackerLib::generateDefaultGrabber();
            }
            catch(const std::exception &e)
            {
                LOG(warning) << e.what();
            }

            Tracker tr = { ht, grabber };
            tracker.reset(new Tracker(tr));
        }

        return *tracker;
    }

	Tracker ht;
};

void Except(JNIEnv *env, const std::exception &e)
{
	jclass Exception = env->FindClass("java/lang/Exception");
	env->ThrowNew(Exception, e.what());
}

typedef HandTrackerHelper HTH;

String J2C(JNIEnv *env, jstring str)
{
	const char* str_ = env->GetStringUTFChars(str, NULL);
	String ret(str_);
	env->ReleaseStringUTFChars(str, str_);
	return ret;
}

ParamVector J2C(JNIEnv *env, jdoubleArray data)
{
	typedef JNI::JavaGetSet<Vector<Double>::Type> Helper;
	Helper::Vec vec;
	Helper::read(env, data, vec);
	return vec;
}

jobject DefaultConstruct(JNIEnv *env, const String &className)
{

	//LOG(info) << "Constructing an instance of class : " << className;
    fprintf(stderr,"Constructing an instance of class : %s\n",className.c_str());
    jclass cls = env->FindClass(className.c_str());
    MBV_ASSERT_MSG(cls, "Class not found");
    jmethodID methodId = env->GetMethodID(cls, "<init>", "()V");
    MBV_ASSERT_MSG(methodId, "No-arg constructor not found");
    jobject ret = env->NewObject(cls, methodId);
    //LOG(info) << "Created : " << ret;
    fprintf(stderr,"Created %s\n",className.c_str());
    return ret;
}

template < class T >
jobject Create(JNIEnv *env)
{
    return DefaultConstruct(env, GetClassString((JNI::Object<T>*)NULL));
}

struct Step1Output {};
struct Step2Output {};
struct Step3Output {};
struct Step4Output {};
struct Step5Output {};
struct Step6Output {};
struct CVImage {};

namespace JNI
{

const char* GetClassString(Object<Step1Output>*)
{
    return "eu/project/rapid/handtracker/HandTrackerJNI$Step1Output";
}

const char* GetClassString(Object<Step2Output>*)
{
    return "eu/project/rapid/handtracker/HandTrackerJNI$Step2Output";
}

const char* GetClassString(Object<Step3Output>*)
{
    return "eu/project/rapid/handtracker/HandTrackerJNI$Step3Output";
}

const char* GetClassString(Object<Step4Output>*)
{
    return "eu/project/rapid/handtracker/HandTrackerJNI$Step4Output";
}

const char* GetClassString(Object<Step5Output>*)
{
    return "eu/project/rapid/handtracker/HandTrackerJNI$Step5Output";
}

const char* GetClassString(Object<Step6Output>*)
{
    return "eu/project/rapid/handtracker/HandTrackerJNI$Step6Output";
}

const char* GetClassString(Object<CVImage>*)
{
    return "eu/project/rapid/handtracker/HandTrackerJNI$Image";
}

const char* GetClassString(Object<Matrix4x4>*)
{
    return "eu/project/rapid/handtracker/HandTrackerJNI$Matrix4x4";
}

const char* GetClassString(Object<cv::Rect>*)
{
    return "eu/project/rapid/handtracker/HandTrackerJNI$BoundingBox";
}

}

cv::Mat FromCVImage(JNIEnv *env, jobject image)
{
    JNI::JNIHelper h(env, image);
    Int width, height, type;
    Vector<Byte>::Type data;
    h.get("width", width);
    h.get("height", height);
    h.get("type", type);
    h.get("data", data);
    cv::Mat ret(cv::Size(width, height), type);
    std::copy(data.begin(), data.end(), ret.ptr<Byte>());
    return ret;
}

jobject ToCVImage(JNIEnv *env, const cv::Mat &m)
{
    jobject ret = Create<CVImage>(env);
    JNI::JNIHelper h(env, ret);
    h.set("width", m.size().width);
    h.set("height", m.size().height);
    h.set("type", m.type());
    Vector<Byte>::Type data(m.ptr<Byte>(), m.ptr<Byte>() + m.size().area() * m.elemSize());
    h.set("data", data);
    return ret;
}

Matrix4x4 FromMatrix(JNIEnv *env, jobject matrix)
{
    JNI::JNIHelper h(env, matrix);
    Vector<Single>::Type data;
    h.get("data", data);
    Matrix4x4 ret;
    std::copy(data.begin(), data.end(), &ret[0][0]);
    return ret;
}

jobject ToMatrix(JNIEnv *env, const Matrix4x4 &mat)
{
    Vector<Single>::Type data(&mat[0][0], &mat[0][0] + 16);
    jobject ret = Create<Matrix4x4>(env);
    JNI::JNIHelper h(env, ret);
    h.set("data", data);
    return ret;
}

cv::Rect FromBoundingBox(JNIEnv *env, jobject bb)
{
    cv::Rect ret;
    JNI::JNIHelper h(env, bb);
    h.get("x", ret.x);
    h.get("y", ret.y);
    h.get("width", ret.width);
    h.get("height", ret.height);
    return ret;
}

jobject ToBoundingBox(JNIEnv *env, const cv::Rect &bb)
{
    jobject ret = Create<cv::Rect>(env);
    JNI::JNIHelper h(env, ret);
    h.set("x", bb.x);
    h.set("y", bb.y);
    h.set("width", bb.width);
    h.set("height", bb.height);
    return ret;
}

void Java_eu_project_rapid_handtracker_HandTrackerJNI_showImage
  (JNIEnv *env, jclass, jstring name, jobject image)
{
    cv::imshow(J2C(env, name), FromCVImage(env, image));
}


jobject Java_eu_project_rapid_handtracker_HandTrackerJNI_native_1step1_1grab
  (JNIEnv *env, jobject obj)
{
    try
    {
        //LOG(info) << "Enter " << __FUNCTION__;
        fprintf(stderr,"Enter %s \n",__FUNCTION__);
        HTH helper(env, obj);

        ImageList imgs;
        CameraMetaList clbs;

        MBV_ASSERT_MSG(helper.ht.grabber, "No grabber initialized");
        helper.ht.grabber->acquire(imgs, clbs);

        fprintf(stderr,"Performed acquisition\n");
        //LOG(info) << "Performed acquisition";

        cv::Mat depth = imgs.front();
        cv::Mat rgb = imgs.back();

        Matrix4x4 viewT = clbs.front().getCamera().Graphics_getViewTransform();
        Matrix4x4 projT = clbs.front().getCamera().Graphics_getProjectionTransform();

        //LOG(info) << "Computed graphics matrices";
        fprintf(stderr,"Computed graphics matrices\n");

        jobject ret = Create<Step1Output>(env);
        //LOG(info) << "Created a Step1Output : " << ret;
        fprintf(stderr,"Created a Step1Output\n");
        {
            JNI::JNIHelper h(env, ret);

            JNI::Object<CVImage> rgbCVImage_ = { ToCVImage(env, rgb) };
            h.set("rgb", rgbCVImage_);

            JNI::Object<CVImage> depthCVImage_ = { ToCVImage(env, depth) };
            h.set("depth", depthCVImage_);

            JNI::Object<Matrix4x4> viewMatrix_ = { ToMatrix(env, viewT) };
            h.set("view", viewMatrix_);

            JNI::Object<Matrix4x4> projMatrix_ = { ToMatrix(env, projT) };
            h.set("projection", projMatrix_);
        }

        //LOG(info) << "Leave " << __FUNCTION__;
        fprintf(stderr,"Leave %s \n",__FUNCTION__);

        return ret;
    }
    catch(const std::exception &e)
    {
        Except(env, e);
    }
}

jobject Java_eu_project_rapid_handtracker_HandTrackerJNI_native_1step2_1computeBoundingBox
  (JNIEnv *env, jobject obj, jobject step2Input)
{
    try
    {
        //LOG(info) << "Enter " << __FUNCTION__;
        fprintf(stderr,"Enter %s \n",__FUNCTION__);

        Matrix4x4 view, projection;
        ParamVector x;
        Single padding;
        Int width, height;
        {
            JNI::JNIHelper h(env, step2Input);
            h.get("x", x);              LOG(info) << "Extracted x";
            h.get("padding", padding);  LOG(info) << "Extracted padding";
            h.get("width", width);      LOG(info) << "Extracted width";
            h.get("height", height);    LOG(info) << "Exracted height";

            JNI::Object<Matrix4x4> viewO; h.get("view", viewO);
            view = FromMatrix(env, viewO.obj);

            JNI::Object<Matrix4x4> projO; h.get("projection", projO);
            projection = FromMatrix(env, projO.obj);
        }

        HTH helper(env, obj);
        helper.ht.tracker->setupVirtualCameraView(view);
        helper.ht.tracker->setupVirtualCameraProjection(projection);
        cv::Rect bb = helper.ht.tracker->step2_computeBoundingBox(x, width, height, padding);
        LOG(info) << "Computed bounding box";

        jobject ret = Create<Step2Output>(env);
        LOG(info) << "Created Step2Output";
        {
            JNI::JNIHelper h(env, ret);

            JNI::Object<cv::Rect> boundingBox_ = { ToBoundingBox(env, bb) };
            h.set("bb", boundingBox_);
        }

        //LOG(info) << "Leave " << __FUNCTION__;
        fprintf(stderr,"Leave %s \n",__FUNCTION__);

        return ret;
    }
    catch(const std::exception &e)
    {
        Except(env, e);
    }
}

jobject Java_eu_project_rapid_handtracker_HandTrackerJNI_native_1step3_1zoomVirtualCamera
  (JNIEnv *env, jobject obj, jobject step3input)
{
    try
    {
        //LOG(info) << "Enter " << __FUNCTION__;
        fprintf(stderr,"Enter %s \n",__FUNCTION__);

        cv::Rect bb;
        Matrix4x4 projection;
        Int width, height;

        {
            JNI::JNIHelper h(env, step3input);
            h.get("width", width);
            h.get("height", height);

            JNI::Object<cv::Rect> bb_; h.get("bb", bb_);
            bb = FromBoundingBox(env, bb_.obj);

            JNI::Object<Matrix4x4> projection_; h.get("projection", projection_);
            projection = FromMatrix(env, projection_.obj);
        }

        HTH helper(env, obj);
        projection = helper.ht.tracker->step3_zoomVirtualCamera(projection, bb, width, height);

        jobject ret = Create<Step3Output>(env);
        {
            JNI::JNIHelper h(env, ret);
            JNI::Object<Matrix4x4> zoomProjectionMatrix_ = { ToMatrix(env, projection) };
            h.set("zoomProjectionMatrix", zoomProjectionMatrix_);
        }

        //LOG(info) << "Leave " << __FUNCTION__;
        fprintf(stderr,"Leave %s \n",__FUNCTION__);

        return ret;
    }
    catch(const std::exception &e)
    {
        Except(env, e);
    }
}

jobject Java_eu_project_rapid_handtracker_HandTrackerJNI_native_1step4_1preprocessInput
  (JNIEnv *env, jobject obj, jobject step4input)
{
    try
    {
        //LOG(info) << "Enter " << __FUNCTION__;
        fprintf(stderr,"Enter %s \n",__FUNCTION__);

        cv::Rect bb;
        cv::Mat rgb, depth;

        {
            JNI::JNIHelper h(env, step4input);
            JNI::Object<cv::Rect> bb_; h.get("bb", bb_);
            bb = FromBoundingBox(env, bb_.obj);

            JNI::Object<CVImage> rgb_; h.get("rgb", rgb_);
            rgb = FromCVImage(env, rgb_.obj);

            JNI::Object<CVImage> depth_; h.get("depth", depth_);
            depth = FromCVImage(env, depth_.obj);
        }

        HTH h(env, obj);
        cv::Mat labels, depths;
        boost::tie(labels, depths) = h.ht.tracker->step4_preprocessInput(rgb, depth, bb);

        jobject ret = Create<Step4Output>(env);
        {
            JNI::JNIHelper h(env, ret);

            JNI::Object<CVImage> labels_ = { ToCVImage(env, labels) };
            h.set("labels", labels_);

            JNI::Object<CVImage> depths_ = { ToCVImage(env, depths) };
            h.set("depths", depths_);
        }

        //LOG(info) << "Leave " << __FUNCTION__;
        fprintf(stderr,"Leave %s \n",__FUNCTION__);

        return ret;
    }
    catch(const std::exception &e)
    {
        Except(env, e);
    }
}

jobject Java_eu_project_rapid_handtracker_HandTrackerJNI_native_1step5_1track
  (JNIEnv *env, jobject obj, jobject step5input)
{
    try
    {
        //LOG(info) << "Enter " << __FUNCTION__;
        fprintf(stderr,"Enter %s \n",__FUNCTION__);

        ParamVector x;
        Matrix4x4 view, projection;
        cv::Mat labels, depths;

        {
            JNI::JNIHelper h(env, step5input);
            h.get("x", x);

            JNI::Object<Matrix4x4> view_; h.get("view", view_);
            view = FromMatrix(env, view_.obj);

            JNI::Object<Matrix4x4> projection_; h.get("projection", projection_);
            projection = FromMatrix(env, projection_.obj);

            JNI::Object<CVImage> labels_; h.get("labels", labels_);
            labels = FromCVImage(env, labels_.obj);

            JNI::Object<CVImage> depths_; h.get("depths", depths_);
            depths = FromCVImage(env, depths_.obj);
        }

        HTH h(env, obj);
        h.ht.tracker->step5_setObservations(labels, depths);
        h.ht.tracker->setupVirtualCameraView(view);
        h.ht.tracker->setupVirtualCameraProjection(projection);
        h.ht.tracker->step6_track(x, x);

        jobject ret = Create<Step5Output>(env);
        {
            JNI::JNIHelper h(env, ret);
            h.set("x", x);
        }

        //LOG(info) << "Leave " << __FUNCTION__;
        fprintf(stderr,"Leave %s \n",__FUNCTION__);

        return ret;
    }
    catch(const std::exception &e)
    {
        Except(env, e);
    }
}

jobject Java_eu_project_rapid_handtracker_HandTrackerJNI_native_1step6_1visualize
  (JNIEnv *env, jobject obj, jobject step6input)
{
    try
    {
        //LOG(info) << "Enter " << __FUNCTION__;
        fprintf(stderr,"Enter %s \n",__FUNCTION__);

        ParamVector x;
        cv::Mat rgb;
        Matrix4x4 view, projection;

        {
            JNI::JNIHelper h(env, step6input);
            h.get("x", x);
            LOG(info) << "Extracted x";

            JNI::Object<CVImage> rgb_; h.get("rgb", rgb_);
            rgb = FromCVImage(env, rgb_.obj);
            LOG(info) << "Extracted rgb";

            JNI::Object<Matrix4x4> view_; h.get("view", view_);
            view = FromMatrix(env, view_.obj);
            LOG(info) << "Extracted view";

            JNI::Object<Matrix4x4> projection_; h.get("projection", projection_);
            projection = FromMatrix(env, projection_.obj);
            LOG(info) << "Extracted projection";
        }

        HTH h(env, obj);
        cv::Mat3f viz = h.ht.tracker->step7_visualize(rgb, view, projection, x, 1, 0.5);
        LOG(info) << "Computed visualization";

        jobject ret = Create<Step6Output>(env);
        {
            JNI::JNIHelper h(env, ret);

            JNI::Object<CVImage> viz_ = { ToCVImage(env, viz) };
            h.set("viz", viz_);
        }

        //LOG(info) << "Leave " << __FUNCTION__;
        fprintf(stderr,"Leave %s \n",__FUNCTION__);

        return ret;
    }
    catch(const std::exception &e)
    {
        Except(env, e);
    }
}

jint Java_eu_project_rapid_handtracker_HandTrackerJNI_waitKey(JNIEnv *, jclass, jint wait)
{
	return cv::waitKey(wait) & 255;
}


jdoubleArray Java_eu_project_rapid_handtracker_HandTrackerJNI_getDefaultInitPos(JNIEnv *env, jclass)
{
	typedef JNI::JavaGetSet<Vector<Double>::Type> Helper;
	return Helper::write(env, HandTrackerLib::defaultInitPos);
}

void Java_eu_project_rapid_handtracker_HandTrackerJNI_initLog(JNIEnv *env, jclass, jobjectArray args)
{
	if (args)
	{
		jint N = env->GetArrayLength(args);

		Vector<String>::Type args_;

		for (jint i = 0 ; i < N ; ++i)
		{
			jstring str = (jstring)env->GetObjectArrayElement(args, i);
			args_.push_back(J2C(env, str));
		}

		Vector<char*>::Type argv;
		for (size_t i = 0 ; i < args_.size() ; ++i)
			argv.push_back(const_cast<char*>(args_[i].c_str()));

		InitLog(argv.size(), argv.data());
	}
}

jdouble Java_eu_project_rapid_handtracker_HandTrackerJNI_getTime(JNIEnv *, jclass)
{
    static StopWatch stopWatch;
    return stopWatch.getElapsedSeconds();
}
