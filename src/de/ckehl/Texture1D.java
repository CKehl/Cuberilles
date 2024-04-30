package de.ckehl;

import java.nio.ByteBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.GLBuffers;

public class Texture1D extends GeometryContainer implements LUTinterface {
	protected ByteBuffer _data = null;
	protected static int _tex1D[] = null;
	protected int _entries = 0;
	
	protected CubrillesLUT _shader = null;
	protected String basePath = "CubrillesLUT";

	public Texture1D()
	{
		_isUpdated = false;
		_tex1D = new int[1];
		_shader = new CubrillesLUT(basePath, basePath);
	}
	
	@Override
	public void dispose(GL2 gl2)
	{
		gl2.glDeleteTextures(1, _tex1D, 0);
		_shader.dispose(gl2);
		if(_data!=null)
			_data.clear();
		_data = null;
	}
	
	@Override
	public void setup(GL2 gl2) {
		// TODO Auto-generated method stub
		if(_isUpdated==false)
		{
			if(_printSetupLine)
				System.out.println("updating "+this.toString()+"("+Long.toString(_ID)+") ...");
	        setupTexture(gl2);
	        _shader.init(gl2);
	        _shader.updateTextureUniform(1);
	        setCurrentShaderGL2(_shader);
	        if(_printSetupLine)
	        	System.out.println(this.toString()+"("+Long.toString(_ID)+"): up-to-date");
		}
		super.setup(gl2);
	}
	
	@Override
	public void render(GL2 gl2) {
		// TODO Auto-generated method stub
		_shader.useShader(gl2);
		gl2.glEnable(GL2.GL_TEXTURE_1D);
		gl2.glActiveTexture(GL.GL_TEXTURE1);
		gl2.glBindTexture(GL2.GL_TEXTURE_1D, _tex1D[0]);
		super.render(gl2);
		
		/*
		 * optionally: render it to pane (in subclass)
		 */
		
  		gl2.glDisable(GL2.GL_TEXTURE_1D);
  		_shader.dontUseShader(gl2);
	}
	
	@Override
	public String toString()
	{
		return "Texture1D";
	}
	
	@Override
	public byte[] getLUT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLUT(byte[] lut) {
		// TODO Auto-generated method stub
    	if(lut!=null)
    	{
    		_entries = lut.length/4;
    		_data = GLBuffers.newDirectByteBuffer(lut.length);
    		//new short[_dimensions[0]*_dimensions[1]*_dimensions[2]];
			for(short k = 0; k< lut.length; k++)
			{
	    		_data.put(lut[k]);
			}
			_data.rewind();
    	}
	}

	@Override
	public boolean isUpdated() {
		// TODO Auto-generated method stub
		return _isUpdated;
	}

	@Override
	/**
	 * here, _isUpdated is true as long as current information are valid;
	 * _isUpdated is false if new information have become available
	 * (consumer behaviour)
	 */
	public void Update(boolean state) {
		// TODO Auto-generated method stub
		_isUpdated = state;
	}

    private void setupTexture(GL2 gl2)
    {

    	if(_data!=null)
    	{
	    	gl2.glGenTextures(1,  _tex1D,  0);
	    	gl2.glBindTexture(GL2.GL_TEXTURE_1D, _tex1D[0]);
	    	
	    	gl2.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	    	gl2.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_RGBA, _entries, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, _data);
	    	gl2.glBindTexture(GL2.GL_TEXTURE_1D, 0);

	    	_data=null;
	    	
    	}
    	_isUpdated = true;
    }

	@Override
	public int getLUTtexUnit() {
		// TODO Auto-generated method stub
		if((_tex1D!=null) && (_tex1D.length>0))
			return _tex1D[0];
		return 0;
	}

	@Override
	public void setLUTtexUnit(int texUnit) {
		// TODO Auto-generated method stub
		
	}

}
