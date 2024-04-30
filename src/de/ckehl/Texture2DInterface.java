package de.ckehl;

import com.jogamp.opengl.GL2;

public interface Texture2DInterface {
	public void updateFloatTexture(float[][] data, int width, int height, GL2 gl2);
	public void updateByteTexture(byte[][] data, int width, int height, GL2 gl2);
}
