package sk.martin.tictactoe.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

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

	private GameView glView;
    private ProgressBar progressBar;
    private RoundedImageView roundedImageView;
    private TextView winText;

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
	    ImageButton cubeView = (ImageButton) findViewById(R.id.cube_view);
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


        progressBar = (ProgressBar) findViewById(R.id.thinking_progress);
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.add_id));
        AdView mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("67AB4AF8172D70E1DBAD9E77D4CB00D5")
                .build();
        mAdView.loadAd(adRequest);


    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    public void nextMove(){}

    public void setGameActivity(GameActivity gameActivity){
        glView.setGameActivity(gameActivity);
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
        anim.setFillAfter( true );
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
    }

}
