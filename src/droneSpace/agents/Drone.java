package droneSpace.agents;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.NdPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.function.Function;

import javax.vecmath.Vector3d;

import droneSpace.agents.Navigator.NavigatorMode;
import droneSpace.interefaces.IContextElement;
import droneSpace.interefaces.IMobileElement;
import droneSpace.interefaces.ISteppable;
import droneSpace.interefaces.ITangibleElement;
import droneSpace.model.LandingSite;
import droneSpace.model.Logger;
import droneSpace.model.Parameters;
import droneSpace.model.Util;
import droneSpace.model.Zone;
import droneSpace.physics.AvionicsSuite;
import droneSpace.physics.SimulationEngine;

/*
 * An instance of this class is an agent of this simulation. It is a drone that flies around
 */
public class Drone implements IMobileElement, ISteppable
{	
	
	/*
	 * The phase of the flight that the drone is currently in
	 */
	FlightPhase flightPhase;
	
	/*
	 * This is the simulation engine
	 */
	private SimulationEngine engine;
	
	/*
	 * The navigation computer on-board the drone
	 */
	private Navigator navigator;
	
	/*
	 * The avionics suite
	 */
	private AvionicsSuite avionics;
	
	/*
	 * Boolean to store whether or not the drone is broadcasting its presence
	 */
	public boolean isBroadcasting = true;
	
	/*
	 * The current destination zone of the drone
	 */
	private Zone currentDestinationZone;
	
	/*
	 * The currently set landing site
	 */
	private LandingSite currentSite;
	
	/*
	 * The set of drones within detection range
	 */
	private ArrayList<Drone> dronesInRange;
	
	/*
	 * Following drone
	 */
	Drone follower;

	
//	/*
//	 * This is the alert that is invoked by the navigator when its navigation task terminates
//	 */
//	private Function<Void, Void> navigationTerminationAlert = new Function<Void, Void>()
//			{
//				public Void apply(Void na)
//	    		{
//					removeFollower();
//					currentWayPoint = generateRandomWayPoint();
//	    			startNavigationTowardsWayPoint();
//	    			return null;
//	    		}
//			};	
	
	private Function<Void, Void> takeOffTerminAlert = new Function<Void, Void>()
			{
	    		public Void apply(Void na)
	    		{
	    			flightPhase = FlightPhase.Cruise;
	    			NdPoint siteLocation = currentSite.getLocation();
	    			currentDestinationZone = new Zone(
	    										new NdPoint(siteLocation.getX(), Parameters.Model_LSALT, siteLocation.getZ()),
	    										Parameters.Drone_CruiseTerminationRadius);
	    			navigator.startNavigationTowards(currentDestinationZone, cruiseTerminAlert);
	    			return null;
	    		}
			};
			
	
	private Function<Void, Void> cruiseTerminAlert = new Function<Void, Void>()
			{
	    		public Void apply(Void na)
	    		{
	    			removeAnyFollower();
	    			currentDestinationZone = new Zone(currentSite.getLocation(), Parameters.Drone_SiteNavTerminationRadius);
	    			flightPhase = FlightPhase.Arrival;
	    			navigator.startNavigationTowards(currentDestinationZone, siteNavAlert);
	    			return null;
	    		}
			};
			
	
	private Function<Void, Void> siteNavAlert = new Function<Void, Void>()
			{
	    		public Void apply(Void na)
	    		{
	    			if(Parameters.DataCollection_CollectSiteTraffic)
	    			{
	    				Logger.appendLogFileLine(currentSite.getID().toString() + "_DroneArrivals", engine.getCurrentTick() + "\t1");
	    			}
	    			removeAnyFollower();
	    			Zone takeOffZone = new Zone(getRandomPointNearSite(
	    											currentSite, Parameters.LandingSite_TakeOffRadius), 
	    											Parameters.Drone_TakeOffNavTerminationRadius);
	    			flightPhase = FlightPhase.Departure;
	    			navigator.startNavigationTowards(takeOffZone, takeOffZoneNavAlert);
	    			return null;
	    		}
			};
	
