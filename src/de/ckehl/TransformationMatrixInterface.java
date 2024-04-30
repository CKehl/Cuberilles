package de.ckehl;

import org.joml.Matrix4f;

public interface TransformationMatrixInterface {
	public void setModelViewProjectionMatrix(Matrix4f mat);
	public Matrix4f getModelViewProjectionMatrix();
}
