package com.example.dtictactoe.frontend;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import com.example.dtictactoe.AI.ScoreCheck;
import com.example.dtictactoe.backend.AlgebraObjects.Quaternion;
import com.example.dtictactoe.frontend.animations.Animation;
import com.example.dtictactoe.backend.Move;
import com.example.dtictactoe.frontend.animations.ZoomOutAnimation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.EmptyStackException;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    public static final int STATE_CUBE = 0;
    public static final int STATE_FLOORS = 3;
    public static final int STATE_ZOOMED_IN = 5;

    public static final int TURN_RED = 1;
    public static final int TURN_BLUE = 5;

    private static final float lighPosition[] = {5.0f, 30.0f, 10.0f, 0.0f};

    Context context;
    LineFloor lineFloor;

    private int[][][] playBoard = new int[4][4][4];
    private Stack<Move> history = new Stack<Move>();

    private static final String TAG = "RENDERTHREAD TAG";

    // Rotational angle and speed (NEW)
    float mDeltaX = 0.0f;
    float mDeltaY = 0.0f;
    public float[][] floorCords = new float[4][3];
    int turn = 1;
    public boolean wasRotation = true;
    public int state = STATE_CUBE;
    public int zoomX;
    public int zoomY;
    public float cPosX;
    public float cPosY;
    public float cPosZ;
    ScoreCheck sc;
    private FloatBuffer lightPositionBuffer;

    //Rotation matrices
    public Quaternion currentRotation = new Quaternion();

    Queue<Animation> animations;
    private GameView viewReference;

    public MyGLRenderer(Context context, GameView viewReference) {
        this.context = context;
        lineFloor = new LineFloor(playBoard);
        sc = new ScoreCheck(playBoard);
        animations = new LinkedBlockingQueue<Animation>();
        this.viewReference = viewReference;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear color and depth buffers


        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(cPosX, cPosY, -7.0f + cPosZ * 4);


        // Set a matrix that contains the current rotation.
        if (wasRotation) {
            /*Matrix.rotateM(mCurrentRotation, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
            Matrix.rotateM(mCurrentRotation, 0, mDeltaY, 1.0f, 0.0f, 0.0f);*/
            //Using quaternions
            currentRotation.mulInplace(new Quaternion(0.0f, 1.0f, 0.0f, -mDeltaX));
            currentRotation.mulInplace(new Quaternion(1.0f, 0.0f, 0.0f, -mDeltaY));
            mDeltaX = 0.0f;
            mDeltaY = 0.0f;

            // Multiply the current rotation by the accumulated rotation, and then set the accumulated
            // rotation to the result.
            /*Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
            System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);*/
        }


        if (!animations.isEmpty()) {
            Animation currentAnim = animations.element();
            if (currentAnim.perform(this)) {
                animations.remove();
            }
            viewReference.requestRender();
        }

        gl.glMultMatrixf(currentRotation.toMatrix().getAsArray(), 0);

        gl.glEnable(GL10.GL_LINE_SMOOTH);


        for (int k = 0; k < 4; k++) {
            gl.glPushMatrix();
            gl.glTranslatef(floorCords[k][0], floorCords[k][1], floorCords[k][2]);

            lineFloor.drawFloor(gl, k);
            gl.glPopMatrix();
        }
        //gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer);

        // Update the rotational angle after each refresh.
        wasRotation = false;


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
        Log.d("aspect", Float.toString(aspect));
        float yangle = 25 / aspect;
        // Use perspective projection
        GLU.gluPerspective(gl, yangle, aspect, 0.1f, 100.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW); // Select model-view matrix
        gl.glLoadIdentity();

    }

    // Call back when the surface is first created or re-created.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // Set color's clear-value to
        //gl.glClearColor(176/255.0f, 190/255.0f, 197/255.0f, 1.0f);
        // black
        gl.glClearDepthf(1.0f); // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST); // Enables depth-buffer for hidden
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glEnable(GL10.GL_COLOR_MATERIAL);

        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 4);
        vbb.order(ByteOrder.nativeOrder());
        lightPositionBuffer = vbb.asFloatBuffer();
        lightPositionBuffer.put(lighPosition);
        lightPositionBuffer.position(0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer);

        gl.glEnable(GL10.GL_LIGHT0);
        float ambientLight[] = {0.3f, 0.3f, 0.3f, 1.0f};
        float diffuseLight[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float specularLight[] = {0.6f, 0.6f, 0.6f, 1.0f};

        ByteBuffer vbba = ByteBuffer.allocateDirect(4 * 4);
        vbba.order(ByteOrder.nativeOrder());
        FloatBuffer lightAmbientBuffer = vbba.asFloatBuffer();
        lightAmbientBuffer.put(ambientLight);
        lightAmbientBuffer.position(0);

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer);

        ByteBuffer vbbs = ByteBuffer.allocateDirect(4 * 4);
        vbbs.order(ByteOrder.nativeOrder());
        FloatBuffer lightSpecularBuffer = vbbs.asFloatBuffer();
        lightSpecularBuffer.put(specularLight);
        lightSpecularBuffer.position(0);

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightSpecularBuffer);

        ByteBuffer vbbd = ByteBuffer.allocateDirect(4 * 4);
        vbbd.order(ByteOrder.nativeOrder());
        FloatBuffer lightDiffuseBuffer = vbba.asFloatBuffer();
        lightDiffuseBuffer.put(diffuseLight);
        lightDiffuseBuffer.position(0);

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer);

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

    public void pushNewAnim(Animation animation) {
        animations.add(animation);
        viewReference.requestRender();
    }

    public boolean markAsPlayer(int xCoor, int yCoor) {
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

    public boolean markSquare(Move move) {
        return markSquare(move.getX(), move.getY(), move.getZ());
    }

    public boolean markSquare(int xCoor, int yCoor, int zCoor) {

        if (playBoard[xCoor][yCoor][zCoor] != 0)
            return false;

        history.add(new Move(xCoor, yCoor, zCoor, turn));

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
        if(state == STATE_ZOOMED_IN) {
            state = MyGLRenderer.STATE_FLOORS;
            pushNewAnim(new ZoomOutAnimation());
        }
        viewReference.requestRender();

        return true;
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
        } catch (EmptyStackException e) {
            e.printStackTrace();
            Toast.makeText(context, "No more moves in history!", Toast.LENGTH_LONG).show();
        }
        viewReference.requestRender();

    }

    public void newGame() {
        playBoard = new int[4][4][4];
        sc.updateTable(playBoard);
        lineFloor.updateTable(playBoard);
        wasRotation = true;
        viewReference.requestRender();
    }

    public int[][][] getPlayBoard() {
        return playBoard;
    }
}
