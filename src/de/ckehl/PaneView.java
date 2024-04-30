package de.ckehl;



import java.text.DecimalFormat;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;


public class PaneView {

	
	protected int _width, _height;
	protected Float[] _textureTranslate = null;
	protected short _pane = 0;
	static protected float _distance = 1.0f;

	public PaneView()
	{
		_textureTranslate = new Float[3];
		_textureTranslate[0] = 0.0f;
		_textureTranslate[1] = 0.0f;
		_textureTranslate[2] = 0.0f;
	}
	
	public void dispose()
	{
		_textureTranslate = null;
	}
	
    public void setup( GL2 gl2, int width, int height ) {
    	_width = width;
    	_height = height;


    	gl2.glEnable(GL.GL_DEPTH_TEST);
    	gl2.glDepthFunc(GL.GL_LEQUAL);
        gl2.glClearColor(0.2f, 0.2f, 0.2f, 1);
        //System.out.println("Camera set up.");
    }
    
    public void reshape(int width, int height)
    {
    	_width = width;
    	_height = height;
    }
    
    public void setPane(short pane)
    {
    	_pane = pane;
    }
    
    public void render(GL2 gl2)
    {
    	gl2.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
        gl2.glViewport( 0, 0, _width, _height );
        gl2.glMatrixMode( GL2.GL_PROJECTION );
        gl2.glLoadIdentity();

        // coordinate system origin at lower left with width and height same as the window
        GLU glu = new GLU();
        //gl2.glOrthof(-1.0f/2.0f, +1.0f/2.0f, -1.0f/2.0f, +1.0f/2.0f, 0.01f, 20.0f);
        glu.gluPerspective(70, 4.0f/3.0f, 0.1f, 20.0f);
        //glu.gluLookAt(0.0f, 0.0f, 1.8f,   0.0f, 0.0f, -1.0f,   0, 1, 0);
    	
        switch(_pane)
    	{
	    	case Texture3DPane.XY_PANE:
	    	{
	    		//glu.gluLookAt(0.0f, 0.0f, 1.8f,   0.0f, 0.0f, 0.0f,   0, 1, 0);
	    		glu.gluLookAt(0.0f, 0.0f, -4.5f*_distance,   0.0f, 0.0f, 0.0f,   0, 1, 0);
	    		break;
	    	}
	    	case Texture3DPane.XZ_PANE:
	    	{
	    		//glu.gluLookAt(0.0f, 1.0f, 0.0f,   0.0, 0.0, 0.0,   0, 1, 0);
	    		glu.gluLookAt(0.0f, -4.5f*_distance, 0.0f,   0.0f, 0.0f, 0.0f,   0, 0, 1);
	    		break;
	    	}
	    	case Texture3DPane.YZ_PANE:
	    	{
	    		//glu.gluLookAt(1.0f, 0.0f, 0.0f,   0.0, 0.0, 0.0,   0, 1, 0);
	    		glu.gluLookAt(-4.5f*_distance, 0.0f, 0.0f,   0.0f, 0.0f, 0.0f,  0, 0, 1);
	    		break;
	    	}
    	}
    	
        
        //glu.gluOrtho2D( 0.0f, _width, 0.0f, _height );

    }
    
    public void reduceDistance()
    {
    	_distance/=2.0f;
    }
    
    public void increaseDistance()
    {
    	_distance*=2.0f;
    }

