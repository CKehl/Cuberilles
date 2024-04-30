package de.ckehl;

public interface ClippingMatrixInterface extends ModelMatrixInterface {
	public void resetMatrix();
	public void invertNormalDirection();
	public void Enable();
	public void Disable();
	public void Toggle();
	public void IncreaseXRotation();
	public void DecreaseXRotation();
	public void IncreaseYRotation();
	public void DecreaseYRotation();
	public void IncreaseZRotation();
	public void DecreaseZRotation();
	public void ForwardX();
	public void BackwardX();
	public void ForwardY();
	public void BackwardY();
	public void ForwardZ();
	public void BackwardZ();
}
