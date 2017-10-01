package droneSpace.physics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import javax.vecmath.Vector3d;

import droneSpace.agents.Drone;
import droneSpace.dataCollection.DataLogger;
import droneSpace.interefaces.IContextElement;
import droneSpace.interefaces.IMobileElement;
import droneSpace.interefaces.ISteppable;
import droneSpace.interefaces.ITangibleElement;
import droneSpace.model.LandingSite;
import droneSpace.model.Logger;
import droneSpace.model.Parameters;
import droneSpace.model.Util;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.visualization.visualization3D.style.DefaultStyle3D;

/*
 * The simulation engine embodies the environmental intelligence of the model
 */
public class SimulationEngine implements ISteppable, IContextElement
{ 	
	/*
	 * The underlying Repast implementation of the space within which the simulation takes place
	 */
	private ContinuousSpace<IContextElement> space;
	
	/*
	 * The last recorded millisecond. This is used to perform live tweaks to the time resolution parameter
	 */
	private long lastMillisecond;
	
	/*
	 * The last recorded tick. This is used to perform live tweaks to the time resolution parameter
	 */
	private double lastTick = 1;
	
	/*
	 * The data logger
	 */
	private DataLogger dataLogger;
	
	/*
	 * private .ctor
	 */
	private SimulationEngine(ContinuousSpace<IContextElement> space)
	{
		this.space = space;
		this.dataLogger = new DataLogger(Parameters.Model_DataLoggingBatchSize);
	}
	
	/*
	 * Initialize the drone space with the given ContinuousSpace object
	 */
	public static SimulationEngine initializeEngine(ContinuousSpace<IContextElement> space)
	{
		return new SimulationEngine(space);
	}
	
	/*
	 * Manufacture the complete avionics suite for the given owner
	 */
	public AvionicsSuite manufactureAvionicsSuiteFor(ITangibleElement owner)
	{
		return new AvionicsSuite(space, owner);
	}
	
	/*
	 * Move the given context element to the position in which it should be at 1 tick after the 
	 * current tick. This is determined based on the velocity and thrust vectors that currently
	 * pertain to the given element. Drag is also modeled in but the coefficient can simply be 0.
	 * This is the core method defining the physics of this universe.
	 */
	public void advanceElementPosition(IMobileElement mobileElement, Vector3d velocityVector, Vector3d thrustVector)
	{
		NdPoint location = mobileElement.getLocation();
		
		//acceleration due to thrust = thrust/mass
		double mass = mobileElement.getMass();
		double xAcc = thrustVector.x/mass;
		double yAcc = thrustVector.y/mass;
		double zAcc = thrustVector.z/mass;
		
		//acceleration due to drag = dragForce/mass
		Vector3d dragVector = getDragForceVector(velocityVector, mobileElement.getDragCoefficient());
		double xDrag = dragVector.x/mass;
		double yDrag = dragVector.y/mass;
		double zDrag = dragVector.z/mass;
		
		//displacement : s = ut + 1/2(at^2)
		double seconds = 1/Parameters.Model_TimeResolution;
		double newX = location.getX() + velocityVector.x*seconds + ((xAcc+xDrag)*square(seconds))/2;
		double newY = location.getY() + velocityVector.y*seconds + ((yAcc+yDrag)*square(seconds))/2;
		double newZ = location.getZ() + velocityVector.z*seconds + ((zAcc+zDrag)*square(seconds))/2;
		
		//optimization added to ensure drones do not travel through ground
		newY = Math.max(newY, 0);
		space.moveTo(mobileElement, newX, newY, newZ);
	}
	
