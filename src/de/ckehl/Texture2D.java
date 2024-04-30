package de.ckehl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.joml.Vector3f;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.GLBuffers;

public class Texture2D extends GeometryContainer implements Texture2DInterface {
	protected static int _tex2D[] = null;
	protected boolean _head = false;
	protected boolean _clearBuffer = true;
	
	/**
	 * 
	 * @param slicePane - tells the plane to render; XY_PANE(1), XZ_PANE(2) or YZ_PANE(4)
	 */
	public Texture2D()
	{
		_head = false;
		_isUpdated = false;
	}
	
	/**
	 * 
	 * @param slicePane - tells the plane to render; XY_PANE(1), XZ_PANE(2) or YZ_PANE(4)
	 * @param headView - in a shared-context setup, tells if this is the (master) head view
	 */
	public Texture2D(boolean headView)
	{
		_tex2D = new int[1];
		_head = headView;
		_isUpdated = false;
	}
	
	@Override
	public void dispose(GL2 gl2)
	{
		super.dispose(gl2);
		if(_head && _clearBuffer)
		{
			gl2.glDeleteTextures(1, _tex2D, 0);
		}
	}
	
	@Override
	public void setup(GL2 gl2) {
		// TODO Auto-generated method stub
		if(_isUpdated==false)
		{
			if(_printSetupLine)
				System.out.println("updating "+this.toString()+"("+Long.toString(_ID)+") ...");
	        if(_head)
	        {
	        	//setupTexture(gl2);
	        	
	        }
	        
	        if(_printSetupLine)
	        	System.out.println(this.toString()+"("+Long.toString(_ID)+"): up-to-date");
		}
		super.setup(gl2);
	}
	
	@Override
	public void render(GL2 gl2) {
		// TODO Auto-generated method stub
		//gl2.glEnable(GL2.GL_TEXTURE_2D);
		//gl2.glActiveTexture(GL.GL_TEXTURE1);
		//gl2.glBindTexture(GL2.GL_TEXTURE_2D, _tex2D[0]);
        
        super.render(gl2);
        //gl2.glDisable(GL2.GL_TEXTURE_2D);
	}
	
	@Override
	public String toString()
	{
		return "Texture2D";
	}
	
	public void activate(GL2 gl2, int TEXTURETYPE)
	{
		gl2.glActiveTexture(TEXTURETYPE);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, _tex2D[0]);
		gl2.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE);
        gl2.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL.GL_REPLACE);
	}
	
	public void deactivate(GL2 gl2)
	{
		gl2.glDisable(GL2.GL_TEXTURE_2D);
	}
	
    private void setupTexture(GL2 gl2)
    {
    	gl2.glGenTextures(1,  _tex2D,  0);

    	_isUpdated = true;
    }
    
    public void setBufferID(int _tboID)
    {
    	if(_head)
    	{
    		_tex2D[0] = _tboID;
    		_clearBuffer = false;
    	}
    }

	@Override
	public void updateFloatTexture(float[][] data, int width, int height, GL2 gl2) {
		//if(_head)
		//{
			FloatBuffer texData = GLBuffers.newDirectFloatBuffer(width*height);
			for(int q=0; q<height; q++)
				for(int p=0; p<width; p++)
					texData.put(data[p][q]);
			texData.rewind();
			
			// TODO Auto-generated method stub
	    	gl2.glBindTexture(GL2.GL_TEXTURE_2D, _tex2D[0]);
		    	
	    	gl2.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
	    	//gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_REPEAT);
	
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	    	gl2.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_LUMINANCE, width, height, 0, GL2.GL_LUMINANCE, GL.GL_FLOAT, texData);
	    	gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		//}
    	_isUpdated = true;
	}

	@Override
	public void updateByteTexture(byte[][] data, int width, int height, GL2 gl2) {
		// TODO Auto-generated method stub
		//if(_head)
		//{
			ByteBuffer texData = GLBuffers.newDirectByteBuffer(width*height);
			for(int q=0; q<height; q++)
				for(int p=0; p<width; p++)
					texData.put(data[p][q]);
			texData.rewind();
			
			// TODO Auto-generated method stub
	    	gl2.glBindTexture(GL2.GL_TEXTURE_2D, _tex2D[0]);
		    	
	    	gl2.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
	    	//gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_REPEAT);
	
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	    	gl2.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_LUMINANCE, width, height, 0, GL2.GL_LUMINANCE, GL.GL_BYTE, texData);
	    	gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		//}
    	_isUpdated = true;
	}
}
