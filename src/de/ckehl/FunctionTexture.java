package de.ckehl;

import java.nio.ShortBuffer;

import org.joml.Vector3d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.GLBuffers;

public class FunctionTexture extends GeometryContainer implements GaussCurveInterface {
	protected ShortBuffer _data = null;
	protected static int _tex1D[] = null;
	protected int _paramPerCurve = 0;
	protected int _numCurves = 0;
	protected boolean _texInitialised = false;
	protected boolean _isTexUpdated = false;
	
	protected GaussCurveLUT _shader = null;
	protected String basePath = "gaussParamsLUT";

	public FunctionTexture()
	{
		_isUpdated = false;
		_tex1D = new int[1];
		_shader = new GaussCurveLUT(basePath, basePath);
		_texInitialised = false;
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
		if((_isUpdated==false) && (_texInitialised==false))
		{
			if(_printSetupLine)
				System.out.println("updating "+this.toString()+"("+Long.toString(_ID)+") ...");
	        setupTexture(gl2);
	        _shader.init(gl2);
	        _shader.updateTextureUniform(0);
	        //_shader.updateTextureUniform(_tex1D[0]);
	        _shader.updateNumberOfEntries(_numCurves);
	        setCurrentShaderGL2(_shader);
	        if(_printSetupLine)
	        	System.out.println(this.toString()+"("+Long.toString(_ID)+"): up-to-date");
		}
		else if((_isTexUpdated==true) && (_texInitialised==true)) {
			if(_printSetupLine)
				System.out.println("updating "+this.toString()+"("+Long.toString(_ID)+") ...");
			renewTexture(gl2);
			_isTexUpdated=false;
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

    private void setupTexture(GL2 gl2)
    {
    	if(_texInitialised==false) {
	    	if(_data!=null)
	    	{
		    	gl2.glGenTextures(1,  _tex1D,  0);
		    	gl2.glBindTexture(GL2.GL_TEXTURE_1D, _tex1D[0]);
		    	
		    	gl2.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		    	gl2.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		    	gl2.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		    	gl2.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		    	if(_paramPerCurve==1) {
		    		gl2.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_INTENSITY, _numCurves, 0, GL2.GL_INTENSITY, GL.GL_UNSIGNED_SHORT, _data);
		    	}
		    	else if(_paramPerCurve==2) {
		    		gl2.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_RG, _numCurves, 0, GL2.GL_RG, GL.GL_UNSIGNED_SHORT, _data);
		    	}
		    	else if(_paramPerCurve==3) {
		    		gl2.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_RGB, _numCurves, 0, GL2.GL_RGB, GL.GL_UNSIGNED_SHORT, _data);
		    	}
		    	else if(_paramPerCurve==4) {
		    		gl2.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_RGBA, _numCurves, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_SHORT, _data);
		    	}
		    	gl2.glBindTexture(GL2.GL_TEXTURE_1D, 0);
	
		    	_data=null;
		    	_texInitialised = true;
	    	}
    	}
    	_isUpdated = true;
    }
    
    public void renewTexture(GL2 gl2) {
    	gl2.glBindTexture(GL2.GL_TEXTURE_1D, _tex1D[0]);
    	if(_paramPerCurve==1) {
    		gl2.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_INTENSITY, _numCurves, 0, GL2.GL_INTENSITY, GL.GL_UNSIGNED_SHORT, _data);
    	}
    	else if(_paramPerCurve==2) {
    		gl2.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_RG, _numCurves, 0, GL2.GL_RG, GL.GL_UNSIGNED_SHORT, _data);
    	}
    	else if(_paramPerCurve==3) {
    		gl2.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_RGB, _numCurves, 0, GL2.GL_RGB, GL.GL_UNSIGNED_SHORT, _data);
    	}
    	else if(_paramPerCurve==4) {
    		gl2.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_RGBA, _numCurves, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_SHORT, _data);
    	}
    	gl2.glBindTexture(GL2.GL_TEXTURE_1D, 0);
    	_data=null;
    	_isUpdated = true;
    }

	@Override
	public String toString()
	{
		return "FunctionTexture";
	}




	@Override
	public float[] getCurveParamArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurveParamArray(float[] gaussFuncParams, int paramsPerItem) {
		// TODO Auto-generated method stub
    	if(gaussFuncParams!=null)
    	{
    		_paramPerCurve = paramsPerItem;
    		_numCurves = gaussFuncParams.length/paramsPerItem;
    		_data = GLBuffers.newDirectShortBuffer(gaussFuncParams.length+paramsPerItem);

			for(short k = 0; k< gaussFuncParams.length; k++)
			{
	    		_data.put((short)gaussFuncParams[k]);
			}
			_data.rewind();
			_isTexUpdated = true;
    	}
	}

	@Override
	public boolean isUpdated() {
		// TODO Auto-generated method stub
		return _isUpdated;
	}

	@Override
	public void Update(boolean state) {
		// TODO Auto-generated method stub
		_isUpdated = state;
	}

	@Override
	public int getNumberOfCurveParameters() {
		// TODO Auto-generated method stub
		return _paramPerCurve;
	}

	@Override
	public short[] getCurveParamUShortArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurveParamUShortArray(short[] gaussFuncParams, int paramsPerItem) {
		// TODO Auto-generated method stub
    	if(gaussFuncParams!=null)
    	{
    		_paramPerCurve = paramsPerItem;
    		_numCurves = gaussFuncParams.length/paramsPerItem;
    		_data = GLBuffers.newDirectShortBuffer(gaussFuncParams.length+paramsPerItem);

			for(short k = 0; k< gaussFuncParams.length; k++)
			{
	    		_data.put(gaussFuncParams[k]);
			}
			_data.rewind();
			_isTexUpdated = true;
    	}
	}

	@Override
	public void setNumberOfCurveParameters(int numCurveParameters) {
		// TODO Auto-generated method stub
		_paramPerCurve = numCurveParameters;
	}

	@Override
	public int getNumberOfCurves() {
		// TODO Auto-generated method stub
		return _numCurves;
	}

	@Override
	public void setNumberOfCurves(int numCurves) {
		// TODO Auto-generated method stub
		_numCurves = numCurves;
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
		
	}

	@Override
	public Vector3d getSelectedGaussCurve() {
		// TODO Auto-generated method stub
		return null;
	}

}
