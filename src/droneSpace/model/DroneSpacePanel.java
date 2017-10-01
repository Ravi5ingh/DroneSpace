package droneSpace.model;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class DroneSpacePanel extends JPanel
{
	private JTextField txtDragCoefficient;
	private JTextField txtY;
	private JTextField txtX;
	private JTextField txtDroneCount;
	private JTextField txtZ;
	
	public DroneSpacePanel()
	{
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setLayout(new GridLayout(5, 4, 10, 100));
		
		JLabel lblDragCoefficient = new JLabel("Drag Coefficient :");
		lblDragCoefficient.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblDragCoefficient);
		
		txtDragCoefficient = new JTextField();
		add(txtDragCoefficient);
		txtDragCoefficient.setColumns(10);
		
		JLabel lblDroneCount = new JLabel("Drone Count :");
		lblDroneCount.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblDroneCount);
		
		txtDroneCount = new JTextField();
		add(txtDroneCount);
		txtDroneCount.setColumns(10);
		
		JLabel lblXWiseSize = new JLabel("X Wise Size :");
		lblXWiseSize.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblXWiseSize);
		
		txtX = new JTextField();
		add(txtX);
		txtX.setColumns(10);
		
		JLabel lblYWiseSize = new JLabel("Y Wise Size :");
		lblYWiseSize.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblYWiseSize);
		
		txtY = new JTextField();
		add(txtY);
		txtY.setColumns(10);
		
		JLabel lblZWiseSize = new JLabel("Z Wise Size :");
		lblZWiseSize.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblZWiseSize);
		
		txtZ = new JTextField();
		add(txtZ);
		txtZ.setColumns(10);
	}
}
