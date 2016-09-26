package sk.martin.tictactoe.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.concurrent.TimeUnit;

import sk.martin.tictactoe.R;
import sk.martin.tictactoe.backend.Move;
import sk.martin.tictactoe.frontend.GameView;
import sk.martin.tictactoe.frontend.LineFloor;
import sk.martin.tictactoe.frontend.RoundedImageView;
import sk.martin.tictactoe.frontend.animations.DismemberCubeAnimation;
import sk.martin.tictactoe.frontend.animations.MakeCubeAnimation;
import sk.martin.tictactoe.frontend.animations.ZoomOutAnimation;
import sk.martin.tictactoe.frontend.MyGLRenderer;

public class GameActivity extends AppCompatActivity {


    public static final String TAG = "ACtivity tag";
    private static final long DELAY = 2000L;

	GameView glView;
    private ProgressBar progressBar;
    protected ImageButton cubeView;
    TextView winText;
    private Button rematch;
    private LinearLayout shade, redInfo, blueInfo;
    TextView redName, blueName, redTime, blueTime;

    private boolean recentlyBackPressed = false;
    private boolean enableAdds = true;
    private Handler exitHandler = new Handler();


    static {
        System.loadLibrary("mcts");
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
     
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.game_layout);
        glView = (GameView) findViewById(R.id.game_view);
	    cubeView = (ImageButton) findViewById(R.id.cube_view);
        cubeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(glView.getState() == MyGLRenderer.STATE_CUBE){
                    glView.pushNewAnim(new DismemberCubeAnimation());
                } else if (glView.getState() == MyGLRenderer.STATE_FLOORS){
                    glView.pushNewAnim(new MakeCubeAnimation());
                } else if (glView.getState() == MyGLRenderer.STATE_ZOOMED_IN) {
                    glView.pushNewAnim(new ZoomOutAnimation());
                    glView.pushNewAnim(new MakeCubeAnimation());
                }
            }
        });

        shade = (LinearLayout) findViewById(R.id.shade);
        shade.setVisibility(View.VISIBLE);
        shade.setAlpha(0.0f);

        rematch = (Button) findViewById(R.id.rematch);
        rematch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseSide();
            }
        });

        redInfo = (LinearLayout) findViewById(R.id.red_info);

        blueInfo = (LinearLayout) findViewById(R.id.blue_info);

        winText = (TextView) findViewById(R.id.winText);

        progressBar = (ProgressBar) findViewById(R.id.thinking_progress);

        redName = (TextView) findViewById(R.id.red_name);
        blueName = (TextView) findViewById(R.id.blue_name);

        redTime = (TextView) findViewById(R.id.red_time);
        blueTime = (TextView) findViewById(R.id.blue_time);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    public void nextMove(boolean winningMove){
        Log.d("tag", "next move");
    }

    public void setGameActivity(GameActivity gameActivity){
        enableAdds  = !(gameActivity instanceof TutorialActivity);
        if(enableAdds) {
            MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.add_id));
            AdView mAdView = (AdView) findViewById(R.id.ad_view);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("67AB4AF8172D70E1DBAD9E77D4CB00D5")
                    .build();
            mAdView.loadAd(adRequest);
        }
        glView.setGameActivity(gameActivity, enableAdds);
    }

    public int[][][] getPlayBoard(){
        return glView.getPlayBoard();
    }

    public boolean markSquare(Move move){
        return glView.markSquare(move);
    }

    public void setWaiting(){
        displayWaiting();
        glView.disableTouch();
    }

    public void setReady(){
        hideWaiting();
        glView.enableTouch();
    }

    public void displayWaiting(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideWaiting(){
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void setWinner(int winner){

        winText.setVisibility(View.VISIBLE);
        if(winner == LineFloor.RED_WIN) {
            winText.setTextColor(Color.RED);
            winText.setText(getString(R.string.red_win));
        } else if(winner == LineFloor.BLUE_WIN){
            winText.setTextColor(Color.BLUE);
            winText.setText(getString(R.string.blue_win));
        } else {
            winText.setTextColor(Color.BLACK);
            winText.setText(getString(R.string.draw));
        }
        rematch.setVisibility(View.VISIBLE);
    }

    public void chooseSide(){
        rematch();
    }

    public void rematch(){

        winText.setVisibility(View.INVISIBLE);
        rematch.setVisibility(View.INVISIBLE);
        glView.newGame(MyGLRenderer.TURN_RED);

    }

    @Override
    public void onBackPressed() {

        Runnable mExitRunnable = new Runnable() {

            @Override
            public void run() {
                recentlyBackPressed=false;
            }
        };

        if (recentlyBackPressed) {
            exitHandler.removeCallbacks(mExitRunnable);
            exitHandler = null;
            super.onBackPressed();
        }
        else
        {
            recentlyBackPressed = true;
            Toast.makeText(this, getString(R.string.exit), Toast.LENGTH_SHORT).show();
            exitHandler.postDelayed(mExitRunnable, DELAY);
        }
    }

    void fadeOut(final int transitionID){

        shade.animate().alpha(1f).setDuration(500)
                .setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fadeIn();
                transition(transitionID);
            }
        });
    }

    void fadeIn(){
        shade.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        }).setInterpolator(new DecelerateInterpolator());
    }

    void transition(int transitionID){}

    public void turnRed(){
        redInfo.animate().alpha(0.9f).setDuration(500).setInterpolator(new DecelerateInterpolator());
        blueInfo.animate().alpha(0.3f).setDuration(500).setInterpolator(new DecelerateInterpolator());
    }

    public void turnBlue(){
        redInfo.animate().alpha(0.3f).setDuration(500).setInterpolator(new DecelerateInterpolator());
        blueInfo.animate().alpha(0.9f).setDuration(500).setInterpolator(new DecelerateInterpolator());
    }

    public void hideControls(){
        redInfo.setVisibility(View.INVISIBLE);
        blueInfo.setVisibility(View.INVISIBLE);
    }

    public void setNames(String nameRed, String nameBlue){
        redName.setText(nameRed);
        blueName.setText(nameBlue);
    }

    public void updateTime(String timeRed, String timeBlue){
        if(timeRed != null) {
            redTime.setText(timeRed);
        }
        if(timeBlue != null){
            blueTime.setText(timeBlue);
        }
    }

    public void updateTime(long time, int turn){
        if(turn == MyGLRenderer.TURN_RED){
            updateTime(String.format("%d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(time),
                    TimeUnit.MILLISECONDS.toSeconds(time) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
            ), null);
        }
        if(turn == MyGLRenderer.TURN_BLUE){
            updateTime(null, String.format("%d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(time),
                    TimeUnit.MILLISECONDS.toSeconds(time) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
            ));
        }
    }

    public void timeOver(int turn){
        glView.renderer.gameOver = true;
        if(turn == MyGLRenderer.TURN_RED){
            winText.setText(getString(R.string.expire_blue_win));
            glView.setWinner(LineFloor.BLUE_WIN);
        }
        if(turn == MyGLRenderer.TURN_BLUE) {
            winText.setText(getString(R.string.expire_red_win));
            glView.setWinner(LineFloor.RED_WIN);
        }
    }

}
