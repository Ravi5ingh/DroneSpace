package droneSpace.agents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Function;

import javax.vecmath.Vector3d;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.NdPoint;
import droneSpace.interefaces.IContextElement;
import droneSpace.interefaces.IMobileElement;
import droneSpace.interefaces.ISteppable;
import droneSpace.model.LandingSite;
import droneSpace.model.Logger;
import droneSpace.model.Parameters;
import droneSpace.model.Util;
import droneSpace.model.Zone;
import droneSpace.physics.AvionicsSuite;
import droneSpace.physics.RADAR;
import droneSpace.physics.SimulationEngine;
import droneSpace.physics.Thruster;
import droneSpace.physics.GPS;

/*
 * This is the navigation computer responsible for all navigation to way points provided by the owner
 */
public class Navigator implements ISteppable
{	
	/*
	 * The avionics suite
	 */
	private AvionicsSuite avionics;
	
	/*
	 * The target to follow
	 */
	private IMobileElement targetToFollow;
	
	/*
	 * The currently set destination zone
	 */
	private Zone currentDestinationZone;
	
	/*
	 * The altitude to achieve
	 */
	private double setAltitude;
	
	/*
	 * The functionality that must be invoked once the current way point is reached and navigation terminates.
	 */
	private Function<Void, Void> terminationAlert;
	
	/*
	 * The navigator must navigate to within this distance of the altitude in order for the altitude achievement termination alert to be invoked
	 */
	private double altitudeAchieveTermRadius;
	
	/*
	 * Switch for collision avoidance mode on/off
	 */
	private boolean collisionAvoidanceSwitch;
	
	/*
	 * The current mode of the navigator
	 */
	private NavigatorMode mode;
	
	//debug
	public UUID uuid;
	
	/*
	 * Instantiate a navigator
	 */
	private Navigator(AvionicsSuite avionicsSuite)
	{
		this.avionics = avionicsSuite;
		this.collisionAvoidanceSwitch = false;
		this.uuid = java.util.UUID.randomUUID();
		this.mode = NavigatorMode.Disengaged;
	}
	
	/*
	 * Initialize a navigator given the avionics suite
	 */
	public static Navigator InitializeNavigatorWith(AvionicsSuite avionicsSuite)
	{
		return new Navigator(avionicsSuite);
	}
	
	/*
	 * Engage the navigator towards the given way point
	 */
	public void startNavigationTowards(Zone destinationZone, Function<Void, Void> navigationTerminationAlert)
	{
		this.currentDestinationZone = destinationZone;
		this.terminationAlert = navigationTerminationAlert;
		this.mode = NavigatorMode.WayPointNavigation;
	}
	
	/*
	 * Engage target following mode for given target
	 */
	public void startFollowing(IMobileElement target)
	{
		this.targetToFollow = target;
		mode = NavigatorMode.TargetFollowing;
	}
	
	/*
	 * Engage the navigator to achieve the given altitude
	 */
	public void achieveAltitude(double altitude, Function<Void, Void> navigationTerminationAlert, double terminationRadius)
	{
		this.setAltitude = altitude;
		this.terminationAlert = navigationTerminationAlert;
		this.altitudeAchieveTermRadius = terminationRadius;
		this.mode = NavigatorMode.AltitudeAchievement;
	}
	
	/*
	 * Disengage the navigator's current navigation task
	 */
	private void disEngageNavigation()
	{
		avionics.THRUSTER.disEngage();
		mode = NavigatorMode.Disengaged;
		terminationAlert.apply(null);
	}
	
	/*
	 * Activate the collision avoidance mode
	 */
	public void activateCollisionAvoidance()
	{
		collisionAvoidanceSwitch = true;
	}
	
	/*
	 * Disactivate the collision avoidance mode
	 */
	public void disActivateCollisionAvoidance()
	{
		collisionAvoidanceSwitch = false;
	}
	
	/*
	 * Is the collision avoidance logic active?
	 */
	public boolean isCollisionAvoidanceActive()
	{
		return collisionAvoidanceSwitch;
	}
	
