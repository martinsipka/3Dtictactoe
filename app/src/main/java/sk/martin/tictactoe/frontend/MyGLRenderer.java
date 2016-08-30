package sk.martin.tictactoe.frontend;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.widget.Toast;

import sk.martin.tictactoe.AI.ScoreCheck;
import sk.martin.tictactoe.activities.TutorialActivity;
import sk.martin.tictactoe.backend.AlgebraObjects.Quaternion;
import sk.martin.tictactoe.frontend.animations.Animation;
import sk.martin.tictactoe.backend.Move;
import sk.martin.tictactoe.frontend.animations.MakeCubeAnimation;
import sk.martin.tictactoe.frontend.animations.RotateAnimation;
import sk.martin.tictactoe.frontend.animations.ZoomOutAnimation;

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

    private float aspect = 1.0f;

    private Context context;
    private LineFloor lineFloor;

    private int[][][] playBoard = new int[4][4][4];
    private Move lastMove = new Move();
    private Stack<Move> history = new Stack<Move>();
    private int winner;

    private static final String TAG = "RENDERTHREAD TAG";

    public float mDeltaX = 0.0f;
    public float mDeltaY = 0.0f;
    public float[][] floorCords = new float[4][3];
    private long time = 0;
    public int turn = TURN_RED;
    public boolean wasRotation = true;
    public boolean gameOver = false;
    public int state = STATE_CUBE;
    public int zoomX;
    public int zoomY;
    public float cPosX;
    public float cPosY;
    public float cPosZ;
    private ScoreCheck sc;
    private FloatBuffer lightPositionBuffer;

    //Rotation matrices
    public Quaternion currentRotation = new Quaternion();

    public Queue<Animation> animations;
    public GameView viewReference;

    public MyGLRenderer(Context context, GameView viewReference) {
        this.context = context;
        lineFloor = new LineFloor(playBoard, lastMove);
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

        if (wasRotation || gameOver) {
            currentRotation.mulInplace(new Quaternion(0.0f, 1.0f, 0.0f, -mDeltaX));
            currentRotation.mulInplace(new Quaternion(1.0f, 0.0f, 0.0f, -mDeltaY));
            mDeltaX = 0.0f;
            mDeltaY = 0.0f;
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

        float pulse = 0.0f;
        if(gameOver) {
            time++;
            viewReference.requestRender();
            pulse = (float) Math.sin(time / 20.0f) / 3.0f;
        }

        for (int k = 0; k < 4; k++) {
            gl.glPushMatrix();
            gl.glTranslatef(floorCords[k][0], floorCords[k][1], floorCords[k][2]);
            lineFloor.drawFloor(gl, k, pulse);
            gl.glPopMatrix();
        }

        wasRotation = false;

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub


        if (height == 0)
            height = 1; // To prevent divide by zero
        aspect = (float) width / height;

        lineFloor.updateAspect(aspect);

        // Set the viewport (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
        gl.glLoadIdentity(); // Reset projection matrix
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

        gl.glClearDepthf(1.0f); // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST); // Enables depth-buffer for hidden
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_COLOR_MATERIAL);
        gl.glEnable(GL10.GL_NORMALIZE);

        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 4);
        vbb.order(ByteOrder.nativeOrder());
        lightPositionBuffer = vbb.asFloatBuffer();
        lightPositionBuffer.put(lighPosition);
        lightPositionBuffer.position(0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lighPosition, 0);

        gl.glEnable(GL10.GL_LIGHT0);
        float ambientLight[] = {0.4f, 0.4f, 0.4f, 1.0f};
        float diffuseLight[] = {0.9f, 0.9f, 0.9f, 1.0f};
        float specularLight[] = {0.0f, 0.0f, 0.0f, 1.0f};

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLight, 0);


        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseLight, 0);

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specularLight, 0);


        float[] brightAmbient = {1.0f, 1.0f, 1.0f, 1.0f};
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, brightAmbient,  0);

        float material[] = {0.5f, 0.5f, 0.5f, 1.0f};

        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, material, 0);

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
        if(gameOver) {
            return false;
        }
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

        if (playBoard[xCoor][yCoor][zCoor] != 0 &&
                playBoard[xCoor][yCoor][zCoor] != LineFloor.TUTORIAL_HIGHLIGHT) {
            return false;
        }

        if(state == STATE_ZOOMED_IN) {
            state = MyGLRenderer.STATE_FLOORS;
            pushNewAnim(new ZoomOutAnimation());
        }
        history.add(new Move(xCoor, yCoor, zCoor, turn));

        playBoard[xCoor][yCoor][zCoor] = turn;
        lastMove.setX(xCoor);
        lastMove.setY(yCoor);
        lastMove.setZ(zCoor);

        int checked = sc.check(xCoor, yCoor, zCoor);
        if (checked == 1) {
            //Toast.makeText(context, "Red win!", Toast.LENGTH_LONG).show();
            winner = LineFloor.RED_WIN;
            endGame();
        } else if (checked == 2) {
            //Toast.makeText(context, "Blue win!", Toast.LENGTH_LONG).show();
            winner = LineFloor.BLUE_WIN;
            endGame();
        }

        switchTurn();

        viewReference.requestRender();

        return true;
    }

    public void switchTurn(){
        if (turn == TURN_RED) {
            turn = TURN_BLUE;
        } else {
            turn = TURN_RED;
        }
    }

    public void back() {
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

    public void endGame(){
        viewReference.setWinner(winner);
        int[][] winningCombination = sc.getWinningCombination();
        for(int i = 0; i < 4; i++){
            playBoard[winningCombination[i][0]][winningCombination[i][1]][winningCombination[i][2]]
                    = winner;
        }
        gameOver = true;
        if(!(viewReference.gameActivity instanceof TutorialActivity)) {
            animations.add(new MakeCubeAnimation());
        }
        lastMove.setX(-1);
    }

    public void newGame() {
        playBoard = new int[4][4][4];
        turn = TURN_RED;
        gameOver = false;
        sc.updateTable(playBoard);
        lineFloor.updateTable(playBoard);
        wasRotation = true;
        viewReference.requestRender();
    }

    public int[][][] getPlayBoard() {
        return playBoard;
    }

    public void setPlayBoard(int[][][] playBoard){
        this.playBoard = playBoard;
        lineFloor.updateTable(playBoard);
        sc.updateTable(playBoard);
    }

    public void startRotation(){

        animations.add(new RotateAnimation());
        viewReference.requestRender();

    }

}
