package eu.project.rapid.handtracker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class HandTrackerJNI implements java.io.Serializable
{
	private static final long serialVersionUID = -7229075871291249851L;

	// Basic types
	static public class ByteImage implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 2066201065657020072L;
		int							width, height;
		byte[]						data;
	}

	static public class ShortImage implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 2484457944228504710L;
		int							width, height;
		short[]						data;
	}

	static public class FloatImage implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -4477354002053783731L;
		int							width, height;
		float[]						data;
	}

	static public class Matrix4x4 implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -7533883944605635103L;
		float[]						data;
	}

	static public class BoundingBox implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -1182080569542218513L;
		int							x, y, width, height;
	}

	// Compound types
	static public class Step1Output implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -7915198404469343079L;
		ByteImage					rgb;
		ShortImage					depth;
		Matrix4x4					view, projection;
	}

	static public class Step2Input implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -8544494730230459685L;
		int							width, height;
		Matrix4x4					view, projection;
		double[]					x;
		float						padding;
	}

	static public class Step2Output implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -1554647958896274332L;
		BoundingBox					bb;
	}

	static public class Step3Input implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -5915250091329431074L;
		int							width, height;
		BoundingBox					bb;
		Matrix4x4					projection;
	}

	static public class Step3Output implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -2490868582531574363L;
		Matrix4x4					zoomProjectionMatrix;
	}

	static public class Step4Input implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 3036005958993968225L;
		BoundingBox					bb;
		ByteImage					rgb;
		ShortImage					depth;
	}

	static public class Step4Output implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1629557625495563033L;
		ByteImage					labels;
		ShortImage					depths;
	}

	static public class Step5Input implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 5866255157108133246L;
		double[]					x;
		Matrix4x4					view, projection;
		ByteImage					labels;
		ShortImage					depths;
	}

	static public class Step5Output implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -1211806922442748433L;
		double[]					x;
	}

	static public class Step6Input implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 39014894702908718L;
		double[]					x;
		ByteImage					rgb;
		Matrix4x4					view, projection;
	}

	static public class Step6Output implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -3435429129025876726L;
		FloatImage					viz;
	}

	static
	{
		try
		{
			System.out.println("HandTrackerJNI : trying to load");
			System.loadLibrary("HandTrackerJNI");
			System.out.println("HandTrackerJNI : loaded successfully");
		}
		catch (UnsatisfiedLinkError e)
		{
			e.printStackTrace();
		}
	}

	public HandTrackerJNI() throws Exception
	{
		this(false);
		System.out.println("Default constructor called");
	}

	public HandTrackerJNI(boolean withGrab) throws Exception
	{
		System.out.println("1-arg constructor called");
		createHandTracker(withGrab);
		hasGrabber = withGrab;
	}

	public HandTrackerJNI(String oniFile, int startFrame) throws Exception
	{
		System.out.println("2-arg constructor called");
		createHandTracker(oniFile, startFrame);
		hasGrabber = false;
	}

	protected void finalize() throws Exception
	{
		destroyHandTracker();
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
		else tracker = new HandTrackerJNI(true);

		boolean stop = false;
		double[] x = HandTrackerJNI.getDefaultInitPos();

		while (!stop)
		{
			Step1Output step1o = tracker.native_step1_grab();

			if (tracking)
			{
				Step2Input step2i = new Step2Input();
				step2i.x = x;
				step2i.width = step1o.rgb.width;
				step2i.height = step1o.rgb.height;
				step2i.padding = 0.1f;
				step2i.view = step1o.view;
				step2i.projection = step2i.projection;
				Step2Output step2o = tracker.native_step2_computeBoundingBox(step2i);

				Step3Input step3i = new Step3Input();
				step3i.bb = step2o.bb;
				step3i.projection = step1o.projection;
				step3i.width = step1o.rgb.width;
				step3i.height = step1o.rgb.height;
				Step3Output step3o = tracker.native_step3_zoomVirtualCamera(step3i);

				Step4Input step4i = new Step4Input();
				step4i.bb = step2o.bb;
				step4i.depth = step1o.depth;
				step4i.rgb = step1o.rgb;
				Step4Output step4o = tracker.native_step4_preprocessInput(step4i);

				Step5Input step5i = new Step5Input();
				step5i.depths = step4o.depths;
				step5i.labels = step4o.labels;
				step5i.projection = step3o.zoomProjectionMatrix;
				step5i.view = step1o.view;
				step5i.x = x;
				Step5Output step5o = tracker.native_step5_track(step5i);

				x = step5o.x;
			}

			Step6Input step6i = new Step6Input();
			step6i.projection = step1o.projection;
			step6i.view = step1o.view;
			step6i.x = x;
			step6i.rgb = step1o.rgb;
			Step6Output step6o = tracker.native_step6_visualize(step6i);

			showImage("Viz", step6o.viz);
			int key = waitKey(1);

			if (key == 's')
			{
				if (tracking) x = HandTrackerJNI.getDefaultInitPos();
				tracking = !tracking;
			}

			if (key == 'q')
			{
				stop = true;
			}
		}
	}

	public static native void initLog(String[] args);

	public static native double[] getDefaultInitPos();

	public static native void showImage(String name, ByteImage img) throws Exception;

	public static native void showImage(String name, ShortImage img) throws Exception;

	public static native void showImage(String name, FloatImage img) throws Exception;

	public static native int waitKey(int wait);

	public native void createHandTracker(boolean withGrabber) throws Exception;

	public native void createHandTracker(String oniFile, int startFrame) throws Exception;

	public native void destroyHandTracker() throws Exception;

	public native Step1Output native_step1_grab() throws Exception;

	public native Step2Output native_step2_computeBoundingBox(Step2Input input) throws Exception;

	public native Step3Output native_step3_zoomVirtualCamera(Step3Input input) throws Exception;

	public native Step4Output native_step4_preprocessInput(Step4Input input) throws Exception;

	public native Step5Output native_step5_track(Step5Input input) throws Exception;

	public native Step6Output native_step6_visualize(Step6Input input) throws Exception;

	transient long	handTracker;
	boolean			hasGrabber;

	private void readObject(ObjectInputStream is) throws Exception
	{
		if (handTracker == 0) createHandTracker(hasGrabber);
		System.out.println("Deserialization started");
		is.defaultReadObject();
		System.out.println("Deserialization ended");
	}

	private void writeObject(ObjectOutputStream os) throws IOException
	{
		System.out.println("Serialization started");
		os.defaultWriteObject();
		System.out.println("Serialization ended");
	}
}
