package de.ckehl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;


import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.common.nio.PointerBuffer;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.GLBuffers;


/**
 * 
 * @author christian
 *
 *	layout:
 *
 *	  6----7
 *	 /|   /|
 *	2-4 -3 5
 *	|/   |/
 *	0 -- 1
 */

public class Cubrille extends GeometryContainer /*implements Geometry*/ {
	protected static short max_val = (short)65535;
	protected static Float extentX = 1.0f;
	protected static Float extentY = 1.0f;
	protected static Float extentZ = 1.0f;
	
	protected static Float spacingX = 1.0f;
	protected static Float spacingY = 1.0f;
	protected static Float spacingZ = 1.0f;
	
	public static void setMaxValue(short v)
	{
		max_val = v;
	}
	
	public static void setExtentX(Float x)
	{
		extentX = x;
	}

	public static void setExtentY(Float y)
	{
		extentY = y;
	}
	
	public static void setExtentZ(Float z)
	{
		extentZ = z;
	}
	
	public static void setSpacingX(Float sx)
	{
		spacingX = sx;
	}

	public static void setSpacingY(Float sy)
	{
		spacingY = sy;
	}
	
	public static void setSpacingZ(Float sz)
	{
		spacingZ = sz;
	}
	
	protected Float _centerX, _centerY, _centerZ;
	protected Short _value;
	protected IntBuffer _vbos, _vao;
	
	public Cubrille()
	{
		super();
		_centerX = 0.0f;
		_centerY = 0.0f;
		_centerZ = 0.0f;
		_vbos = Buffers.newDirectIntBuffer(3);
		_isUpdated = false;
	}
	
	public Cubrille(float cx, float cy, float cz, short value)
	{
		super();
		_centerX = cx;
		_centerY = cy;
		_centerZ = cz;
		_value = value;
		_vbos = Buffers.newDirectIntBuffer(3);
		_isUpdated = false;
	}
	
	public void dispose(GL2 gl2)
	{
		gl2.glDeleteBuffers(3, _vbos);
		_vbos = null;
	}
	
	@Override
	public void render(GL2 gl2) {
		// TODO Auto-generated method stub
		
		super.render(gl2);
		
		/*
		 * use a shader for the LUT
		 */
		
    	gl2.glMatrixMode( GL2.GL_MODELVIEW );
        // draw a triangle filling the window
        //gl2.glLoadIdentity();
        
        //setupImmediateMode(gl2);
        renderVBO(gl2);

	}

	@Override
	public void setup(GL2 gl2) {
		// TODO Auto-generated method stub
		if(_isUpdated==false)
		{
			if(_printSetupLine)
				System.out.println("updating "+this.toString()+"("+Long.toString(_ID)+") ...");
			//setupImmediateMode(gl2);
			setupVBOs(gl2);

	        if(_printSetupLine)
	        	System.out.println(this.toString()+"("+Long.toString(_ID)+"): up-to-date");
	        _isUpdated = true;
		}
		
		super.setup(gl2);


	}
	
