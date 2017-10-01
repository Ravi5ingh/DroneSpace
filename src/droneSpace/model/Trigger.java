package droneSpace.model;

public class Trigger
{
	public static final String Class_Drone = "droneSpace.agents.Drone";
	
	/*
	 * The name of the broadcasting flag field in the watched agent
	 */
	public static final String Field_DroneIsBroadcasting = "isBroadcasting";
	
	/*
	 * String query for checking if the watched agent is within the detection range
	 */
	public static final String Query_WithinDetectionRange = "within " + Parameters.Drone_DetectionRange;
	
	/*
	 * String condition for checking if the watched agent is currently broadcasting
	 */
	public static final String Condition_WatcheeIsBroadcasting = "$watchee." + Field_DroneIsBroadcasting;
	
	/*
	 * String condition to invoke the register method on the watcher, which registers with it the watchee
	 */
	public static final String Condition_WatcheeUnregistered = "$watcher.registerDrone($watchee)";
	
	/*
	 * String condition for checking that the watcher is not equal to the watchee
	 */
	public static final String Condition_WatcherUnequaltoWatcher = "$watcher != $watchee";
}
