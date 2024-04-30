package de.ckehl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class Cubille {
	
	protected static short max_val = (short)65535;
	
	protected static void setMaxValue(short v)
	{
		max_val = v;
	}
	
	protected static void renderTextured( GL2 gl2, float cx, float cy, float cz ) {
        
    	gl2.glMatrixMode( GL2.GL_MODELVIEW );
        // draw a triangle filling the window
        gl2.glLoadIdentity();
        
        // Front
        gl2.glBegin( GL.GL_TRIANGLES );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 0, 0);
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 0, 0);
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz-0.5f );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 1, 0);
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz-0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 1, 0);
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz-0.5f );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 0, 0);
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz-0.5f );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 1, 0);
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        gl2.glEnd();
        
        // Left
        gl2.glBegin( GL.GL_TRIANGLES );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 0, 1);
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz+0.5f );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 0, 0);
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 1, 1);
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 1, 1);
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 0, 0);
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 1, 0);
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz-0.5f );
        gl2.glEnd();
        
        // Right
        gl2.glBegin( GL.GL_TRIANGLES );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 0, 0);
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz-0.5f );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 0, 1);
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 1, 0);
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 1, 0);
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 0, 1);
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 1, 1);
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();
        
        // Back
        gl2.glBegin( GL.GL_TRIANGLES );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 0, 1);
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 0, 1);
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz+0.5f );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 1, 1);
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 1, 1);
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 1, 1);
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz+0.5f );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 0, 1);
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glEnd();
        
        // Bottom
        gl2.glBegin( GL.GL_TRIANGLES );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 0, 1);
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz+0.5f );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 0, 1);
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 0, 0);
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 0, 0);
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 0, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 0, 1);
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 0, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 0, 0);
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz-0.5f );
        gl2.glEnd();
        
        // Top
        gl2.glBegin( GL.GL_TRIANGLES );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 1, 0);
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz-0.5f );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 1, 0);
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 1, 1);
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE0, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 0, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 0, 1, 1);
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        //gl2.glColor3f( 1, 0, 0 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 1, 0);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 1, 0);
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        //gl2.glColor3f( 0, 0, 1 );
        gl2.glTexCoord3f(GL.GL_TEXTURE1, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, 1, 1, 1);
        gl2.glMultiTexCoord3f(GL.GL_TEXTURE1, 1, 1, 1);
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();
    }

    protected static void render( GL2 gl2, float cx, float cy, float cz ) {
        
    	//gl2.glMatrixMode( GL2.GL_MODELVIEW );
        // draw a triangle filling the window
        //gl2.glLoadIdentity();
        
        //gl2.glRotatef(45, 1.0f, 0, 0);
        //gl2.glRotatef(45, 0, 1.0f, 0);
        
        // Front
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz-0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz-0.5f );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        gl2.glEnd();
        
        // Left
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz-0.5f );
        gl2.glEnd();
        
        // Right
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();
        
        // Back
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz+0.5f );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glEnd();
        
        // Bottom
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz-0.5f );
        gl2.glEnd();
        
        // Top
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz-0.5f );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();
    }

    protected static void render( GL2 gl2, float cx, float cy, float cz, short value ) {
        
    	gl2.glMatrixMode( GL2.GL_MODELVIEW );
        // draw a triangle filling the window
        gl2.glLoadIdentity();
        
        //gl2.glRotatef(45, 1.0f, 0, 0);
        //gl2.glRotatef(45, 0, 1.0f, 0);
        
        float valueF = new Float(value)/new Float(max_val);
        //System.out.printf("Value: %f\n", valueF);
        
        // Front
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz-0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz-0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        gl2.glEnd();
        
        // Left
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz-0.5f );
        gl2.glEnd();
        
        // Right
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();
        
        // Back
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz+0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glEnd();
        
        // Bottom
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy-0.5f, cz-0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz+0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy-0.5f, cz-0.5f );
        gl2.glEnd();
        
        // Top
        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz-0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx-0.5f, cy+0.5f, cz+0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz-0.5f );
        gl2.glColor3f( valueF, valueF, valueF );
        gl2.glVertex3f( cx+0.5f, cy+0.5f, cz+0.5f );
        gl2.glEnd();
    }
}
