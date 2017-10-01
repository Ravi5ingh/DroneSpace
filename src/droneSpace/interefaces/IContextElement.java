package droneSpace.interefaces;

/*
 * This interface must be exposed by all objects that exist independently within the simulation
 */
public interface IContextElement
{	
	/*
	 * This method is a facility that allows context elements to run tasks that can only be run after
	 * construction of the elements. This method is automatically called by the context builder
	 */
	public void initializeInSpace();
}
