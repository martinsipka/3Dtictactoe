package com.example.dtictactoe.frontend;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Floor {

	private FloatBuffer vertexBuffer; // Vertex Buffer

	// private FloatBuffer texBuffer; // Texture Coords Buffer

	// Constructor - Set up the vertex buffer
	public Floor() {
		// Allocate vertex buffer. An float has 4 bytes
		ByteBuffer vbb = ByteBuffer.allocateDirect(12 * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();

		// Read images. Find the aspect ratio and adjust the vertices
		// accordingly.

		// Define the vertices for this face
		float[] vertices = {
				// top sqare
				-1.0f, 0.5f, -1.0f,
				1.0f, 0.5f, -1.0f,
				-1.0f, 0.5f, 1.0f,
				1.0f, 0.5f, 1.0f,
				// side rectangle
				-1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.5f, 0.0f, 1.0f,
				0.5f, 0.0f,
				//single sqare
				-1.0f, 0.0f, -1.0f,
				1.0f, 0.0f, -1.0f,
				-1.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 1.0f 
				 
		};

		vertexBuffer.put(vertices); // Populate

		vertexBuffer.position(0); // Rewind

		// Allocate texture buffer. An float has 4 bytes. Repeat for 6 faces.
		/*
		 * float[] texCoords = { 0.0f, 1.0f, // A. left-bottom 1.0f, 1.0f, // B.
		 * right-bottom 0.0f, 0.0f, // C. left-top 1.0f, 0.0f // D. right-top };
		 * ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4 );
		 * tbb.order(ByteOrder.nativeOrder()); texBuffer = tbb.asFloatBuffer();
		 * for (int face = 0; face < 6; face++) { texBuffer.put(texCoords); }
		 * texBuffer.position(0); // Rewind
		 */
	}

	// Render the shape
	public void draw(GL10 gl) {
		gl.glFrontFace(GL10.GL_CCW);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		//gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		// gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
		
		gl.glColor4f(0.5f, 0.5f, 0.4f, 0.5f);
		
		// front
		gl.glPushMatrix();
		gl.glColor4f(0.5f, 0.5f, 0.4f, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, -0.5f, 0.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, 0.5f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, 0.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, -0.5f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, -1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glPopMatrix();
		
		gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
		
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, 0.5f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, 0.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, -0.5f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, -1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glPopMatrix();

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	public void drawCube(GL10 gl) {

		gl.glFrontFace(GL10.GL_CCW);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		//gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glColor4f(0.0f, 1.0f, 0.0f, 0.2f);
		//draw horizontal sqares
		drawSqares(gl);

		//draw vertical sqares
		//gl.glTranslatef(0.0f, 1.0f, -1.0f);
		gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
		drawSqares(gl);
		gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
		drawSqares(gl);

	}

	// Load images into 6 GL textures
	public void loadTexture(GL10 gl) {

	}
	
	public void drawSqares(GL10 gl){
		
		
		

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, -1.0f, 0.0f);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 8, 4);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, -0.5f, 0.0f);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 8, 4);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 8, 4);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.5f, 0.0f);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 8, 4);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 1.0f, 0.0f);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 8, 4);
		gl.glPopMatrix();
		
	}

}