	/*
	 * Get the drag force vector given the velocity vector and drag coefficient
	 */
	private static Vector3d getDragForceVector(Vector3d velocityVector, double dragCoefficient)
	{
		//get absolute value of drag components
		double xDrag = getDragForceMagFromVelocity(velocityVector.x, dragCoefficient);
		double yDrag = getDragForceMagFromVelocity(velocityVector.y, dragCoefficient);
		double zDrag = getDragForceMagFromVelocity(velocityVector.z, dragCoefficient);
		
		//sign of components is opposite to velocity
		xDrag *= -1*sign(velocityVector.x);
		yDrag *= -1*sign(velocityVector.y); 
		zDrag *= -1*sign(velocityVector.z);
		
		return new Vector3d(xDrag, yDrag, zDrag);
	}
	
	/*
	 * Get the current tick in the simulation
	 */
	public static double getCurrentTick()
	{
		return RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	}
	
	/*
	 * 1-D Gaussian
	 */
	public static double gaussian1D(double coefficient, double μ, double σ, double x)
	{
		return coefficient * e( (-1/2) * square( (x-μ)/σ ) );
	}
	
	/*
	 * Cubic polynomial
	 */
	public static double cubic(double a, double b, double c, double d, double x)
	{
		return a*cube(x) + b*square(x) + c*x + d;
	}
	
	/*
	 * return +1 for +ve value, -1 for -ve value, +1 for 0
	 */
	public static int sign(double x)
	{
		return x == 0 ? 1 : (int)(x/Math.abs(x));
	}
	
	public static double cube(double x)
	{
		return x*x*x;
	}
	
	/*
	 * Squared
	 */
	public static double square(double x)
	{
		return x*x;
	}
	
	/*
	 * e to the x
	 */
	public static double e(double x)
	{
		return Math.pow(Math.E, x);
	}
	
	/*
	 * Get the drag force given the speed and drag coefficient
	 */
	private static double getDragForceMagFromVelocity(double velocity, double dragCoefficient)
	{
		return dragCoefficient * square(velocity);
	}
	

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
	public void step()
	{
		if(lastMillisecond == 0)
		{
			lastMillisecond = System.currentTimeMillis();
		}
		//if 1 second or more has passed since last check, update time resolution parameter if the switch is on
		else if(Parameters.Model_TimeResolutionAutoTune && System.currentTimeMillis() - lastMillisecond >= 1000)
		{
			Parameters.Model_TimeResolution = getCurrentTick() - lastTick;
			lastMillisecond = System.currentTimeMillis();
			lastTick = getCurrentTick();
		}
		
		dataLogger.step();
		
		//data collection
		if(Parameters.DataCollection_CollectMinDroneDistance && getCurrentTick()%100==0)
		{
			double minDistance = Double.MAX_VALUE;
			for(IContextElement element : space.getObjects())
			{
				if(element instanceof Drone)
				{
					double dist = ((Drone)element).getClosestDroneDistance();
					if(dist != -1 && minDistance >= dist)
					{
						minDistance = dist;
					}
				}
			}
			if(minDistance != Double.MAX_VALUE)
			{
				dataLogger.Log(getCurrentTick() + " -- \t" + minDistance);
			}
		}
		
		if(Parameters.DataCollection_CollectHoldingPatternSize && getCurrentTick()%100==0)
		{
			HashMap<UUID, Integer> trafficMap = new HashMap<UUID, Integer>();
			for(IContextElement element : space.getObjects())
			{
				if(element instanceof Drone)
				{
					LandingSite site = ((Drone)element).getCurrentSite();
					if(site != null)
					{
						if(trafficMap.containsKey(site.getID()))
						{
							trafficMap.put(site.getID(), trafficMap.get(site.getID()) + 1);
						}
						else
						{
							trafficMap.put(site.getID(), 1);
						}	
					}
				}
			}
			for(UUID id : trafficMap.keySet())
			{
				Logger.appendLogFileLine(id.toString() + "_HoldingPattern", getCurrentTick() + "\t" + trafficMap.get(id));
			}
		}
	}
	
	/*
	 * Log a line to the log
	 */
	public void logLine(String line)
	{
		dataLogger.Log(line);
	}

	@Override
	public void initializeInSpace()
	{
		//nothing to do
	}
}
