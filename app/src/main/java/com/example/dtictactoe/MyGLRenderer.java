package com.example.dtictactoe;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import android.widget.Toast;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    Context context;
    Floor floor;
    LineFloor lf;
    private boolean rotate = true;

    private int[][][] a = new int[4][4][4];

    private static final String TAG = "RENDERTHREAD TAG";
    private static final float animSpeed = 0.02f;
    // Rotational angle and speed (NEW)
    float angleX = 0.0f;
    float speedX = 0.0f;
    float angleY = 0.0f;
    float speedY = 0.0f;
    float p = 0;
    float[][] floorCords = new float[4][3];
    int turn = 1;
    boolean dismem = true;
    int state = 0;
    private int cWidth;
    private int cHeight;
    int zoomX;
    int zoomY;
    float cPosX;
    float cPosY;
    float cPosZ;
    ScoreCheck sc;

    boolean lightingEnabled = false;   // Is lighting on? (NEW)
    private float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
    private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] lightPosition = {0.0f, 0.0f, 2.0f, 1.0f};

    public MyGLRenderer(Context context) {
        // Set up the data-array buffers for these shapes ( NEW )
        this.context = context;
        lf = new LineFloor(a);
        sc = new ScoreCheck();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear color and depth buffers
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity(); // Reset the current model-view matrix
        gl.glTranslatef(cPosX, cPosY, -6.0f + cPosZ * 4); // Translate into the screen (NEW)
        gl.glRotatef(angleX, 0.0f, 1.0f, 0.0f); // Rotate (NEW)
        gl.glRotatef(angleY, 1.0f, 0.0f, 0.0f); // Rotate (NEW)

        if (false) {

            gl.glEnable(GL10.GL_BLEND);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            //gl.glBlendFunc(GL10.GL_ZERO, GL10.GL_SRC_COLOR);

            p = p + 0.05f;
            float x = (float) ((float) 0.1f * Math.sin(p)) + 0.1f;


            // draw single floors not together
            gl.glTranslatef(0.0f, 0.0f + x, 0.0f);
            floor.draw(gl);
            gl.glTranslatef(0.0f, 0.5f + x, 0.0f);
            floor.draw(gl);
            gl.glTranslatef(0.0f, -1.0f - 2 * x, 0.0f);
            floor.draw(gl);
            gl.glTranslatef(0.0f, -0.5f - x, 0.0f);
            floor.draw(gl);
        } else {

            if (state == -1) {
                makeCubeAnim();
            } else if (state == 1) {
                dismemberCubeAnim();
            } else if (state ==4) {
                zoomInAnim();
            } else if(state == 6){
                zoomOutAnim();
            }
            //gl.glDisable(GL10.GL_CULL_FACE);
            gl.glEnable(GL10.GL_LINE_SMOOTH);


            for (int k = 0; k < 4; k++) {
                gl.glPushMatrix();
                gl.glTranslatef(floorCords[k][0], floorCords[k][1], floorCords[k][2]);

                lf.drawFloor(gl, k);
                gl.glPopMatrix();
            }

        }
        // Update the rotational angle after each refresh.
        angleX += speedX;
        angleY += speedY;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub

        cWidth = width;
        cHeight = height;
        if (height == 0)
            height = 1; // To prevent divide by zero
        float aspect = (float) width / height;

        // Set the viewport (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
        gl.glLoadIdentity(); // Reset projection matrix
        // Use perspective projection
        GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW); // Select model-view matrix
        gl.glLoadIdentity();

    }

    // Call back when the surface is first created or re-created.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // Set color's clear-value to
        // black
        gl.glClearDepthf(1.0f); // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST); // Enables depth-buffer for hidden
        // surface removal
        gl.glDepthFunc(GL10.GL_LEQUAL); // The type of depth testing to do
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); // nice

        // perspective
        // view
        gl.glShadeModel(GL10.GL_SMOOTH); // Enable smooth shading of color
        gl.glDisable(GL10.GL_DITHER); // Disable dithering for better
      /*  gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbient, 0);
        //gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPosition, 0);
        gl.glEnable(GL10.GL_LIGHT1);   // Enable Light 1 (NEW)
        gl.glEnable(GL10.GL_LIGHT0);  							// performance

		// Setup Texture, each time the surface is created (NEW)
		// cube.loadTexture(gl); // Load image into Texture (NEW)
		//gl.glEnable(GL10.GL_TEXTURE_2D); // Enable texture (NEW)*/

        for (int i = 0; i < 4; i++) {
            floorCords[i][0] = 0.0f;
            floorCords[i][1] = 0.0f;
            floorCords[i][2] = -0.75f + 0.5f * i;
        }

    }

    private void makeCubeAnim() {
        int sign = -1;
        if (dismem) {
            for (int i = 0; i < 4; i++) {
                if (i % 2 == 1)
                    sign = -1 * sign;
                floorCords[i][0] += sign * animSpeed;
                if (i % 3 == 0)
                    sign = -sign;
                floorCords[i][1] += sign * animSpeed;
                sign = state;

                if (floorCords[3][1] < 0.0f) {
                    for (int j = 0; j < 4; j++) {
                        floorCords[j][0] = 0.0f;
                        floorCords[j][1] = 0.0f;
                        floorCords[j][2] = -0.75f + 0.5f * j;
                    }
                    state = 0;
                    rotate = true;
                }
            }
        } else {

            for (int j = 0; j < 4; j++) {
                floorCords[j][2] += (j + 3.0f) * 0.01f;
                if (floorCords[j][2] > 0.75f) {
                    for (int i = 0; i < 4; i++) {
                        floorCords[i][2] = -0.75f + 0.5f * i;
                    }
                    dismem = true;
                }
            }

        }
    }

    private void dismemberCubeAnim() {
        int sign = 1;
        if (rotate){
            rotateAnimation();
        } if (dismem && !rotate) {
            for (int i = 0; i < 4; i++) {
                if (i % 2 == 1)
                    sign = -1 * sign;
                floorCords[i][0] += sign * animSpeed;
                if (i % 3 == 0)
                    sign = -sign;
                floorCords[i][1] += sign * animSpeed;
                sign = state;

                if (floorCords[3][1] > 1.15f) {

                    dismem = false;

                }
            }
        } else if(!rotate){

            for (int j = 0; j < 4; j++) {
                floorCords[j][2] -= (j + 3.0f) * animSpeed;
                if (floorCords[j][2] < -3.0f) {

                    state = 3;

                }
            }
        }
    }

    private void zoomInAnim() {
        cPosX += zoomX * animSpeed;
        cPosY += zoomY * animSpeed;
        cPosZ += animSpeed;
        if (cPosX*zoomX > 1.0f)
            state = 5;
    }

    private void zoomOutAnim() {
        cPosX -= zoomX * animSpeed;
        cPosY -= zoomY *animSpeed;
        cPosZ -= animSpeed;
        if (cPosX * zoomX < 0.0f)
            state = 3;
    }

    private void rotateAnimation(){
        int sgnX = 0, sgnY = 0;
        if(angleX > 180.0f){
            sgnX  = 1;
        }else if(angleX < 180.0f){
            sgnX = -1;
        }
        if(angleY > 180.0f){
            sgnY = 1;
        }else if(angleY < 180.0f){
            sgnY = -1;
        }
        angleX += sgnX * 1.0f;
        angleY += sgnY * 1.0f;
        if(angleX > 360.0f || angleX < 0.0f){
            angleX = 0.0f;
            if(angleY == 0.0f)
                rotate = false;
        }
        if(angleY > 360.0f || angleY < 0.0f){
            angleY = 0.0f;
            if(angleX == 0.0f)
                rotate = false;
        }
    }

    public void markSquare(int xCoor, int yCoor){

        int zCoor;
        if(zoomX == -1){
            if(zoomY == 1)
                zCoor = 0;
            else
                zCoor = 2;
        }else{
            if(zoomY == 1)
                zCoor = 1;
            else
                zCoor = 3;
        }
        if(a[xCoor][yCoor][zCoor] !=0 )
            return;
        a[xCoor][yCoor][zCoor] = turn;
        lf.updateTable(a);
        int checked = sc.check(xCoor, yCoor, zCoor, turn);
        Log.d(TAG, Integer.toString(checked));
        if(checked == 1) {
            Toast.makeText(context, "Red win!", Toast.LENGTH_LONG).show();
          //  endGame();
        }else if(checked == 2){
            Toast.makeText(context, "Blue win!", Toast.LENGTH_LONG).show();
           // endGame();
        }
        if(turn == 1)
            turn = 5;
        else
            turn = 1;
        state = 6;
    }

    private void endGame(){
       state = -1;
       lf.updateTable(new int[4][4][4]);
       sc.resetBoard();
    }
}
