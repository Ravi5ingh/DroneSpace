package droneSpace.physics;

import repast.simphony.space.continuous.NdPoint;
import droneSpace.interefaces.IContextElement;
import droneSpace.interefaces.ITangibleElement;
import droneSpace.model.Util;
import droneSpace.model.Zone;

/*
 * Class that defines any sensing equipment on-board the drone
 */
public class Sensor
{
	/*
	 * The owner of the sensor
	 */
	protected ITangibleElement owner;
	
	/*
	 * .ctor
	 */
	Sensor(ITangibleElement owner)
	{
		this.owner = owner;
	}
	
	/*
	 * Get whether or not the owner is within the spherical space of this zone 
	 */
	public boolean isWithin(Zone zone)
	{
		return isWithinRadius(zone, zone.RADIUS);
	}
	
	/*
	 * Get whether or not the owner is within radius units of the given zone center
	 */
	public boolean isWithinRadius(Zone zone, double radius)
	{
		return getDistanceTo(zone.CENTER) <= radius;
	}
	
	/*
	 * Get the straight line distance to the given point
	 */
	public double getDistanceTo(NdPoint point)
	{
		return getDistanceBetween(owner.getLocation(), point);
	}
	
	/*
	 * Get the straight-line distance between the given points
	 */
	public double getDistanceBetween(NdPoint point1, NdPoint point2)
	{
		return Util.getEuclideanDistanceBetween(point1, point2);
	}
}