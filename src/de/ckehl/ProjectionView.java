package de.ckehl;


import java.text.DecimalFormat;

import jogamp.graph.geom.plane.Crossing.QuadCurve;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.glu.GLU;



public class ProjectionView implements ModelViewProjectionInterface, ViewPlaneInterface, ProjectionViewInterface {

	//View width and height, in pixels
	protected int _viewWidth, _viewHeight;
	//view dimensions and center, in (semi-) real world coords
	protected Float[] _dims;
	protected Float[] _viewCenter;
	protected Float _fov;
	protected Float[] _eye;
	protected Float[] _translateEyeStep;
	protected Float[] _translateEye;
	protected Float[] _translateModel;
	protected Float[] _rotateStep;
	protected Float[] _rotateEye;
	protected Float[] _rotateModel;
	
	protected Float _viewRadius;
	protected Float[] _position;
	protected Float _horAngle;
	protected Float _verAngle;
	
	protected Matrix4f _projection = new Matrix4f();
	protected Matrix4f _view = new Matrix4f();
	protected Matrix4f _model = new Matrix4f();
	protected Matrix4f _pvm = new Matrix4f();
	
	protected Vector3f _clearColour = new Vector3f(0.65f, 0.65f, 0.7f);
	
	private GLU gluContext;
	
	protected PlaneViewpoint _currentPlane = PlaneViewpoint.FRONT;
	
	public ProjectionView()
	{
		_viewWidth = 100;
		_viewHeight = 100;
		_dims = new Float[3];
		_dims[0] = 10.0f;
		_dims[1] = 10.0f;
		_dims[2] = 10.0f;
		_viewCenter = new Float[3];
		_viewCenter[0] = 0.0f;
		_viewCenter[1] = 0.0f;
		_viewCenter[2] = 0.0f;
		_fov = 60.0f;
		_eye = new Float[3];
		_eye[0] = 0.0f;
		_eye[1] = 0.0f;
		_eye[2] = 1.0f;
		
		// Along axes
		_translateEyeStep = new Float[3];
		_translateEyeStep[0] = 25.0f;
		_translateEyeStep[1] = 25.0f;
		_translateEyeStep[2] = 25.0f;
		_translateEye = new Float[3];
		_translateEye[0] = 0.0f;
		_translateEye[1] = 0.0f;
		_translateEye[2] = 0.0f;
		_translateModel = new Float[3];
		_translateModel[0] = 0.0f;
		_translateModel[1] = 0.0f;
		_translateModel[2] = 0.0f;
		
		// Angles around axes - XYZ transform
		_rotateStep = new Float[3];
		_rotateStep[0] = 2.0f;
		_rotateStep[1] = 2.0f;
		_rotateStep[2] = 2.0f;
		_rotateEye = new Float[3];
		_rotateEye[0] = 0.0f;
		_rotateEye[1] = 0.0f;
		_rotateEye[2] = 0.0f;
		_rotateModel = new Float[3];
		_rotateModel[0] = 0.0f;
		_rotateModel[1] = 0.0f;
		_rotateModel[2] = 0.0f;
		
		_viewRadius = 1.0f;
		_position = new Float[3];
		_position[0] = 1.0f;
		_position[1] = 1.0f;
		_position[2] = -1.0f;
		_horAngle = 0.0f;
		_verAngle = 0.0f;
	}
	
	public void setBackgroundColour(Vector3f colour, GL2 gl2)
	{
		_clearColour = colour;
		gl2.glClearColor(_clearColour.x, _clearColour.y, _clearColour.z, 1);
	}
	
	public void setup( GL2 gl2 ) {
    	gl2.glEnable(GL.GL_DEPTH_TEST);
    	gl2.glDepthFunc(GL.GL_LEQUAL);
    	//gl2.glDepthMask(true);
    	//gl2.glClearColor(0.1f, 0.1f, 0.1f, 1);
    	gl2.glClearColor(_clearColour.x, _clearColour.y, _clearColour.z, 1);
    	gl2.glClearDepthf(1.0f);
    	//gl2.glEnable(GL2.GL_CULL_FACE);
    	//gl2.glCullFace(GL.GL_BACK);
    	gl2.glEnable(GL.GL_BLEND);
    	gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    }
	
