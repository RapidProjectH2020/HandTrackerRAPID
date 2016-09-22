package eu.project.rapid.handtracker;

import java.io.Serializable;

public class HandTrackerJNI implements java.io.Serializable
{
	private static final long serialVersionUID = -7229075871291249851L;

	// Basic types
	static public class Image implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1820484566470547231L;
		public int					width, height, type;
		public byte[]				data;
	}

	static public class Matrix4x4 implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -7533883944605635103L;
		public float[]				data;
	}

	static public class BoundingBox implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -1182080569542218513L;
		public int					x, y, width, height;
	}

	// Compound types
	static public class Step1Output implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -7915198404469343079L;
		public Image				rgb;
		public Image				depth;
		public Matrix4x4			view, projection;
	}

	static public class Step2Input implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -8544494730230459685L;
		public int					width;
		public int					height;
		public Matrix4x4			view, projection;
		public double[]				x;
		public float				padding;
	}

	static public class Step2Output implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -1554647958896274332L;
		public BoundingBox			bb;
	}

	static public class Step3Input implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -5915250091329431074L;
		public int					width, height;
		public BoundingBox			bb;
		public Matrix4x4			projection;
	}

	static public class Step3Output implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -2490868582531574363L;
		public Matrix4x4			zoomProjectionMatrix;
	}

	static public class Step4Input implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 3036005958993968225L;
		public BoundingBox			bb;
		public Image				rgb;
		public Image				depth;
	}

	static public class Step4Output implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1629557625495563033L;
		public Image				labels;
		public Image				depths;
	}

	static public class Step5Input implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 5866255157108133246L;
		public double[]				x;
		public Matrix4x4			view, projection;
		public Image				labels;
		public Image				depths;
	}

	static public class Step5Output implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -1211806922442748433L;
		public double[]				x;
	}

	static public class Step6Input implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 39014894702908718L;
		public double[]				x;
		public Image				rgb;
		public Matrix4x4			view, projection;
	}

	static public class Step6Output implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= -3435429129025876726L;
		public Image				viz;
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
	
	public HandTrackerJNI()
	{
		
	}

	public static void main(String args[]) throws Exception
	{
		HandTrackerJNI tracker = new HandTrackerJNI();

		boolean tracking = false;
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
				step2i.projection = step1o.projection;
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

	public static native void showImage(String name, Image img);

	public static native int waitKey(int wait);

	public native Step1Output native_step1_grab();

	public native Step2Output native_step2_computeBoundingBox(Step2Input input);

	public native Step3Output native_step3_zoomVirtualCamera(Step3Input input);

	public native Step4Output native_step4_preprocessInput(Step4Input input);

	public native Step5Output native_step5_track(Step5Input input);

	public native Step6Output native_step6_visualize(Step6Input input);
}
