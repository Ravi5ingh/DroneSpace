package droneSpace.model;

/*
 * The static model parameters
 */
public /*static*/ final class Parameters
{
	/*
	 * Hide the .ctor
	 */
	private Parameters() {}
	
	/*
	 * The string ID of the context
	 */
	public static final String Model_ContextId = "DroneSpace";
	
	/*
	 * The name of the virtual space within which the model runs
	 */
	public static final String Model_SpaceName = "droneSpace";
	
	/*
	 * The minimum altitude below which a drone must not fly unless it is above a landing site
	 */
	public static final double Model_LSALT = 500;
	
	/*
	 * The minimum distance between landing sites
	 */
	public static final double Model_LandingSiteMinimumSeparation = 1000;
	
	/*
	 * The size the continuous space in the x direction
	 */
	public static final int Model_X_WiseSpaceSize = 5000;
	
	/*
	 * The size the continuous space in the y direction
	 */
	public static final int Model_Y_WiseSpaceSize = 1000;
		
	/*
	 * The size the continuous space in the z direction
	 */
	public static final int Model_Z_WiseSpaceSize = 5000;
	
	/*
	 * The number of simulation ticks that represent 1 second of time in the model
	 */
	public static double Model_TimeResolution = 100000;
	
	/*
	 * If this is set to true, the simulation engine will constantly synchronize 1 model second with 1 'real' second
	 */
	public static boolean Model_TimeResolutionAutoTune = true;
	
	/*
	 * The batch size while logging data
	 */
	public static int Model_DataLoggingBatchSize = 5;
	
	/*
	 * The number of drones in the space
	 */
	public static final int Drone_Count = 500;
	
	/*
	 * The range within which objects can detect each others' presence
	 */
	public static final double Drone_DetectionRange = 50;
	
	/*
	 * The mass of a drone
	 */
	public static final double Drone_Mass = 1;
	
	/*
	 * The drag coefficient of the drone
	 * [drag force = coefficient * f(velocity)]
	 */
	public static final double Drone_DragCoefficient = 0.05;
	
	/*
	 * The maximum thrust that each individual jet on a thruster can generate (in Newtons)
	 */
	public static final double Drone_ThrusterLimit = 25;
	
	/*
	 * Whether or not to engage minima avoidance behavior
	 */
	public static final boolean Drone_MinimaAvoidBehavior = true;
	
	/*
	 * If the drone is within this radius of its way point, it will enter minima avoidance mode
	 */
	public static final double Drone_MinimaAvoidanceRadius = 1000;
	
	/*
	 * Drone must be within this distance of the altitude when altitude achievement termination alert is invoked
	 */
	public static final double Drone_AltitudeTerminationRadius = 10;
	
	/*
	 * Drone must be within this distance of the waypoint when the cruise termination alert is invoked
	 */
	public static final double Drone_CruiseTerminationRadius = 20;
	
	/*
	 * Drone must be within this distance of the landing site when the site navigation termination alert is invoked
	 */
	public static final double Drone_SiteNavTerminationRadius = 30;
	
	/*
	 * Drone must be within this distance of the take-off zone when the take-off zone navigation termination alert is invoked
	 */
	public static final double Drone_TakeOffNavTerminationRadius = 30;
	
	/*
	 * The number of landing sites
	 */
	public static final int LandingSite_Count = 10;
	
	/*
	 * The distance from the site location to the take-off point
	 */
	public static final double LandingSite_TakeOffRadius = 70;
	
	/*
	 * The coefficient for the Gaussian that the potential field is modeled with
	 */
	public static final double Physics_PotentialFieldCoefficient = 30;
	
	/*
	 * The mean for the Gaussian that the potential field is modeled with 
	 */
	public static final double Physics_PotentialField_μ = 0;
	
	/*
	 * The standard deviation for the Gaussian that the potential field is modeled with
	 */
	public static final double Physics_PotentialField_σ = 15;
	
	//data collection
	public static final boolean DataCollection_CollectMinDroneDistance = false;
	
	public static final boolean DataCollection_CollectSiteTraffic = false;
	
	public static final boolean DataCollection_CollectHoldingPatternSize = false;
	
}
