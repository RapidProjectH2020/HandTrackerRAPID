package eu.project.rapid.handtrackerapp;

import eu.project.rapid.ac.DFE;
import eu.project.rapid.handtracker.HandTracker;

class HandTrackerApp
{
	public static void main(String[] args) throws Exception
	{
                //System.out.println("Starting up HandTracker..!\n");
		DFE dfe = DFE.getInstance("127.0.0.1"); //"127.0.0.1"
		HandTracker tracker = null;
		
		if (args.length > 1)
			tracker = new HandTracker(dfe, args[0], Integer.parseInt(args[1]));
		else
			tracker = new HandTracker(dfe, true);

		boolean tracking = false;
		boolean stop = false;
		double[] x = tracker.getDefaultInitPos();

		while (!stop)
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
}
