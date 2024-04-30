package de.ckehl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

public interface ViewPlaneInterface {
	public void setPlaneViewpoint(PlaneViewpoint p);
	public PlaneViewpoint getPlaneViewpoint();
	public void update(GL3 gl3);
	public void update(GL2 gl2);
}
