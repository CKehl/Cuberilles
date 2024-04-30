package de.ckehl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class ShaderGL3ControlJOGL2 extends ShaderControlJOGL2 {
	protected ShaderCode _geomShader;
	protected String _gShaderSourceFile;
	
	public ShaderGL3ControlJOGL2()
	{
		super();
	}
	
	public ShaderGL3ControlJOGL2(String vShaderFile, String fShaderFile)
	{
		super(vShaderFile, fShaderFile);
		_gShaderSourceFile = null;
	}
	
	public ShaderGL3ControlJOGL2(String vShaderFile, String fShaderFile, String gShaderFile)
	{
		super(vShaderFile, fShaderFile);
		_gShaderSourceFile = gShaderFile;
	}
	
	
	public void dispose(GL3 gl)
	{
		gl.glDeleteProgram(_shaderProgram.id());
	}
	
	// this will attach the shaders
	public void init( GL3 gl )
	{
		try
		{
			attachShaders(gl);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void attachShaders( GL3 gl ) throws Exception
	{
		/**
		 * ShaderCode vp0 = ShaderCode.create(gl, GL2ES2.GL_VERTEX_SHADER, 1, this.getClass(),
     *                                         "shader", new String[] { "vertex" }, null,
     *                                         "shader/bin", "vertex", null, true);
     *      ShaderCode fp0 = ShaderCode.create(gl, GL2ES2.GL_FRAGMENT_SHADER, 1, this.getClass(),
     *                                         "shader", new String[] { "vertex" }, null,
     *                                         "shader/bin", "fragment", null, true);
		 */
		
		_vertexShader = ShaderCode.create(gl, GL2.GL_VERTEX_SHADER, 1, this.getClass(), "res/shaders", new String[] {_vShaderSourceFile}, "vert", "shaders/bin", _vShaderSourceFile, null, true);
		_vertexShader.compile(gl, System.out);
		_fragShader = ShaderCode.create(gl, GL2.GL_FRAGMENT_SHADER, 1, this.getClass(), "res/shaders", new String[] {_fShaderSourceFile}, "frag", "shaders/bin", _fShaderSourceFile, null, true);
		_fragShader.compile(gl, System.out);
		if(_gShaderSourceFile!=null)
		{
			_geomShader = ShaderCode.create(gl, GL3.GL_GEOMETRY_SHADER, 1, this.getClass(), "res/shaders", new String[] {_gShaderSourceFile}, "geom", "shaders/bin", _gShaderSourceFile, null, true);
			_geomShader.compile(gl, System.out);
		}
		
		_shaderProgram = new ShaderProgram();
		_shaderProgram.add(_vertexShader);
		_shaderProgram.add(_fragShader);
		if(_gShaderSourceFile!=null)
		{
			_shaderProgram.add(_geomShader);
		}
		_shaderProgram.init(gl);
		_shaderProgram.link(gl, System.out);
	}
	
	public ShaderProgram useShader( GL3 gl )
	{
		gl.glUseProgram(_shaderProgram.program());
		return _shaderProgram;
	}

	// when you have finished drawing everything that you want using the shaders, 
	// call this to stop further shader interactions.
	public void dontUseShader( GL3 gl )
	{
		gl.glUseProgram(0);
	}
}
