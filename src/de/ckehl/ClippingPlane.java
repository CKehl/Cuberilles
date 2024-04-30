package de.ckehl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.gl.CLGLContext;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

public class ClippingPlane extends GeometryContainer implements ClippingMatrixInterface
{
	protected Vector4f _planeCentre;
	protected Vector3f _planeNormal;
	protected Matrix4f _planeMatrix;
	protected Vector3f _planeTranslate;
	protected Vector3f _planeRotationAngles; // in degrees
	protected boolean _isActive = false;
	
	public ClippingPlane()
	{
		_planeCentre = new Vector4f(0f,0f,0f,1f);
		_planeNormal = new Vector3f(0f,0f,1f);
		_planeMatrix = new Matrix4f();
		_planeTranslate = new Vector3f();
		_planeRotationAngles = new Vector3f();
	}
	
	public ClippingPlane(Vector4f centre, Vector3f normal)
	{
		_planeCentre = new Vector4f(centre);
		_planeNormal = new Vector3f(normal);
		_planeMatrix = new Matrix4f().identity();
		_planeTranslate = new Vector3f();
		_planeRotationAngles = new Vector3f();
	}
	
	public ClippingPlane(Float cX, Float cY, Float cZ, Float nX, Float nY, Float nZ)
	{
		_planeCentre = new Vector4f(cX,cY,cZ,1f);
		_planeNormal = new Vector3f(nX,nY,nZ);
		_planeMatrix = new Matrix4f().identity();
		_planeTranslate = new Vector3f();
		_planeRotationAngles = new Vector3f();
	}
	
	@Override
	public void dispose(GL2 gl2)
	{
		super.dispose(gl2);
		_planeCentre = null;
		_planeNormal = null;
		_planeMatrix = null;
		_planeTranslate = null;
		_planeRotationAngles = null;
	}
	
	@Override
	public void dispose(GL3 gl3, CLCommandQueue queue)
	{
		super.dispose(gl3, queue);
		_planeCentre = null;
		_planeNormal = null;
		_planeMatrix = null;
		_planeTranslate = null;
		_planeRotationAngles = null;
	}
	
	@Override
	public void render(GL2 gl2)
	{
		/*
		 * Order: first set up environment
		 */
		_planeMatrix.identity().translate(_planeTranslate)
		.rotate(_planeRotationAngles.z, 0f, 0f, 1f)
		.rotate(_planeRotationAngles.y, 0f, 1f, 0f)
		.rotate(_planeRotationAngles.x, 1f, 0f, 0f);
		//System.out.println(getShaderInstance().getClass());
		if((getShaderInstance()!=null) && (getShaderInstance().getClass() == CubrillesLUT.class))
		{
			// add code
			//System.out.println("Doing some LUT stuff ...");
		}
		
		super.render(gl2);
		
	}
	
	@Override
	public void render(GL3 gl3)
	{
		_planeMatrix.identity().translate(_planeTranslate)
		.rotate((float)Math.toRadians(_planeRotationAngles.z), 0f, 0f, 1f)
		.rotate((float)Math.toRadians(_planeRotationAngles.y), 0f, 1f, 0f)
		.rotate((float)Math.toRadians(_planeRotationAngles.x), 1f, 0f, 0f);
		//System.out.println(getShaderInstanceGL3().getClass());
		if((getShaderInstanceGL3()!=null) && (getShaderInstanceGL3().getClass() == InstancedCubrilleShader.class))
		{
			//System.out.println("Doing some InstancedCubrille stuff ...");
			InstancedCubrilleShader _currentShaderInstance = (InstancedCubrilleShader)getShaderInstanceGL3();
			// Now: add normal, point and matrix
			if(_isActive)
			{
				gl3.glEnable(GL3.GL_CLIP_DISTANCE0);
				_currentShaderInstance.EnableClipping();
			}
			else
			{
				gl3.glDisable(GL3.GL_CLIP_DISTANCE0);
				_currentShaderInstance.DisableClipping();
			}
			_currentShaderInstance.updateClippingCentre(_planeCentre);
			_currentShaderInstance.updateClippingNormal(_planeNormal);
			_currentShaderInstance.updateClippingMatrix(_planeMatrix);
			
		}
		super.render(gl3);
	}
	
