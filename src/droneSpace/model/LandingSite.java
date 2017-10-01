package droneSpace.model;

import java.util.UUID;

import droneSpace.interefaces.ITangibleElement;
import repast.simphony.space.continuous.NdPoint;

/*
 * An instance of this class represents a landing site in the simulation
 */
public class LandingSite implements ITangibleElement
{
	private UUID id;
	
	/*
	 * The location
	 */
	private NdPoint location;
	
	/*
	 * .ctor
	 */
	public LandingSite(NdPoint location)
	{
		this.location = location;
		this.id = UUID.randomUUID();
	}
	
	@Override
	public NdPoint getLocation()
	{
		return location;
	}
	
	@Override
	public void initializeInSpace()
	{
		//nothing to do
	}
	
	//data collection
	public UUID getID()
	{
		return this.id;
	}

}
