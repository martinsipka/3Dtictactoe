package com.example.dtictactoe.frontend;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import android.widget.Toast;

import com.example.dtictactoe.AI.ArtificialIntelligence;
import com.example.dtictactoe.AI.ScoreCheck;
import com.example.dtictactoe.animations.Animation;
import com.example.dtictactoe.backend.Move;

import java.util.Deque;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingDeque;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    public static final int STATE_CUBE = 0;
    public static final int STATE_MAKING_CUBE = -1;
    public static final int STATE_DESTROYING_CUBE = 1;
    public static final int STATE_FLOORS = 3;
    public static final int STATE_ZOOMING_IN = 4;
    public static final int STATE_ZOOMED_IN = 5;
    public static final int STATE_ZOOMING_OUT = 6;

    public static final int TURN_RED = 1;
    public static final int TURN_BLUE = 5;

    private static final float animSpeed = 0.02f;

    Context context;
    LineFloor lineFloor;
    private boolean rotate = true;

    private int[][][] playBoard = new int[4][4][4];
    private Stack<Move> history = new Stack<Move>();

    private static final String TAG = "RENDERTHREAD TAG";

    // Rotational angle and speed (NEW)
    float angleX = 0.0f;
    float speedX = 0.0f;
    float angleY = 0.0f;
    float speedY = 0.0f;
    float[][] floorCords = new float[4][3];
    int turn = 1;
    boolean dismem = true;
    int state;
    int zoomX;
    int zoomY;
    float cPosX;
    float cPosY;
    float cPosZ;
    ScoreCheck sc;

    Deque<Animation> animations;

    public MyGLRenderer(Context context) {
        this.context = context;
        lineFloor = new LineFloor(playBoard);
        sc = new ScoreCheck(playBoard);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear color and depth buffers
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(cPosX, cPosY, -6.0f + cPosZ * 4);
        gl.glRotatef(angleX, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(angleY, 1.0f, 0.0f, 0.0f);

        if (state == STATE_MAKING_CUBE) {
            makeCubeAnim();
        } else if (state == STATE_DESTROYING_CUBE) {
            dismemberCubeAnim();
        } else if (state == STATE_ZOOMING_IN) {
            zoomInAnim();
        } else if (state == STATE_ZOOMING_OUT) {
            zoomOutAnim();
        }

        gl.glEnable(GL10.GL_LINE_SMOOTH);


        for (int k = 0; k < 4; k++) {
            gl.glPushMatrix();
            gl.glTranslatef(floorCords[k][0], floorCords[k][1], floorCords[k][2]);

            lineFloor.drawFloor(gl, k);
            gl.glPopMatrix();
        }

        // Update the rotational angle after each refresh.
        angleX += speedX;
        angleY += speedY;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub


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
                    state = STATE_CUBE;
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
        if (rotate) {
            rotateAnimation();
        }
        if (dismem && !rotate) {
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
        } else if (!rotate) {

            for (int j = 0; j < 4; j++) {
                floorCords[j][2] -= (j + 3.0f) * animSpeed;
                if (floorCords[j][2] < -3.0f) {
                    for(int p = 0; p < 4; p++){
                        floorCords[p][2] = -3.0f;
                    }
                    state = STATE_FLOORS;

                }
            }
        }
    }

    private void zoomInAnim() {
        cPosX += zoomX * animSpeed;
        cPosY += zoomY * animSpeed;
        cPosZ += animSpeed;
        if (cPosX * zoomX > 1.0f) {
            state = STATE_ZOOMED_IN;
            cPosX = 1.0f * zoomX;
            cPosY = 1.0f * zoomY;
            cPosZ = 1.0f;
        }
    }

    private void zoomOutAnim() {
        cPosX -= zoomX * animSpeed;
        cPosY -= zoomY * animSpeed;
        cPosZ -= animSpeed;
        if (cPosX * zoomX < 0.0f){
            state = STATE_FLOORS;
            cPosX = 0.0f;
            cPosY = 0.0f;
            cPosZ = 0.0f;
        }
    }

    private void rotateAnimation() {
        int sgnX = 0, sgnY = 0;
        if (angleX > 180.0f) {
            sgnX = 1;
        } else if (angleX < 180.0f) {
            sgnX = -1;
        }
        if (angleY > 180.0f) {
            sgnY = 1;
        } else if (angleY < 180.0f) {
            sgnY = -1;
        }
        angleX += sgnX * 1.0f;
        angleY += sgnY * 1.0f;
        if (angleX > 360.0f || angleX < 0.0f) {
            angleX = 0.0f;
            if (angleY == 0.0f)
                rotate = false;
        }
        if (angleY > 360.0f || angleY < 0.0f) {
            angleY = 0.0f;
            if (angleX == 0.0f)
                rotate = false;
        }
    }

    public boolean markAsPlayer(int xCoor, int yCoor){
        int zCoor;
        if (zoomX == -1) {
            if (zoomY == 1)
                zCoor = 0;
            else
                zCoor = 2;
        } else {
            if (zoomY == 1)
                zCoor = 1;
            else
                zCoor = 3;
        }
        return markSquare(xCoor, yCoor, zCoor);
    }

    public boolean markSquare(Move move){
        return markSquare(move.getX(), move.getY(), move.getZ());
    }

    public boolean markSquare(int xCoor, int yCoor, int zCoor) {

        if (playBoard[xCoor][yCoor][zCoor] != 0)
            return false;

        history.add(new Move(xCoor, yCoor, zCoor, turn));

        Log.d("x y z: ", xCoor + " " + yCoor + " " + zCoor);

        playBoard[xCoor][yCoor][zCoor] = turn;
        //lineFloor.updateTable(playBoard);
        int checked = sc.check(xCoor, yCoor, zCoor);
        Log.d(TAG, Integer.toString(checked));
        if (checked == 1) {
            Toast.makeText(context, "Red win!", Toast.LENGTH_LONG).show();
            //  endGame();
        } else if (checked == 2) {
            Toast.makeText(context, "Blue win!", Toast.LENGTH_LONG).show();
            // endGame();
        }
        if (turn == 1) {
            turn = 5;
        } else {
            turn = 1;
        }
        state = STATE_ZOOMING_OUT;

        return true;
    }

    private void endGame() {
        state = STATE_CUBE;
        lineFloor.updateTable(new int[4][4][4]);
    }

    public void back() {
        Log.d("backing", "up");
        try {
            Move move = history.pop();
            int x = move.getX();
            int y = move.getY();
            int z = move.getZ();
            playBoard[x][y][z] = 0;
            move = history.pop();
            x = move.getX();
            y = move.getY();
            z = move.getZ();
            playBoard[x][y][z] = 0;
        } catch (EmptyStackException e){
            e.printStackTrace();
            Toast.makeText(context, "No more moves in history!", Toast.LENGTH_LONG).show();
        }
    }

    public void newGame(){
        playBoard = new int[4][4][4];
        sc.updateTable(playBoard);
        lineFloor.updateTable(playBoard);
    }

    public int[][][] getPlayBoard(){
        return playBoard;
    }
}
