package de.ckehl;

import java.text.DecimalFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

class Corner
{
	public Vector3f _center;
	public float _radius;
	public boolean _active;
	
	public Corner(Vector3f center, float radius)
	{
		_center = center;
		_radius = radius;
		_active = false;
	}
	
	public void activate()
	{
		_active = true;
	}
	
	public void deactivate()
	{
		_active = false;
	}
	
	public Sphere toSphere()
	{
		return new Sphere(_center, _radius);
	}
}

public class LightingView implements Listener, ViewUpdateInterface, LightInformationInterface 
{
	protected SashForm topComposite = null;
	protected Composite _parent = null;
	protected Scale _distanceSlider = null;
	protected Scale _lightImpactSlider = null;
	
	protected GLData gldata;
	protected GLCanvas glcanvas;
	protected GLProfile glprofile;
	protected GLContext glcontext;
	
	private boolean _isOpen = false;
	
	private Corner _corners[] = null;
	protected Vector3f _lightPosition;
	protected float _lightDistance = 1.0f;
	protected float _lightImpact = 1.0f;
	protected Matrix4f _mMat, _vMat, _pMat;
	protected Vector3f _bRotate;
	protected Vector2i _viewport;
	protected ViewUpdateInterface _vuInterface = null;
	
	public LightingView(Composite arg0, int arg1)
	{
		topComposite = new SashForm(arg0, SWT.VERTICAL);
		
		Composite buttonArea = new Composite(topComposite, SWT.PUSH | SWT.BORDER);
		/*
		RowLayout buttonAreaLayout = new RowLayout(SWT.HORIZONTAL);
		buttonAreaLayout.wrap = true;
		buttonAreaLayout.pack  = false;
		buttonAreaLayout.justify = true;
		buttonArea.setLayout(buttonAreaLayout);
		*/
		
		Layout buttonAreaLayout = new FillLayout();
		GridData buttonAreaLayoutData = new GridData();
		buttonAreaLayoutData.horizontalAlignment = SWT.LEFT;
		buttonAreaLayoutData.verticalAlignment = SWT.TOP;
		buttonAreaLayoutData.horizontalSpan = 1;
		buttonAreaLayoutData.verticalSpan = 1;
		buttonArea.setLayout(buttonAreaLayout);
		buttonArea.setLayoutData(buttonAreaLayoutData);
		
		
		_corners = new Corner[8];
		_corners[0] = new Corner(new Vector3f(-0.5f, -0.5f, 0.5f), 0.1f);
		_corners[1] = new Corner(new Vector3f(0.5f, -0.5f, 0.5f), 0.1f);
		_corners[2] = new Corner(new Vector3f(0.5f, 0.5f, 0.5f), 0.1f);
		_corners[3] = new Corner(new Vector3f(-0.5f, 0.5f, 0.5f), 0.1f);

		_corners[4] = new Corner(new Vector3f(-0.5f, -0.5f, -0.5f), 0.1f);
		_corners[5] = new Corner(new Vector3f(0.5f, -0.5f, -0.5f), 0.1f);
		_corners[6] = new Corner(new Vector3f(0.5f, 0.5f, -0.5f), 0.1f);
		_corners[7] = new Corner(new Vector3f(-0.5f, 0.5f, -0.5f), 0.1f);
		
		_bRotate = new Vector3f(25.0f, 30.0f, 0f);
		//_bRotate = new Vector3f(0f, 0f, 0f);
		_viewport = new Vector2i();
		_mMat = new Matrix4f();
		_vMat = new Matrix4f();
		_pMat = new Matrix4f();
		_lightPosition = new Vector3f(0.5f,0.5f,0.5f);
		
		gldata = new GLData();
		gldata.doubleBuffer = true;
		gldata.depthSize = 8;
		_parent = arg0;
		glcanvas = new GLCanvas(topComposite, SWT.NO_BACKGROUND, gldata);
		
		_distanceSlider = new Scale(buttonArea, SWT.HORIZONTAL | SWT.PUSH);
		//_opacityScale.setOrientation(SWT.RIGHT_TO_LEFT);
		_distanceSlider.setMinimum(1);
		_distanceSlider.setMaximum(500);
		_distanceSlider.setIncrement(25);
		_distanceSlider.setSelection(1);
		_distanceSlider.redraw();
		_distanceSlider.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				_lightDistance = (float)(_distanceSlider.getSelection())*2.0f;
				if(_vuInterface!=null)
					_vuInterface.update();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		_lightImpactSlider = new Scale(buttonArea, SWT.HORIZONTAL | SWT.PUSH);
		_lightImpactSlider.setMinimum(0);
		_lightImpactSlider.setMaximum(100);
		_lightImpactSlider.setIncrement(1);
		_lightImpactSlider.setSelection(100);
		_lightImpactSlider.redraw();
		_lightImpactSlider.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				_lightImpact = (float)(_lightImpactSlider.getSelection())/100.0f;
				System.out.println("Light impact: "+_lightImpact);
				if(_vuInterface!=null)
					_vuInterface.update();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		topComposite.setWeights(new int[]{1,6});
		
		setup();
		
		_isOpen = true;
	}
	
