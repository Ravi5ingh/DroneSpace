package droneSpace.physics;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import droneSpace.agents.Drone;
import droneSpace.interefaces.IContextElement;
import droneSpace.interefaces.ITangibleElement;
import droneSpace.model.LandingSite;
import droneSpace.model.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;

/*
 * Component for detection and ranging
 */
public class RADAR extends Sensor
{
	/*
	 * The space
	 */
	private ContinuousSpace<IContextElement> space;
	
	/*
	 * The current list of landing sites
	 */
	private ArrayList<LandingSite> landingSites;
	
	/*
	 * overridden .ctor
	 */
	private RADAR(ITangibleElement owner)
	{
		super(owner);
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * .ctor
	 */
	RADAR(ContinuousSpace<IContextElement> space, ITangibleElement owner)
	{
		this(owner);
		this.space = space;
		this.landingSites = new ArrayList<LandingSite>();
		getAllLandingSites(true);
	}
	
	/*
	 * Get all objects in the simulation within range
	 */
	public ArrayList<Drone> getAllDronesWithinRange()
	{
		return getAllDronesWithin(Parameters.Drone_DetectionRange);
	}
	
	//data collection - get rid of this!
	public ArrayList<Drone> getAllDronesWithin(double radius)
	{
		ArrayList<Drone> retVal = new ArrayList<Drone>();
		for(IContextElement element : space.getObjects())
		{
			if(element instanceof Drone && element != owner)
			{
				Drone drone = (Drone)element;
				if(getDistanceTo(drone.getLocation()) <= radius)
				{
					retVal.add(drone);
				}
			}
		}
		
		return retVal;
	}
	
	/*
	 * Get a list of all landing sites (If rescan flag is set to true, the RADAR component will re-scan
	 * the space for the list of landing sites)
	 */
	public ArrayList<LandingSite> getAllLandingSites(boolean rescan)
	{
		if(rescan)
		{
			landingSites.clear();
			for(IContextElement element : space.getObjects())
			{
				if(element instanceof LandingSite)
				{
					landingSites.add((LandingSite)element);
				}
			}
		}
		
		return landingSites;
	}
}