	private Function<Void, Void> takeOffZoneNavAlert = new Function<Void, Void>()
			{
				public Void apply(Void na)
				{
					currentSite = getRandomSite();
					startNavigationTowardsSite();
					return null;
				}
			};

	/*
	 * .ctor
	 */
	public Drone(SimulationEngine engine, boolean isTest)
	{
		this.engine = engine;
		this.dronesInRange = new ArrayList<Drone>();
	}

	/*
	 * This method is first invoked at 'start' time units and then repetitively with an interval
	 * of 'interval' time units
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void step()
	{	
		//update situational awareness
		if(engine.getCurrentTick()%30==0)
		{
			dronesInRange = avionics.RADAR.getAllDronesWithinRange();
		}
		
		if(Parameters.Drone_MinimaAvoidBehavior)
		{
			//step the drone depending on the navigation mode
			NavigatorMode navMode = navigator.getMode();
			switch (navMode)
			{
				case Disengaged:
					stepDisengaged();
					break;
				case WayPointNavigation:
					stepWayPointNavigation();
					break;
				case TargetFollowing:
					stepTargetFollowing();
					break;
				case AltitudeAchievement:
					stepAltitudeAchievement();
					break;
				default:
					throw new RuntimeException("No step method defined for drone state : " + navMode.toString());
			}	
		}
		
		//advance position using the simulation engine
		engine.advanceElementPosition(this, 
				avionics.GPS.getVelocityVector(), 
				avionics.THRUSTER.getThrustVector());
		
		//step the navigator
		navigator.step();
		
		//step the avionics
		avionics.step();
		
		//setting this field to true does the work of broadcasting a beacon which will trigger
		//the watch methods of other agents in the vicinity
		isBroadcasting = true;
	}
	
	/*
	 * Step logic for disengaged mode
	 */
	private void stepDisengaged()
	{
		//does nothing
	}
	
	/*
	 * Step logic for way point navigation
	 */
	private void stepWayPointNavigation()
	{
		
		if(engine.getCurrentTick()%20==0 && flightPhase == FlightPhase.Cruise)
		{	
			if(avionics.GPS.isWithinRadius(currentDestinationZone, Parameters.Drone_MinimaAvoidanceRadius))
			{
				boolean breakLoop = false;
				for(Drone drone : dronesInRange)
				{
					if(drone.getCurrentDestinationZone() != null)
					{
						double wayPointDiff = avionics.GPS.getDistanceBetween(
																currentDestinationZone.CENTER, 
																drone.getCurrentDestinationZone().CENTER);
						if(wayPointDiff <= 30 && !isEffectiveFollower(drone))
						{
							Drone droneToFollow = startFollowing(drone);
							navigator.startFollowing(droneToFollow);
							breakLoop = true;
						}
					}
					if(breakLoop){break;}
				}
			}
		}
		
		
	}
	
	/*
	 * Step logic for in line mode
	 */
	private void stepTargetFollowing()
	{
		if(engine.getCurrentTick()%20==0)
		{
			if(avionics.GPS.isWithin(currentDestinationZone))
			{
				stopFollowing(navigator.getFollowDrone());
			}
		}
	}
	
	/*
	 * Step logic for altitude achievement mode
	 */
	private void stepAltitudeAchievement()
	{
		//do nothing
	}
	
	/*
	 * Register the given drone as a follower
	 */
	public Drone registerFollower(Drone droneToRegister)
	{
		Drone retVal;
		if(follower == null)
		{
			follower = droneToRegister;
			retVal = this;
			if(navigator.getMode() != NavigatorMode.TargetFollowing)
			{
				navigator.disActivateCollisionAvoidance();
			}
			else
			{
				navigator.activateCollisionAvoidance();
			}
		}
		else
		{
			retVal = follower.registerFollower(droneToRegister);
		}
		return retVal;
	}
	
	/*
	 * Un-register the given drone as a follower
	 */
	public void unregisterFollower(Drone droneToUnregister)
	{
		if(follower == droneToUnregister)
		{
			follower = null;
			navigator.activateCollisionAvoidance();
		}
		else
		{
			throw new RuntimeException("ERROR! Requesting drone is not authorized to un-register itself with me.");
		}
	}
	
