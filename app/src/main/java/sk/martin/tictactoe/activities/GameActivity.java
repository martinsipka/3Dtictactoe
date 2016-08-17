package sk.martin.tictactoe.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
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

import sk.martin.tictactoe.R;
import sk.martin.tictactoe.backend.Move;
import sk.martin.tictactoe.frontend.GameView;
import sk.martin.tictactoe.frontend.LineFloor;
import sk.martin.tictactoe.frontend.RoundedImageView;
import sk.martin.tictactoe.frontend.animations.DismemberCubeAnimation;
import sk.martin.tictactoe.frontend.animations.MakeCubeAnimation;
import sk.martin.tictactoe.frontend.animations.ZoomOutAnimation;
import sk.martin.tictactoe.frontend.MyGLRenderer;

public class GameActivity extends Activity {

    private static final long DELAY = 2000L;

	GameView glView;
    private ProgressBar progressBar;
    private RoundedImageView roundedImageView;
    protected ImageButton cubeView;
    private TextView winText;
    private Button rematch;
    private LinearLayout shade;

    private boolean recentlyBackPressed = false;
    private boolean enableAdds = true;
    private Handler exitHandler = new Handler();


    static {
        System.loadLibrary("mcts");
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
     
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.game_activity);
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

        /*Button back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				glView.back();
            }
        });
        Button newGame = (Button) findViewById(R.id.new_game_button);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glView.newGame();
            }
        });*/
		roundedImageView = (RoundedImageView) findViewById(R.id.turn_view);
		glView.setTurnText(roundedImageView);

        winText = (TextView) findViewById(R.id.winText);

        rematch = (Button) findViewById(R.id.rematch);
        rematch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseSide();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.thinking_progress);



    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    public void nextMove(){
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
        TranslateAnimation anim = new TranslateAnimation( 0, -100 , 0, 0 );
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        roundedImageView.startAnimation(anim);
        winText.setVisibility(View.VISIBLE);
        if(winner == LineFloor.RED_WIN) {
            winText.setTextColor(Color.RED);
            roundedImageView.setTurn(MyGLRenderer.TURN_RED);
        } else if(winner == LineFloor.BLUE_WIN){
            winText.setTextColor(Color.BLUE);
            roundedImageView.setTurn(MyGLRenderer.TURN_BLUE);
        }
        rematch.setVisibility(View.VISIBLE);
        notifyWin();
    }

    public void chooseSide(){
        rematch();
    }

    public void notifyWin(){}

    public void rematch(){

        TranslateAnimation anim = new TranslateAnimation( -100, 0 , 0, 0 );
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        roundedImageView.startAnimation(anim);
        winText.setVisibility(View.INVISIBLE);
        rematch.setVisibility(View.INVISIBLE);
        glView.newGame();

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
            Toast.makeText(this, "press again to exit", Toast.LENGTH_SHORT).show();
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
        shade.animate().alpha(0f).setDuration(500)
                .setInterpolator(new DecelerateInterpolator());
    }

    void transition(int transitionID){}

}
