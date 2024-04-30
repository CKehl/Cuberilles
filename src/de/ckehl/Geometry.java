package de.ckehl;

import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.gl.CLGLContext;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

public class Geometry extends Object {
	protected GeometryList _children = null;
	
	public Geometry()
	{
		_children = new GeometryList();
	}
	
	public void dispose(GL2 gl2)
	{
		
	}
	
	public void dispose(GL3 gl3, CLCommandQueue queue)
	{
		
	}
	
	public void render(GL2 gl2)
	{
		
	}
	
	public void render(GL3 gl3)
	{
		
	}
	
	public void compute(CLCommandQueue queue)
	{
		
	}
	
	public void setup(GL2 gl2)
	{
		
	}
	
	public void setup(GL3 gl3, CLCommandQueue queue)
	{
		
	}
	
	public void reshape(int width, int height)
	{
		
	}
	
	public boolean add(Geometry e)
	{
		return _children.add(e);
	}
	
	public Geometry remove(int index)
	{
		return _children.remove(index);
	}
	
	public boolean remove(Geometry o)
	{
		return _children.remove(o);
	}
	
	public void forceUpdate()
	{
		
	}
	
	public GeometryList getChildren()
	{
		return _children;
	}
	
	public String toString()
	{
		return "Geometry";
	}
}
