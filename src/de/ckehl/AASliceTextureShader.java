package de.ckehl;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import com.jogamp.opengl.util.glsl.ShaderUtil;

public class AASliceTextureShader extends ShaderControlJOGL2 {

	private int valueWindowUniform;
	private int valueLevelUniform;
	private int valueIncreaseUniform;
	private int textureUniform;
	private int markTexXYUniform;
	private int markTexXZUniform;
	private int markTexYZUniform;
	private int texmatUniform;
	private int baseLevelUniform;
	private int maxOriginalUniform;
	private int spacingUniform;
	private int lowCutUniform, highCutUniform;
	private float _window;
	private float _level;
	private float _increase;
	private int _tex, _markTexXY, _markTexXZ, _markTexYZ;
	private float _baseLevel;
	private float _max_original;
	private Vector3f _spacing;
	private float _lowCut, _highCut;
	private float[] mTextureMatrix = new float[16];
	private FloatBuffer spacingBuffer;
	
	public AASliceTextureShader()
	{
		super();
		spacingBuffer = GLBuffers.newDirectFloatBuffer(3);
	}
	
	public AASliceTextureShader(String vShaderFile, String fShaderFile)
	{
		super(vShaderFile, fShaderFile);
		spacingBuffer = GLBuffers.newDirectFloatBuffer(3);
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
		valueWindowUniform = gl.glGetUniformLocation(_shaderProgram.program(), "valueWindow");
		valueLevelUniform = gl.glGetUniformLocation(_shaderProgram.program(), "valueLevel");
		valueIncreaseUniform = gl.glGetUniformLocation(_shaderProgram.program(), "valueIncrease");
		textureUniform = gl.glGetUniformLocation(_shaderProgram.program(), "volTexture");
		markTexXYUniform = gl.glGetUniformLocation(_shaderProgram.program(), "markTextureXY");
		markTexXZUniform = gl.glGetUniformLocation(_shaderProgram.program(), "markTextureXZ");
		markTexYZUniform = gl.glGetUniformLocation(_shaderProgram.program(), "markTextureYZ");
		texmatUniform = gl.glGetUniformLocation(_shaderProgram.program(), "mTexMat");
		baseLevelUniform = gl.glGetUniformLocation(_shaderProgram.program(), "baseValue");
		maxOriginalUniform = gl.glGetUniformLocation(_shaderProgram.program(), "max_original");
		spacingUniform = gl.glGetUniformLocation(_shaderProgram.program(), "spacing");
		lowCutUniform = gl.glGetUniformLocation(_shaderProgram.program(), "lowCut");
		highCutUniform = gl.glGetUniformLocation(_shaderProgram.program(), "highCut");
	}
	
	public void updateWindowValue(float value)
	{
		_window = value;
	}
	
	public void updateLevelValue(float value)
	{
		_level = value;
	}
	
	public void updateIncreaseValue(float value)
	{
		_increase = value;
	}
	
	public void updateTextureUniform(int tex_unit)
	{
		_tex = tex_unit;
	}
	
	public void updateMarkingTextureXYUniform(int tex_unit)
	{
		_markTexXY = tex_unit;
	}

	public void updateMarkingTextureXZUniform(int tex_unit)
	{
		_markTexXZ = tex_unit;
	}

	public void updateMarkingTextureYZUniform(int tex_unit)
	{
		_markTexYZ = tex_unit;
	}

	public void updateSpacing(Vector3f value)
	{
		_spacing = new Vector3f(value);
		_spacing.get(spacingBuffer);
	}
	
	public void updateTextureMatrixUniform(float[] value)
	{
		// switch row-major to column-major order
		System.arraycopy(value, 0, mTextureMatrix, 0, value.length);
		//System.out.printf("copied %d values.\n", value.length);
	}
	
	public void updateTextureMatrixUniform(Matrix4f value)
	{
		value.get(mTextureMatrix);
	}
	
	public void updateBaseLevel(float value)
	{
		_baseLevel = value;
	}
	
	public void updateMaxOriginal(float value)
	{
		_max_original = value;
		_lowCut = 0f;
		_highCut = value;
	}
	
	public void updateLowCut(float value)
	{
		_lowCut = value;
	}
	
	public void updateHighCut(float value)
	{
		_highCut = value;
	}
	
	@Override
	public ShaderProgram useShader( GL2 gl )
	{
		ShaderProgram _top = super.useShader(gl);
		gl.glUniform1f(valueWindowUniform, _window);
		gl.glUniform1f(valueLevelUniform, _level);
		gl.glUniform1f(valueIncreaseUniform, _increase);
		gl.glUniform1i(textureUniform, _tex);
		gl.glUniform1i(markTexXYUniform, _markTexXY);
		gl.glUniform1i(markTexXZUniform, _markTexXZ);
		gl.glUniform1i(markTexYZUniform, _markTexYZ);
		gl.glUniformMatrix4fv(texmatUniform, 1, false, mTextureMatrix, 0);
		gl.glUniform1f(baseLevelUniform, _baseLevel);
		gl.glUniform1f(maxOriginalUniform, _max_original);
		gl.glUniform1f(lowCutUniform, _lowCut);
		gl.glUniform1f(highCutUniform, _highCut);
		gl.glUniform3fv(spacingUniform, 1, spacingBuffer);
		return _top;
	}
}
