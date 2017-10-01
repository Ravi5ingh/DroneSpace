package droneSpace.physics;

import javax.vecmath.Vector3d;

import droneSpace.interefaces.ISteppable;

/*
 * The engine capable of thrust in 3 dimension
 */
public class Thruster
{	
	/*
	 * The underlying thrust vector
	 */
	private Vector3d thrustVector;
	
	/*
	 * Create thruster
	 */
	Thruster()
	{
		this.thrustVector = new Vector3d();
	}
	
	public void setThrusts(double xWiseThrust, double yWiseThrust, double zWiseThrust)
	{
		thrustVector = new Vector3d(xWiseThrust, yWiseThrust, zWiseThrust);
	}
	
	/*
	 * Disengage the thruster (ie. set thrusts to 0)
	 */
	public void disEngage()
	{
		setThrusts(0, 0, 0);
	}
	
	/*
	 * Get the thrust vector being currently applied by the thruster
	 */
	public Vector3d getThrustVector()
	{
		return thrustVector;
	}
	
	/*
	 * Is the thruster currently engaged or not
	 */
	public boolean isEngaged()
	{
		return thrustVector.x > 0 || thrustVector.y > 0 || thrustVector.z > 0;
	}
}
