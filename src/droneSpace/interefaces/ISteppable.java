package droneSpace.interefaces;

/*
 * This interface is exposed by any simulation entities exposing a state advancement method
 */
public interface ISteppable
{
	/*
	 * Advance the state of the object by 1 step
	 */
	public void step();
}
