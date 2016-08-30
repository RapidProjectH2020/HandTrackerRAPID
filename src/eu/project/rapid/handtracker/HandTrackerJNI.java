package eu.project.rapid.handtracker;

class HandTrackerJNI
{
    static
    {
        System.loadLibrary("HandTrackerJNI");
    }

    public HandTrackerJNI() throws Exception
    {
        createHandTracker();
    }

    public HandTrackerJNI(String oniFile, int startFrame) throws Exception
    {
        createHandTracker(oniFile, startFrame);
    }

    protected void finalize() throws Exception
    {
        destroyHandTracker();
    }

    public void step1_grab() throws Exception
    {
        this.grab();
    }

    public void step2_setupVirtualCameraFromInput() throws Exception
    {
        this.setCamera(this.lastViewMatrix, this.lastProjectionMatrix);
    }

    public void step3_computeBoundingBox(double [] hypothesis, float padding) throws Exception
    {
        this.computeBoundingBox(hypothesis, padding);
    }

    public void step4_zoomVirtualCamera() throws Exception
    {
        this.zoomVirtualCamera();
    }

    public void step5_preprocessInput() throws Exception
    {
        this.preprocessInput();
    }

    public void step6_uploadObservations() throws Exception
    {
        this.uploadObservations();
    }

    public double [] step7_track(double [] x) throws Exception
    {
        return this.track(x);
    }

    public int step8_visualize(double [] x) throws Exception
    {
        this.visualize(x);
        this.showRGB("viz", this.width, this.lastVisualization);
        return this.waitKey(1);
    }

    public static void main(String args[]) throws Exception
    {
        HandTrackerJNI tracker = null;

        boolean tracking = false;
        if (args.length > 1)
        {
            tracker = new HandTrackerJNI(args[0], Integer.parseInt(args[1]));
            tracking = true;
        }
        else
            tracker = new HandTrackerJNI();

        boolean stop = false;
        double [] x = tracker.getDefaultInitPos();

        while(!stop)
        {
            tracker.step1_grab();
            tracker.step2_setupVirtualCameraFromInput();

            if (tracking)
            {
                tracker.step3_computeBoundingBox(x, 0.1f);
                tracker.step4_zoomVirtualCamera();
                tracker.step5_preprocessInput();
                tracker.step6_uploadObservations();
                x = tracker.step7_track(x);
            }

            int key = tracker.step8_visualize(x);

            if (key == 's')
            {
                if (tracking) x = tracker.getDefaultInitPos();
                tracking = !tracking;
            }

            if (key == 'q')
            {
                stop = true;
            }
        }
    }

    public static native void initLog(String [] args);
    public static native double [] getDefaultInitPos();

    private static native void showRGB(String name, int width, byte [] data) throws Exception;
    private static native void showDepth(String name, int width, short [] data) throws Exception;
    private static native int waitKey(int wait);


    private native void createHandTracker() throws Exception;
    private native void createHandTracker(String oniFile, int startFrame) throws Exception;
    private native void destroyHandTracker() throws Exception;

    private native void grab() throws Exception;
    private native void setCamera(float [] view, float [] projection) throws Exception;
    private native void computeBoundingBox(double [] hypothesis, float padding) throws Exception;
    private native void zoomVirtualCamera() throws Exception;
    private native void preprocessInput() throws Exception;
    private native void uploadObservations() throws Exception;
    private native double [] track(double [] x) throws Exception;
    private native void visualize(double [] x) throws Exception;

    int handTracker;

    int width;
    int height;
    byte [] lastRGB;
    short [] lastDepth;
    float [] lastViewMatrix;
    float [] lastProjectionMatrix;
    float [] lastZoomMatrix;
    byte [] lastProcessedLabels;
    short [] lastProcessedDepths;
    int bbx, bby, bbwidth, bbheight;
    byte [] lastVisualization;
}
