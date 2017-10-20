#pragma once

#include <HandTrackerLib/HandTrackerLib.hpp>

namespace MBV { namespace HandTracking {

class HandTrackerOtherThread
{
public:
    typedef boost::shared_ptr<HandTrackerLib> HTPtr;

    HandTrackerOtherThread();

    static void startHTThread();
    static void stopHTThread();

    static Grabber::Ptr generateLiveGrabber();
    static Grabber::Ptr generateFileGrabber(const String &oniFile, UInt start = 0);

    Decoder::Ptr getDecoder() const;
    ExposedRenderer::Ptr getRenderer() const;
    void setupVirtualCameraView(const Matrix4x4 &mat);
    void setupVirtualCameraProjection(const Matrix4x4 &mat);
    cv::Rect step2_computeBoundingBox(const ParamVector &x, UInt width, UInt height, Single padding);
    Matrix4x4 step3_zoomVirtualCamera(const Matrix4x4 &projection, const cv::Rect &focus, UInt width, UInt height);
    std::pair<cv::Mat, cv::Mat> step4_preprocessInput(const cv::Mat &rgb, const cv::Mat &depth, const cv::Rect &focus);
    void step5_setObservations(const cv::Mat &labels, const cv::Mat &depths);
    double step6_track(const ParamVector &previousSolution, ParamVector &solution);
    cv::Mat step7_visualize(const cv::Mat &rgb, const Matrix4x4 &view, const Matrix4x4 &projection,
                            const ParamVector &solution, UInt edgeThickness = 1, float alpha = 0.5f);
private:
    HTPtr m_handTracker;
};

}}
