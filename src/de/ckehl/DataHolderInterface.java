package de.ckehl;

import org.joml.Vector3f;

public interface DataHolderInterface {
	public void setData(short data[][][]);
	public short[][][] getData();
	public short getDataValue(int x, int y, int z);
	public void setDimensions(short dims[]);
	public int[] getDimensionsInt();
	public short[] getDimensions();
	public void setSpacing(float spacing[]);
	public float[] getSpacing();
	public boolean hasData();
	public boolean hasNormals();
	public boolean hasNormalTexture();
	public void computeNormals();
	public void computeNormalTexture();
	public Vector3f[][][] getNormals();
	public byte[] getNormalTextureData();
	public int getTextureAddress(int blockX, int blockY, int blockZ);
	public Vector3f getNormal(int blockX, int blockY, int blockZ);
	public int getNumberElements();
}
