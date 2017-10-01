package droneSpace.model;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.vecmath.Vector3d;

import repast.simphony.space.continuous.NdPoint;

//this should probably not exist
public class Util
{
	/*
	 * Generate a random double within the given range
	 */
	public static double generateRandomDoubleBetween(double from, double to)
	{
		return from + (to - from) * new Random().nextDouble();
	}
	
	/*
	 * Generate a random int within the given range (inclusive)
	 */
	public static int generateRandomIntBetween(int inclusiveFrom, int inclusiveTo)
	{
		return (int)generateRandomDoubleBetween(inclusiveFrom, inclusiveTo + 0.99);
	}
	
	/*
	 * Get a random point in space
	 */
	public static NdPoint getRandomPointWithinSpace()
	{
		return getRandomPointWithinSpace(0);
	}
	
	/*
	 * Get a random point in space at least buffer units away from the surfaces
	 */
	public static NdPoint getRandomPointWithinSpace(double buffer)
	{
		return new NdPoint(	generateRandomDoubleBetween(buffer, Parameters.Model_X_WiseSpaceSize - buffer),
							generateRandomDoubleBetween(buffer, Parameters.Model_Y_WiseSpaceSize - buffer),
							generateRandomDoubleBetween(buffer, Parameters.Model_Z_WiseSpaceSize - buffer));
	}
	
	/*
	 * Get the Euclidean distance between the given points
	 */
	public static double getEuclideanDistanceBetween(NdPoint point1, NdPoint point2)
	{
		return Math.sqrt(	Math.pow(point2.getX() - point1.getX(), 2) +
							Math.pow(point2.getY() - point1.getY(), 2) +
							Math.pow(point2.getZ() - point1.getZ(), 2));
	}
}
