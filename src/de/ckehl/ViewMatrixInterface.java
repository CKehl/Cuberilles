package de.ckehl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface ViewMatrixInterface  extends MatrixInterface {
	public void setLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ);
	public void setTranslateView(float x, float y, float z);
	public void setRotateXView(float rX);
	public void setRotateYView(float rY);
	public void setRotateZView(float rZ);

	public Vector3f getTranslateView();
	public float getRotateXView();
	public float getRotateYView();
	public float getRotateZView();
	
	public Matrix4f getViewMatrix();
	public Matrix4f getViewTransformationMatrix();
}
