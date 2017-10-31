package eu.project.rapid.handtrackerapp;

import eu.project.rapid.ac.DFE;
import eu.project.rapid.handtracker.HandTracker;
import eu.project.rapid.handtracker.HandTrackerJNI;

class HandTrackerApp
{
	public static void main(String[] args) throws Exception
	{
                System.out.println("HandTrackerApp for RAPID started with "+args.length+" arguments\n");
                String ip="127.0.0.1"; 

                for (int i=0; i<args.length; i++)
                  {
                     if ( (args[i].equals("-ip")) && (i+1<args.length) ) 
                     {
                       ip = args[i+1];
                     }
                  }
                
                 System.out.println("HandTrackerApp will try to connect to IP : "+ip+"\n");

		DFE dfe = DFE.getInstance(ip);
		HandTracker tracker = new HandTracker(dfe);

		boolean tracking = false;
		boolean stop = false;
		double[] x = tracker.getDefaultInitPosRAPID();
		
		int iterations = 0;
		double time = 0.0;

		while (!stop)
		{
			double start = HandTrackerJNI.getTime();
			
			HandTrackerJNI.Step1Output step1o = tracker.step1_grab();

			if (tracking)
			{
				HandTrackerJNI.Step2Input step2i = new HandTrackerJNI.Step2Input();
				step2i.x = x;
				step2i.width = step1o.rgb.width;
				step2i.height = step1o.rgb.height;
				step2i.padding = 0.1f;
				step2i.view = step1o.view;
				step2i.projection = step1o.projection;
				HandTrackerJNI.Step2Output step2o = tracker.step2_computeBoundingBoxRAPID(step2i);

				HandTrackerJNI.Step3Input step3i = new HandTrackerJNI.Step3Input();
				step3i.bb = step2o.bb;
				step3i.projection = step1o.projection;
				step3i.width = step1o.rgb.width;
				step3i.height = step1o.rgb.height;
				HandTrackerJNI.Step3Output step3o = tracker.step3_zoomVirtualCameraRAPID(step3i);

				HandTrackerJNI.Step4Input step4i = new HandTrackerJNI.Step4Input();
				step4i.bb = step2o.bb;
				step4i.depth = step1o.depth;
				step4i.rgb = step1o.rgb;
				HandTrackerJNI.Step4Output step4o = tracker.step4_preprocessInputRAPID(step4i);

				HandTrackerJNI.Step5Input step5i = new HandTrackerJNI.Step5Input();
				step5i.depths = step4o.depths;
				step5i.labels = step4o.labels;
				step5i.projection = step3o.zoomProjectionMatrix;
				step5i.view = step1o.view;
				step5i.x = x;
				HandTrackerJNI.Step5Output step5o = tracker.step5_trackRAPID(step5i);

				x = step5o.x;				
			}

			HandTrackerJNI.Step6Input step6i = new HandTrackerJNI.Step6Input();
			step6i.projection = step1o.projection;
			step6i.view = step1o.view;
			step6i.x = x;
			step6i.rgb = step1o.rgb;
			HandTrackerJNI.Step6Output step6o = tracker.step6_visualize(step6i);

			tracker.showImage("Viz", step6o.viz);
			int key = tracker.waitKey(1);

			if (key == 's')
			{
				if (tracking) x = HandTrackerJNI.getDefaultInitPos();
				tracking = !tracking;
			}

			if (key == 'q')
			{
				stop = true;
			}

			if (tracking)
			{
				double now = HandTrackerJNI.getTime();
				time = now - start;
				iterations++;				
			}

		}
		
		System.out.print(String.format("FPS : %f", time / iterations));
               //System.out.println(String.format("Iterations %03d", iterations));
	}
}
