package de.ckehl;

public interface LUTinterface {
	public byte[] getLUT();
	public void setLUT(byte[] lut);
	public int getLUTtexUnit();
	public void setLUTtexUnit(int texUnit);
	public boolean isUpdated();
	public void Update(boolean state);
}
