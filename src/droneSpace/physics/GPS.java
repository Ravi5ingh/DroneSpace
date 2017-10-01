package droneSpace.physics;

import javax.vecmath.Vector3d;

import droneSpace.interefaces.IContextElement;
import droneSpace.interefaces.ISteppable;
import droneSpace.interefaces.ITangibleElement;
import droneSpace.model.Logger;
import droneSpace.model.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;

/*
 * This component is provides its' owner with positional metrics
 */
public class GPS extends Sensor implements ISteppable
{
	private ContinuousSpace<IContextElement> space;
	
	/*
	 * The position in the previous time step
	 */
	private NdPoint previousLocation;
	
	/*
	 * The position in the current time step
	 */
	private NdPoint currentLocation;
	
	/*
	 * overriden .ctor
	 */
	private GPS(ITangibleElement owner)
	{
		super(owner);
	}
	
	/*
	 * .ctor
	 */
	GPS(ContinuousSpace<IContextElement> space, ITangibleElement owner)
	{
		this(owner);
		this.space = space;
		this.previousLocation = getLocation();
		this.currentLocation = getLocation();
	}
	
	/*
	 * Get the current location
	 */
	public NdPoint getLocation()
	{
		return space.getLocation(owner);
	}
	
	/*
	 * Get the current altitude
	 */
	public double getAltitude()
	{
		return getLocation().getY();
	}

	@Override
	public void step()
	{
		previousLocation = currentLocation;
		currentLocation = getLocation();
	}
	
	/*
	 * Get the current velocity vector (units : distance units/time units)
	 */
	public Vector3d getVelocityVector()
	{
		double[] displacement = space.getDisplacement(previousLocation, currentLocation);
		double timeRes = Parameters.Model_TimeResolution;
		return new Vector3d(displacement[0]*timeRes, 
							displacement[1]*timeRes,
							displacement[2]*timeRes);
	}
	
	/*
	 * Generate a unit vector towards the given point
	 */
	public Vector3d getUnitVectorTowards(NdPoint point)
	{	
		NdPoint from = currentLocation;
		double distance = getDistanceTo(point);
		return distance == 0 ? 	new Vector3d(0,0,0) : 
			new Vector3d(	(point.getX() - from.getX())/distance, 
							(point.getY() - from.getY())/distance, 
							(point.getZ() - from.getZ())/distance);
	}
}
