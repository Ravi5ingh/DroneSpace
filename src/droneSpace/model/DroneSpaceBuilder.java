package droneSpace.model;

import java.util.ArrayList;

import droneSpace.agents.Drone;
import droneSpace.interefaces.IContextElement;
import droneSpace.physics.SimulationEngine;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;

/*
 * The context object for this simulation passes through the build method of this class
 * so it can be initialized by this class
 */
public class DroneSpaceBuilder implements ContextBuilder<IContextElement>
{

	@Override
	public Context<IContextElement> build(Context<IContextElement> context)
	{

		context.setId(Parameters.Model_ContextId);

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);

		ContinuousSpace<IContextElement> space = spaceFactory.createContinuousSpace(
				Parameters.Model_SpaceName, context, new RandomCartesianAdder<IContextElement>(),
				new repast.simphony.space.continuous.WrapAroundBorders(),
				Parameters.Model_X_WiseSpaceSize,
				Parameters.Model_Y_WiseSpaceSize,
				Parameters.Model_Z_WiseSpaceSize);
		
		SimulationEngine droneSpaceEngine = 
				SimulationEngine.initializeEngine(space);
		
		context.add(droneSpaceEngine);
		
		//Initialize drones
		ArrayList<Drone> drones = new ArrayList<Drone>();
		for (int i = 0; i < Parameters.Drone_Count; i++)
		{
			Drone drone = i == 0 ? new Drone(droneSpaceEngine, true) : new Drone(droneSpaceEngine, false);
			context.add(drone);
			drones.add(drone);
		}
		
		//Initialize landing sites
		ArrayList<LandingSite> sites = new ArrayList<LandingSite>();
		for(int i = 0; i < Parameters.LandingSite_Count; i++)
		{
//			//Get a sufficiently remote point
//			NdPoint randomPoint;
//			do
//			{
//				randomPoint = Util.getRandomPointWithinSpace(500);
//				randomPoint = new NdPoint(randomPoint.getX(), 10, randomPoint.getZ());
//			}while(!isValidSiteLocation(randomPoint, space));
			
			//Initialize landing sites according to a control configuration
			NdPoint siteLocation = controlLandingSiteConfigurations[i];
			
			//Initialize and add the landing site at that point
			LandingSite site = new LandingSite(siteLocation);
			droneSpaceEngine.logLine("Landing Site [" + site.getID() + "] at " + siteLocation);
			context.add(site);
			space.moveTo(site, siteLocation.getX(), siteLocation.getY(), siteLocation.getZ());
			sites.add(site);
		}
		
		//moveAllDronesToLandingSites(space, drones, sites);
		
		//Post initialize of all context elements
		for(IContextElement element : space.getObjects())
		{
			element.initializeInSpace();
		}

		return context;

	}
	
	private static boolean isValidSiteLocation(NdPoint location, ContinuousSpace<IContextElement> space)
	{
		boolean retVal = true;
		for(IContextElement element : space.getObjects())
		{
			if(element instanceof LandingSite)
			{
				retVal = Util.getEuclideanDistanceBetween
						(location, ((LandingSite)element).getLocation()) >= 
						Parameters.Model_LandingSiteMinimumSeparation;
				if(!retVal){break;}
			}
		}
		return retVal;
	}
	
	/*
	 * Move all the drones to the floor
	 */
	private void moveAllDronesToFloor(ContinuousSpace<IContextElement> space, Iterable<Drone> drones)
	{
		for(Drone drone : drones)
		{
			NdPoint location = drone.getLocation();
			space.moveTo(drone, location.getX(), 10, location.getZ());
		}
	}
	
	/*
	 * Distribute all the drones randomly between the landing sites
	 */
	private void moveAllDronesToLandingSites(ContinuousSpace<IContextElement> space, ArrayList<Drone> drones, ArrayList<LandingSite> sites)
	{
		for(Drone drone : drones)
		{
			int siteIndex = Util.generateRandomIntBetween(0, sites.size() - 1);
			NdPoint siteLocation = sites.get(siteIndex).getLocation();
			space.moveTo(drone, siteLocation.getX(), siteLocation.getY(), siteLocation.getZ());
		}
	}
	
	private NdPoint[] controlLandingSiteConfigurations = new NdPoint[]
			{
				new NdPoint(4038.45065056345, 10.0, 665.469603334575),
				new NdPoint(4106.00488407762, 10.0, 3496.724204814174),
				new NdPoint(2377.4839937196693, 10.0, 1328.67017354034),
				new NdPoint(2108.2243531808404, 10.0, 3227.044304548939),
				new NdPoint(1243.1667686603487, 10.0, 1186.429384178164),
				new NdPoint(985.7349839944268, 10.0, 3128.4374947071187),
				new NdPoint(4388.087834015965, 10.0, 1874.639342964068),
				new NdPoint(2387.3500122061414, 10.0, 4498.126606238971),
				new NdPoint(3080.0759413621463, 10.0, 2920.350654432277),
				new NdPoint(1433.5365557795349, 10.0, 2192.006775162861)
			};

}
