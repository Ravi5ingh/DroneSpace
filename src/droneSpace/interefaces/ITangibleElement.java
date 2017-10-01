package droneSpace.interefaces;

import repast.simphony.space.continuous.NdPoint;

/*
 * This interface is exposed by any elements in the simulation that have a physical existence at some point in space
 */
public interface ITangibleElement extends IContextElement
{	
	/*
	 * Get the location of the element
	 */
	public NdPoint getLocation();
}
