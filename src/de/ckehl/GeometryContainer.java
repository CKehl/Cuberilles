package de.ckehl;

import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.gl.CLGLContext;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

public class GeometryContainer extends Geometry {
	protected static ShaderControlJOGL2 _finalShaderInstanceGL2 = null;
	protected static ShaderControlJOGL2 getShaderInstance() {return _finalShaderInstanceGL2;}
	protected static ShaderGL3ControlJOGL2 _finalShaderInstanceGL3 = null;
	protected static ShaderGL3ControlJOGL2 getShaderInstanceGL3() {return _finalShaderInstanceGL3;}
	
	public static long runID = 0;
	protected boolean _isUpdated = false;
	protected long _ID = 0;
	protected boolean _printSetupLine = false;
	
	public GeometryContainer()
	{
		super();
		_ID = runID;
		runID++;
	}
	
	protected void setCurrentShaderGL2(ShaderControlJOGL2 shader)
	{
		_finalShaderInstanceGL2 = shader;
	}
	
	protected void setCurrentShaderGL3(ShaderGL3ControlJOGL2 shader)
	{
		_finalShaderInstanceGL3 = shader;
	}
	
	@Override
	public void dispose(GL2 gl2)
	{
		if(_children.isEmpty()==false)
		{
			for(Geometry child : _children)
			{
				child.dispose(gl2);
			}
		}
		/*
		 * if(_parent == null)
		 * {
		 * dispose(gl2);
		 * }
		 */
	}
	
	@Override
	public void dispose(GL3 gl3, CLCommandQueue queue)
	{
		if(_children.isEmpty()==false)
		{
			for(Geometry child : _children)
			{
				child.dispose(gl3, queue);
			}
		}
		/*
		 * if(_parent == null)
		 * {
		 * dispose(gl2);
		 * }
		 */
	}

	@Override
	public void render(GL2 gl2) {
		// TODO Auto-generated method stub
		if(_children.isEmpty()==false)
		{
			for(Geometry child : _children)
				child.render(gl2);
		}
	}
	
	@Override
	public void render(GL3 gl3) {
		// TODO Auto-generated method stub
		if(_children.isEmpty()==false)
		{
			for(Geometry child : _children)
				child.render(gl3);
		}
	}
	
	@Override
	public void compute(CLCommandQueue queue)
	{
		if(_children.isEmpty()==false)
		{
			for(Geometry child : _children)
				child.compute(queue);
		}
	}

	@Override
	public void setup(GL2 gl2) {
		// TODO Auto-generated method stub
		if(_children.isEmpty()==false)
		{
			if(_printSetupLine)
				System.out.println("updating "+this.toString()+"("+Long.toString(_ID)+") ...");
			for(Geometry child : _children)
			{
				child.setup(gl2);
			}
			if(_printSetupLine)
				System.out.println(this.toString()+"("+Long.toString(_ID)+"): up-to-date");
		}
		
		/*
		if(_isUpdated==false)
		{
			// update/setup
		}
		*/
	}
	
	@Override
	public void setup(GL3 gl3, CLCommandQueue queue) {
		// TODO Auto-generated method stub
		if(_children.isEmpty()==false)
		{
			if(_printSetupLine)
				System.out.println("updating "+this.toString()+"("+Long.toString(_ID)+") ...");
			for(Geometry child : _children)
			{
				child.setup(gl3, queue);
			}
			if(_printSetupLine)
				System.out.println(this.toString()+"("+Long.toString(_ID)+"): up-to-date");
		}
		
		/*
		if(_isUpdated==false)
		{
			// update/setup
		}
		*/
	}
	
	@Override
	public void reshape(int width, int height)
	{
		if(_children.isEmpty()==false)
		{
			for(Geometry child : _children)
			{
				child.reshape(width, height);
			}
		}
	}
	
	@Override
	public void forceUpdate()
	{
		if(_children.isEmpty()==false)
		{
			for(Geometry child : _children)
			{
				child.forceUpdate();
			}
		}
		_isUpdated = false;
	}

	@Override
	public String toString()
	{
		return "GeometryContainer";
	}
}
