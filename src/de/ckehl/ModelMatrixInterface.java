package de.ckehl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface ModelMatrixInterface extends MatrixInterface {

	
	public void setTranslateModel(float x, float y, float z);
	public void setRotateXModel(float rX);
	public void setRotateYModel(float rY);
	public void setRotateZModel(float rZ);

	public Vector3f getTranslateModel();
	public float getRotateXModel();
	public float getRotateYModel();
	public float getRotateZModel();
	
	public Matrix4f getModelMatrix();
	public void setModelMatrix(Matrix4f mat);
}
