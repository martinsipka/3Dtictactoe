package sk.martin.tictactoe.frontend;

import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import sk.martin.tictactoe.activities.GameActivity;
import sk.martin.tictactoe.activities.TutorialActivity;
import sk.martin.tictactoe.backend.Move;
import sk.martin.tictactoe.frontend.animations.Animation;
import sk.martin.tictactoe.frontend.animations.ZoomInAnimation;
import sk.martin.tictactoe.frontend.animations.ZoomOutAnimation;

public class GameView extends GLSurfaceView {

    private final float TOUCH_SCALE_FACTOR = 90.0f / 320.0f;
    public static final String TAG = "GAMEVIEW TAG";

    public GameActivity gameActivity;
    public MyGLRenderer renderer;
    private float previousX;
    private float previousY;
    private float width;
    private float height;
    private float bottomMargin;
    private float topMargin;
    private float leftMargin;
    private float rightMargin;
    private RoundedImageView roundedImageView;
    private int turn = MyGLRenderer.TURN_RED;
    private boolean enableTouch = true;
    private boolean enableAdds = true;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        renderer = new MyGLRenderer(context, this);

        this.setRenderer(renderer);
        this.setRenderMode(RENDERMODE_WHEN_DIRTY);

        // Request focus, otherwise key/button won't react
        this.requestFocus();
        this.setFocusableInTouchMode(true);
        this.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                width = getWidth();
                leftMargin =  0.04f;
                rightMargin = 0.17f;
                height = getHeight();
                topMargin = 0.21f;
                bottomMargin = 0.3f;
                Log.d(TAG, width + " " + height);
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
        float currentX = evt.getX();
        float currentY = evt.getY();

        if(enableTouch) {
            if (renderer.animations.isEmpty()) {
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

                            renderer.pushNewAnim(new ZoomInAnimation());


                        } else if (renderer.state == MyGLRenderer.STATE_ZOOMED_IN) {

                            int xCoor;
                            int yCoor;

                            float left, right, top, bottom;

                            if (renderer.zoomX == -1) {
                                left = rightMargin;
                                right = leftMargin;
                            } else {
                                left = leftMargin;
                                right = rightMargin;
                            }
                            if (renderer.zoomY == 1) {
                                top = bottomMargin;
                                bottom = topMargin;
                            } else {
                                top = topMargin;
                                bottom = bottomMargin;
                            }

                            if (currentX / width < left || currentX / width > 1.0f - right) {
                                renderer.pushNewAnim(new ZoomOutAnimation());
                                renderer.state = MyGLRenderer.STATE_FLOORS;
                                return true;
                            }
                            if (currentY / height < top || currentY / height > 1.0f - bottom) {
                                renderer.pushNewAnim(new ZoomOutAnimation());
                                renderer.state = MyGLRenderer.STATE_FLOORS;
                                return true;
                            }

                            float relativeX = currentX / width;
                            float relativeY = currentY / height;
                            if (relativeX < 0.251f - leftMargin + left) {
                                xCoor = 0;
                            } else if (relativeX < 0.444f - leftMargin + left) {
                                xCoor = 1;
                            } else if (relativeX < 0.631 - leftMargin + left) {
                                xCoor = 2;
                            } else {
                                xCoor = 3;
                            }
                            if (relativeY < 0.35f - topMargin + top) {
                                yCoor = 0;
                            } else if (relativeY < 0.464f - topMargin + top) {
                                yCoor = 1;
                            } else if (relativeY < 0.576f - topMargin + top) {
                                yCoor = 2;
                            } else {
                                yCoor = 3;
                            }

                            if (renderer.markAsPlayer(xCoor, yCoor)) {
                                if(!renderer.gameOver) {
                                    if(!(gameActivity instanceof TutorialActivity)) {
                                        switchTurn();
                                    }
                                    gameActivity.nextMove();
                                }
                            } else {
                                Log.d(TAG, "false on touch");
                            }
                        }


                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX, deltaY;

                        // Modify rotational angles according to movement
                        if (renderer.state == MyGLRenderer.STATE_CUBE) {

                            deltaX = (currentX - previousX) * TOUCH_SCALE_FACTOR;
                            deltaY = (currentY - previousY) * TOUCH_SCALE_FACTOR;
                            renderer.mDeltaX += (float) Math.toRadians(deltaX);
                            renderer.mDeltaY += (float) Math.toRadians(deltaY);
                            renderer.wasRotation = true;
                        }


                        break;
                }
            }
            // Save current x, y
            previousX = currentX;
            previousY = currentY;

            requestRender();
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

    public void back() {
        renderer.back();
    }

    public void newGame() {
        gameActivity.turnRed();
        renderer.newGame();
    }

    public int getState(){
        return renderer.state;
    }

    public void pushNewAnim(Animation animation) {
        renderer.pushNewAnim(animation);
    }

    public void setWinner(int winner){
        gameActivity.setWinner(winner);
    }


    public void setGameActivity(GameActivity gameActivity, boolean enableAdds) {
        this.gameActivity = gameActivity;
        this.enableAdds = enableAdds;
    }

    public void switchTurn() {
        if (turn == MyGLRenderer.TURN_RED) {
            gameActivity.turnBlue();//roundedImageView.setTurn(MyGLRenderer.TURN_BLUE);
            turn = MyGLRenderer.TURN_BLUE;
        } else if (turn == MyGLRenderer.TURN_BLUE) {
            gameActivity.turnRed();//roundedImageView.setTurn(MyGLRenderer.TURN_RED);
            turn = MyGLRenderer.TURN_RED;
        }

    }

    public int[][][] getPlayBoard() {
        return renderer.getPlayBoard();
    }

    public boolean markSquare(Move move) {
        if (renderer.markSquare(move)) {
            if(!renderer.gameOver) {
                switchTurn();
            }
            return true;
        }
        return false;

    }

    public void enableTouch() {
        enableTouch = true;
        System.out.println("enabling touch");
    }

    public void disableTouch() {
        enableTouch = false;
        System.out.println("diabling touch");
    }

}
