package de.ckehl;

import java.util.List;

import org.joml.Vector3i;

public interface RaySelectionInterface {
	public void setRayInformation(List<Vector3i> intersections);
	public void resetRayInformation();
	//public void showHistogram(List<Vector3i> intersections);
	//public void showRayProfile(List<Vector3i> intersections);
	//public void showRaySemivariogram(List<Vector3i> intersections);
}
