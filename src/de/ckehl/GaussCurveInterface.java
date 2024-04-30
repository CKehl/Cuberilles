package de.ckehl;

import org.joml.Vector3d;

public interface GaussCurveInterface {
	public float[] getCurveParamArray();
	public void setCurveParamArray(float[] gaussFuncParams, int paramsPerItem);
	public short[] getCurveParamUShortArray();
	public void setCurveParamUShortArray(short[] gaussFuncParams, int paramsPerItem);
	public int getNumberOfCurveParameters();
	public void setNumberOfCurveParameters(int numCurveParameters);
	public int getNumberOfCurves();
	public void setNumberOfCurves(int numCurves);
	public boolean isCurveSelected();
	public Vector3d getSelectedGaussCurve();
	public Vector3d getGaussCurve(int index);
	public void setGaussCurve(Vector3d gaussParameters);
	public boolean isUpdated();
	public void Update(boolean state);
}
