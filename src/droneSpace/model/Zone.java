package droneSpace.model;

import repast.simphony.space.continuous.NdPoint;

/*
 * This class represents a spherical zone in the continuous space
 */
public class Zone
{
	/*
	 * The center of the zone
	 */
	public NdPoint CENTER;
	
	/*
	 * The radius of the zone
	 */
	public double RADIUS;
	
	/*
	 * .ctor
	 */
	public Zone(NdPoint center, double radius)
	{
		this.CENTER = center;
		this.RADIUS = radius;
	}
}