	public LightingView(Composite arg0, int arg1, ViewUpdateInterface arg2)
	{
		topComposite = new SashForm(arg0, SWT.VERTICAL);
		
		Composite buttonArea = new Composite(topComposite, SWT.PUSH | SWT.BORDER);
		/*
		RowLayout buttonAreaLayout = new RowLayout(SWT.HORIZONTAL);
		buttonAreaLayout.wrap = true;
		buttonAreaLayout.pack  = false;
		buttonAreaLayout.justify = true;
		buttonArea.setLayout(buttonAreaLayout);
		*/
		
		Layout buttonAreaLayout = new FillLayout();
		GridData buttonAreaLayoutData = new GridData();
		buttonAreaLayoutData.horizontalAlignment = SWT.LEFT;
		buttonAreaLayoutData.verticalAlignment = SWT.TOP;
		buttonAreaLayoutData.horizontalSpan = 1;
		buttonAreaLayoutData.verticalSpan = 1;
		buttonArea.setLayout(buttonAreaLayout);
		buttonArea.setLayoutData(buttonAreaLayoutData);
		
		
		_corners = new Corner[8];
		_corners[0] = new Corner(new Vector3f(-0.5f, -0.5f, 0.5f), 0.1f);
		_corners[1] = new Corner(new Vector3f(0.5f, -0.5f, 0.5f), 0.1f);
		_corners[2] = new Corner(new Vector3f(0.5f, 0.5f, 0.5f), 0.1f);
		_corners[2].activate();
		_corners[3] = new Corner(new Vector3f(-0.5f, 0.5f, 0.5f), 0.1f);

		_corners[4] = new Corner(new Vector3f(-0.5f, -0.5f, -0.5f), 0.1f);
		_corners[5] = new Corner(new Vector3f(0.5f, -0.5f, -0.5f), 0.1f);
		_corners[6] = new Corner(new Vector3f(0.5f, 0.5f, -0.5f), 0.1f);
		_corners[7] = new Corner(new Vector3f(-0.5f, 0.5f, -0.5f), 0.1f);
		
		_bRotate = new Vector3f(25.0f, 30.0f, 0f);
		//_bRotate = new Vector3f(0f, 0f, 0f);
		_viewport = new Vector2i();
		_mMat = new Matrix4f();
		_vMat = new Matrix4f();
		_pMat = new Matrix4f();
		_lightPosition = new Vector3f(0.5f,0.5f,0.5f);
		
		gldata = new GLData();
		gldata.doubleBuffer = true;
		gldata.depthSize = 16;
		_parent = arg0;
		glcanvas = new GLCanvas(topComposite, SWT.NO_BACKGROUND, gldata);
		_vuInterface = arg2;
		
		
		setup();
		
		_isOpen = true;		
	}
	
	public void setViewerConnection(ViewUpdateInterface adaptor)
	{
		_vuInterface = adaptor;
	}
	
	public void dispose()
	{
		if((glcanvas!=null) && (glcanvas.isDisposed()==false))
		{
			glcanvas.setCurrent();
			glcontext.makeCurrent();

			

			glcontext.release();
			glcanvas.dispose();
			glcanvas = null;
			

		}
		_parent = null;
	}
	
	public void close()
	{
		if(_isOpen==true)
		{
			this.dispose();
		}
		_isOpen=false;
	}
	
	public void setLayoutData(Object arg0)
	{
		glcanvas.setLayoutData(arg0);
	}
	
	public void setLayout(Layout arg0)
	{
		glcanvas.setLayout(arg0);
	}
	
	public void setSize(int arg0, int arg1)
	{
		glcanvas.setSize(arg0, arg1);
	}
	