	/*
	 * Get the navigator mode
	 */
	public NavigatorMode getMode()
	{
		return this.mode;
	}
	
	/*
	 * Get the drone that was last set to be followed
	 */
	public Drone getFollowDrone()
	{
		return (Drone)targetToFollow;
	}
	
	@Override
	public void step()
	{
		switch (mode)
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
				throw new RuntimeException("No step method defined for navigator state : " + mode.toString());
		}
	}
	
	/*
	 * Step method for disengaged mode
	 */
	private void stepDisengaged()
	{
		//do nothing
	}
	
	/*
	 * Step method for way point navigation mode
	 */
	private void stepWayPointNavigation()
	{
		if(avionics.GPS.isWithin(currentDestinationZone))
		{
			disEngageNavigation();
		}
		else
		{
			Vector3d thrustVector = avionics.GPS.getUnitVectorTowards(currentDestinationZone.CENTER);
			thrustVector.x *= Parameters.Drone_ThrusterLimit;
			thrustVector.y *= Parameters.Drone_ThrusterLimit;
			thrustVector.z *= Parameters.Drone_ThrusterLimit;

			setThrust(thrustVector);
		}
	}
	
	/*
	 * Step method for target following mode
	 */
	private void stepTargetFollowing()
	{
		Vector3d thrustVector = avionics.GPS.getUnitVectorTowards(targetToFollow.getLocation());
		thrustVector.x *= Parameters.Drone_ThrusterLimit;
		thrustVector.y *= Parameters.Drone_ThrusterLimit;
		thrustVector.z *= Parameters.Drone_ThrusterLimit;

		setThrust(thrustVector);
	}
	
	/*
	 * Step method for altitude achievement
	 */
	private void stepAltitudeAchievement()
	{
		if(Math.abs(avionics.GPS.getAltitude() - setAltitude) <= altitudeAchieveTermRadius)
		{
			disEngageNavigation();
		}
		else
		{
			Vector3d thrustVector = avionics.GPS.getAltitude() < setAltitude ? 
					new Vector3d(0, Parameters.Drone_ThrusterLimit, 0) : 
					new Vector3d(0, -Parameters.Drone_ThrusterLimit, 0);
			
			setThrust(thrustVector);
		}
	}
	
	/*
	 * Set the given thrust for the thrusters
	 */
	private void setThrust(Vector3d thrustVector)
	{
		thrustVector = collisionAvoidanceSwitch ? 
							deltaThrustForCollisionAvoidance(thrustVector) :
							thrustVector;

		avionics.THRUSTER.setThrusts(thrustVector.x, thrustVector.y, thrustVector.z);
	}
	
	/*
	 * Moderate the given thrust vector and return a new one which accounts for objects
	 * in the vicinity. This is done to achieve collision avoidance behavior
	 */
	private Vector3d deltaThrustForCollisionAvoidance(Vector3d thrustVector)
	{
		for(Drone drone : avionics.RADAR.getAllDronesWithinRange())
		{
			NdPoint droneLocation = drone.getLocation();
			double x = avionics.GPS.getDistanceTo(droneLocation);
			double repulsion = SimulationEngine.gaussian1D(
									Parameters.Physics_PotentialFieldCoefficient, 
									Parameters.Physics_PotentialField_μ, 
									Parameters.Physics_PotentialField_σ, x);
			Vector3d direction = avionics.GPS.getUnitVectorTowards(droneLocation);
			Vector3d repulsionVector = new Vector3d(direction.x * -1 * repulsion,
													direction.y * -1 * repulsion,
													direction.z * -1 * repulsion);
			thrustVector.add(repulsionVector);
		}
		
		return thrustVector;
	}
	
	/*
	 * Enum to store the current mode of the navigator
	 */
	public enum NavigatorMode
	{	
		/*
		 * Navigator is not currently engaged in any navigation
		 */
		Disengaged,
		
		/*
		 * Navigator is currently engaged towards a fixed way point
		 */
		WayPointNavigation,
		
		/*
		 * Navigator is currently in target following mode
		 */
		TargetFollowing,
		
		/*
		 * Navigator is currently engaged in 
		 */
		AltitudeAchievement
	}

}