	/*
	 * Start following the given drone
	 */
	public Drone startFollowing(Drone droneToFollow)
	{
		navigator.activateCollisionAvoidance();
		return droneToFollow.registerFollower(this);
	}
	
	/*
	 * Stop following the given drone
	 */
	public void stopFollowing(Drone droneToStopFollowing)
	{	
		if(navigator.getFollowDrone() == droneToStopFollowing)
		{
			//inform leading drone
			droneToStopFollowing.unregisterFollower(this);
			//check if currently leader
			if(follower != null)
			{
				navigator.disActivateCollisionAvoidance();
			}
			else
			{
				navigator.activateCollisionAvoidance();
			}
			continueFlightSequence();
		}
		else
		{
			throw new RuntimeException("ERROR! Cannot un-follow this drone as it is not currently being followed.");
		}
	}
	
	/*
	 * If, there is a follower, request it to stop following this drone
	 */
	public void removeAnyFollower()
	{
		if(follower != null)
		{
			follower.stopFollowing(this);
		}
	}
	
	/*
	 * Get the currently set way point
	 */
	public Zone getCurrentDestinationZone()
	{
		return currentDestinationZone;
	}
	
	/*
	 * Get the Drone ID (for testing purposes)
	 */
	public UUID getUUID()
	{
		return navigator.uuid;
	}
	
	/*
	 * Check if the given drone is one of the followers in the chain of drones following this one
	 */
	public boolean isEffectiveFollower(Drone droneToCheck)
	{
		boolean retVal = false;
		if(follower != null)
		{	
			retVal = follower == droneToCheck ? 
						true :
						follower.isEffectiveFollower(droneToCheck);
		}
		return retVal;
	}

	@Override
	public NdPoint getLocation()
	{
		return avionics.GPS.getLocation();
	}
	
	@Override
	public double getMass()
	{
		return Parameters.Drone_Mass;
	}
	
	@Override
	public double getDragCoefficient()
	{
		return Parameters.Drone_DragCoefficient;
	}
	
	/*
	 * Get the velocity vector
	 */
	public Vector3d getVelocity()
	{
		return avionics.GPS.getVelocityVector();
	}
	
	/*
	 * Continue flight sequence after any possible deviations caused by line following behavior
	 */
	private void continueFlightSequence()
	{
		switch (flightPhase)
		{
			case Departure:
				continueDeparture();
				break;
			case Cruise:
				continueToCruiseEnd();
				break;
			case Arrival:
				continueToSiteNavEnd();
				break;
			default:
				throw new RuntimeException("Cannot continue flight sequence, no sequence specified for mode : " + flightPhase.toString());
		}
	}
	
	/*
	 * Continue departure sequence
	 */
	private void continueDeparture()
	{
		flightPhase = FlightPhase.Departure;
		currentDestinationZone = null;
		navigator.achieveAltitude(Parameters.Model_LSALT, takeOffTerminAlert, Parameters.Drone_AltitudeTerminationRadius);
	}
	
	/*
	 * Continue navigating towards LSALT above landing site
	 */
	private void continueToCruiseEnd()
	{
		flightPhase = FlightPhase.Cruise;
		navigator.startNavigationTowards(currentDestinationZone, cruiseTerminAlert);
	}
	
	/*
	 * Continue navigating towards landing site
	 */
	private void continueToSiteNavEnd()
	{	
		flightPhase = FlightPhase.Arrival;
		navigator.startNavigationTowards(currentDestinationZone, siteNavAlert);
	}
	
