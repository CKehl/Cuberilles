package de.ckehl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class GaussCurveLUT extends ShaderControlJOGL2 {
	private int textureUniform;
	private int _tex;
	private int numEntriesUniform;
	private int _numEntries;
	
	public GaussCurveLUT()
	{
		super();
	}
	
	public GaussCurveLUT(String vShaderFile, String fShaderFile)
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
		textureUniform = gl.glGetUniformLocation(_shaderProgram.program(), "gaussCurves");
		numEntriesUniform = gl.glGetUniformLocation(_shaderProgram.program(), "gaussEntry");
	}
	
	public void updateTextureUniform(int tex_unit)
	{
		_tex = tex_unit;
	}
	
	public void updateNumberOfEntries(int entryNumber)
	{
		_numEntries = entryNumber;
	}
	
	@Override
	public ShaderProgram useShader( GL2 gl )
	{
		ShaderProgram _top = super.useShader(gl);
		gl.glUniform1i(textureUniform, _tex);
		gl.glUniform1i(numEntriesUniform, _numEntries);
		return _top;
	}
}
