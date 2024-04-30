package de.ckehl;

//import java.nio.ByteBuffer;
//import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.GLBuffers;


//import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Texture3DPane extends GeometryContainer implements BaseLevelAdaptorInterface, WindowLevelInterface {
	protected int _projectionWidth, _projectionHeight;
	protected byte NUMBER_TEXTURE_ALLOCATE = 4;
	
	public final static short XY_PANE = 1;
	public final static short XZ_PANE = 2;
	public final static short YZ_PANE = 4;
	public final static short MINUS_XY_PANE = 8;
	public final static short MINUS_XZ_PANE = 16;
	public final static short MINUS_YZ_PANE = 32;
	
	protected static int _volWidth, _volHeight, _volDepth;
	//protected static ShortBuffer imageBuf;
	
	protected static ShortBuffer _data = null;
	//protected static short[] _dimensions = null;
	//protected static Vector3f _spacing = null;
	
	protected short _activePane = 0;
	
	//protected static IntBuffer _tex3D = null;
	protected static int _tex3D[] = null;
	protected boolean _head = false;
	
	/*
	 * 
	 */
	protected AASliceTextureShader _shader = null;
	//protected static final String vshaderFile = "planarTexturing";
	//protected static final String fshaderFile = "planarTexturing";
	protected static String vshaderFile = "";
	protected static String fshaderFile = "";
	
	protected float _window = 1.0f;
	protected float _level = 0.0f;
	protected float _increase = 0.0f;
	
	/**
	 * 
	 * @param slicePane - tells the plane to render; XY_PANE(1), XZ_PANE(2) or YZ_PANE(4)
	 */
	public Texture3DPane(short slicePane)
	{
		_activePane = slicePane;
		_head = false;
		_isUpdated = false;
	}
	
	/**
	 * 
	 * @param slicePane - tells the plane to render; XY_PANE(1), XZ_PANE(2) or YZ_PANE(4)
	 * @param headView - in a shared-context setup, tells if this is the (master) head view
	 */
	public Texture3DPane(short slicePane, boolean headView)
	{
		//_tex3D = GLBuffers.newDirectIntBuffer(1);
		_tex3D = new int[NUMBER_TEXTURE_ALLOCATE];
		_activePane = slicePane;
		_head = headView;
		_isUpdated = false;
	}
	
	@Override
	public void dispose(GL2 gl2)
	{
		if(_head)
		{
			gl2.glDeleteTextures(NUMBER_TEXTURE_ALLOCATE, _tex3D, 0);
			if(_data!=null)
				_data.clear();
			_data = null;
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
	        	//System.out.println("updating "+this.toString()+"("+Long.toString(_ID)+") ...");
	        	setupTexture(gl2);
	        	
	        }
	        _shader.init(gl2);
	        _shader.updateTextureUniform(0);
	        //_shader.updateTextureUniform(1);
	        //_shader.updateMarkingTextureUniform(0);
	        _shader.updateLevelValue(_level);
	        _shader.updateWindowValue(_window);
	        _shader.updateIncreaseValue(_increase);
	        _shader.updateMaxOriginal(4095.0f);
	        _shader.updateBaseLevel(0.0f);
	        Vector3f _space = new Vector3f(AppWindow.getDataStorage().getSpacing()[0],AppWindow.getDataStorage().getSpacing()[1],AppWindow.getDataStorage().getSpacing()[2]);
	        _shader.updateSpacing(_space);
	        
	        if(_printSetupLine)
	        	System.out.println(this.toString()+"("+Long.toString(_ID)+"): up-to-date");
		}
		super.setup(gl2);
	}
	
	@Override
	public void render(GL2 gl2) {
		// TODO Auto-generated method stub
		super.render(gl2);
        
		_shader.useShader(gl2);
		//gl2.glEnable(GL2.GL_TEXTURE_3D);
		gl2.glActiveTexture(GL.GL_TEXTURE0);

        _shader.updateLevelValue(_level);
        _shader.updateWindowValue(_window);
        _shader.updateIncreaseValue(_increase);
        Vector3f _space = new Vector3f(AppWindow.getDataStorage().getSpacing()[0],AppWindow.getDataStorage().getSpacing()[1],AppWindow.getDataStorage().getSpacing()[2]);
        _shader.updateSpacing(_space);
        
		gl2.glBindTexture(GL2.GL_TEXTURE_3D, _tex3D[0]);
		
        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        gl2.glLoadIdentity();

        Cubille.renderTextured(gl2, 0, 0, 0);

        gl2.glDisable(GL2.GL_TEXTURE_3D);

        _shader.dontUseShader(gl2);
	}
	
	@Override
	public String toString()
	{
		return "Texture3DPane";
	}
	
    public void reshape(int width, int height)
    {
    	super.reshape(width, height);
    	_projectionWidth = width;
    	_projectionHeight = height;
    }
	
    public void setActivePane(short paneID)
    {
    	_activePane = paneID;
    }
    
    private void setupTexture(GL2 gl2)
    {
    	if(_data!=null)
    	{
    		gl2.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
	    	gl2.glGenTextures(NUMBER_TEXTURE_ALLOCATE,  _tex3D,  0);
	    	//gl2.glBindTexture(GL2.GL_TEXTURE_3D, _tex3D.get(0));
	    	gl2.glBindTexture(GL2.GL_TEXTURE_3D, _tex3D[0]);
	    	
	    	gl2.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_REPEAT);
	    	//gl2.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
	    	//gl2.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
	    	//gl2.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_CLAMP);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	    	gl2.glTexImage3D(GL2.GL_TEXTURE_3D, 0, GL2.GL_LUMINANCE, _volWidth, _volHeight, _volDepth, 0, GL2.GL_LUMINANCE, GL.GL_UNSIGNED_SHORT, _data);
	    	//gl2.glEnable(GL2.GL_TEXTURE_3D);
	    	gl2.glBindTexture(GL2.GL_TEXTURE_3D, 0);
	    	
	    	/**
	    	 * using GL_TEXTURE_2D_ARRAY
	    	 * 
	    	gl2.glBindTexture(GL2.GL_TEXTURE_2D_ARRAY, _tex3D[0]);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D_ARRAY, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D_ARRAY, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D_ARRAY, GL2.GL_TEXTURE_WRAP_R, GL2.GL_REPEAT);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D_ARRAY, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	    	gl2.glTexParameteri(GL2.GL_TEXTURE_2D_ARRAY, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	    	gl2.glTexStorage3D(GL2.GL_TEXTURE_2D_ARRAY,
	                1,             // No mipmaps
	                GL2.GL_LUMINANCE,      // Internal format
	                _volWidth, _volHeight, // width,height
	                _volDepth              // Number of layers
	                );
	    	gl2.glTexSubImage3D(GL2.GL_TEXTURE_2D_ARRAY,
	                0,                // Mipmap number
	                0, 0, 0,          // xoffset, yoffset, zoffset
	                _volWidth, _volHeight, _volDepth, // width, height, depth
	                GL2.GL_LUMINANCE,         // format
	                GL.GL_UNSIGNED_SHORT, // type
	                imageBuf);           // pointer to data
	    	gl2.glEnable(GL2.GL_TEXTURE_2D_ARRAY);
	    	gl2.glBindTexture(GL2.GL_TEXTURE_2D_ARRAY, 0);
	    	 */
	    	
	    	
	    	_data=null;
	    	_isUpdated = true;
    	}
    }
    
    public void initialiseData()
    {
		_volWidth = AppWindow.getDataStorage().getDimensionsInt()[0];
		_volHeight = AppWindow.getDataStorage().getDimensionsInt()[1];
		_volDepth = AppWindow.getDataStorage().getDimensionsInt()[2];

		if((AppWindow.getDataStorage().getDimensions()!=null) && (AppWindow.getDataStorage().getData()!=null))
    	{
    		_data = GLBuffers.newDirectShortBuffer(_volWidth*_volHeight*_volDepth);

			for(short k = 0; k< _volDepth; k++)
			{
				for(short j = 0; j< _volHeight; j++)
	    		{
	    			for(short i = 0; i< _volWidth; i++)
	    			{
	    				_data.put(AppWindow.getDataStorage().getData()[i][j][k]);

	    			}
	    		}
			}
			_data.rewind();
    	}
    }
    
	@Override
	public void centerValue(int value, int occurence) {
		// TODO Auto-generated method stub
		_shader.updateBaseLevel(value/4095.0f);
		System.out.printf("adapt to (%d, %d).\n", value, occurence);
	}

	@Override
	public void updateValueBounds(int min, int max) {
		// TODO Auto-generated method stub
		_shader.updateMaxOriginal((float)max);
	}

	@Override
	public void increaseWindow() {
		// TODO Auto-generated method stub
		_window = (float)Math.max(0.01f,Math.min(1.0f, _window+0.015f));
        _shader.updateWindowValue(_window);
        System.out.printf("Window: %f\n", _window);
	}

	@Override
	public void decreaseWindow() {
		// TODO Auto-generated method stub
		_window = (float)Math.max(0.01f,Math.min(1.0f, _window-0.015f));
		_shader.updateWindowValue(_window);
		System.out.printf("Window: %f\n", _window);
	}

	@Override
	public void increaseLevel() {
		// TODO Auto-generated method stub
		_level = (float)Math.max(-1.0,Math.min(1.0f, _level+0.01f));
		_shader.updateLevelValue(_level);
		System.out.printf("Level: %f\n", _level);
	}

	@Override
	public void decreaseLevel() {
		// TODO Auto-generated method stub
		_level = (float)Math.max(-1.0,Math.min(1.0f, _level-0.01f));
		_shader.updateLevelValue(_level);
		System.out.printf("Level: %f\n", _level);
	}

	@Override
	public void increaseBrightness() {
		// TODO Auto-generated method stub
		_increase +=0.01;
		_shader.updateIncreaseValue(_increase);
	}

	@Override
	public void decreaseBrightness() {
		// TODO Auto-generated method stub
		_increase -=0.01;
		_shader.updateIncreaseValue(_increase);
	}

	@Override
	public void markValues(Vector2i range) {
		// TODO Auto-generated method stub
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
			_shader.updateHighCut(4095.0f);
		}
	}
}
