package sk.martin.tictactoe.frontend;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import sk.martin.tictactoe.backend.Move;

/**
 * Created by Martin on 6. 2. 2015.
 */
public class LineFloor {

    public static final int RED_CUBE = 1;
    public static final int BLUE_CUBE = 5;
    public static final int RED_WIN = 21;
    public static final int BLUE_WIN = 85;
    public static final int HIGHLIGHT = 50;
    public static final int RED_HIGHLIGHT = RED_CUBE + HIGHLIGHT;
    public static final int BLUE_HIGHLIGHT = BLUE_CUBE + HIGHLIGHT;
    public static final int TUTORIAL_HIGHLIGHT = 200;


    private FloatBuffer vertexBuffer, normalBuffer;
    private int[][][] a;
    private Move lastMove;
    private int floorIndex;
    public static final int[][] floorColors = {{255, 86, 34}, {255, 150, 0}, {255, 203, 7}, {255, 214, 0}};
    float[] normals;

    //public static final int[][] floorColors = {{255,115,115}, {64,224,208}, {247,194,130}, {123,37,242}};



    public LineFloor(int[][][] b, Move lastMove) {
        a = b;
        this.lastMove = lastMove;
        float[] vertices = {
                -0.25f, 0.0f, -0.25f,
                -0.25f, 0.0f, 0.25f,
                0.25f, 0.0f, 0.25f,
                0.25f, 0.0f, -0.25f,
                -0.2f, 0.0f, -0.2f,
                -0.2f, 0.0f, 0.2f,
                0.2f, 0.0f, -0.2f,
                0.2f, 0.0f, 0.2f,
        };

        float[] normals = {
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
        };

        this.normals = normals;

        ByteBuffer vbb = ByteBuffer.allocateDirect(12 * 2 * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        ByteBuffer vbb2 = ByteBuffer.allocateDirect(12 * 4);
        vbb2.order(ByteOrder.nativeOrder());
        normalBuffer = vbb2.asFloatBuffer();
        normalBuffer.put(normals);
        normalBuffer.position(0);
    }

    public void drawFloor(GL10 gl, int i, float pulse) {
        //gl.glFrontFace(GL10.GL_CCW);
        floorIndex = i;
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);
        //gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);
        //gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        linedFloor(gl, i, pulse);
        // gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
    }

    private void linedFloor(GL10 gl, int position, float pulse) {

        gl.glColor4f(0.1f, 0.1f, 0.1f, 1.0f);

        for (int j = 0; j < 4; j++) {

            gl.glPushMatrix();
            gl.glTranslatef(0.0f, -0.75f + 0.5f * j, 0.0f);

            for (int i = 0; i < 4; i++) {
                gl.glPushMatrix();
                gl.glTranslatef(-0.75f + 0.5f * i, 0.0f, 0.0f);
                drawCube(gl, i, 3-j, position, pulse);
                gl.glPopMatrix();
            }
            gl.glPopMatrix();

        }


        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        //gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    private void drawCube(GL10 gl, int x, int y, int z, float pulse) {



        if (a[x][y][z] != 0) {

            int color = a[x][y][z];
            if(x == lastMove.getX() && y == lastMove.getY() && z == lastMove.getZ()){
                color = color + HIGHLIGHT;
            }

            gl.glPushMatrix();
            gl.glTranslatef(0.0f, 0.2f, 0.0f);
            drawColoredSite(gl, color, pulse);
            gl.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
            gl.glTranslatef(0.0f, 0.4f, 0.0f);
            drawColoredSite(gl, color, pulse);
            gl.glPopMatrix();

            gl.glPushMatrix();
            gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
            gl.glTranslatef(0.0f, 0.2f, 0.0f);
            drawColoredSite(gl, color, pulse);
            gl.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
            gl.glTranslatef(0.0f, 0.4f, 0.0f);
            drawColoredSite(gl, color, pulse);
            gl.glPopMatrix();

            gl.glPushMatrix();
            gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            gl.glTranslatef(0.0f, 0.2f, 0.0f);
            drawColoredSite(gl, color, pulse);
            gl.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
            gl.glTranslatef(0.0f, 0.4f, 0.0f);
            drawColoredSite(gl, color, pulse);
            gl.glPopMatrix();


        }

        gl.glDisable(GL10.GL_LIGHT0);
        gl.glEnable(GL10.GL_LIGHT1);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.25f, 0.0f);

        gl.glPushMatrix();

        gl.glTranslatef(0.0f, -0.5f, 0.0f);
        drawLinedSite(gl, 1, floorIndex);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        gl.glTranslatef(0.0f, -0.25f, 0.25f);
        drawLinedSite(gl, 1, floorIndex);
        gl.glTranslatef(0.0f, 0.5f, 0.0f);
        drawLinedSite(gl, 1, floorIndex);
        gl.glPopMatrix();

        drawLinedSite(gl, 1, floorIndex);
        gl.glPopMatrix();
        gl.glDisable(GL10.GL_LIGHT1);
        gl.glEnable(GL10.GL_LIGHT0);

    }

    private void drawLinedSite(GL10 gl, int width, int i) {
        gl.glLineWidth(3);
        gl.glColor4f(floorColors[i][0] / 255.0f, floorColors[i][1] / 255.0f,
                floorColors[i][2] / 255.0f, 1.0f);
        gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, 4);
    }

    private void drawColoredSite(GL10 gl, int color, float pulse) {

        if (color == RED_CUBE) {
            gl.glColor4f(1.0f, 0.321f, 0.321f, 1.0f);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
        } else if(color == BLUE_CUBE){
            gl.glColor4f(0.09f, 0.463f, 0.823f, 1.0f);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
        } else if(color == RED_WIN){
            //gl.glDisable(GL10.GL_LIGHT0);
            //gl.glEnable(GL10.GL_LIGHT1);
            //gl.glColor4f(0.5f + pulse, 0.0f, 0.0f, 1.0f);
            gl.glColor4f(1.0f, 0.321f + pulse, 0.321f, 1.0f);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
            //gl.glDisable(GL10.GL_LIGHT1);
            //gl.glEnable(GL10.GL_LIGHT0);
        } else if(color == BLUE_WIN){
            //gl.glDisable(GL10.GL_LIGHT0);
            //gl.glEnable(GL10.GL_LIGHT1);
            //gl.glColor4f(0.0f, 0.0f, 0.5f + pulse, 1.0f);
            gl.glColor4f(0.09f, 0.463f + pulse, 0.823f, 1.0f);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
            //.glDisable(GL10.GL_LIGHT1);
            //gl.glEnable(GL10.GL_LIGHT0);
        } else if(color == RED_HIGHLIGHT){
            gl.glColor4f(0.913f, 0.117f, 0.352f, 1.0f);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
        } else if(color == BLUE_HIGHLIGHT){
            gl.glColor4f(0.425f, 0.427f, 1.0f, 1.0f);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
        } else if(color == TUTORIAL_HIGHLIGHT){
            gl.glColor4f(0.0f, 0.2f, 0.2f, 0.2f);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
        }

    }

    //TODO nice X
    private void drawXSymbol() {

    }

    //TODO nice Y
    private void drawYSymbol() {
    }

    public void updateTable(int[][][] b) {
        a = b;
    }

    public void updateAspect(float aspect){
        /*normals[1] = 0.01f;
        normals[4] = 0.01f;
        normals[7] = 0.01f;
        normals[10] = 0.01f;*/
    }

}