    public Vector2f getGeometryCoord(int xIn, int yIn, Vector3f spacing, GL2 gl)
    {
    	GLU glu = new GLU();
    	Vector2f result = new Vector2f();
    	
    	
        int viewport[] = new int[4];
        double mvmatrix[] = new double[16];
        double projmatrix[] = new double[16];
        int realy = 0;// GL y coord pos
        double wcoord[] = new double[4];// wx, wy, wz;// returned xyz coords
    	
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
    	
        realy = viewport[3] - (int) yIn - 1;
        //System.out.println("Coordinates at cursor are (" + xIn + ", " + realy+")");
        //glu.gluUnProject((double) xIn, (double) realy, 0.0, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
        //System.out.println("World coords at z=0.0 are ( " + wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2] + ")");
        
        /*
        glu.gluUnProject((double) xIn, (double) realy, 1.0, //
            mvmatrix, 0,
            projmatrix, 0,
            viewport, 0, 
            wcoord, 0);
        System.out.println("World coords at z=1.0 are (" //
                           + wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2]
                           + ")");
                           */
        
        
    	/*
    	Matrix4f vMat = new Matrix4f(), pMat = new Matrix4f(), M = new Matrix4f();
    	vMat.setLookAt(0.0f, 0.0f, -4.5f,   0.0f, 0.0f, 0.0f,   0, 1, 0);
    	pMat.setPerspective((float)Math.toRadians(70.0), 4.0f/3.0f, 0.1f, 20.0f);
    	M.identity().mul(pMat).mul(vMat);
    	*/
    	
    	Vector4f viewBox[] = new Vector4f[4], unprojectViewBox[] = new Vector4f[4];
    	

    	
        switch(_pane)
    	{
	    	case Texture3DPane.XY_PANE:
	    	{
	        	viewBox[0] = new Vector4f(0.5f, -0.5f, -0.5f, 1.0f); // BL
	        	viewBox[1] = new Vector4f(-0.5f, -0.5f, -0.5f, 1.0f); //BR
	        	viewBox[2] = new Vector4f(-0.5f, 0.5f, -0.5f, 1.0f); //TR
	        	viewBox[3] = new Vector4f(0.5f, 0.5f, -0.5f, 1.0f); //TL
	        	//System.out.println("XY");
	    		break;
	    	}
	    	case Texture3DPane.XZ_PANE:
	    	{
	        	viewBox[0] = new Vector4f(-0.5f, -0.5f, -0.5f, 1.0f); //BL
	        	viewBox[1] = new Vector4f(0.5f, -0.5f, -0.5f, 1.0f); // BR
	        	viewBox[2] = new Vector4f(0.5f, -0.5f, 0.5f, 1.0f); //TR
	        	viewBox[3] = new Vector4f(-0.5f, -0.5f, 0.5f, 1.0f); //TL
	        	//System.out.println("XZ");
	    		break;
	    	}
	    	case Texture3DPane.YZ_PANE:
	    	{
	        	viewBox[0] = new Vector4f(-0.5f, 0.5f, -0.5f, 1.0f); // BL
	        	viewBox[1] = new Vector4f(-0.5f, -0.5f, -0.5f, 1.0f); //BR
	        	viewBox[2] = new Vector4f(-0.5f, -0.5f, 0.5f, 1.0f); //TR
	        	viewBox[3] = new Vector4f(-0.5f, 0.5f, 0.5f, 1.0f); //TL
	        	//System.out.println("YZ");
	    		break;
	    	}
    	}
        
		for(int i=0; i<4; i++)
		{
			viewBox[i].x*=spacing.x;
			viewBox[i].y*=spacing.y;
			viewBox[i].z*=spacing.z;
		}
		
		for(int i=0; i<4; i++)
		{
			glu.gluProject(viewBox[i].x, viewBox[i].y, viewBox[i].z, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
			unprojectViewBox[i] = new Vector4f((float)wcoord[0],(float)wcoord[1],(float)wcoord[2],(float)wcoord[3]);
			//System.out.println(unprojectViewBox[i].toString(new DecimalFormat( "#,###,###,##0.00" )));
		}
    	
    	//unprojectViewBox[0] = new Vector4f();
    	//unprojectViewBox[1] = new Vector4f();
    	//unprojectViewBox[2] = new Vector4f();
    	//unprojectViewBox[3] = new Vector4f();
    	
    	/*
    	for(int i=0; i<4; i++)
		{
    		M.transform(viewBox[i], unprojectViewBox[i]);
    		unprojectViewBox[i].div(unprojectViewBox[i].w);
    		unprojectViewBox[i].div(unprojectViewBox[i].z);
    		System.out.println(unprojectViewBox[i].toString(new DecimalFormat( "#,###,###,##0.00" )));
		}
		*/
    	
    	//float f =  1.0f / (float)Math.tan((float)Math.toRadians(70.0)/2.0f);
    	//float dy = 0.1f / f;
    	//float dx = (4.0f/3.0f)*dy;
    	
		//float pY = ((_height-yIn)-(_height/2.0f))/(_height/2.0f);
    	//float pX = (xIn-(_width/2.0f))/(_width/2.0f);
    	
    	//Vector3f viewPoint = new Vector3f(dx*pX, dy*pY, 0.1f);
    	//System.out.println(viewPoint.toString(new DecimalFormat( "#,###,###,##0.00" )));
    	
		//System.out.printf("Input pointer: (%f,  %f)\n", (float)xIn, (float)yIn);
		
    	float tX = ((float)xIn-unprojectViewBox[0].x)/(unprojectViewBox[1].x-unprojectViewBox[0].x);
    	float tY = ((float)realy-unprojectViewBox[0].y)/(unprojectViewBox[3].y-unprojectViewBox[0].y);
    	result.x = tX;
    	result.y = tY;
    	
    	//System.out.println(result.toString(new DecimalFormat( "#,###,###,##0.00" )));
    	
    	return result;
    }
    
}
