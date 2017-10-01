package droneSpace.interefaces;

/*
 * This interface is exposed by all elements in the simulation that are physically mobile
 */
public interface IMobileElement extends ITangibleElement
{
	/*
	 * Get the mass of the element
	 */
	public double getMass();
	
	/*
	 * Get the drag coefficient of the element
	 */
	public double getDragCoefficient();
}
