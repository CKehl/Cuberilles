package de.ckehl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class ShaderControlJOGL2 {
	protected ShaderCode _vertexShader;
	protected ShaderCode _fragShader;
	//ShaderCode _geometryShader;
	protected ShaderProgram _shaderProgram;
	protected String _vShaderSourceFile;
	protected String _fShaderSourceFile;
	
	public ShaderControlJOGL2()
	{
		
	}
	
	public ShaderControlJOGL2(String vShaderFile, String fShaderFile)
	{
		_vShaderSourceFile = vShaderFile;
		_fShaderSourceFile = fShaderFile;
	}
	
	public void dispose(GL2 gl)
	{
		gl.glDeleteProgram(_shaderProgram.id());
	}
	
	// this will attach the shaders
	public void init( GL2 gl )
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
	
	protected void attachShaders( GL2 gl ) throws Exception
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
		
		_shaderProgram = new ShaderProgram();
		_shaderProgram.add(_vertexShader);
		_shaderProgram.add(_fragShader);
		_shaderProgram.init(gl);
		_shaderProgram.link(gl, System.out);
	}
	
	public ShaderProgram useShader( GL2 gl )
	{
		gl.glUseProgram(_shaderProgram.program());
		return _shaderProgram;
	}

	// when you have finished drawing everything that you want using the shaders, 
	// call this to stop further shader interactions.
	public void dontUseShader( GL2 gl )
	{
		gl.glUseProgram(0);
	}
}
