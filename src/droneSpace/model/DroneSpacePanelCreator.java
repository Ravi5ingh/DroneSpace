package droneSpace.model;

import javax.swing.JPanel;

import repast.simphony.userpanel.ui.UserPanelCreator;

public class DroneSpacePanelCreator implements UserPanelCreator
{

	public static DroneSpacePanel spacePanel = new DroneSpacePanel();
	
	@Override
	public JPanel createPanel()
	{
		return spacePanel;
	}

}
