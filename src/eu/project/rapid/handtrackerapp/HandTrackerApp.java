package eu.project.rapid.handtrackerapp;

import eu.project.rapid.ac.DFE;
import eu.project.rapid.common.RapidConstants;
import eu.project.rapid.common.RapidConstants.COMM_TYPE;
import eu.project.rapid.handtracker.HandTracker;
import eu.project.rapid.handtracker.HandTrackerJNI;

class HandTrackerApp
{
	public static void main(String[] args) throws Exception
	{
                System.out.println("HandTrackerApp for RAPID started with "+args.length+" arguments\n");
                String ip="127.0.0.1"; 
                
                int framesReceived=0; 
		boolean enableRemoteForce = false;
		boolean enableSinglestep = false;
                boolean enableAutostart=false;
                boolean enableAutostop=false;
                int autostart=0;
                int autostop=0;

                for (int i=0; i<args.length; i++)
                  {
                     if ( (args[i].equals("-ip")) && (i+1<args.length) ) 
                     {
                       ip = args[i+1];
                     }
                     if ( (args[i].equals("-autostart")) && (i+1<args.length) ) 
                     {
                       autostart = Integer.parseInt(args[i+1]);
                       enableAutostart = true;
                     }

                     if (args[i].equals("-forceremote")) 
                     { 
                       enableRemoteForce = true;
                     }
                     
                     if ( (args[i].equals("-autostop")) && (i+1<args.length) ) 
                     {
                       autostop = Integer.parseInt(args[i+1]);
                       enableAutostop = true;
                     }
                     
                     if ( args[i].equals("-singlestep") ) 
                     {
                       enableSinglestep = true;
                     }
                  }
                
                 System.out.println("HandTrackerApp will try to connect to IP : "+ip+"\n");

		DFE dfe = DFE.getInstance(ip);
                
                //Also Local
                if (enableRemoteForce) { dfe.setUserChoice(RapidConstants.ExecLocation.REMOTE); } else
                                       { dfe.setUserChoice(RapidConstants.ExecLocation.DYNAMIC); }

		HandTracker tracker = new HandTracker(dfe);

		boolean tracking = false;
		boolean stop = false;
		double[] x = tracker.getDefaultInitPosRAPID();
		
		int iterations = 0;
		double time = 0.0;
                 
                double timeSummed =0.0;

		while (!stop)
		{
			
			double start = HandTrackerJNI.getTime(); 
                        HandTrackerJNI.Step1Output step1o = tracker.step1_grab();
                        ++framesReceived;

			if (tracking)
			{
			        start = HandTrackerJNI.getTime(); //If we are tracking we only care about optimization loop
                                 
                        	HandTrackerJNI.Step2Input step2i = new HandTrackerJNI.Step2Input();
				step2i.x = x;
				step2i.width = step1o.rgb.width;
				step2i.height = step1o.rgb.height;
				step2i.padding = 0.1f;
				step2i.view = step1o.view;
				step2i.projection = step1o.projection;

                                if (enableSinglestep)
                                   {  
                                    System.out.print(String.format("Singlestep\n"));
	                            HandTrackerJNI.Step5Output step5o = tracker.step2to5_AllInOneRAPID(step2i,step1o);
				    x = step5o.x;		
                                   } else
                                   {
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
			}

			HandTrackerJNI.Step6Input step6i = new HandTrackerJNI.Step6Input();
			step6i.projection = step1o.projection;
			step6i.view = step1o.view;
			step6i.x = x;
			step6i.rgb = step1o.rgb;
			HandTrackerJNI.Step6Output step6o = tracker.step6_visualize(step6i);

			tracker.showImage("Viz", step6o.viz);
			int key = tracker.waitKey(1);
                        

                       if (enableAutostart)
                         {
                          if (autostart==framesReceived)
                           {
                             key='s';
                             enableAutostart=false;
                             iterations=0;
                             timeSummed=0;
                           }  
                         }

                       if (enableAutostop)
                        { 
                          if (autostop==framesReceived)
                           {
                             key='q'; 
                             System.exit(0);
                           }  
                        } 

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
				 timeSummed += time; 
				 iterations++; 	
			}

	         if (time>0)       { System.out.print(String.format("FPS %d %f %f \n", iterations, 1 / time , iterations / timeSummed )); } else
                                   { System.out.print(String.format("Not ready to log framerate yet \n")); }
		}
		
	}
}
