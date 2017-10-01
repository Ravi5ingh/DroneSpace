package droneSpace.physics;

import repast.simphony.space.continuous.ContinuousSpace;
import droneSpace.interefaces.IContextElement;
import droneSpace.interefaces.ISteppable;
import droneSpace.interefaces.ITangibleElement;

/*
 * Class that encompasses the complete avionics suite that is available to aerial vehicles in the model
 */
public class AvionicsSuite implements ISteppable
{
	/*
	 * The GPS
	 */
	public GPS GPS;
	
	/*
	 * The RADAR
	 */
	public RADAR RADAR;
	
	/*
	 * The Thruster
	 */
	public Thruster THRUSTER;
	
	/*
	 * .ctor
	 */
	AvionicsSuite(ContinuousSpace<IContextElement> space, ITangibleElement owner)
	{
		this.GPS = new GPS(space, owner);
		this.RADAR = new RADAR(space, owner);
		this.THRUSTER = new Thruster();
	}

	@Override
	public void step()
	{
		GPS.step();
	}
}
