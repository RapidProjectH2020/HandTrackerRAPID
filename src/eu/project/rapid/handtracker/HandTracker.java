package eu.project.rapid.handtracker;

import java.lang.reflect.Method;
import eu.project.rapid.ac.DFE;
import eu.project.rapid.ac.Remoteable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HandTracker extends Remoteable
{
	private HandTrackerJNI	tracker;
	private transient DFE				dfe;
	private final static Logger log = LogManager.getLogger(HandTracker.class.getSimpleName());

	public HandTracker(DFE dfe) throws Exception
	{
		this.dfe = dfe;
		this.tracker = new HandTrackerJNI();
	}
	
	public HandTracker(DFE dfe, String oniFile, int startFrame) throws Exception
	{
		this.dfe = dfe;
		this.tracker = new HandTrackerJNI(oniFile, startFrame);
	}

	public void step1_grab()
	{
		Class<?>[] parameterTypes = {};
		Method method;
		try
		{
			method = this.getClass().getMethod("step1_grabRAPID", parameterTypes);
			dfe.execute(method, this);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void step1_grabRAPID()
	{
		try
		{
			log.info("Trying to execute step1_grab");
			tracker.step1_grab();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void step2_setupVirtualCameraFromInput()
	{
		Class<?>[] parameterTypes = {};
		Method method;
		try
		{
			method = this.getClass().getMethod("step2_setupVirtualCameraFromInputRAPID", parameterTypes);
			dfe.execute(method, this);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void step2_setupVirtualCameraFromInputRAPID()
	{
		try
		{
			log.info("Trying to execute step2_setupVirtualCameraFromInput");
			tracker.step2_setupVirtualCameraFromInput();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void step3_computeBoundingBox(double[] hypothesis, float padding)
	{
		Class<?>[] parameterTypes = { double[].class, float.class };
		Method method;
		Object[] paramValues = { hypothesis, padding };
		try
		{
			method = this.getClass().getMethod("step3_computeBoundingBoxRAPID", parameterTypes);
			dfe.execute(method, paramValues, this);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void step3_computeBoundingBoxRAPID(double[] hypothesis, float padding)
	{
		try
		{
			log.info("Trying to execute step3_computeBoundingBox");
			this.tracker.step3_computeBoundingBox(hypothesis, padding);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void step4_zoomVirtualCamera()
	{
		Class<?>[] parameterTypes = {};
		Method method;
		try
		{
			method = this.getClass().getMethod("step4_zoomVirtualCameraRAPID", parameterTypes);
			dfe.execute(method, this);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void step4_zoomVirtualCameraRAPID()
	{
		try
		{
			log.info("Trying to execute step4_zoomVirtualCamera");
			this.tracker.step4_zoomVirtualCamera();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void step5_preprocessInput()
	{
		Class<?>[] parameterTypes = {};
		Method method;
		try
		{
			method = this.getClass().getMethod("step5_preprocessInputRAPID", parameterTypes);
			dfe.execute(method, this);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void step5_preprocessInputRAPID()
	{
		try
		{
			log.info("Trying to execute step5_preprocessInput");
			this.tracker.step5_preprocessInput();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void step6_uploadObservations()
	{
		Class<?>[] parameterTypes = {};
		Method method;
		try
		{
			method = this.getClass().getMethod("step6_uploadObservationsRAPID", parameterTypes);
			dfe.execute(method, this);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void step6_uploadObservationsRAPID()
	{
		try
		{
			log.info("Trying to execute step6_uploadObservations");
			this.tracker.step6_uploadObservations();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public double[] step7_track(double[] x)
	{
		Class<?>[] parameterTypes = { double[].class };
		Method method;
		Object[] paramValues = { x };
		try
		{
			method = this.getClass().getMethod("step7_trackRAPID", parameterTypes);
			return (double[]) dfe.execute(method, paramValues, this);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public double[] step7_trackRAPID(double[] x)
	{
		try
		{
			log.info("Trying to execute step7_track");
			return this.tracker.step7_track(x);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public int step8_visualize(double[] x)
	{
		Class<?>[] parameterTypes = { double[].class };
		Method method;
		Object[] paramValues = { x };
		try
		{
			method = this.getClass().getMethod("step8_visualizeRAPID", parameterTypes);
			return (int) dfe.execute(method, paramValues, this);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public int step8_visualizeRAPID(double[] x)
	{
		try
		{
			log.info("Trying to execute step8_visualize");
			return this.tracker.step8_visualize(x);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	public double[] getDefaultInitPos()
	{
		Class<?>[] parameterTypes = {};
		Method method;
		try
		{
			method = this.getClass().getMethod("getDefaultInitPosRAPID", parameterTypes);
			return (double[]) dfe.execute(method, this);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public double[] getDefaultInitPosRAPID()
	{
		log.info("Trying to execute getDefaultInitPos");
		return this.tracker.getDefaultInitPos();
	}

	public void initLog(String[] args)
	{
		Class<?>[] parameterTypes = { String[].class };
		Method method;
		Object[] paramValues = { args };
		try
		{
			method = this.getClass().getMethod("initLogRAPID", parameterTypes);
			dfe.execute(method, paramValues, this);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initLogRAPID(String[] args)
	{
		log.info("Trying to execute initLog");
		tracker.initLog(args);
	}

	@Override
	public void copyState(Remoteable state)
	{
		// TODO Auto-generated method stub

	}

}
