package de.ckehl;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class InstancedCubrilleShader extends ShaderGL3ControlJOGL2 {

	private int maxValueUniform;
	private float _maxValue;
	private int lowCutUniform, highCutUniform;
	private float _lowCut, _highCut;
	
	private int pvmUniform;
	private Matrix4f _pvm;
	private FloatBuffer _pvmBuffer = null;
	private int modelMatrixUniform;
	private Matrix4f _modelMatrix;
	private FloatBuffer _modelMatrixBuffer = null;
	private int viewMatrixUniform;
	private Matrix4f _viewMatrix;
	private FloatBuffer _viewMatrixBuffer = null;
	private int projectionMatrixUniform;
	private Matrix4f _projectionMatrix;
	private FloatBuffer _projectionMatrixBuffer = null;

	private int dimensionsUniform;
	private Vector3f _dimensions = new Vector3f();
	private FloatBuffer _dimensionsBuffer = null;
	
	private int spacingUniform;
	private Vector3f _spacing = new Vector3f();
	private FloatBuffer _spacingBuffer = null;
	
	private int scalingUniform;
	private Vector3f _scaling = new Vector3f(0.5f,0.5f,0.5f);
	private FloatBuffer _scalingBuffer = null;
	
	private boolean _useLighting = false;
	private int lightPositionUniform;
	private Vector3f lightPosition = new Vector3f();
	private FloatBuffer lightPositionBuffer = null;
	private int lightImpactUniform;
	private float lightImpact = 1.0f;
	
	private int lutUniform;
	private int _lutTexIndex;
	//private int gaussUniform;
	//private int _gaussTexIndex;
	private int numGaussCurvesUniform;
	private int _numGaussCurves;
	private int gaussParameterUniform;
	private Vector3f _gaussParameter = new Vector3f();
	private FloatBuffer _gaussParameterBuffer = null;
	
	//normal texture
	private boolean _useNormals = true;
	private int normalTexUniform;
	private int _normalTexIndex;
	
	private int clipPlaneCentreUniform;
	private int clipPlaneNormalUniform;
	private int clipPlaneMatrixUniform;
	private int clipPlaneSwitchUniform;
	private Vector4f _clipPlaneCentre;
	private Vector3f _clipPlaneNormal;
	private Matrix4f _clipPlaneMatrix;
	private int _clipPlaneSwitch = 0;
	private FloatBuffer _clipPlaneCentreBuffer;
	private FloatBuffer _clipPlaneNormalBuffer;
	private FloatBuffer _clipPlaneMatrixBuffer;
	
	public InstancedCubrilleShader()
	{
		super();
		_pvmBuffer = GLBuffers.newDirectFloatBuffer(16);
		_modelMatrixBuffer = GLBuffers.newDirectFloatBuffer(16);
		_viewMatrixBuffer = GLBuffers.newDirectFloatBuffer(16);
		_projectionMatrixBuffer = GLBuffers.newDirectFloatBuffer(16);
		_dimensionsBuffer = GLBuffers.newDirectFloatBuffer(3);
		_spacingBuffer = GLBuffers.newDirectFloatBuffer(3);
		_scalingBuffer = GLBuffers.newDirectFloatBuffer(3);
		_scaling.get(_scalingBuffer);
		_gaussParameterBuffer = GLBuffers.newDirectFloatBuffer(3);
		_clipPlaneCentreBuffer = GLBuffers.newDirectFloatBuffer(4);
		_clipPlaneNormalBuffer = GLBuffers.newDirectFloatBuffer(3);
		_clipPlaneMatrixBuffer = GLBuffers.newDirectFloatBuffer(16);
	}
	
	public InstancedCubrilleShader(String vShaderFile, String fShaderFile, String gShaderFile)
	{
		super(vShaderFile, fShaderFile, gShaderFile);
		_pvmBuffer = GLBuffers.newDirectFloatBuffer(16);
		_modelMatrixBuffer = GLBuffers.newDirectFloatBuffer(16);
		_viewMatrixBuffer = GLBuffers.newDirectFloatBuffer(16);
		_projectionMatrixBuffer = GLBuffers.newDirectFloatBuffer(16);
		_dimensionsBuffer = GLBuffers.newDirectFloatBuffer(3);
		_spacingBuffer = GLBuffers.newDirectFloatBuffer(3);
		_scalingBuffer = GLBuffers.newDirectFloatBuffer(3);
		_scaling.get(_scalingBuffer);
		_gaussParameterBuffer = GLBuffers.newDirectFloatBuffer(3);
		_clipPlaneCentreBuffer = GLBuffers.newDirectFloatBuffer(4);
		_clipPlaneNormalBuffer = GLBuffers.newDirectFloatBuffer(3);
		_clipPlaneMatrixBuffer = GLBuffers.newDirectFloatBuffer(16);
	}
	
	@Override
	public void init( GL3 gl )
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
	
	public void setupUniforms(GL3 gl)
	{
		maxValueUniform = gl.glGetUniformLocation(_shaderProgram.program(), "maxValue");
		pvmUniform = gl.glGetUniformLocation(_shaderProgram.program(), "pvm");
		modelMatrixUniform = gl.glGetUniformLocation(_shaderProgram.program(), "mMat");
		viewMatrixUniform = gl.glGetUniformLocation(_shaderProgram.program(), "vMat");
		projectionMatrixUniform = gl.glGetUniformLocation(_shaderProgram.program(), "pMat");
		lutUniform = gl.glGetUniformLocation(_shaderProgram.program(), "lookup");
		spacingUniform = gl.glGetUniformLocation(_shaderProgram.program(), "spacing");
		scalingUniform = gl.glGetUniformLocation(_shaderProgram.program(), "scale");
		numGaussCurvesUniform = gl.glGetUniformLocation(_shaderProgram.program(), "numGaussCurves");
		gaussParameterUniform = gl.glGetUniformLocation(_shaderProgram.program(), "gaussParameter");
		clipPlaneCentreUniform = gl.glGetUniformLocation(_shaderProgram.program(), "clipPlaneCentre");
		clipPlaneNormalUniform = gl.glGetUniformLocation(_shaderProgram.program(), "clipPlaneNormal");
		clipPlaneMatrixUniform = gl.glGetUniformLocation(_shaderProgram.program(), "clipPlaneMatrix");
		clipPlaneSwitchUniform = gl.glGetUniformLocation(_shaderProgram.program(), "clipPlaneSwitch");
		//lowCut, highCut
		lowCutUniform = gl.glGetUniformLocation(_shaderProgram.program(), "lowCut");
		highCutUniform = gl.glGetUniformLocation(_shaderProgram.program(), "highCut");
		normalTexUniform = gl.glGetUniformLocation(_shaderProgram.program(), "normalTexture");
		dimensionsUniform = gl.glGetUniformLocation(_shaderProgram.program(), "dimensions");
	}
	
	public void EnableLighting(GL3 gl, Vector3f LightPos)
	{
		lightPositionBuffer = GLBuffers.newDirectFloatBuffer(3);
		lightPositionUniform = gl.glGetUniformLocation(_shaderProgram.program(), "LightPosition");
		lightPosition = new Vector3f(LightPos);
		lightPosition.get(lightPositionBuffer);
		lightImpactUniform = gl.glGetUniformLocation(_shaderProgram.program(), "lightImpact");
		
		_useLighting = true;
	}
	
	public void DisableLighting(GL3 gl)
	{
		lightPositionUniform = 0;
		lightPosition = new Vector3f();
		lightPositionBuffer = GLBuffers.newDirectFloatBuffer(0);
		_useLighting = false;
	}
	
	public void EnableClipping()
	{
		_clipPlaneSwitch = 1;
	}
	
	public void DisableClipping()
	{
		_clipPlaneSwitch = 0;
	}
	
	public void updateMaxValue(float value)
	{
		_maxValue = value;
		_lowCut = 0f;
		_highCut = value;
	}
	
	public void updateModelViewProjectionMatrix(Matrix4f pmv)
	{
		_pvm = pmv;
		if((_pvm!=null) && (_pvmBuffer!=null))
			_pvm.get(_pvmBuffer);
	}
	
	public void updateModelMatrix(Matrix4f modelMatrix)
	{
		_modelMatrix = modelMatrix;
		if((_modelMatrix!=null) && (_modelMatrixBuffer!=null))
			_modelMatrix.get(_modelMatrixBuffer);
	}
	
	public void updateViewMatrix(Matrix4f viewMatrix)
	{
		_viewMatrix = viewMatrix;
		if((_viewMatrix!=null) && (_viewMatrixBuffer!=null))
			_viewMatrix.get(_viewMatrixBuffer);
	}
	
	public void updateProjectionMatrix(Matrix4f projectionMatrix)
	{
		_projectionMatrix = projectionMatrix;
		if((_projectionMatrix!=null) && (_projectionMatrixBuffer!=null))
			_projectionMatrix.get(_projectionMatrixBuffer);
	}
	
	public void updateLUTtexIndexUniform(int tex_unit)
	{
		_lutTexIndex = tex_unit;
	}
	
	public void updateNumGaussCurves(int num_curves)
	{
		_numGaussCurves = num_curves;
	}
	
	public void updateGaussParameter(Vector3f value)
	{
		_gaussParameter = new Vector3f(value);
		_gaussParameter.get(_gaussParameterBuffer);
	}
	
	public void updateSpacing(float[] spacing)
	{
		if(spacing.length==3)
		{
			_spacing.set(spacing[0], spacing[1], spacing[2]);
			_spacing.get(_spacingBuffer);
		}
	}
	
	public void updateScaling(Vector3f value)
	{
		_scaling = new Vector3f(value);
		_scaling.get(_scalingBuffer);
	}
	
	public void updateScaling(float x, float y, float z)
	{
		_scaling = new Vector3f(x,z,y);
		_scaling.get(_scalingBuffer);
	}
	
	public void updateLightPosition(Vector3f position)
	{
		if(lightPositionBuffer.capacity()<3)
			lightPositionBuffer = GLBuffers.newDirectFloatBuffer(3);
		lightPosition = new Vector3f(position);
		lightPosition.get(lightPositionBuffer);
	}
	
	public void updateLightImpact(float impact)
	{
		lightImpact = impact;
	}
	
	public void updateDimensions(Vector3f value)
	{
		_dimensions = new Vector3f(value);
		_dimensions.get(_dimensionsBuffer);
	}
	
	public void updateSpacing(Float[] spacing)
	{
		_spacing.set(spacing[0], spacing[1], spacing[2]);
		_spacing.get(_spacingBuffer);
	}
	
	public void updateClippingCentre(Vector4f centre)
	{
		_clipPlaneCentre = centre;
		_clipPlaneCentre.get(_clipPlaneCentreBuffer);
	}
	
	public void updateClippingNormal(Vector3f normal)
	{
		_clipPlaneNormal = normal;
		_clipPlaneNormal.get(_clipPlaneNormalBuffer);
	}
	
	public void updateClippingMatrix(Matrix4f mat)
	{
		_clipPlaneMatrix = mat;
		if((_clipPlaneMatrix!=null) && (_clipPlaneMatrixBuffer!=null))
			_clipPlaneMatrix.get(_clipPlaneMatrixBuffer);
	}
	
	public void updateLowCut(float value)
	{
		_lowCut = value;
	}
	
	public void updateHighCut(float value)
	{
		_highCut = value;
	}
	
	public void updateNormalTextureIndex(int index)
	{
		_normalTexIndex = index;
		_useNormals = true;
	}

	@Override
	public ShaderProgram useShader( GL3 gl )
	{
		ShaderProgram _top = super.useShader(gl);
		gl.glUniform1f(maxValueUniform, _maxValue);
		gl.glUniform1f(lowCutUniform, _lowCut);
		gl.glUniform1f(highCutUniform, _highCut);
		gl.glUniformMatrix4fv(pvmUniform, 1, false, _pvmBuffer);
		gl.glUniformMatrix4fv(modelMatrixUniform, 1, false, _modelMatrixBuffer);
		gl.glUniformMatrix4fv(viewMatrixUniform, 1, false, _viewMatrixBuffer);
		gl.glUniformMatrix4fv(projectionMatrixUniform, 1, false, _projectionMatrixBuffer);
		gl.glUniform1i(lutUniform, _lutTexIndex);;
		gl.glUniform3fv(spacingUniform, 1, _spacingBuffer);
		gl.glUniform3fv(scalingUniform, 1, _scalingBuffer);
		gl.glUniform3fv(dimensionsUniform, 1, _dimensionsBuffer);
		gl.glUniform1i(numGaussCurvesUniform, _numGaussCurves);
		gl.glUniform3fv(gaussParameterUniform, 1, _gaussParameterBuffer);
		gl.glUniform1i(clipPlaneSwitchUniform, _clipPlaneSwitch);
		gl.glUniform4fv(clipPlaneCentreUniform, 1, _clipPlaneCentreBuffer);
		gl.glUniform3fv(clipPlaneNormalUniform, 1, _clipPlaneNormalBuffer);
		gl.glUniformMatrix4fv(clipPlaneMatrixUniform, 1, false, _clipPlaneMatrixBuffer);
		
		if(_useLighting) {
			gl.glUniform3fv(lightPositionUniform, 1, lightPositionBuffer);
			gl.glUniform1f(lightImpactUniform, lightImpact);
		}
		if(_useNormals)
			gl.glUniform1i(normalTexUniform, _normalTexIndex);
		return _top;
	}
}
