package com.example.dtictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dtictactoe.AI.ArtificialIntelligence;
import com.example.dtictactoe.R;
import com.example.dtictactoe.frontend.GameView;

public class GameActivity extends Activity {

	GameView glView;
    int localState = 1;

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
            }
        });
        Button back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				glView.newGame();
            }
        });
		TextView textView = (TextView) findViewById(R.id.turn_text);
		glView.setTurnText(textView);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