	protected void setup()
	{
		Rectangle pRect = _parent.getClientArea();
		_viewport.x = pRect.width;
		_viewport.y = pRect.height;
		glcanvas.setCurrent();
		glprofile = GLProfile.getDefault();
		try {
			glcontext = GLDrawableFactory.getFactory(glprofile).createExternalGLContext();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		glcanvas.setCurrent();
		glcontext.makeCurrent();

		GL2 gl2 = glcontext.getGL().getGL2();
		GLU glu = new GLU();
		gl2.glViewport( 0, 0, pRect.width, pRect.width );
    	gl2.glEnable(GL.GL_DEPTH_TEST);
    	gl2.glDepthFunc(GL.GL_LEQUAL);
		gl2.glClearColor(0.65f, 0.65f, 0.7f, 1.0f);
		gl2.glClearDepthf(1.0f);
		

        
		

		render(gl2);

		glcontext.release();
		
		glcanvas.addListener(SWT.Resize, this);
		glcanvas.addListener(SWT.Paint, this);
		glcanvas.addListener(SWT.MouseDown, this);
		//glcanvas.addListener(eventType, listener)
	}
	
	protected void render(GL2 gl2)
	{
		GLU glu = new GLU();
		gl2.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
		gl2.glViewport( 0, 0, _viewport.x, _viewport.y );
		gl2.glPolygonMode( GL.GL_FRONT_AND_BACK, GL2.GL_LINE );
		gl2.glLineWidth(1.2f);
        gl2.glMatrixMode( GL2.GL_PROJECTION );
        gl2.glLoadIdentity();
        //glu.gluPerspective(60.0f, 4.0f/3.0f, 0.1f, 10.0f);
        glu.gluPerspective(40.0f, (float)_viewport.x / (float)_viewport.y, 0.1f, 10.0f);
        _pMat.setPerspective((float)Math.toRadians(60.0f), (float)_viewport.x / (float)_viewport.y, 0.1f, 10.0f);
        //glu.gluLookAt(0.5f, 0.5f, 1.0f,   0f, 0f, 0f,   0, 1, 0);
        glu.gluLookAt(0f, 0f, 4f,   0f, 0f, 0f,   0f, 1f, 0f);
        _vMat.setLookAt(0f, 0f, 4f,   0f, 0f, 0f,   0f, 1f, 0f);
        
        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        _mMat.identity();
        //_mMat.translate(0f, -1.0f, 0f);
        //_mMat.translation(0f, -2.0f, 0f);
        _mMat.rotate((float)Math.toRadians(_bRotate.z), 0.0f, 0.0f, 1.0f);
        _mMat.rotate((float)Math.toRadians(_bRotate.y), 0.0f, 1.0f, 0.0f);
        _mMat.rotate((float)Math.toRadians(_bRotate.x), 1.0f, 0.0f, 0.0f);
        gl2.glLoadIdentity();
        //gl2.glTranslatef(0f, -1.0f, 0f);
        gl2.glRotatef(_bRotate.z, 0.0f, 0.0f, 1.0f);
        gl2.glRotatef(_bRotate.y, 0.0f, 1.0f, 0.0f);
        gl2.glRotatef(_bRotate.x, 1.0f, 0.0f, 0.0f);
        Cubille.render(gl2, 0f, 0f, 0f);
        for(int i = 0; i < 8; i++)
        {
	        if(_corners[i]._active)
	        {
	        	gl2.glPushMatrix();
	        	GLUT glut = new GLUT();
	        	gl2.glTranslatef(_corners[i]._center.x, _corners[i]._center.y, _corners[i]._center.z);
	        	glut.glutSolidSphere(_corners[i]._radius, 20, 10);
	        	gl2.glPopMatrix();
	        }
        }
	}
	
	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		switch(event.type)
		{
			case SWT.Resize: {
				Rectangle rectangle = glcanvas.getClientArea();
				_viewport.x = rectangle.width;
				_viewport.y = rectangle.height;
				glcanvas.setCurrent();
				glcontext.makeCurrent();
				GL2 gl2 = glcontext.getGL().getGL2();
				GLU glu = new GLU();
				gl2.glViewport( 0, 0, rectangle.width, rectangle.width );
		    	gl2.glEnable(GL.GL_DEPTH_TEST);
		    	gl2.glDepthFunc(GL.GL_LEQUAL);
				gl2.glClearColor(0.65f, 0.65f, 0.7f, 1.0f);
				gl2.glClearDepthf(1.0f);
				glcanvas.swapBuffers();
				glcontext.release();
				update();
				break;
			}
			case SWT.Paint: {
				update();
				break;
			}
			case SWT.MouseDown: {
				Ray rCamSpace = getViewRay(event.x, event.y);
				//System.out.println(rCamSpace.toString());
				Matrix4f transform = new Matrix4f(), mInv = new Matrix4f(), vInv = new Matrix4f(), M = new Matrix4f();
				_mMat.invert(mInv);
				_vMat.invert(vInv);
				M.identity().mul(_pMat).mul(_vMat).mul(_mMat);
				mInv.mul(vInv, transform);
				
				Vector4f rayOrigin_in = new Vector4f(rCamSpace._orig.x, rCamSpace._orig.y, rCamSpace._orig.z, 1.0f), rayOrigin_out = new Vector4f();
				Vector3f rayDir_in = new Vector3f(rCamSpace._dir.x, rCamSpace._dir.y, rCamSpace._dir.z), rayDir_out = new Vector3f();
				
				
				//transform.transform(rayOrigin_in, rayOrigin_out);
				//transform.transformDirection(rayDir_in, rayDir_out);
				_pMat.transform(rayOrigin_in, rayOrigin_out);
				_pMat.transformDirection(rayDir_in, rayDir_out);
				rayDir_out.normalize();
				Ray rObjectSpace = new Ray(new Vector3f(rayOrigin_out.x, rayOrigin_out.y, rayOrigin_out.z), rayDir_out);
		        //System.out.println(rObjectSpace.toString());
		        
				
				boolean _found = false;
		        for(int i = 0; i < 8; i++)
		        {
			        //if(Intersections.intersectSphereRay(_corners[i].toSphere(), rObjectSpace) && !_found)
		        	Vector4f corner = new Vector4f(_corners[i]._center.x, _corners[i]._center.y, _corners[i]._center.z, 1f);
		        	M.transform(corner);
		        	Sphere checkSphere = new Sphere(new Vector3f(corner.x, corner.y, corner.z), _corners[i]._radius);
		        	//System.out.println(checkSphere.toString());
		        	if(Intersections.intersectSphereRay(checkSphere, rObjectSpace) && !_found)
			        {
			        	_corners[i].activate();
			        	_lightPosition = new Vector3f(_corners[i]._center);
			        	_found = true;
			        	System.out.println("Selected corner: "+_corners[i].toSphere().toString());
			        }
			        else
			        {
			        	_corners[i].deactivate();
			        }
		        }
				
		        /*
		        if(_found)
		        {
			        Matrix3f viewpointMatrix = new Matrix3f();
			        viewpointMatrix.identity();
			        viewpointMatrix.scale(_lightDistance, _lightDistance, _lightDistance);
			        viewpointMatrix.transform(_lightPosition);
		        }
		        */
				update();
				
				if(_vuInterface!=null)
					_vuInterface.update();
				break;
			}
		}
	}
	