	private void setupVBOs(GL2 gl2)
	{
		//FloatBuffer vertices = getVerticesAsBuffer();
		
		float[] verts = {
				(_centerX*spacingX)-0.5f*extentX, (_centerY*spacingY)-0.5f*extentY, (_centerZ*spacingZ)-0.5f*extentZ,
				(_centerX*spacingX)+0.5f*extentX, (_centerY*spacingY)-0.5f*extentY, (_centerZ*spacingZ)-0.5f*extentZ,
				(_centerX*spacingX)-0.5f*extentX, (_centerY*spacingY)+0.5f*extentY, (_centerZ*spacingZ)-0.5f*extentZ,
				(_centerX*spacingX)+0.5f*extentX, (_centerY*spacingY)+0.5f*extentY, (_centerZ*spacingZ)-0.5f*extentZ,
				(_centerX*spacingX)-0.5f*extentX, (_centerY*spacingY)-0.5f*extentY, (_centerZ*spacingZ)+0.5f*extentZ,
				(_centerX*spacingX)+0.5f*extentX, (_centerY*spacingY)-0.5f*extentY, (_centerZ*spacingZ)+0.5f*extentZ,
				(_centerX*spacingX)-0.5f*extentX, (_centerY*spacingY)+0.5f*extentY, (_centerZ*spacingZ)+0.5f*extentZ,
				(_centerX*spacingX)+0.5f*extentX, (_centerY*spacingY)+0.5f*extentY, (_centerZ*spacingZ)+0.5f*extentZ
		};
		
		/*
		float[] verts = {
				_centerX-0.5f, _centerY-0.5f, _centerZ-0.5f,
				_centerX+0.5f, _centerY-0.5f, _centerZ-0.5f,
				_centerX-0.5f, _centerY+0.5f, _centerZ-0.5f,
				_centerX+0.5f, _centerY+0.5f, _centerZ-0.5f,
				_centerX-0.5f, _centerY-0.5f, _centerZ+0.5f,
				_centerX+0.5f, _centerY-0.5f, _centerZ+0.5f,
				_centerX-0.5f, _centerY+0.5f, _centerZ+0.5f,
				_centerX+0.5f, _centerY+0.5f, _centerZ+0.5f
		};
		*/
		FloatBuffer vertices = GLBuffers.newDirectFloatBuffer(verts.length);
		vertices.put(verts);
		vertices.rewind();
		
		//FloatBuffer colours = getInitialColoursAsBuffer();
		float valueF = new Float(_value)/new Float(max_val);
		float[] cols = {
				valueF, valueF, valueF,
				valueF, valueF, valueF,
				valueF, valueF, valueF,
				valueF, valueF, valueF,
				valueF, valueF, valueF,
				valueF, valueF, valueF,
				valueF, valueF, valueF,
				valueF, valueF, valueF
		};
		FloatBuffer colours = GLBuffers.newDirectFloatBuffer(cols.length);
		colours.put(cols);
		colours.rewind();

		
        byte[] indices = { 0, 1, 2, 2, 1, 3,   4, 0, 6, 6, 0, 2,   1, 5, 3, 3, 5, 7,   5, 4, 7, 7, 4, 6,   4, 5, 0, 0, 5, 1,   2, 3, 6, 6, 3, 7};
        ByteBuffer indicesBuf = GLBuffers.newDirectByteBuffer(indices.length);
        indicesBuf.put(indices);
        indicesBuf.rewind();
		
		//gl2.glGenVertexArrays(1, _vao);
		//gl2.glBindVertexArray(_vao.get(0));
		gl2.glGenBuffers(3, _vbos);
		
		gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, _vbos.get(0));
		gl2.glBufferData(GL.GL_ARRAY_BUFFER, 8*3*VersionHelper.FloatBYTES(), vertices, GL.GL_STATIC_DRAW);
		gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

		
		gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, _vbos.get(1));
		gl2.glBufferData(GL.GL_ARRAY_BUFFER, 8*3*VersionHelper.FloatBYTES(), colours, GL.GL_STATIC_DRAW);
		gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		
		gl2.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, _vbos.get(2));
		gl2.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, 6*6*VersionHelper.ByteBYTES(), indicesBuf, GL.GL_STATIC_DRAW);
		gl2.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	private FloatBuffer getVerticesAsBuffer()
	{
		FloatBuffer vertices = Buffers.newDirectFloatBuffer(8*3);
		// 0
		vertices.put(_centerX-0.5f*extentX);
		vertices.put(_centerY-0.5f*extentY);
		vertices.put(_centerZ-0.5f*extentZ);
		// 1
		vertices.put(_centerX+0.5f*extentX);
		vertices.put(_centerY-0.5f*extentY);
		vertices.put(_centerZ-0.5f*extentZ);
		// 2
		vertices.put(_centerX-0.5f*extentX);
		vertices.put(_centerY+0.5f*extentY);
		vertices.put(_centerZ-0.5f*extentZ);
		// 3
		vertices.put(_centerX+0.5f*extentX);
		vertices.put(_centerY+0.5f*extentY);
		vertices.put(_centerZ-0.5f*extentZ);
		// 4
		vertices.put(_centerX-0.5f*extentX);
		vertices.put(_centerY-0.5f*extentY);
		vertices.put(_centerZ+0.5f*extentZ);
		// 5
		vertices.put(_centerX+0.5f*extentX);
		vertices.put(_centerY-0.5f*extentY);
		vertices.put(_centerZ+0.5f*extentZ);
		// 6
		vertices.put(_centerX-0.5f*extentX);
		vertices.put(_centerY+0.5f*extentY);
		vertices.put(_centerZ+0.5f*extentZ);
		// 7
		vertices.put(_centerX+0.5f*extentX);
		vertices.put(_centerY+0.5f*extentY);
		vertices.put(_centerZ+0.5f*extentZ);
		
		vertices.flip();
		return vertices;
	}
	
	private FloatBuffer getInitialColoursAsBuffer()
	{
		FloatBuffer colours = Buffers.newDirectFloatBuffer(8*3);
		float valueF = new Float(_value)/new Float(max_val);
		for(int i=0;i<8*3;i++)
		{
			colours.put(valueF);
		}
		colours.flip();
		return colours;
	}
	
	
	
	private void setupImmediateMode(GL2 gl2)
	{
        float valueF = new Float(_value)/new Float(max_val);
        //System.out.printf("Value: %f\n", valueF);
        
        // Front
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY-0.5f, _centerZ-0.5f ); // 0
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY-0.5f, _centerZ-0.5f ); // 1
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY+0.5f, _centerZ-0.5f ); // 2
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY+0.5f, _centerZ-0.5f ); // 2
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY-0.5f, _centerZ-0.5f ); // 1
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY+0.5f, _centerZ-0.5f ); // 3
        gl2.glEnd();
        
        // Left
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY-0.5f, _centerZ+0.5f ); // 4
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY-0.5f, _centerZ-0.5f ); // 0
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY+0.5f, _centerZ+0.5f ); // 6
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY+0.5f, _centerZ+0.5f ); // 6
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY-0.5f, _centerZ-0.5f ); // 0
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY+0.5f, _centerZ-0.5f ); // 2
        gl2.glEnd();
        
        // Right
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY-0.5f, _centerZ-0.5f ); // 1
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY-0.5f, _centerZ+0.5f ); // 5
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY+0.5f, _centerZ-0.5f ); // 3
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY+0.5f, _centerZ-0.5f ); // 3
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY-0.5f, _centerZ+0.5f ); // 5
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY+0.5f, _centerZ+0.5f ); // 7
        gl2.glEnd();
        
        // Back
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY-0.5f, _centerZ+0.5f ); // 5
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY-0.5f, _centerZ+0.5f ); // 4
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY+0.5f, _centerZ+0.5f ); // 7
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY+0.5f, _centerZ+0.5f ); // 7
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY-0.5f, _centerZ+0.5f ); // 4
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY+0.5f, _centerZ+0.5f ); // 6
        gl2.glEnd();
        
        // Bottom
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY-0.5f, _centerZ+0.5f ); // 4
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY-0.5f, _centerZ+0.5f ); // 5
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY-0.5f, _centerZ-0.5f ); // 0
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY-0.5f, _centerZ-0.5f ); // 0
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY-0.5f, _centerZ+0.5f ); // 5
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY-0.5f, _centerZ-0.5f ); // 1
        gl2.glEnd();
        
        // Top
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY+0.5f, _centerZ-0.5f ); // 2
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY+0.5f, _centerZ-0.5f ); // 3
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY+0.5f, _centerZ+0.5f ); // 6
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX-0.5f, _centerY+0.5f, _centerZ+0.5f ); // 6
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY+0.5f, _centerZ-0.5f ); // 3
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( _centerX+0.5f, _centerY+0.5f, _centerZ+0.5f ); // 7
        gl2.glEnd();
	}
	
	private void renderVBO(GL2 gl2)
	{
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, _vbos.get(0));
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, 0l);
		

		gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
		gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, _vbos.get(1));
		gl2.glColorPointer(3, GL2.GL_FLOAT, 0, 0l);
		

        //glDrawArrays(GL_TRIANGLES, 0, vertices);

        gl2.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, _vbos.get(2));
        gl2.glDrawElements(GL.GL_TRIANGLES, 36, GL.GL_UNSIGNED_BYTE, 0l);
        
        gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
	}

	@Override
	public String toString()
	{
		return "Cubrille";
	}
}
