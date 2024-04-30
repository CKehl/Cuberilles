package de.ckehl;

import org.joml.Vector3f;

public interface LightInformationInterface {
	public Vector3f getLightPosition();
	public void setLightPosition(Vector3f position);
	public void updateLightPosition();
	public void EnableLighting();
	public void DisableLighting();
	public float getLightImpact();
	public void setLightImpact(float lightImpact);
	public void updateLightImpact();
}
