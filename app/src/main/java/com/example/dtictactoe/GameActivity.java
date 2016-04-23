package com.example.dtictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dtictactoe.AI.ArtificialIntelligence;
import com.example.dtictactoe.R;
import com.example.dtictactoe.backend.Move;
import com.example.dtictactoe.frontend.GameView;
import com.example.dtictactoe.frontend.MyGLRenderer;
import com.example.dtictactoe.frontend.animations.Animation;
import com.example.dtictactoe.frontend.animations.DismemberCubeAnimation;
import com.example.dtictactoe.frontend.animations.MakeCubeAnimation;

public class GameActivity extends Activity {

	private GameView glView;
    private ProgressBar progressBar;
    private int localState = 1;

    static {
        System.loadLibrary("mcts");
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glView = (GameView) findViewById(R.id.game_view);
	    Button cubeView = (Button) findViewById(R.id.cube_view);
        cubeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glView.setState(-localState);
                localState = -localState;
                Animation animation;
                if(localState == MyGLRenderer.STATE_CUBE){
                    animation = new DismemberCubeAnimation();
                }else if(localState == MyGLRenderer.STATE_FLOORS){
                    animation = new MakeCubeAnimation();
                }
                glView.pushNewAnimation(animation);
            }
        });
        Button back = (Button) findViewById(R.id.back_button);
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
        });
		TextView textView = (TextView) findViewById(R.id.turn_text);
		glView.setTurnText(textView);

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
