package com.example.dtictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class GameView extends GLSurfaceView {

    MyGLRenderer renderer;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320.0f;
    private float previousX;
    private float previousY;
    private float width;
    private float height;
    private static final int heightTouchOffset = 250;
    private static final int widthTouchOffset = 50;

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

                if (renderer.state == 3) {

                    int newState = 4;
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

                    renderer.state = newState;

                } else if (renderer.state == 5) {
                    int xCoor;
                    int yCoor;
                    if (currentX < 0 || height < 0) {
                        renderer.state = 3;
                        return true;
                    }
                    if (currentX > width - widthTouchOffset || currentY > height - heightTouchOffset) {
                        renderer.state = 3;
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
                    renderer.markSquare(xCoor, yCoor);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX, deltaY;

                // Modify rotational angles according to movement
                if (renderer.state == 0) {

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

                } else {
                    //  renderer.touched(deltaX * TOUCH_SCALE_FACTOR, deltaY * TOUCH_SCALE_FACTOR);
                }

                // Save current x, y
                previousX = currentX;
                previousY = currentY;
                break;
        }
        return true;  // Event handled
    }

    public void setState(int state) {
        if (renderer.state == 0 || renderer.state == 3) {
            state = -state;
            renderer.state = state;
        }
    }


}
