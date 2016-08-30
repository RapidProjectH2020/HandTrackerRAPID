package eu.project.rapid.handtracker;

import java.lang.reflect.Method;

import eu.project.rapid.ac.DFE;
import eu.project.rapid.ac.Remoteable;

public class HandTracker extends Remoteable
{
	private transient HandTrackerJNI tracker;
	private transient DFE dfe;
	
	public HandTracker(DFE dfe) throws Exception
	{
		this.dfe = dfe;
		this.tracker = new HandTrackerJNI();
	}
	
	public void step1_grab()
	{
	    Class<?>[] parameterTypes = {};
	    Method method;
	    try {
	      method = this.getClass().getMethod("step1_grabRAPID", parameterTypes);
	      dfe.execute(method, this);
	    } catch (NoSuchMethodException | SecurityException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	}
	
	public void step1_grabRAPID()
	{
		try
		{
			tracker.step1_grab();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void step2_setupVirtualCameraFromInput()
	{
		Class<?>[] parameterTypes = {};
	    Method method;
	    try {
	      method = this.getClass().getMethod("step2_setupVirtualCameraFromInputRAPID", parameterTypes);
	      dfe.execute(method, this);
	    } catch (NoSuchMethodException | SecurityException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	}
	
	public void step2_setupVirtualCameraFromInputRAPID()
	{
		try
		{
			tracker.step2_setupVirtualCameraFromInput();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void step3_computeBoundingBox(double [] hypothesis, float padding)
    {
		Class<?>[] parameterTypes = {double[].class, float.class};
	    Method method;
	    Object [] paramValues = {hypothesis, padding};
	    try {
	      method = this.getClass().getMethod("step3_computeBoundingBoxRAPID", parameterTypes);
	      dfe.execute(method, paramValues, this);
	    } catch (NoSuchMethodException | SecurityException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
    }
	
	public void step3_computeBoundingBoxRAPID(double [] hypothesis, float padding)
	{
		try
		{
			this.tracker.step3_computeBoundingBox(hypothesis, padding);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void step4_zoomVirtualCamera()
    {
		Class<?>[] parameterTypes = {};
	    Method method;
	    try {
	      method = this.getClass().getMethod("step4_zoomVirtualCameraRAPID", parameterTypes);
	      dfe.execute(method, this);
	    } catch (NoSuchMethodException | SecurityException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
    }
	
	public void step4_zoomVirtualCameraRAPID()
	{
		try
		{
			this.tracker.step4_zoomVirtualCamera();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void step5_preprocessInput()
	{
		Class<?>[] parameterTypes = {};
	    Method method;
	    try {
	      method = this.getClass().getMethod("step5_preprocessInputRAPID", parameterTypes);
	      dfe.execute(method, this);
	    } catch (NoSuchMethodException | SecurityException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	}
	
	public void step5_preprocessInputRAPID()
	{
		try
		{
			this.tracker.step5_preprocessInput();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void step6_uploadObservations()
    {
		Class<?>[] parameterTypes = {};
	    Method method;
	    try {
	      method = this.getClass().getMethod("step6_uploadObservationsRAPID", parameterTypes);
	      dfe.execute(method, this);
	    } catch (NoSuchMethodException | SecurityException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
    }
	
	public void step6_uploadObservationsRAPID()
	{
		try
		{
			this.tracker.step6_uploadObservations();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public double [] step7_track(double [] x)
    {
		Class<?>[] parameterTypes = {double[].class};
	    Method method;
	    Object [] paramValues = {x};
	    try {
	      method = this.getClass().getMethod("step7_trackRAPID", parameterTypes);
	      return (double[])dfe.execute(method, paramValues, this);
	    } catch (NoSuchMethodException | SecurityException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	      return null;
	    }
    }
	
	public double [] step7_trackRAPID(double [] x)
	{
		try
		{
			return this.tracker.step7_track(x);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public int step8_visualize(double [] x)
	{
		Class<?>[] parameterTypes = {double[].class};
	    Method method;
	    Object [] paramValues = {x};
	    try {
	      method = this.getClass().getMethod("step8_visualizeRAPID", parameterTypes);
	      return (int)dfe.execute(method, paramValues, this);
	    } catch (NoSuchMethodException | SecurityException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	      return -1;
	    }
	}
	
	public int step8_visualizeRAPID(double [] x)
	{
		try
		{
			return this.tracker.step8_visualize(x);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	public double [] getDefaultInitPos()
	{
		Class<?>[] parameterTypes = {};
	    Method method;
	    try {
	      method = this.getClass().getMethod("getDefaultInitPosRAPID", parameterTypes);
	      return (double[])dfe.execute(method, this);
	    } catch (NoSuchMethodException | SecurityException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	      return null;
	    }
	}
	
	public double [] getDefaultInitPosRAPID()
	{
		return this.tracker.getDefaultInitPos();
	}
	
	@Override
	public void copyState(Remoteable state)
	{
		// TODO Auto-generated method stub
		
	}

}
