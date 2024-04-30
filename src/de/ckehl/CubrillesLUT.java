package de.ckehl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class CubrillesLUT extends ShaderControlJOGL2 {
	private int textureUniform;
	private int _tex;
	
	public CubrillesLUT()
	{
		super();
	}
	
	public CubrillesLUT(String vShaderFile, String fShaderFile)
	{
		super(vShaderFile, fShaderFile);
	}
	
	@Override
	public void init( GL2 gl )
	{
		try
		{
			attachShaders(gl);
			setupUniforms(gl);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setupUniforms(GL2 gl)
	{
		textureUniform = gl.glGetUniformLocation(_shaderProgram.program(), "lookup");
	}
	
	public void updateTextureUniform(int tex_unit)
	{
		_tex = tex_unit;
	}
	
	@Override
	public ShaderProgram useShader( GL2 gl )
	{
		ShaderProgram _top = super.useShader(gl);
		gl.glUniform1i(textureUniform, _tex);
		return _top;
	}
}
