package de.ckehl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface TextureMatrixInterface extends MatrixInterface {

	
	public void setTranslateTexture(float x, float y, float z);
	public void setRotateXTexture(float rX);
	public void setRotateYTexture(float rY);
	public void setRotateZTexture(float rZ);

	public Vector3f getTranslateTexture();
	public float getRotateXTexture();
	public float getRotateYTexture();
	public float getRotateZTexture();
	
	public Matrix4f getTextureMatrix();
}