    public Ray getViewRay(int pixelX, int pixelY)
    {
    	float f =  1.0f / (float)Math.tan((float)Math.toRadians(60.0f)/2.0f);
    	float dy = 0.1f / f;
    	//float dx = (4.0f/3.0f)*dy;
    	float dx = ((float)_viewport.x / (float)_viewport.y)*dy;
    	/*
    	 * According to 
    	 * http://www.ccs.neu.edu/course/cs4300old/s11/L19/L19.html
    	 */
    	float pY = ((_viewport.y-pixelY)-(_viewport.y/2.0f))/(_viewport.y/2.0f);
    	float pX = (pixelX-(_viewport.x/2.0f))/(_viewport.x/2.0f);
    	Vector3f viewPoint = new Vector3f(dx*pX, dy*pY, 0.1f);
    	
    	//orthographic
    	//Vector3f viewDir = new Vector3f(0f,0f,-1.0f);
    	//perspective
    	Vector3f viewDir = new Vector3f(dx*pX, dy*pY, -0.1f);
    	
    	return new Ray(viewPoint, viewDir);
    }
	
	public void resetView()
	{
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub


		glcanvas.setCurrent();
		glcontext.makeCurrent();

		render(glcontext.getGL().getGL2());
		
		glcanvas.swapBuffers();
		glcontext.release();
	}

	@Override
	public Vector3f getLightPosition() {
		// TODO Auto-generated method stub
		Vector3f result = new Vector3f();
        Matrix3f viewpointMatrix = new Matrix3f();
        viewpointMatrix.identity();
        viewpointMatrix.scale(_lightDistance, _lightDistance, _lightDistance);
        viewpointMatrix.transform(_lightPosition, result);
        //System.out.println("Light pos: "+result.toString(new DecimalFormat( "#,###,###,##0.00" )));;
		return result;
	}

	@Override
	public void setLightPosition(Vector3f position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateLightPosition() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void EnableLighting() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void DisableLighting() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getLightImpact() {
		// TODO Auto-generated method stub
		System.out.println("Light impact: "+_lightImpact);
		return _lightImpact;
	}

	@Override
	public void setLightImpact(float lightImpact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateLightImpact() {
		// TODO Auto-generated method stub
		
	}
}
