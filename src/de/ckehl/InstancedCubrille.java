package de.ckehl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
//import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jogamp.opengl.gl4.GL4bcImpl;

import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;


import com.jogamp.common.nio.Buffers;
import com.jogamp.opencl.CLCommandQueue;
//import com.jogamp.opencl.CLContext;
//import com.jogamp.opencl.gl.CLGLContext;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GL4bc;
import com.jogamp.opengl.util.GLBuffers;

class CenterIndexEntry implements Comparable<CenterIndexEntry>
{

	
	private Vector3f _center;
	private int _index;
	private float _viewDistance;
	
	public CenterIndexEntry(Vector3f center, int index, float viewDistance)
	{
		_center = center;
		_index = index;
		_viewDistance = viewDistance;
	}
	
	public Vector3f center()
	{
		return _center;
	}
	
	public void setCenter(Vector3f center)
	{
		_center = center;
	}
	
	public int index()
	{
		return _index;
	}
	
	public void setIndex(int index)
	{
		_index = index;
	}
	
	public float viewDistance()
	{
		return _viewDistance;
	}
	
	public void setViewDistance(float distance)
	{
		_viewDistance = distance;
	}
	
	@Override
	public int compareTo(CenterIndexEntry lhs)
	{
		return Float.compare(_viewDistance, lhs.viewDistance());
	}
	
	static class CenterIndexComparator implements Comparator<CenterIndexEntry>
	{

		@Override
		public int compare(CenterIndexEntry o1, CenterIndexEntry o2) {
			// TODO Auto-generated method stub
			return Float.compare(o1.viewDistance(), o2.viewDistance());
		}
	}
	
	public String toString()
	{
		return "c: ("+_center.toString(new DecimalFormat( "#,###,###,##0.00" ))+"); i: "+Integer.toString(_index)+"; d: "+Float.toString(_viewDistance)+";";
	}
}

public class InstancedCubrille extends GeometryContainer implements ViewPlaneInterface, LightInformationInterface, NormalUpdateInterface, RaySelectionInterface, HistogramSelectionInterface, GaussCurveInterface, LUTinterface {
	public static final int RENDERMODE_COMMON = 0;
	public static final int RENDERMODE_PRECOMPUTE = 1;
	public static final int RENDERMODE_ORDERCORRECT = 2;
	public static final int RENDERMODE_ORDER_GPU = 3;
	//public static int renderMode = RENDERMODE_PRECOMPUTE;

	public static int renderMode = RENDERMODE_ORDERCORRECT;
	
	// explanation: OpenGL only digests texture dimension sizes as intergers-of-4; if we say we always use 3 bytes, problem seems solved
	private static final int NORMDEPTH = 4;
	
	protected static final int NUM_BUFFERS = 5;
	protected static final int NUM_TEXTURES = 1;
	
	protected int _num_cubrilles = 0;
	protected FloatBuffer _vertices = null;
	protected FloatBuffer _normals = null;
	protected ByteBuffer _normalTextureData = null;
	protected FloatBuffer _values = null;
	protected IntBuffer _indices = null;
	protected FloatBuffer _visibility = null;
	protected Float _dimensions[] = null;
	protected float _maxValue = 65535.0f;
	protected Vector3f _lightVector;
	protected float _lightImpact;
	protected Vector3f _scaling;
	protected IntBuffer _vbos;
	protected IntBuffer _vao;
	protected IntBuffer _tbos;
	protected InstancedCubrilleShader _shader = null;
	protected DistanceSortKernel _kernel = null;
	protected String basePath = "instancedCubrilles";
	protected String clPath = "res/kernels/sorting.cl";
	
	protected ProjectionViewInterface _projection = null;
	protected ModelViewProjectionInterface _globalMatrix = null;
	
	protected PlaneViewpoint _currentPlane = PlaneViewpoint.FRONT;
	
	protected boolean _hasNormal = false;
	protected boolean _useNormals = false;
	protected boolean _useLighting = false;
	protected boolean _buffersInitialised = false;
	protected boolean _texturesInitialised = false;
	
	protected CenterIndexEntry _elementRefList[] = null;
	protected int _vIndices[] = null;
	private int _runEntry = 0;
	
	protected boolean raySelection = false;
	
	// information for using a volume texture
	protected int _volWidth, _volHeight, _volDepth;
	
	protected boolean _gaussUpdated = true;
	
