package de.ckehl;

import org.joml.Vector3f;
import org.joml.Vector3i;

public interface NormalUpdateInterface {
	public boolean hasNormal();
	public void setNormals(Vector3f normals[][][]);
	public Vector3f[][][] getNormals();
	public Vector3f getNormal(Vector3i index);
	public void ActivateNormalVis();
	public void DeactivateNormalVis();
	public boolean hasNormals();
	public boolean usesNormals();
}
