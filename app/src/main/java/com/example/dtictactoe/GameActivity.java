package com.example.dtictactoe;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dtictactoe.backend.Move;
import com.example.dtictactoe.frontend.GameView;
import com.example.dtictactoe.frontend.MyGLRenderer;
import com.example.dtictactoe.frontend.RoundedImageView;
import com.example.dtictactoe.frontend.animations.DismemberCubeAnimation;
import com.example.dtictactoe.frontend.animations.MakeCubeAnimation;
import com.example.dtictactoe.frontend.animations.ZoomOutAnimation;

public class GameActivity extends Activity {

	private GameView glView;
    private ProgressBar progressBar;

    static {
        System.loadLibrary("mcts");
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
     
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);
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
		RoundedImageView roundedImageView = (RoundedImageView) findViewById(R.id.turn_view);
		glView.setTurnText(roundedImageView);


        progressBar = (ProgressBar) findViewById(R.id.thinking_progress);
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

}
