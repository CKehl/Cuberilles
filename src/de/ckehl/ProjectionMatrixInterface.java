package de.ckehl;

import org.joml.Matrix4f;

public interface ProjectionMatrixInterface {
	public void setPerspectiveProjection(float fovy, float aspect, float zNear, float zFar);
	public Matrix4f getProjection();
}
