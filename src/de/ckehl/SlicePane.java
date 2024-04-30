package de.ckehl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class SlicePane extends Texture3DPane implements TextureMatrixInterface {
	protected Float[] _textureTranslate = null;
	private Matrix4f mTextureMatrix = null;
	static protected Texture2D _markerTexXY = null, _markerTexXZ = null, _markerTexYZ = null;

	public SlicePane(short slicePane) {
		//NUMBER_TEXTURE_ALLOCATE=2;
		super(slicePane);
		// TODO Auto-generated constructor stub
		_textureTranslate = new Float[3];
		_textureTranslate[0] = 0f; _textureTranslate[1] = 0f; _textureTranslate[2] = 0f;
		mTextureMatrix = new Matrix4f();
		
		switch (_activePane) {
			case XY_PANE:
			{
				vshaderFile = "planarTexturing_xy";
				fshaderFile = "planarTexturing_xy";
				break;
			}
			case XZ_PANE:
			{
				vshaderFile = "planarTexturing_xz";
				fshaderFile = "planarTexturing_xz";
				break;
			}
			case YZ_PANE:
			{
				vshaderFile = "planarTexturing_yz";
				fshaderFile = "planarTexturing_yz";
				break;
			}
			default:
			{
				vshaderFile = "planarTexturing_xy";
				fshaderFile = "planarTexturing_xy";
				break;
			}
		}
		_shader = new AASliceTextureShader(vshaderFile, fshaderFile);
		//_markerTexXY = new Texture2D();
		//_markerTexXZ = new Texture2D();
		//_markerTexYZ = new Texture2D();
	}

	/**
	 * 
	 * @param slicePane - tells the plane to render; XY_PANE(1), XZ_PANE(2) or YZ_PANE(4)
	 * @param headView - in a shared-context setup, tells if this is the (master) head view
	 */
	public SlicePane(short slicePane, boolean headView)
	{
		super(slicePane, headView);
		// TODO Auto-generated constructor stub
		_textureTranslate = new Float[3];
		_textureTranslate[0] = 0f; _textureTranslate[1] = 0f; _textureTranslate[2] = 0f;
		mTextureMatrix = new Matrix4f();
		
		
		switch (_activePane) {
			case XY_PANE:
			{
				vshaderFile = "planarTexturing_xy";
				fshaderFile = "planarTexturing_xy";
				break;
			}
			case XZ_PANE:
			{
				vshaderFile = "planarTexturing_xz";
				fshaderFile = "planarTexturing_xz";
				break;
			}
			case YZ_PANE:
			{
				vshaderFile = "planarTexturing_yz";
				fshaderFile = "planarTexturing_yz";
				break;
			}
			default:
			{
				vshaderFile = "planarTexturing_xy";
				fshaderFile = "planarTexturing_xy";
				break;
			}
		}
		
		_shader = new AASliceTextureShader(vshaderFile, fshaderFile);
		_markerTexXY = new Texture2D(_head);
		_markerTexXZ = new Texture2D(_head);
		_markerTexYZ = new Texture2D(_head);
	}
	
	@Override
	public void dispose(GL2 gl2)
	{
		super.dispose(gl2);
		_textureTranslate = null;
		mTextureMatrix = null;
		if(_markerTexXY!=null)
		{
			_markerTexXY.dispose(gl2);
			_markerTexXY = null;
		}
		if(_markerTexXZ!=null)
		{
			_markerTexXZ.dispose(gl2);
			_markerTexXZ = null;
		}
		if(_markerTexYZ!=null)
		{
			_markerTexYZ.dispose(gl2);
			_markerTexYZ = null;
		}
	}
	
	@Override
	public void setup(GL2 gl2)
	{
		super.setup(gl2);
		if(_head)
		{
			_markerTexXY.setBufferID(_tex3D[1]);
			_markerTexXY.setup(gl2);
			_markerTexXZ.setBufferID(_tex3D[2]);
			_markerTexXZ.setup(gl2);
			_markerTexYZ.setBufferID(_tex3D[3]);
			_markerTexYZ.setup(gl2);
		}
		mTextureMatrix.identity();
		
		if(_shader!=null)
		{
			_shader.updateTextureMatrixUniform(mTextureMatrix);
			_shader.updateMarkingTextureXYUniform(1);
			_shader.updateMarkingTextureXZUniform(2);
			_shader.updateMarkingTextureYZUniform(3);
		}
	}
	
	@Override
	public void render(GL2 gl2) {
		// TODO Auto-generated method stub
		mTextureMatrix.identity();
		try
		{
			mTextureMatrix.translate(_textureTranslate[0]/new Float(AppWindow.getDataStorage().getDimensions()[0]), _textureTranslate[1]/new Float(AppWindow.getDataStorage().getDimensions()[1]), _textureTranslate[2]/new Float(AppWindow.getDataStorage().getDimensions()[2]));
			//mTextureMatrix[3] = _textureTranslate[0]/(float)_dimensions[0];
			//mTextureMatrix[7] = _textureTranslate[1]/(float)_dimensions[1];
			//mTextureMatrix[11] = _textureTranslate[2]/(float)_dimensions[2];
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			System.out.println(mTextureMatrix);
			System.out.println(_textureTranslate);
			System.out.printf("size: %d\n", _textureTranslate.length);
			System.out.printf("values: (%f %f %f)\n", _textureTranslate[0], _textureTranslate[1], _textureTranslate[2]);
			System.out.println(AppWindow.getDataStorage().getDimensions());
			System.out.printf("size: %d\n", AppWindow.getDataStorage().getDimensions().length);
			System.out.printf("values: (%d %d %d)\n", AppWindow.getDataStorage().getDimensions()[0], AppWindow.getDataStorage().getDimensions()[1], AppWindow.getDataStorage().getDimensions()[2]);
		}
		_shader.updateTextureMatrixUniform(mTextureMatrix);
		
		gl2.glMatrixMode(GL.GL_TEXTURE);
		gl2.glLoadIdentity();
		try
		{
			gl2.glTranslatef(_textureTranslate[0]/new Float(AppWindow.getDataStorage().getDimensions()[0]), _textureTranslate[1]/new Float(AppWindow.getDataStorage().getDimensions()[1]), _textureTranslate[2]/new Float(AppWindow.getDataStorage().getDimensions()[2]));
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			System.out.println(_textureTranslate);
			System.out.printf("size: %d\n", _textureTranslate.length);
			System.out.printf("values: (%f %f %f)\n", _textureTranslate[0], _textureTranslate[1], _textureTranslate[2]);
			System.out.println(AppWindow.getDataStorage().getDimensions());
			System.out.printf("size: %d\n", AppWindow.getDataStorage().getDimensions().length);
			System.out.printf("values: (%d %d %d)\n", AppWindow.getDataStorage().getDimensions()[0], AppWindow.getDataStorage().getDimensions()[1], AppWindow.getDataStorage().getDimensions()[2]);
		}
		
		_markerTexXY.render(gl2);
		_markerTexXZ.render(gl2);
		_markerTexYZ.render(gl2);
		
		// COPIED FROM SUPERCLASS
		_shader.useShader(gl2);
        _shader.updateLevelValue(_level);
        _shader.updateWindowValue(_window);
        _shader.updateIncreaseValue(_increase);
        Vector3f _space = new Vector3f(AppWindow.getDataStorage().getSpacing()[0],AppWindow.getDataStorage().getSpacing()[1],AppWindow.getDataStorage().getSpacing()[2]);
        _shader.updateSpacing(_space);
        
		
		gl2.glActiveTexture(GL.GL_TEXTURE0);
		gl2.glEnable(GL2.GL_TEXTURE_3D);
		gl2.glBindTexture(GL2.GL_TEXTURE_3D, _tex3D[0]);
		gl2.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE);
        gl2.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL.GL_REPLACE);
		_markerTexXY.activate(gl2, GL.GL_TEXTURE1);
		_markerTexXZ.activate(gl2, GL.GL_TEXTURE2);
		_markerTexYZ.activate(gl2, GL.GL_TEXTURE3);
		//_shader.updateMarkingTextureUniform(_tex3D[1]);


		
		
        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        gl2.glLoadIdentity();

        Cubille.renderTextured(gl2, 0, 0, 0);

        _markerTexXY.deactivate(gl2);
        _markerTexXZ.deactivate(gl2);
        _markerTexYZ.deactivate(gl2);
        gl2.glDisable(GL2.GL_TEXTURE_3D);

        _shader.dontUseShader(gl2);
        
        //super.render(gl2);
	}
	
	@Override
	public String toString()
	{
		return "SlicePane";
	}
	
	@Override
	public void setTranslateTexture(float x, float y, float z) {
		// TODO Auto-generated method stub
		_textureTranslate[0] = x;
		_textureTranslate[1] = y;
		_textureTranslate[2] = z;
	}

	@Override
	public void setRotateXTexture(float rX) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRotateYTexture(float rY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRotateZTexture(float rZ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRotateOrderXYZ() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRotateOrderZYX() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRotateOrderYXZ() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getRotateOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector3f getTranslateTexture() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getRotateXTexture() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotateYTexture() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotateZTexture() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Matrix4f getTextureMatrix() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Texture2DInterface getMarkerInterface()
	{
		switch (_activePane) {
			case XY_PANE:
			{
				return _markerTexXY;
			}
			case XZ_PANE:
			{
				return _markerTexXZ;
			}
			case YZ_PANE:
			{
				return _markerTexYZ;
			}
			default:
			{
	
				break;
			}
		}
		return null;
	}
}