	public InstancedCubrille(int num_elements, ModelViewProjectionInterface globalMatrixInterface, ProjectionViewInterface projector)
	{
		_buffersInitialised = false;
		_num_cubrilles = num_elements;
		_dimensions = new Float[3];
		_scaling = new Vector3f(0.5f,0.5f,0.5f);
		_vertices = GLBuffers.newDirectFloatBuffer(_num_cubrilles*3);
		_values = GLBuffers.newDirectFloatBuffer(_num_cubrilles);
		_indices = GLBuffers.newDirectIntBuffer(_num_cubrilles);
		_visibility = GLBuffers.newDirectFloatBuffer(_num_cubrilles);
		//_values = Buffers.newDirectFloatBuffer(_num_cubrilles);
		_vbos = Buffers.newDirectIntBuffer(NUM_BUFFERS);
		_tbos = Buffers.newDirectIntBuffer(NUM_TEXTURES);
		_vao = Buffers.newDirectIntBuffer(1);
		_shader = new InstancedCubrilleShader(basePath, basePath, basePath);
		_kernel = new DistanceSortKernel(clPath);
		//System.out.println("ShaderFile: "+getClass().getResource(clPath).getFile());
		_globalMatrix = globalMatrixInterface;
		_projection = projector;
		_lightVector = new Vector3f();
		_lightImpact = 1.0f;
		
		_elementRefList = new CenterIndexEntry[_num_cubrilles];
		
		_currentPlane = PlaneViewpoint.FRONT;
	}
	
	public void setGlobalMatrixInterface(ModelViewProjectionInterface globalMatrixInterface)
	{
		_globalMatrix = globalMatrixInterface;
	}
	
	@Override
	public void dispose(GL3 gl3, CLCommandQueue queue)
	{
		if(queue!=null)
			_kernel.dispose(queue);
		_shader.dispose(gl3);
		if(_buffersInitialised)
		{
			gl3.glDeleteBuffers(NUM_BUFFERS, _vbos);
			gl3.glDeleteVertexArrays(1, _vao);
		}
		if(_texturesInitialised)
		{
			gl3.glDeleteTextures(NUM_TEXTURES, _tbos);
		}
		_vbos = null;
	}
	
	public void add(float x, float y, float z, short value)
	{
		try
		{
		_vertices.put(x);
		_vertices.put(y);
		_vertices.put(z);
		_values.put((float)value/65535.0f);
		_visibility.put(1f);
		if(renderMode==RENDERMODE_ORDERCORRECT)
			_elementRefList[_runEntry] =  new CenterIndexEntry(new Vector3f(x,y,z), _runEntry, 0f);
		_runEntry++;
		}
		catch(Exception e)
		{
			Vector3f v = new Vector3f(x,y,z);
			System.out.println("Failed to add element "+v.toString(new DecimalFormat( "#,###,###,##0.000" ))+" at index "+Integer.toString(_runEntry)+" of "+Integer.toString(_num_cubrilles)+".");
		}
	}
	
	public void setMaxValue(short value)
	{
		_maxValue = (float)value;
		if(_shader!=null)
			_shader.updateMaxValue((float)value);
	}
	
	public void setDimensions(short dims[])
	{
		if(dims.length>=3)
		{
			_dimensions[0] = new Float(dims[0]);
			_dimensions[1] = new Float(dims[1]);
			_dimensions[2] = new Float(dims[2]);
		}
	}
	
	public void setScaling(float sX, float sY, float sZ)
	{
		_scaling = new Vector3f(sX,sY,sZ);
		if(_shader!=null)
		{
			_shader.updateScaling(sX,sY,sZ);
		}
	}
	
