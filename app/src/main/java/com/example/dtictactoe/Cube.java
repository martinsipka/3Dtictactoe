package com.example.dtictactoe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/*
 * A photo cube with 6 pictures (textures) on its 6 faces.
 */
public class Cube {
	private FloatBuffer vertexBuffer; // Vertex Buffer
	private FloatBuffer texBuffer; // Texture Coords Buffer

	private int numFaces = 3;
	private int[] imageFileIDs = { // Image file IDs
            //R.raw.crate, R.raw.crate, R.raw.crate };
    };
	private int[] textureIDs = new int[numFaces];
	private Bitmap[] bitmap = new Bitmap[numFaces];
	private float cubeHalfSize = 1.0f;

	// Constructor - Set up the vertex buffer
	public Cube(Context context) {
		// Allocate vertex buffer. An float has 4 bytes
		ByteBuffer vbb = ByteBuffer.allocateDirect(12 * 4 * 6);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();

		// Read images. Find the aspect ratio and adjust the vertices
		// accordingly.
		bitmap[2] = BitmapFactory.decodeStream(context.getResources()
				.openRawResource(imageFileIDs[2]));
		for (int face = 0; face < 6; face++) {
			bitmap[face/3] = BitmapFactory.decodeStream(context.getResources()
					.openRawResource(imageFileIDs[face/3]));
			int imgWidth = bitmap[0].getWidth();
			int imgHeight = bitmap[0].getHeight();
			float faceWidth = 2.0f;
			float faceHeight = 2.0f;
			// Adjust for aspect ratio
			if (imgWidth > imgHeight) {
				faceHeight = faceHeight * imgHeight / imgWidth;
			} else {
				faceWidth = faceWidth * imgWidth / imgHeight;
			}
			float faceLeft = -faceWidth / 2;
			float faceRight = -faceLeft;
			float faceTop = faceHeight / 2;
			float faceBottom = -faceTop;

			// Define the vertices for this face
			float[] vertices = { faceLeft, faceBottom, 0.0f, // 0.
																// left-bottom-front
					faceRight, faceBottom, 0.0f, // 1. right-bottom-front
					faceLeft, faceTop, 0.0f, // 2. left-top-front
					faceRight, faceTop, 0.0f, // 3. right-top-front
			};
			vertexBuffer.put(vertices); // Populate
		}
		vertexBuffer.position(0); // Rewind

		// Allocate texture buffer. An float has 4 bytes. Repeat for 6 faces.
		float[] texCoords = { 0.0f, 1.0f, // A. left-bottom
				1.0f, 1.0f, // B. right-bottom
				0.0f, 0.0f, // C. left-top
				1.0f, 0.0f // D. right-top
		};
		ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4
				* 6);
		tbb.order(ByteOrder.nativeOrder());
		texBuffer = tbb.asFloatBuffer();
		for (int face = 0; face < 6; face++) {
			texBuffer.put(texCoords);
		}
		texBuffer.position(0); // Rewind
	}

	// Render the shape
	public void draw(GL10 gl, int tf) {
		gl.glFrontFace(GL10.GL_CCW);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);

		// front
		gl.glPushMatrix();
		gl.glTranslatef(0f, 0f, cubeHalfSize);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[tf]);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glPopMatrix();

		// left
		gl.glPushMatrix();
		gl.glRotatef(270.0f, 0f, 1f, 0f);
		gl.glTranslatef(0f, 0f, cubeHalfSize);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[tf]);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glPopMatrix();

		// back
		gl.glPushMatrix();
		gl.glRotatef(180.0f, 0f, 1f, 0f);
		gl.glTranslatef(0f, 0f, cubeHalfSize);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[tf]);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
		gl.glPopMatrix();

		// right
		gl.glPushMatrix();
		gl.glRotatef(90.0f, 0f, 1f, 0f);
		gl.glTranslatef(0f, 0f, cubeHalfSize);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[tf]);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
		gl.glPopMatrix();

		// top
		gl.glPushMatrix();
		gl.glRotatef(270.0f, 1f, 0f, 0f);
		gl.glTranslatef(0f, 0f, cubeHalfSize);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[tf]);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
		gl.glPopMatrix();

		// bottom
		gl.glPushMatrix();
		gl.glRotatef(90.0f, 1f, 0f, 0f);
		gl.glTranslatef(0f, 0f, cubeHalfSize);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[tf]);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
		gl.glPopMatrix();

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	// Load images into 6 GL textures
	public void loadTexture(GL10 gl) {
		gl.glGenTextures(3, textureIDs, 0); // Generate texture-ID array for 6
											// IDs

		// Generate OpenGL texture images

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);

		// Build Texture from loaded bitmap for the currently-bind texture ID
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap[0], 0);
		bitmap[0].recycle();

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[1]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		// Build Texture from loaded bitmap for the currently-bind texture ID
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap[1], 0);
		bitmap[1].recycle();

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[2]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		// Build Texture from loaded bitmap for the currently-bind texture ID
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap[2], 0);
		bitmap[2].recycle();

	}
}