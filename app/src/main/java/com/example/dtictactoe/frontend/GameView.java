package com.example.dtictactoe.frontend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.TextView;

public class GameView extends GLSurfaceView {

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320.0f;

    MyGLRenderer renderer;
    private float previousX;
    private float previousY;
    private float width;
    private float height;
    private static final int heightTouchOffset = 250;
    private static final int widthTouchOffset = 50;
    private TextView turnText;
    private int turn = 1;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        renderer = new MyGLRenderer(context);

        this.setRenderer(renderer);
        // Request focus, otherwise key/button won't react
        this.requestFocus();
        this.setFocusableInTouchMode(true);
        this.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                width = getWidth() - 2 * widthTouchOffset;
                height = getHeight() - 2 * heightTouchOffset;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent evt) {
        float currentX = evt.getX() - widthTouchOffset;
        float currentY = evt.getY() - heightTouchOffset;

        switch (evt.getAction()) {

            case MotionEvent.ACTION_DOWN:

                if (renderer.state == MyGLRenderer.STATE_FLOORS) {

                    if (currentX > width / 2) {
                        renderer.zoomX = -1;
                    } else {
                        renderer.zoomX = 1;
                    }
                    if (currentY > height / 2) {
                        renderer.zoomY = 1;
                    } else {
                        renderer.zoomY = -1;
                    }

                    renderer.state = MyGLRenderer.STATE_ZOOMING_IN;

                } else if (renderer.state == MyGLRenderer.STATE_ZOOMED_IN) {
                    int xCoor;
                    int yCoor;
                    if (currentX < 0 || height < 0) {
                        renderer.state = MyGLRenderer.STATE_FLOORS;
                        return true;
                    }
                    if (currentX > width - widthTouchOffset || currentY > height - heightTouchOffset) {
                        renderer.state = MyGLRenderer.STATE_FLOORS;
                        return true;
                    }
                    if (currentX < width / 4) {
                        xCoor = 0;
                    } else if (currentX < width / 2) {
                        xCoor = 1;
                    } else if (currentX < width * 2 / 3) {
                        xCoor = 2;
                    } else {
                        xCoor = 3;
                    }
                    if (currentY < height / 4) {
                        yCoor = 0;
                    } else if (currentY < height / 2) {
                        yCoor = 1;
                    } else if (currentY < height * 2 / 3) {
                        yCoor = 2;
                    } else {
                        yCoor = 3;
                    }
                    switchTurn();
                    renderer.markSquare(xCoor, yCoor);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX, deltaY;

                // Modify rotational angles according to movement
                if (renderer.state == MyGLRenderer.STATE_CUBE) {

                    deltaX = currentX - previousX;
                    deltaY = currentY - previousY;
                    if (renderer.angleY + deltaY * TOUCH_SCALE_FACTOR > 360.0f) {
                        renderer.angleY += deltaY * TOUCH_SCALE_FACTOR - 360.0f;
                    } else if (renderer.angleY + deltaY * TOUCH_SCALE_FACTOR < 0.0f) {
                        renderer.angleY = 360.0f - deltaY * TOUCH_SCALE_FACTOR;
                    } else {
                        renderer.angleY += deltaY * TOUCH_SCALE_FACTOR;
                    }
                    if (renderer.angleX + deltaX * TOUCH_SCALE_FACTOR > 360.0f) {
                        renderer.angleX += deltaX * TOUCH_SCALE_FACTOR - 360.0f;
                    } else if (renderer.angleX + deltaX * TOUCH_SCALE_FACTOR < 0.0f) {
                        renderer.angleX = 360.0f - deltaX * TOUCH_SCALE_FACTOR;

                    } else {
                        renderer.angleX += deltaX * TOUCH_SCALE_FACTOR;
                    }

                }

                // Save current x, y
                previousX = currentX;
                previousY = currentY;
                break;
        }
        return true;  // Event handled
    }

    public void setState(int state) {
        if (renderer.state == MyGLRenderer.STATE_CUBE ||
                renderer.state == MyGLRenderer.STATE_FLOORS) {
            state = -state;
            renderer.state = state;
        }
    }

    public void back(){
        renderer.back();
    }

    public void setTurnText(TextView textView){
        turnText = textView;
    }

    private void switchTurn(){
        if(turn == MyGLRenderer.TURN_RED){
            turnText.setText("Blue turn");
            turnText.setTextColor(Color.BLUE);
            turn = MyGLRenderer.TURN_BLUE;
        } else if (turn == MyGLRenderer.TURN_BLUE){
            turnText.setText("Red turn");
            turnText.setTextColor(Color.RED);
            turn = MyGLRenderer.TURN_RED;
        }
    }


}