	@Override
	public void setup(GL2 gl2)
	{
		super.setup(gl2);
	}
	
	@Override
	public String toString()
	{
		return "ClippingPlane";
	}
	
	public Vector3f getTranslateVector()
	{
		return _planeTranslate;
	}
	
	public Vector3f getRotationVector(){
		return _planeRotationAngles;
	}
	
	public Vector3f getNormalVector()
	{
		return _planeNormal;
	}

	@Override
	public void setTranslateModel(float x, float y, float z) {
		// TODO Auto-generated method stub
		_planeTranslate.set(x, y, z);
	}

	@Override
	public void setRotateXModel(float rX) {
		// TODO Auto-generated method stub
		_planeRotationAngles.x = rX;
	}

	@Override
	public void setRotateYModel(float rY) {
		// TODO Auto-generated method stub
		_planeRotationAngles.y = rY;
	}

	@Override
	public void setRotateZModel(float rZ) {
		// TODO Auto-generated method stub
		_planeRotationAngles.z = rZ;
	}

	@Override
	public Vector3f getTranslateModel() {
		// TODO Auto-generated method stub
		return _planeTranslate;
	}

	@Override
	public float getRotateXModel() {
		// TODO Auto-generated method stub
		return _planeRotationAngles.x;
	}

	@Override
	public float getRotateYModel() {
		// TODO Auto-generated method stub
		return _planeRotationAngles.y;
	}

	@Override
	public float getRotateZModel() {
		// TODO Auto-generated method stub
		return _planeRotationAngles.z;
	}

	@Override
	public Matrix4f getModelMatrix() {
		// TODO Auto-generated method stub
		return _planeMatrix;
	}

	@Override
	public void setModelMatrix(Matrix4f mat) {
		// TODO Auto-generated method stub
		_planeMatrix = mat;
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
	public void invertNormalDirection() {
		// TODO Auto-generated method stub
		_planeNormal.negate();
	}

	@Override
	public void resetMatrix() {
		// TODO Auto-generated method stub
		_planeMatrix.identity();
	}

	@Override
	public void Enable() {
		// TODO Auto-generated method stub
		_isActive = true;
	}

	@Override
	public void Disable() {
		// TODO Auto-generated method stub
		_isActive = false;
	}

	@Override
	public void Toggle() {
		// TODO Auto-generated method stub
		_isActive = !_isActive;
	}

	@Override
	public void IncreaseXRotation() {
		// TODO Auto-generated method stub
		_planeRotationAngles.x += 1f;
	}

	@Override
	public void DecreaseXRotation() {
		// TODO Auto-generated method stub
		_planeRotationAngles.x -= 1f;
	}

	@Override
	public void IncreaseYRotation() {
		// TODO Auto-generated method stub
		_planeRotationAngles.y += 1f;
	}

	@Override
	public void DecreaseYRotation() {
		// TODO Auto-generated method stub
		_planeRotationAngles.y -= 1f;
	}

	@Override
	public void IncreaseZRotation() {
		// TODO Auto-generated method stub
		_planeRotationAngles.z += 1f;
	}

	@Override
	public void DecreaseZRotation() {
		// TODO Auto-generated method stub
		_planeRotationAngles.z -= 1f;
	}

	@Override
	public void ForwardX() {
		// TODO Auto-generated method stub
		_planeTranslate.x += 5f;
	}

	@Override
	public void BackwardX() {
		// TODO Auto-generated method stub
		_planeTranslate.x -= 5f;
	}

	@Override
	public void ForwardY() {
		// TODO Auto-generated method stub
		_planeTranslate.y += 5f;
	}

	@Override
	public void BackwardY() {
		// TODO Auto-generated method stub
		_planeTranslate.y -= 5f;
	}

	@Override
	public void ForwardZ() {
		// TODO Auto-generated method stub
		_planeTranslate.z += 5f;
	}

	@Override
	public void BackwardZ() {
		// TODO Auto-generated method stub
		_planeTranslate.z -= 5f;
	}
}
