package droneSpace.dataCollection;

import java.util.ArrayList;

import droneSpace.interefaces.ISteppable;
import droneSpace.model.Logger;

/*
 * Generic class to log lines to data in batches
 */
public class DataLogger implements ISteppable
{
	/*
	 * The current data cache
	 */
	private ArrayList<String> dataCache;
	
	/*
	 * The batch size
	 */
	private int batchSize;
	
	/*
	 * .ctor
	 */
	public DataLogger(int batchSize)
	{
		this.dataCache = new ArrayList<String>();
		this.batchSize = batchSize;
	}
	
	/*
	 * log a line of data
	 */
	public void Log(String logLine)
	{
		dataCache.add(logLine);
	}
	
	@Override
	public void step()
	{
		if(dataCache.size() >= batchSize)
		{
			for(String line : dataCache)
			{
				Logger.appendLogLine(line);
			}
			dataCache.clear();
		}
	}
	
}