	@Override
	public void initializeInSpace()
	{
		this.avionics = engine.manufactureAvionicsSuiteFor(this);
		this.navigator = Navigator.InitializeNavigatorWith(avionics);
		navigator.activateCollisionAvoidance();
		
		flightPhase = FlightPhase.Departure;
		
		avionics.RADAR.getAllLandingSites(true);
		currentSite = getRandomSite();
		startNavigationTowardsSite();
		
//		navigator.activateCollisionAvoidance();
//		currentWayPoint = generateRandomWayPoint();
//		startNavigationTowardsWayPoint();
	}
	
//	/*
//	 * Engage the navigator towards the current set way point
//	 */
//	private void startNavigationTowardsWayPoint()
//	{	
//		double radius = currentWayPoint.getY() >= Parameters.Model_LSALT ? 
//				Parameters.Drone_CruiseTerminationRadius :
//				Parameters.Drone_SiteNavTerminationRadius;
//		navigator.startNavigationTowards(currentWayPoint, navigationTerminationAlert, radius);
//	}
	
	private void startNavigationTowardsSite()
	{
		currentDestinationZone = null;
		navigator.achieveAltitude(Parameters.Model_LSALT, takeOffTerminAlert, Parameters.Drone_AltitudeTerminationRadius);
	}
	
//	private NdPoint generateRandomWayPoint()
//	{
////		return new NdPoint(	1250, 500, 500);//Util.generateRandomDouble(0, Parameters.X_WiseSpaceSize),
////							//Util.generateRandomDouble(0, Parameters.Y_WiseSpaceSize),
////							//Util.generateRandomDouble(0, Parameters.Z_WiseSpaceSize));
//		
//		//return Util.getRandomPointWithinSpace(10);
//		if(currentWayPoint == null)
//		{
//			return new NdPoint(100, 100, 100);
//		}
//		else
//		{
//			return Util.getRandomPointWithinSpace(10);
//		}
//	}
	
	/*
	 * Get a point N, E, S, or W at given radius from landing site. Direction is random
	 */
	private NdPoint getRandomPointNearSite(LandingSite site, double radius)
	{
		NdPoint siteLocation = site.getLocation();
		int directionIndex = Util.generateRandomIntBetween(0, 3);
		switch(directionIndex)
		{
			//North
			case 0:
				return new NdPoint(siteLocation.getX(), siteLocation.getY(), siteLocation.getZ() + radius);
			//East
			case 2:
				return new NdPoint(siteLocation.getX(), siteLocation.getY(), siteLocation.getZ() - radius);
			//West
			case 1:
				return new NdPoint(siteLocation.getX() + radius, siteLocation.getY(), siteLocation.getZ());
			//South
			case 3:
				return new NdPoint(siteLocation.getX() - radius, siteLocation.getY(), siteLocation.getZ());
			default:
				throw new RuntimeException("directionIndex of : " + directionIndex + " was returned. Should be b/w 0 & 3");
		}
	}
	
	private LandingSite getRandomSite()
	{
		ArrayList<LandingSite> sites = avionics.RADAR.getAllLandingSites(false);
		int siteIndex = Util.generateRandomIntBetween(0, sites.size() - 1);
		
		return sites.get(siteIndex);
	}
	
	public String reportStatus()
	{
		return "";
	}
	
	//data collection
	public double getClosestDroneDistance()
	{
		dronesInRange = avionics.RADAR.getAllDronesWithin(500);
		if(dronesInRange.size() != 0)
		{
			Collections.sort(dronesInRange, new Comparator<Drone>()
					{
						@Override
						public int compare(Drone drone1, Drone drone2)
						{
							return SimulationEngine.sign(	avionics.GPS.getDistanceTo(drone1.getLocation()) - 
															avionics.GPS.getDistanceTo(drone2.getLocation()));
						}
					});
			return avionics.GPS.getDistanceTo(dronesInRange.get(0).getLocation());
		}
		return -1;
	}
	
	//data collection
	public LandingSite getCurrentSite()
	{
		return this.currentSite;
	}
	
	/*
	 * Enum to store flight phases
	 */
	private enum FlightPhase
	{
		/*
		 * Drone is currently achieving LSALT in order to enter cruise mode
		 */
		Departure,
		
		/*
		 * Drone is currently cruising
		 */
		Cruise,
		
		/*
		 * Drone is currently in the arrival phase
		 */
		Arrival,
	}
	
}