	public void setWidth(int width, GL2 gl2)
	{
		_viewWidth = width;
		gl2.glViewport( 0, 0, _viewWidth, _viewHeight );
	}
	
	public void setHeight(int height, GL2 gl2)
	{
		_viewHeight = height;
		gl2.glViewport( 0, 0, _viewWidth, _viewHeight );
	}
	
	public void setDimensions(Float[] dims)
	{
		try
		{
			System.arraycopy(dims, 0, _dims, 0, 3);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setDimensions(Float cx, Float cy, Float cz)
	{
		try
		{
			_dims[0] = cx;
			_dims[1] = cy;
			_dims[2] = cz;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setCenter(Float[] center)
	{
		try
		{
			//System.arraycopy(center, 0, _viewCenter, 0, 3);
			_viewCenter[0] = center[0];
			_viewCenter[1] = center[1];
			_viewCenter[2] = center[2];
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setCenter(Float cx, Float cy, Float cz)
	{
		try
		{
			_viewCenter[0] = cx;
			_viewCenter[1] = cy;
			_viewCenter[2] = cz;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setViewPoint(Float[] viewpoint)
	{
		try
		{
			_eye[0] = viewpoint[0];
			_eye[1] = viewpoint[1];
			//_eye[2] = -center[2];
			_eye[2] = viewpoint[2];
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setViewPoint(Float cx, Float cy, Float cz)
	{
		try
		{
			_eye[0] = cx;
			_eye[1] = cy;
			//_eye[2] = -cz;
			_eye[2] = cz;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets view as bounding box, auto-calculates dimensions, center,
	 * and updates the view
	 * @param box = 6D array [x0,x1,y0,y1,z0,z1]
	 */
	public void setAABB(Float[] box)
	{
		try
		{
			_dims[0] = box[1]-box[0];
			_dims[1] = box[3]-box[2];
			_dims[2] = box[5]-box[4];
			_viewCenter[0] = box[0] + (box[1]-box[0])/2.0f;
			_viewCenter[1] = box[2] + (box[3]-box[2])/2.0f;
			_viewCenter[2] = box[4] + (box[5]-box[4])/2.0f;
			
			_eye[0] = box[0] + (box[1]-box[0])/2.0f;
			_eye[1] = box[2] + (box[3]-box[2])/2.0f;
			_eye[2] = box[4] + (box[5]-box[4])/2.0f;
			//_eye[2] = box[4] - (box[5]-box[4])/2.0f;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets view as bounding box, auto-calculates dimensions, center,
	 * and updates the view
	 * @param x0
	 * @param x1
	 * @param y0
	 * @param y1
	 * @param z0
	 * @param z1
	 */
	public void setAABB(Float x0, Float x1, Float y0, Float y1, Float z0, Float z1)
	{
		try
		{
			_dims[0] = x1-x0;
			_dims[1] = y1-y0;
			_dims[2] = z1-z0;
			_viewCenter[0] = x0 + (x1-x0)/2.0f;
			_viewCenter[1] = y0 + (y1-y0)/2.0f;
			_viewCenter[2] = z0 + (z1-z0)/2.0f;
			
			_eye[0] = x0 + (x1-x0)/2.0f;
			_eye[1] = y0 + (y1-y0)/2.0f;
			_eye[2] = z0 + (z1-z0)/2.0f;
			//_eye[2] = z0 - (z1-z0)/2.0f;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void stepBackward()
	{
		_translateEye[2] -= _translateEyeStep[2];
		//_viewRadius = Math.max(0, _viewRadius+_translateEyeStep[2]);
	}
	
	public void stepForward()
	{
		_translateEye[2] += _translateEyeStep[2];
		//_viewRadius = Math.max(0, _viewRadius-_translateEyeStep[2]);
	}
	
	public void stepLeft()
	{
		_translateEye[0] -= _translateEyeStep[0];
	}
	
	public void strafeLeft()
	{
		//_translateEye[0] -= _translateEyeStep[0];
		//_viewCenter[0] -= _translateEyeStep[0];
		_translateModel[0] -= _translateEyeStep[0];
	}
	
	public void stepRight()
	{
		_translateEye[0] += _translateEyeStep[0];
	}
	
	public void strafeRight()
	{
		//_translateEye[0] += _translateEyeStep[0];
		//_viewCenter[0] += _translateEyeStep[0];
		_translateModel[0] += _translateEyeStep[0];
	}
	
	public void strafeUp()
	{
		//_translateEye[1] += _translateEyeStep[1];
		//_viewCenter[1] += _translateEyeStep[1];
		_translateModel[1] += _translateEyeStep[1];
	}
	
	public void strafeDown()
	{
		//_translateEye[1] -= _translateEyeStep[1];
		//_viewCenter[1] -= _translateEyeStep[1];
		_translateModel[1] -= _translateEyeStep[1];
	}
	
	public void turnUp()
	{
		_rotateModel[0] += _rotateStep[0];
		//_verAngle += (float)Math.toRadians(_rotateStep[0]);
	}
	
	public void turnDown()
	{
		_rotateModel[0] -= _rotateStep[0];
		//_verAngle -= (float)Math.toRadians(_rotateStep[0]);
	}
	
	public void turnLeft()
	{
		//_rotateEye[1] -= _rotateEyeStep[1];
		_rotateModel[1] -= _rotateStep[1];
		//_horAngle -= (float)Math.toRadians(_rotateStep[1]);
	}
	
	public void turnRight()
	{
		//_rotateEye[1] += _rotateStep[1];
		_rotateModel[1] += _rotateStep[1];
		//_horAngle += (float)Math.toRadians(_rotateStep[1]);
	}
	
	public void computePlaneViewpoint()
	{
        Matrix4f viewpointMatrix = new Matrix4f();
        viewpointMatrix.identity();
	    viewpointMatrix.rotate((float)Math.toRadians(_rotateModel[2]), 0.0f, 0.0f, 1.0f).
	        				rotate((float)Math.toRadians(_rotateModel[1]), 0.0f, 1.0f, 0.0f).
	        				rotate((float)Math.toRadians(_rotateModel[0]), 1.0f, 0.0f, 0.0f);
        
	    /*
        Quaternionf q_z = new Quaternionf(new AxisAngle4f((float)Math.toRadians(_rotateModel[2]), 0.0f, 0.0f, 1.0f));
        Quaternionf q_y = new Quaternionf(new AxisAngle4f((float)Math.toRadians(_rotateModel[1]), 0.0f, 1.0f, 0.0f));
        Quaternionf q_x = new Quaternionf(new AxisAngle4f((float)Math.toRadians(_rotateModel[0]), 1.0f, 0.0f, 0.0f));
        
        Quaternionf q = new Quaternionf(q_z);
        q.mul(q_y);
        q.mul(q_x);
        */
        
        Matrix4f rot = new Matrix4f();
        rot.identity();
        //rot.rotateZ((float)Math.toRadians(_rotateModel[2]));
        //rot.rotateY((float)Math.toRadians(_rotateModel[1]));
        //rot.rotateX((float)Math.toRadians(_rotateModel[0]));
        
        //Vector4f viewCheck = new Vector4f(0,0,1,1);
        Vector4f viewCheck = new Vector4f(-1,1,1,0);
        //rot.transform(viewCheck);
        viewpointMatrix.transformAffine(viewCheck);
        //q.transform(viewCheck);
        //System.out.println(viewCheck.toString(new DecimalFormat( "#,###,###,##0.00" )));
        _currentPlane = PlaneViewpoint.determinePlaneViewpoint(new Vector3f(viewCheck.x, viewCheck.y, viewCheck.z));
	}
    
    public void render(GL2 gl2)
    {
    	gl2.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
        gl2.glMatrixMode( GL2.GL_PROJECTION );
        gl2.glLoadIdentity();
        
        //System.out.println("ViewCenter: ("+Float.toString(_viewCenter[0])+","+Float.toString(_viewCenter[1])+","+Float.toString(_viewCenter[2])+")");
        //System.out.println("ViewTranslation: ("+Float.toString(_translateEye[0])+","+Float.toString(_translateEye[1])+","+Float.toString(_translateEye[2])+")");
        //System.out.println("ViewRotation: ("+Float.toString(_rotateEye[0])+","+Float.toString(_rotateEye[1])+","+Float.toString(_rotateEye[2])+")");
        
        /*
         * Turn-based camera - gimbal-locked
         */
        /*
        _position[0] = (_viewRadius * (float)Math.cos(_verAngle) * (float)Math.sin(_horAngle))+_viewCenter[0];
        _position[1] = (_viewRadius * (float)Math.sin(_verAngle))+_viewCenter[1];
        _position[2] = (_viewRadius * (float)Math.cos(_verAngle) * (float)Math.cos(_horAngle))+_viewCenter[2];
        System.out.println("Eye Position: ("+Float.toString(_position[0])+","+Float.toString(_position[1])+","+Float.toString(_position[2])+")");
        */

        // coordinate system origin at lower left with width and height same as the window
        gluContext = new GLU();
        Float vdim = Math.max(_dims[0], _dims[1]);
        
        //gl2.glOrthof(-vdim/2.0f, +vdim/2.0f, -vdim/2.0f, +vdim/2.0f, 0.01f, _dims[2]+_eye[2]+_translateEye[2]);
        
        gluContext.gluPerspective(_fov, 4.0f/3.0f, 0.1f, 2.0f*_dims[2]+Math.abs(_eye[2])+Math.abs(_translateEye[2]));
        gluContext.gluLookAt(_eye[0]+_translateEye[0], _eye[1]+_translateEye[1], _eye[2]+_translateEye[2],   _viewCenter[0], _viewCenter[1], _viewCenter[2],   0, 1, 0);
        
        _projection.setPerspective((float)Math.toRadians(_fov), 4.0f/3.0f, 0.1f, 2.0f*_dims[2]+Math.abs(_eye[2])+Math.abs(_translateEye[2]));
        _view.setLookAt(_eye[0]+_translateEye[0], _eye[1]+_translateEye[1], _eye[2]+_translateEye[2],   _viewCenter[0], _viewCenter[1], _viewCenter[2],   0, 1, 0);
        
        
        //gl2.glRotatef(_rotateEye[0], 1.0f, 0.0f, 0.0f);
        //gl2.glRotatef(_rotateEye[1], 0.0f, 1.0f, 0.0f);
        //gl2.glRotatef(_rotateEye[2], 0.0f, 0.0f, 1.0f);
        

        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        gl2.glLoadIdentity();
        gl2.glTranslatef(_translateModel[0], _translateModel[1], _translateModel[2]);
        gl2.glTranslatef(_viewCenter[0], _viewCenter[1], _viewCenter[2]);
        gl2.glRotatef(_rotateModel[2], 0.0f, 0.0f, 1.0f);
        gl2.glRotatef(_rotateModel[1], 0.0f, 1.0f, 0.0f);
        gl2.glRotatef(_rotateModel[0], 1.0f, 0.0f, 0.0f);
        gl2.glTranslatef(-_viewCenter[0], -_viewCenter[1], -_viewCenter[2]);
        
        _model.identity().translate(_translateModel[0], _translateModel[1], _translateModel[2])
        				.translate(_viewCenter[0], _viewCenter[1], _viewCenter[2])
        				.rotate((float)Math.toRadians(_rotateModel[2]), 0.0f, 0.0f, 1.0f)
        				.rotate((float)Math.toRadians(_rotateModel[1]), 0.0f, 1.0f, 0.0f)
        				.rotate((float)Math.toRadians(_rotateModel[0]), 1.0f, 0.0f, 0.0f)
        				.translate(-_viewCenter[0], -_viewCenter[1], -_viewCenter[2]);
        				
        /*
        _model.identity().translate(-_viewCenter[0], -_viewCenter[1], -_viewCenter[2])
        				.rotate((float)Math.toRadians(_rotateModel[0]), 1.0f, 0.0f, 0.0f)
        				.rotate((float)Math.toRadians(_rotateModel[1]), 0.0f, 1.0f, 0.0f)
        				.rotate((float)Math.toRadians(_rotateModel[2]), 0.0f, 0.0f, 1.0f)
        				.translate(_viewCenter[0], _viewCenter[1], _viewCenter[2])
        				.translate(_translateModel[0], _translateModel[1], _translateModel[2]);
        */
        
        //pvm.set(_projection).mul(_view).mul(_model);
        
        _pvm.setPerspective((float)Math.toRadians(_fov), 4.0f/3.0f, 0.1f, 2.0f*_dims[2]+Math.abs(_eye[2])+Math.abs(_translateEye[2]));
        _pvm.lookAt(_eye[0]+_translateEye[0], _eye[1]+_translateEye[1], _eye[2]+_translateEye[2],   _viewCenter[0], _viewCenter[1], _viewCenter[2],   0, 1, 0);
        _pvm.translate(_translateModel[0], _translateModel[1], _translateModel[2])
		.translate(_viewCenter[0], _viewCenter[1], _viewCenter[2])
		.rotate((float)Math.toRadians(_rotateModel[2]), 0.0f, 0.0f, 1.0f)
		.rotate((float)Math.toRadians(_rotateModel[1]), 0.0f, 1.0f, 0.0f)
		.rotate((float)Math.toRadians(_rotateModel[0]), 1.0f, 0.0f, 0.0f)
		.translate(-_viewCenter[0], -_viewCenter[1], -_viewCenter[2]);
        
        

    }
    
    public Ray getViewRay(int pixelX, int pixelY)
    {
    	
    	//float f =  ((float)_viewWidth/2.0f) / (float)Math.tan((float)Math.toRadians(_fov)/2.0f);
    	float f =  1.0f / (float)Math.tan((float)Math.toRadians(_fov)/2.0f);
    	float dy = 0.1f / f;
    	float dx = (4.0f/3.0f)*dy;
    	//Vector3f viewPoint = new Vector3f(0f,0f,0f);
    	

    	
    	//Vector4f viewDir = new Vector4f((pixelX-(_viewWidth/2.0f))/_viewWidth, ((_viewHeight-pixelY)-(_viewHeight/2.0f))/_viewHeight, -f, 1.0f).normalize();
    	//Vector3f viewDir = new Vector3f((pixelX-(_viewWidth/2.0f))/_viewWidth, ((_viewHeight-pixelY)-(_viewHeight/2.0f))/_viewHeight, -f);


    	//return new Ray(new Vector3f(TviewPoint.x, TviewPoint.y, TviewPoint.z), new Vector3f(TviewDir.x, TviewDir.y, TviewDir.z));
    	
    	
    	/*
    	 * According to 
    	 * http://www.ccs.neu.edu/course/cs4300old/s11/L19/L19.html
    	 */
    	float pY = ((_viewHeight-pixelY)-(_viewHeight/2.0f))/(_viewHeight/2.0f);
    	float pX = (pixelX-(_viewWidth/2.0f))/(_viewWidth/2.0f);
    	Vector3f viewPoint = new Vector3f(dx*pX, dy*pY, 0.1f);
    	
    	//orthographic
    	//Vector3f viewDir = new Vector3f(0f,0f,-1.0f);
    	//perspective
    	Vector3f viewDir = new Vector3f(dx*pX, dy*pY, -0.1f);
    	
    	return new Ray(viewPoint, viewDir);
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
		return ROTATE_ORDER_XYZ;
	}

	@Override
	public void setModelViewProjectionMatrix(Matrix4f mat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Matrix4f getModelViewProjectionMatrix() {
		// TODO Auto-generated method stub
		return _pvm;
	}

	@Override
	public void setTranslateModel(float x, float y, float z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRotateXModel(float rX) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRotateYModel(float rY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRotateZModel(float rZ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector3f getTranslateModel() {
		// TODO Auto-generated method stub
		return new Vector3f(_translateModel[0], _translateModel[1], _translateModel[2]);
	}

	@Override
	public float getRotateXModel() {
		// TODO Auto-generated method stub
		return _rotateModel[0];
	}

	@Override
	public float getRotateYModel() {
		// TODO Auto-generated method stub
		return _rotateModel[1];
	}

	@Override
	public float getRotateZModel() {
		// TODO Auto-generated method stub
		return _rotateModel[2];
	}

	@Override
	public Matrix4f getModelMatrix() {
		// TODO Auto-generated method stub
		return _model;
	}

	@Override
	public void setLookAt(float eyeX, float eyeY, float eyeZ, float centerX,
			float centerY, float centerZ, float upX, float upY, float upZ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTranslateView(float x, float y, float z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRotateXView(float rX) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRotateYView(float rY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRotateZView(float rZ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector3f getTranslateView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getRotateXView() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotateYView() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRotateZView() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Matrix4f getViewMatrix() {
		// TODO Auto-generated method stub
		return _view;
	}

	@Override
	public Matrix4f getViewTransformationMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPerspectiveProjection(float fovy, float aspect, float zNear,
			float zFar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Matrix4f getProjection() {
		// TODO Auto-generated method stub
		return _projection;
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
		
	}

	@Override
	public void update(GL2 gl2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector3f getViewDirectionLocal() {
		// TODO Auto-generated method stub
		return new Vector3f(0f,0f,1f);
	}

	@Override
	public Vector4f getViewPointLocal() {
		// TODO Auto-generated method stub
		return new Vector4f(0f,0f,0f,-1f);
	}

	@Override
	public Vector3f getViewDirectionWorld() {
		// TODO Auto-generated method stub
		Matrix4f transform = new Matrix4f(), mInv = new Matrix4f(), vInv = new Matrix4f();
		_model.invert(mInv);
		_view.invert(vInv);
		mInv.mul(vInv, transform);
		Vector3f rayDir_in = new Vector3f(0f, 0f, -1f), rayDir_out = new Vector3f();

		transform.transformDirection(rayDir_in, rayDir_out);
		rayDir_out.normalize();
		return rayDir_out;
	}

	@Override
	public Vector4f getViewPointWorld() {
		// TODO Auto-generated method stub
		Matrix4f transform = new Matrix4f(), mInv = new Matrix4f(), vInv = new Matrix4f();
		_model.invert(mInv);
		_view.invert(vInv);
		mInv.mul(vInv, transform);
		Vector4f rayOrigin_in = new Vector4f(0f, 0f, 0f, 1.0f), rayOrigin_out = new Vector4f();
		transform.transform(rayOrigin_in, rayOrigin_out);
		return rayOrigin_out;
	}

	@Override
	public Vector3f getRawWorldViewPoint() {
		// TODO Auto-generated method stub
		
		return new Vector3f(_eye[0]+_translateEye[0], _eye[1]+_translateEye[1], _eye[2]+_translateEye[2]);
	}

	@Override
	public Vector3f getRawWorldViewDirection() {
		// TODO Auto-generated method stub
		return new Vector3f(_viewCenter[0]-(_eye[0]+_translateEye[0]), _viewCenter[1]-(_eye[1]+_translateEye[1]), _viewCenter[2]-(_eye[2]+_translateEye[2]));
	}

	@Override
	public void setModelMatrix(Matrix4f mat) {
		// TODO Auto-generated method stub
		_model = mat;
	}
}
