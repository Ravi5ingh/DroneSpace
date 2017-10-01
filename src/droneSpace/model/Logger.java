package droneSpace.model;

import java.io.FileWriter;
import java.util.HashMap;

import javax.vecmath.Vector3d;

public class Logger
{
	/*
	 * The log writer
	 */
	private static FileWriter writer;
	
	/*
	 * For custom file writing
	 */
	private static HashMap<String, FileWriter> fileWriters;
	
	/*
	 * Static handled creation of file writer
	 */
	static
	{
		try
		{
			writer = new FileWriter("C:/Users/Ravi_2/shed/DroneSpace/customLogs/Log.txt");
		}
		catch(Exception ex){}
		
		fileWriters = new HashMap<String, FileWriter>();
	}
	
	/*
	 * Append a named vector to the log
	 */
	public static void appendVectorLine(String name, Vector3d vector)
	{
		try
		{
			writer.append("\n" + name + "[" + vector.x + "," + vector.y + "," + vector.z + "]").flush();
		}
		catch(Exception ex){}
	}
	
	/*
	 * Append a line to the custom log file
	 */
	public static void appendLog(Object logLine)
	{
		try
		{
			writer.append(logLine.toString()).flush();
		}
		catch(Exception ex){}
	}
	
	/*
	 * Append a new line to the custom log file
	 */
	public static void appendLogLine(Object logLine)
	{
		appendLog("\n" + logLine.toString());
	}
	
	/*
	 * Log a line to a custom file name
	 */
	public static void appendLogFileLine(String fileName, Object logLine)
	{
		if(fileWriters.containsKey(fileName))
		{
			try
			{
				fileWriters.get(fileName).append("\n" + logLine).flush();	
			}
			catch(Exception ex){}
		}
		else
		{
			try
			{
				FileWriter newWriter = new FileWriter("C:/Users/Ravi_2/shed/DroneSpace/customLogs/" + fileName + ".txt");
				fileWriters.put(fileName, newWriter);
			}
			catch(Exception ex){}
			appendLogFileLine(fileName, logLine);
		}
	}
}
