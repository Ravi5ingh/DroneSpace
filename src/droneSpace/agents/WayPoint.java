package droneSpace.agents;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import droneSpace.interefaces.IContextElement;
import droneSpace.interefaces.IMobileElement;
import droneSpace.interefaces.ITangibleElement;

public class WayPoint implements ITangibleElement
{

	private ContinuousSpace<IContextElement> space;
	
	public WayPoint(ContinuousSpace<IContextElement> space)
	{
		this.space = space;
	}
	
	@Override
	public NdPoint getLocation()
	{
		return space.getLocation(this);
	}
	
	public void moveTo(NdPoint point)
	{
		space.moveTo(this, point.getX(), point.getY(), point.getZ());
	}

	@Override
	public void initializeInSpace()
	{
		// TODO Auto-generated method stub
		
	}

}
