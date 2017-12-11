package eu.project.rapid.handtracker;

import java.lang.reflect.Method;
import eu.project.rapid.ac.DFE;
import eu.project.rapid.ac.Remoteable;
import eu.project.rapid.handtracker.HandTrackerJNI.Image;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HandTracker extends Remoteable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3845013264516843637L;
	private HandTrackerJNI		tracker;
	private transient DFE		dfe;
	private final static Logger	log					= LogManager.getLogger(HandTracker.class.getSimpleName());

	public HandTracker(DFE dfe)
	{
		this.dfe = dfe;
		this.tracker = new HandTrackerJNI();
	}

	public HandTrackerJNI.Step1Output step1_grabRAPID()
	{
		Class<?>[] parameterTypes = {};
		Method method;
		try
		{
			method = this.getClass().getMethod("step1_grab", parameterTypes);
			Object result = dfe.execute(method, this);
			if (result instanceof Exception) throw (Exception) result;
			else return (HandTrackerJNI.Step1Output) result;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public HandTrackerJNI.Step1Output step1_grab()
	{
		try
		{
			log.info("Trying to execute step1_grab");
			return tracker.native_step1_grab();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public HandTrackerJNI.Step2Output step2_computeBoundingBoxRAPID(HandTrackerJNI.Step2Input input)
	{
		Class<?>[] parameterTypes = { input.getClass() };
		Object[] paramValues = { input };
		Method method;
		try
		{
			method = this.getClass().getMethod("step2_computeBoundingBox", parameterTypes);
			Object result = dfe.execute(method, paramValues, this);
			if (result instanceof Exception) throw (Exception) result;
			else return (HandTrackerJNI.Step2Output) result;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public HandTrackerJNI.Step2Output step2_computeBoundingBox(HandTrackerJNI.Step2Input input)
	{
		try
		{
			return tracker.native_step2_computeBoundingBox(input);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public HandTrackerJNI.Step3Output step3_zoomVirtualCameraRAPID(HandTrackerJNI.Step3Input input)
	{
		Class<?>[] parameterTypes = { input.getClass() };
		Object[] paramValues = { input };
		Method method;
		try
		{
			method = this.getClass().getMethod("step3_zoomVirtualCamera", parameterTypes);
			Object result = dfe.execute(method, paramValues, this);
			if (result instanceof Exception) throw (Exception) result;
			else return (HandTrackerJNI.Step3Output) result;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public HandTrackerJNI.Step3Output step3_zoomVirtualCamera(HandTrackerJNI.Step3Input input)
	{
		try
		{
			return tracker.native_step3_zoomVirtualCamera(input);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public HandTrackerJNI.Step4Output step4_preprocessInputRAPID(HandTrackerJNI.Step4Input input)
	{
		Class<?>[] parameterTypes = { input.getClass() };
		Object[] paramValues = { input };
		Method method;
		try
		{
			method = this.getClass().getMethod("step4_preprocessInput", parameterTypes);
			Object result = dfe.execute(method, paramValues, this);
			if (result instanceof Exception) throw (Exception) result;
			else return (HandTrackerJNI.Step4Output) result;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public HandTrackerJNI.Step4Output step4_preprocessInput(HandTrackerJNI.Step4Input input)
	{
		try
		{
			return tracker.native_step4_preprocessInput(input);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public HandTrackerJNI.Step5Output step5_trackRAPID(HandTrackerJNI.Step5Input input)
	{
		Class<?>[] parameterTypes = { input.getClass() };
		Object[] paramValues = { input };
		Method method;
		try
		{
			method = this.getClass().getMethod("step5_track", parameterTypes);
			Object result = dfe.execute(method, paramValues, this);
			if (result instanceof Exception) throw (Exception) result;
			else return (HandTrackerJNI.Step5Output) result;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public HandTrackerJNI.Step5Output step5_track(HandTrackerJNI.Step5Input input)
	{
		try
		{
			return tracker.native_step5_track(input);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public HandTrackerJNI.Step6Output step6_visualizeRAPID(HandTrackerJNI.Step6Input input)
	{
		Class<?>[] parameterTypes = { input.getClass() };
		Object[] paramValues = { input };
		Method method;
		try
		{
			method = this.getClass().getMethod("step6_visualize", parameterTypes);
			Object result = dfe.execute(method, paramValues, this);
			if (result instanceof Exception) throw (Exception) result;
			else return (HandTrackerJNI.Step6Output) result;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public HandTrackerJNI.Step6Output step6_visualize(HandTrackerJNI.Step6Input input)
	{
		try
		{
			return tracker.native_step6_visualize(input);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public double[] getDefaultInitPosRAPID()
	{
		Class<?>[] parameterTypes = {};
		Method method;
		try
		{
			method = this.getClass().getMethod("getDefaultInitPos", parameterTypes);
			Object result = dfe.execute(method, this);
			if (result instanceof Exception) throw (Exception) result;
			else return (double[]) result;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public double[] getDefaultInitPos()
	{
		log.info("Trying to execute getDefaultInitPos which doesnt work for some reason");
		return HandTrackerJNI.getDefaultInitPos(); 
	}

	public void initLogRAPID(String[] args)
	{
		Class<?>[] parameterTypes = { String[].class };
		Method method;
		Object[] paramValues = { args };
		try
		{
			method = this.getClass().getMethod("initLog", parameterTypes);
			Object result = dfe.execute(method, paramValues, this);
			if (result instanceof Exception) throw (Exception) result;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initLog(String[] args)
	{
		log.info("Trying to execute initLog");
		HandTrackerJNI.initLog(args);
	}

	@Override
	public void copyState(Remoteable state)
	{
		// TODO Auto-generated method stub

	}

	public void showImage(String name, Image viz)
	{
		// TODO Auto-generated method stub
		HandTrackerJNI.showImage(name, viz);
	}

	public int waitKey(int i)
	{
		// TODO Auto-generated method stub
		return tracker.waitKey(i);
	}

}
