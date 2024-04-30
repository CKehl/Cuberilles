package de.ckehl;

import org.joml.Vector3f;
import org.joml.Vector4f;

public interface ProjectionViewInterface {
	/*
	 * These functions go out from the common
	 * view coord system p_l=(0,0,0), d_l=(0,0,-1),
	 * and multiply that with the inverse model-view
	 * p_w = M^-1 * V^-1 * p_l
	 * d_w = M^-1 * V^-1 * d_l
	 */
	public Vector3f getViewDirectionLocal();
	public Vector4f getViewPointLocal();
	public Vector3f getViewDirectionWorld();
	public Vector4f getViewPointWorld();
	
	/*
	 * returns direct parameters of underlying
	 * glLookAt()
	 */
	public Vector3f getRawWorldViewPoint();
	public Vector3f getRawWorldViewDirection();
}
