package eu.project.rapid.handtrackerapp;

import eu.project.rapid.ac.DFE;
import eu.project.rapid.handtracker.HandTracker;

class HandTrackerApp
{
	public static void main(String[] args) throws Exception
	{
		DFE dfe = new DFE();
		HandTracker tracker = new HandTracker(dfe);

		boolean tracking = false;
		boolean stop = false;
		double[] x = tracker.getDefaultInitPosRAPID();

		while (!stop)
		{
			tracker.step1_grabRAPID();
			tracker.step2_setupVirtualCameraFromInputRAPID();

			if (tracking)
			{
				tracker.step3_computeBoundingBoxRAPID(x, 0.1f);
				tracker.step4_zoomVirtualCameraRAPID();
				tracker.step5_preprocessInputRAPID();
				tracker.step6_uploadObservationsRAPID();
				x = tracker.step7_trackRAPID(x);
			}

			int key = tracker.step8_visualizeRAPID(x);

			if (key == 's')
			{
				if (tracking) x = tracker.getDefaultInitPosRAPID();
				tracking = !tracking;
			}

			if (key == 'q')
			{
				stop = true;
			}
		}
	}
}