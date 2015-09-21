package com.example.dtictactoe.frontend;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Martin on 6. 2. 2015.
 */
public class LineFloor {

    private FloatBuffer vertexBuffer;
    private int[][][] a;

    public LineFloor(int[][][] b) {
        a = b;
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
        ByteBuffer vbb = ByteBuffer.allocateDirect(12 * 2 * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    public void drawFloor(GL10 gl, int i) {
        //gl.glFrontFace(GL10.GL_CCW);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        //gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        linedFloor(gl, i);
        // gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
    }

    private void linedFloor(GL10 gl, int position) {
        gl.glColor4f(0.1f, 0.1f, 0.1f, 1.0f);

        for (int j = 0; j < 4; j++) {

            gl.glPushMatrix();
            gl.glTranslatef(0.0f, -0.75f + 0.5f * j, 0.0f);

            for (int i = 0; i < 4; i++) {
                gl.glPushMatrix();
                gl.glTranslatef(-0.75f + 0.5f * i, 0.0f, 0.0f);
                drawCube(gl, i, 3-j, position);
                gl.glPopMatrix();
            }
            gl.glPopMatrix();

        }


        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        //gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    private void drawCube(GL10 gl, int x, int y, int z) {

        if (a[x][y][z] != 0) {
            gl.glPushMatrix();
            gl.glTranslatef(0.0f, 0.2f, 0.0f);

            gl.glPushMatrix();
            //gl.glColor4f(0.5f, 0.5f, 0.4f, 1.0f);
            gl.glTranslatef(0.0f, -0.4f, 0.0f);
            drawColoredSite(gl, a[x][y][z]);
            gl.glPopMatrix();

            gl.glPushMatrix();
            //gl.glColor4f(0.5f, 0.5f, 0.4f, 1.0f);
            gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            gl.glTranslatef(0.0f, -0.2f, 0.2f);
            drawColoredSite(gl, a[x][y][z]);
            gl.glTranslatef(0.0f, 0.4f, 0.0f);
            drawColoredSite(gl, a[x][y][z]);
            gl.glPopMatrix();
            drawColoredSite(gl, a[x][y][z]);
            gl.glPopMatrix();

            gl.glPushMatrix();
            gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
            gl.glTranslatef(0.0f, 0.0f, 0.0f);
            drawColoredSite(gl, a[x][y][z]);
            gl.glPopMatrix();

            gl.glPushMatrix();
            gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);

            drawColoredSite(gl, a[x][y][z]);
            gl.glPopMatrix();
        }

        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.25f, 0.0f);

        gl.glPushMatrix();

        gl.glTranslatef(0.0f, -0.5f, 0.0f);
        drawSite(gl, 1);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        gl.glTranslatef(0.0f, -0.25f, 0.25f);
        drawSite(gl, 1);
        gl.glTranslatef(0.0f, 0.5f, 0.0f);
        drawSite(gl, 1);
        gl.glPopMatrix();

        drawSite(gl, 1);
        gl.glPopMatrix();

    }

    private void drawSite(GL10 gl, int width) {
        gl.glLineWidth(3);
        gl.glColor4f(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, 4);
    }

    private void drawColoredSite(GL10 gl, int color) {
        if (color == 1)
            gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        else
            gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
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
}