	private void computeIndexList()
	{
		//CenterIndexEntry arr[] = new CenterIndexEntry[_elementRefList.size()];
		//_elementRefList.toArray(arr);
		Vector4f eye_world = _projection.getViewPointWorld();
		Vector3f _direction = new Vector3f();
		//for(int i=0; i<_elementRefList.length; i++)
		for(int i=0; i<_runEntry; i++)
		{
			try
			{
			//CenterIndexEntry item = _elementRefList.get(i);
			//-------------------------
			// determine view distance
			//-------------------------
			_elementRefList[i].center().sub(new Vector3f(eye_world.x, eye_world.y, eye_world.z), _direction);
			_elementRefList[i].setViewDistance(_direction.lengthSquared());
			//item.setViewDistance(_direction.lengthSquared());
			//--------------------------
			// view distance determined
			//--------------------------
			//_elementRefList.set(i, item);
			}
			catch(Exception e)
			{
				System.out.println("Error ...");
				if(_elementRefList!=null)
					System.out.println("Element Array Size: "+Integer.toString(_elementRefList.length)+"; Element: "+Integer.toString(i));
				else
					System.out.println("Element Array is NULL.");
				if(_elementRefList[i]!=null)
					System.out.println(_elementRefList[i].toString());
				else
					System.out.println("Element ("+Integer.toString(i)+") is NULL.");
				if(eye_world!=null)
					System.out.println(eye_world.toString(AppWindow._vecStdFmt));
				else
					System.out.println("Eye coord vector is NULL");
				if(_direction!=null)
					System.out.println(_direction.toString(AppWindow._vecStdFmt));
				else
					System.out.println("Direction vector is NULL");
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		Arrays.parallelSort(_elementRefList);
		//Collections.sort(_elementRefList);
		//Collections.reverse(_elementRefList);
		
		if(_indices == null)
		{
			GLBuffers.newDirectIntBuffer(_elementRefList.length);
		}
		_indices.rewind();

		//for(CenterIndexEntry CIobject : _elementRefList)
		//for(CenterIndexEntry CIobject : arr)
		for(int i=0; i<_elementRefList.length; i++)
		{
			_indices.put(_elementRefList[(_elementRefList.length-1)-i].index());
		}
		_indices.rewind();
	}
	
	/**
	 * re-computed the index array based on the current view plane
	 * BASE ASSUMPTION: points are put in for x-y-z order
	 * @param gl3
	 */
	private void setupIndexLists()
	{
		
		//_indices.clear();
		_indices.rewind();
		switch(_currentPlane)
		{
			case FRONT:
			{
				for(int k = _dimensions[2].intValue()-1; k>-1 ; k--)
				{
					for(int j = 0; j< _dimensions[1].intValue(); j++)
					{
						for(int i = 0; i< _dimensions[0].intValue(); i++)
						{
							_indices.put(i*(_dimensions[2].intValue()*_dimensions[1].intValue()) + j*_dimensions[2].intValue() + k);
						}
					}
				}
				break;
			}
			case BACK:
			{
				for(int k = 0; k< _dimensions[2].intValue(); k++)
				{
					for(int j = 0; j< _dimensions[1].intValue(); j++)
					{
						for(int i = 0; i<_dimensions[0].intValue() ; i++)
						{
							_indices.put(i*(_dimensions[2].intValue()*_dimensions[1].intValue()) + j*_dimensions[2].intValue() + k);
						}
					}
				}
				break;
			}
			case LEFT:
			{
				for(int i = 0; i<_dimensions[0].intValue() ; i++)
				{
					for(int j = 0; j< _dimensions[1].intValue(); j++)
					{
						for(int k = 0; k< _dimensions[2].intValue(); k++)
						{
							_indices.put(i*(_dimensions[2].intValue()*_dimensions[1].intValue()) + j*_dimensions[2].intValue() + k);
						}
					}
				}
				break;
			}
			case RIGHT:
			{
				for(int i = (_dimensions[0].intValue()-1); i>-1 ; i--)
				{
					for(int j = 0; j< _dimensions[1].intValue(); j++)
					{
						for(int k = 0; k< _dimensions[2].intValue(); k++)
						{
							_indices.put(i*(_dimensions[2].intValue()*_dimensions[1].intValue()) + j*_dimensions[2].intValue() + k);
						}
					}
				}
				break;
			}
			case TOP:
			{
				for(int j = (_dimensions[1].intValue()-1); j>-1 ; j--)
				{
					for(int k = 0; k< _dimensions[2].intValue(); k++)
					{
						for(int i = 0; i< _dimensions[0].intValue(); i++)
						{
							_indices.put(i*(_dimensions[2].intValue()*_dimensions[1].intValue()) + j*_dimensions[2].intValue() + k);
						}
					}
				}
				break;
			}
			case BOTTOM:
			{
				for(int j = 0; j<_dimensions[1].intValue() ; j++)
				{
					for(int k = 0; k< _dimensions[2].intValue(); k++)
					{
						for(int i = 0; i< _dimensions[0].intValue(); i++)
						{
							_indices.put(i*(_dimensions[2].intValue()*_dimensions[1].intValue()) + j*_dimensions[2].intValue() + k);
						}
					}
				}
				break;
			}
			default:
			{
				break;
			}
		}

		//gl3.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void render(GL3 gl3) {
		// TODO Auto-generated method stub
		
		super.render(gl3);
		
    	if(_isUpdated)
    	{
    		_shader.useShader(gl3);
			if(_globalMatrix!=null)
			{
				_shader.updateModelViewProjectionMatrix(_globalMatrix.getModelViewProjectionMatrix());
				_shader.updateModelMatrix(_globalMatrix.getModelMatrix());
				_shader.updateViewMatrix(_globalMatrix.getViewMatrix());
				_shader.updateProjectionMatrix(_globalMatrix.getProjection());
			}
			_shader.updateLUTtexIndexUniform(1);
			
			// ===================================== //
			//_shader.updateGaussTexIndexUniform(4);
			//_shader.updateNumGaussCurves(0);
			// ===================================== //
			
			_shader.updateSpacing(AppWindow.getDataStorage().getSpacing());
	        renderVBO(gl3);
	        _shader.dontUseShader(gl3);
    	}
	}
	
	@Override
	public void compute(CLCommandQueue queue)
	{
		super.compute(queue);
    	if(_isUpdated)
    	{
    		if(renderMode==RENDERMODE_ORDER_GPU)
    		{
    			Vector4f eye_world = _projection.getViewPointWorld();
    			_kernel.setEyeCoordinate(eye_world);
    			_kernel.compute(queue);
    		}
    	}
	}
	
	@Override
	public void setup(GL3 gl3, CLCommandQueue queue) {
		// TODO Auto-generated method stub
		if(_isUpdated==false)
		{
			if(_printSetupLine)
				System.out.println("updating "+this.toString()+"("+Long.toString(_ID)+") ...");
			
			if(_buffersInitialised==false)
			{
				gl3.glGenVertexArrays(1, _vao);
				gl3.glGenBuffers(NUM_BUFFERS, _vbos);
			}
			if(_texturesInitialised==false)
			{
				gl3.glGenTextures(NUM_TEXTURES, _tbos);
			}
			setupVBOs(gl3);
			
			
			switch (renderMode) {
			case RENDERMODE_COMMON:
				{
					_indices.rewind();
					for(int i = 0; i<_dimensions[0].intValue() ; i++)
					{
						for(int j = 0; j< _dimensions[1].intValue(); j++)
						{
							for(int k = 0; k< _dimensions[2].intValue(); k++)
							{
								_indices.put(i*(_dimensions[2].intValue()*_dimensions[1].intValue()) + j*_dimensions[2].intValue() + k);
							}
						}
					}
					_indices.rewind();
					break;
				}
			case RENDERMODE_PRECOMPUTE:
				setupIndexLists();
				break;
			case RENDERMODE_ORDERCORRECT:
				computeIndexList();
				break;
			case RENDERMODE_ORDER_GPU:
				{
					gl3.glFinish();
					if(_vIndices==null)
					{
						_vIndices = new int[_num_cubrilles];
						for(int i = 0; i < _num_cubrilles; i++)
							_vIndices[i] = i;
					}
					if(_kernel.isInitialized()==false)
						_kernel.init(queue, _num_cubrilles, _vbos.get(0), _vbos.get(4), _vIndices);
					break;
				}
			default:
				{
					_indices.rewind();
					for(int i = 0; i<_dimensions[0].intValue() ; i++)
					{
						for(int j = 0; j< _dimensions[1].intValue(); j++)
						{
							for(int k = 0; k< _dimensions[2].intValue(); k++)
							{
								_indices.put(i*(_dimensions[2].intValue()*_dimensions[1].intValue()) + j*_dimensions[2].intValue() + k);
							}
						}
					}
					_indices.rewind();
					break;
				}
			}

			if(_hasNormal)
			{
				if(_useLighting)
				{
					if(_normalTextureData==null)
					{
						basePath = "instancedCubrillesLighting";
					}
					else
					{
						basePath = "instancedCubrillesLightingNTex";
					}
				}
				else if(_useNormals)
				{
					if(_normalTextureData==null)
					{
						basePath = "instancedCubrillesNormals";
					}
					else
					{
						basePath = "instancedCubrillesNormalTextures";
					}
				}
				else
					basePath = "instancedCubrilles";
				
				System.out.println(basePath);
				_shader = new InstancedCubrilleShader(basePath, basePath, basePath);
				
			}
			
			_shader.init(gl3);
			if(_useLighting)
			{
				gl3.glEnable(GL2.GL_LIGHT0);
				_shader.EnableLighting(gl3, _lightVector);
			}
			else
			{
				gl3.glDisable(GL2.GL_LIGHT0);
				_shader.DisableLighting(gl3);
			}
			
			//_shader.updateMaxValue(1.0f);
			//_shader.updateMaxValue(4095.0f);
			_shader.updateMaxValue(_maxValue);
			if(_globalMatrix!=null)
			{
				_shader.updateModelViewProjectionMatrix(_globalMatrix.getModelViewProjectionMatrix());
				_shader.updateModelMatrix(_globalMatrix.getModelMatrix());
				_shader.updateViewMatrix(_globalMatrix.getViewMatrix());
				_shader.updateProjectionMatrix(_globalMatrix.getProjection());
			}
			_shader.updateSpacing(AppWindow.getDataStorage().getSpacing());
			_shader.updateScaling(_scaling.x, _scaling.y, _scaling.z);
			_shader.updateLUTtexIndexUniform(1);
			//_shader.updateGaussTexIndexUniform(0);
			if(_normalTextureData!=null)
			{
				_shader.updateNormalTextureIndex(3);
				//_shader.updateNormalTextureIndex(2);
				//if(_tbos!=null) {
				//	System.out.printf("NORMtex: %d\n", _tbos.get(0));
				//	_shader.updateNormalTextureIndex(_tbos.get(0));
				//}
				Vector3f dims = new Vector3f(AppWindow.getDataStorage().getDimensionsInt()[0], AppWindow.getDataStorage().getDimensionsInt()[1], AppWindow.getDataStorage().getDimensionsInt()[2]);
				_shader.updateDimensions(dims);
			}
			setCurrentShaderGL3(_shader);

	        if(_printSetupLine)
	        	System.out.println(this.toString()+"("+Long.toString(_ID)+"): up-to-date");
	        _isUpdated = true;
		}
		
		super.setup(gl3, queue);


	}
	
	private void setupVBOs(GL3 gl3)
	{
		_vertices.rewind();
		if((_useNormals) && (_normals!=null))
			_normals.rewind();
		_values.rewind();
		_indices.rewind();
		_visibility.rewind();
		
		gl3.glBindVertexArray(_vao.get(0));

		
		
		gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, _vbos.get(0));
		gl3.glBufferData(GL.GL_ARRAY_BUFFER, _num_cubrilles*3*VersionHelper.FloatBYTES(), _vertices, GL.GL_STATIC_DRAW);
		gl3.glEnableVertexAttribArray(0);
		gl3.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0l);
		gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

		
		gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, _vbos.get(1));
		gl3.glBufferData(GL.GL_ARRAY_BUFFER, _num_cubrilles*VersionHelper.FloatBYTES(), _values, GL.GL_STATIC_DRAW);
		gl3.glEnableVertexAttribArray(1);
		gl3.glVertexAttribPointer(1, 1, GL.GL_FLOAT, true, 0, 0l);
		gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

		gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, _vbos.get(2));
		gl3.glBufferData(GL.GL_ARRAY_BUFFER, _num_cubrilles*VersionHelper.FloatBYTES(), _visibility, GL.GL_STATIC_DRAW);
		gl3.glEnableVertexAttribArray(2);
		gl3.glVertexAttribPointer(2, 1, GL.GL_FLOAT, true, 0, 0l);
		gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		
		if((_hasNormal) && (_normals!=null))
		{
			gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, _vbos.get(3));
			gl3.glBufferData(GL.GL_ARRAY_BUFFER, _num_cubrilles*3*VersionHelper.FloatBYTES(), _normals, GL.GL_STATIC_DRAW);
			gl3.glEnableVertexAttribArray(3);
			gl3.glVertexAttribPointer(3, 3, GL.GL_FLOAT, true, 0, 0l);
			gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		}
		

		
		gl3.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, _vbos.get(4));
		gl3.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, _num_cubrilles*VersionHelper.IntBYTES(), _indices, GL.GL_DYNAMIC_DRAW);
		gl3.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		
		
		gl3.glBindVertexArray(0);
		
		if((_hasNormal) && (_normalTextureData!=null))
		{
			gl3.glEnable(GL2.GL_TEXTURE_3D);
			gl3.glActiveTexture(GL.GL_TEXTURE3);
	    	gl3.glBindTexture(GL2.GL_TEXTURE_3D, _tbos.get(0));
	    	
	    	//gl3.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
	    	gl3.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
	    	gl3.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
	    	gl3.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_REPEAT);
	    	//gl2.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
	    	//gl2.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
	    	//gl2.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_CLAMP);
	    	gl3.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	    	gl3.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	    	if(NORMDEPTH==4)
	    		gl3.glTexImage3D(GL2.GL_TEXTURE_3D, 0, GL2.GL_RGB, _volWidth, _volHeight, _volDepth, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, _normalTextureData);
	    	else
	    		gl3.glTexImage3D(GL2.GL_TEXTURE_3D, 0, GL2.GL_RGB, _volWidth, _volHeight, _volDepth, 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, _normalTextureData);
	    	//gl3.glTexImage3D(GL2.GL_TEXTURE_3D, 0, GL2.GL_RGB, _volWidth, _volHeight, _volDepth, 0, GL2.GL_RGB, GL.GL_BYTE, _normalTextureData);
	    	//gl2.glEnable(GL2.GL_TEXTURE_3D);
	    	gl3.glBindTexture(GL2.GL_TEXTURE_3D, 0);
	    	gl3.glDisable(GL2.GL_TEXTURE_3D);
	    	_texturesInitialised = true;
		}
		
		_buffersInitialised = true;
	}
	
	private void renderVBO(GL3 gl3)
	{
		gl3.glBindVertexArray(_vao.get(0));
		gl3.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, _vbos.get(0));
		gl3.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0l);
		gl3.glEnableVertexAttribArray(0);
		

		gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, _vbos.get(1));
		gl3.glVertexAttribPointer(1, 1, GL.GL_FLOAT, true, 0, 0l);
		gl3.glEnableVertexAttribArray(1);

		gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, _vbos.get(2));
		if(raySelection)
		{
			//gl3.glBufferSubData(GL.GL_ARRAY_BUFFER, 0, _num_cubrilles*VersionHelper.FloatBYTES(), _visibility);
			gl3.glBufferData(GL.GL_ARRAY_BUFFER, _num_cubrilles*VersionHelper.FloatBYTES(), _visibility, GL.GL_STATIC_DRAW);
			raySelection=false;
		}
		gl3.glVertexAttribPointer(2, 1, GL.GL_FLOAT, true, 0, 0l);
		gl3.glEnableVertexAttribArray(2);
		
		if((_hasNormal) && (_normals!=null))
		{
			gl3.glEnableClientState(GL2.GL_NORMAL_ARRAY);
			gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, _vbos.get(3));
			gl3.glVertexAttribPointer(3, 3, GL.GL_FLOAT, true, 0, 0l);
			gl3.glEnableVertexAttribArray(3);
		}

		if(_texturesInitialised==true)
		{
			gl3.glEnable(GL2.GL_TEXTURE_3D);
			gl3.glActiveTexture(GL.GL_TEXTURE3);
			gl3.glBindTexture(GL2.GL_TEXTURE_3D, _tbos.get(0));
		}


        gl3.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, _vbos.get(4));
        gl3.glBufferSubData(GL.GL_ELEMENT_ARRAY_BUFFER, 0, _num_cubrilles*VersionHelper.IntBYTES(), _indices);
		gl3.glDrawElements(GL.GL_POINTS, _num_cubrilles, GL.GL_UNSIGNED_INT, 0l);
		gl3.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
        
        gl3.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        
        if((_hasNormal) && (_normals!=null))
		{
			gl3.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		}
		
		if(_texturesInitialised==true)
		{
			gl3.glBindTexture(GL2.GL_TEXTURE_3D, 0);
			gl3.glDisable(GL2.GL_TEXTURE_3D);
		}
        gl3.glBindVertexArray(0);
	}

	@Override
	public String toString()
	{
		return "InstancedCubrille";
	}

	@Override
	public void setPlaneViewpoint(PlaneViewpoint p) {
		// TODO Auto-generated method stub
		_currentPlane = p;
	}

	@Override
	public PlaneViewpoint getPlaneViewpoint() {
		// TODO Auto-generated method stub
		return _currentPlane;
	}

	@Override
	public void update(GL3 gl3) {
		// TODO Auto-generated method stub
		switch (renderMode) {
		case RENDERMODE_COMMON:
			{
				break;
			}
		case RENDERMODE_PRECOMPUTE:
			setupIndexLists();
			//System.out.println(_currentPlane.toString());
			break;
		case RENDERMODE_ORDERCORRECT:
			computeIndexList();
			//System.out.println(_currentPlane.toString());
			break;
		case RENDERMODE_ORDER_GPU:
			{

				break;
			}
		default:
			{
				break;
			}
		}

		
	}

	@Override
	public void update(GL2 gl2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector3f getLightPosition() {
		// TODO Auto-generated method stub
		return _lightVector;
	}

	@Override
	public void setLightPosition(Vector3f position) {
		// TODO Auto-generated method stub
		_lightVector = position;
	}

	@Override
	public void updateLightPosition() {
		// TODO Auto-generated method stub
		_shader.updateLightPosition(_lightVector);
	}

	@Override
	public void EnableLighting() {
		// TODO Auto-generated method stub
		_useLighting = true;
	}

	@Override
	public void DisableLighting() {
		// TODO Auto-generated method stub
		_useLighting = false;
	}

	@Override
	public void setRayInformation(List<Vector3i> intersections) {
		// TODO Auto-generated method stub
		//System.out.println("# intersections: "+Integer.toString(intersections.size()));
		_visibility.rewind();
		//System.out.printf("Dimensions: %d %d %d\n", _dimensions[0].intValue(), _dimensions[1].intValue(), _dimensions[2].intValue());
		for(int i = 0; i < _dimensions[0].intValue(); i++)
		{
			for(int j = 0; j < _dimensions[1].intValue(); j++)
			{
				for(int k = 0; k < _dimensions[2].intValue(); k++)
				{
					if(intersections.contains(new Vector3i(i,j,k)))
					{
						_visibility.put(1f);
					}
					else
					{
						_visibility.put(0.05f);
					}
				}
			}
		}
		_visibility.rewind();
		raySelection = true;
	}

	@Override
	public void resetRayInformation() {
		// TODO Auto-generated method stub
		_visibility.rewind();
		for(int i = 0; i < _dimensions[0]; i++)
		{
			for(int j = 0; j < _dimensions[1]; j++)
			{
				for(int k = 0; k < _dimensions[2]; k++)
				{
					_visibility.put(1f);
				}
			}
		}
		_visibility.rewind();
		raySelection = true;
	}

	@Override
	public void center(int value, int occurence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markValues(Vector2i range) {
		// TODO Auto-generated method stub
		//System.out.println(range.toString(new DecimalFormat( "#,###,###,##0.00" )));
		if(_shader!=null)
		{
			_shader.updateLowCut((float)range.x);
			_shader.updateHighCut((float)range.y);
		}
	}

	@Override
	public void resetHistogramSelection() {
		// TODO Auto-generated method stub
		if(_shader!=null)
		{
			_shader.updateLowCut(0f);
			_shader.updateHighCut(_maxValue);
		}		
	}

	@Override
	public boolean hasNormal() {
		// TODO Auto-generated method stub
		return _hasNormal;
	}

	@Override
	public void setNormals(Vector3f[][][] normals) {
		// TODO Auto-generated method stub
		if(AppWindow.getDataStorage().hasNormals())
		{
			if(_normals == null)
				_normals = GLBuffers.newDirectFloatBuffer(_num_cubrilles*3);
			else
				_normals.rewind();
			
			normals = AppWindow.getDataStorage().getNormals();
			
			if((normals!=null) && (normals.length>2))
			{
				for(int l = (int)_scaling.x; l< normals.length; l+=(int)(_scaling.x*2))
				{
					for(int m = (int)_scaling.y; m< normals[l].length; m+=(int)(_scaling.y*2))
					{
						for(int n =(int)_scaling.z; n< normals[l][m].length; n+=(int)(_scaling.z*2))
						{
							_normals.put(normals[l][m][n].x);
							_normals.put(normals[l][m][n].y);
							_normals.put(normals[l][m][n].z);
						}
					}
				}
			}
			System.out.println("[setup] original Normal array size: "+Integer.toString(_normals.capacity()/3));
			_hasNormal = true;
		}
		else if(AppWindow.getDataStorage().hasNormalTexture())
		{
			if(_normals!=null)
				_normals.clear();
			_normals = null;
			
			if(_normalTextureData == null)
			{
				int tex_alloc = _num_cubrilles*NORMDEPTH;
				/*
				int tex_alloc = _num_cubrilles*3;
				if(((AppWindow.getDataStorage().getDimensionsInt()[0]%4)!=0) || ((AppWindow.getDataStorage().getDimensionsInt()[1]%4)!=0) || ((AppWindow.getDataStorage().getDimensionsInt()[2]%4)!=0))
				{
					int tmpW = (int)(Math.ceil(((float)(AppWindow.getDataStorage().getDimensionsInt()[0]))/4.0f)*4);
					int tmpH = (int)(Math.ceil(((float)(AppWindow.getDataStorage().getDimensionsInt()[1]))/4.0f)*4);
					int tmpD = (int)(Math.ceil(((float)(AppWindow.getDataStorage().getDimensionsInt()[2]))/4.0f)*4);
					System.out.println("Alloc dim: ("+Integer.toString(tmpW)+"*"+Integer.toString(tmpH)+"*"+Integer.toString(tmpD)+")");
					tex_alloc = tmpW*tmpH*tmpD*3;
				}
				*/
				_normalTextureData = GLBuffers.newDirectByteBuffer(tex_alloc);
			}
			else
				_normalTextureData.rewind();

			if(AppWindow.getDataStorage().getNormalTextureData()!=null)
			{
				Vector3i dimVec = new Vector3i(AppWindow.getDataStorage().getDimensionsInt()[0], AppWindow.getDataStorage().getDimensionsInt()[1], AppWindow.getDataStorage().getDimensionsInt()[2]);
				System.out.println("Dimensions: "+dimVec.toString()+"; Scaling: "+_scaling.toString());
				_volWidth = (int)Math.ceil((float)(AppWindow.getDataStorage().getDimensionsInt()[0]-_scaling.x)/(_scaling.x*2f));
				_volHeight = (int)Math.ceil((float)(AppWindow.getDataStorage().getDimensionsInt()[1]-_scaling.y)/(_scaling.y*2f));
				_volDepth = (int)Math.ceil((float)(AppWindow.getDataStorage().getDimensionsInt()[2]-_scaling.z)/(_scaling.z*2f));
				System.out.println("(w*h*d) = ("+Integer.toString(_volWidth)+"*"+Integer.toString(_volHeight)+"*"+Integer.toString(_volDepth)+")");
				//_volWidth = 0;
				//_volHeight = 0;
				//_volDepth = 0;
				
				int SCALEX = (int)(_scaling.x*2f);
				int SCALEY = (int)(_scaling.y*2f);
				int SCALEZ = (int)(_scaling.z*2f);
				
				System.out.println("(Sw*Sh*Sd) = ("+Integer.toString(SCALEX)+"*"+Integer.toString(SCALEY)+"*"+Integer.toString(SCALEZ)+")");
				
				int idx = 0, cellnum = 0;
				//for(int n = (int)Math.round(SCALEZ/2); n< AppWindow.getDataStorage().getDimensionsInt()[2]; n+=SCALEZ)
				for(int n = SCALEZ/2; n< AppWindow.getDataStorage().getDimensionsInt()[2]; n+=SCALEZ)
				{
					for(int m = SCALEY/2; m< AppWindow.getDataStorage().getDimensionsInt()[1]; m+=SCALEY)
					{
						for(int l = SCALEX/2; l< AppWindow.getDataStorage().getDimensionsInt()[0]; l+=SCALEX)
						{
							idx = AppWindow.getDataStorage().getTextureAddress(l, m, n);
							_normalTextureData.put(AppWindow.getDataStorage().getNormalTextureData()[idx+0]);
							_normalTextureData.put(AppWindow.getDataStorage().getNormalTextureData()[idx+1]);
							_normalTextureData.put(AppWindow.getDataStorage().getNormalTextureData()[idx+2]);
							if(NORMDEPTH==4)
								_normalTextureData.put((byte) 255);
							cellnum++;
						}
					}
				}
				//int bytesNecessary = GL4bcImpl.imageSizeInBytes(GL2.GL_RGB,  GL2.GL_UNSIGNED_INT, _volWidth, _volHeight, _volDepth, false);
				//System.out.println(Integer.toString(bytesNecessary));
				System.out.println("sizeof(buffer): "+Integer.toString(_normalTextureData.capacity())+", # indices: "+Integer.toString(cellnum)+", # cuberilles: "+Integer.toString(_num_cubrilles)+"; num bytesPerPixel: "+Integer.toString(GLBuffers.bytesPerPixel(GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE)));
				_normalTextureData.rewind();
			}
			_hasNormal = true;
		}
	}

	@Override
	public Vector3f[][][] getNormals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector3f getNormal(Vector3i index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ActivateNormalVis() {
		// TODO Auto-generated method stub
		_useNormals = true;
	}

	@Override
	public void DeactivateNormalVis() {
		// TODO Auto-generated method stub
		_useNormals = false;
	}

	@Override
	public boolean hasNormals() {
		// TODO Auto-generated method stub
		return _hasNormal;
	}

	@Override
	public boolean usesNormals() {
		// TODO Auto-generated method stub
		return _useNormals;
	}

	@Override
	public float getLightImpact() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLightImpact(float lightImpact) {
		// TODO Auto-generated method stub
		_lightImpact = lightImpact;
	}

	@Override
	public void updateLightImpact() {
		// TODO Auto-generated method stub
		_shader.updateLightImpact(_lightImpact);
	}

	@Override
	public float[] getCurveParamArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurveParamArray(float[] gaussFuncParams, int paramsPerItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public short[] getCurveParamUShortArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurveParamUShortArray(short[] gaussFuncParams, int paramsPerItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNumberOfCurveParameters() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setNumberOfCurveParameters(int numCurveParameters) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNumberOfCurves() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setNumberOfCurves(int numCurves) {
		// TODO Auto-generated method stub
		if(_shader!=null)
			_shader.updateNumGaussCurves(numCurves);
	}

	@Override
	public boolean isUpdated() {
		// TODO Auto-generated method stub
		return _gaussUpdated;
	}

	@Override
	public void Update(boolean state) {
		// TODO Auto-generated method stub
		_gaussUpdated = state;
	}

	@Override
	public byte[] getLUT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLUT(byte[] lut) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLUTtexUnit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLUTtexUnit(int texUnit) {
		// TODO Auto-generated method stub
		System.out.printf("LUT texUnit:%d\n", texUnit);

		//if(_shader!=null)
		//	_shader.updateLUTtexIndexUniform(texUnit);
	}

	@Override
	public boolean isCurveSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Vector3d getGaussCurve(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGaussCurve(Vector3d gaussParameters) {
		// TODO Auto-generated method stub
		if(_shader!=null)
			_shader.updateGaussParameter(new Vector3f((float)gaussParameters.x, (float)gaussParameters.y, (float)gaussParameters.z));
	}

	@Override
	public Vector3d getSelectedGaussCurve() {
		// TODO Auto-generated method stub
		return null;
	}
}
