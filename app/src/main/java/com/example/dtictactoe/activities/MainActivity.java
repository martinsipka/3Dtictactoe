package com.example.dtictactoe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dtictactoe.AI.ArtificialIntelligence;
import com.example.dtictactoe.R;
import com.example.dtictactoe.frontend.GameView;

public class MainActivity extends Activity {

	GameView glView;
    int localState = 1;

    static {
        System.loadLibrary("mcts");
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArtificialIntelligence ai = new ArtificialIntelligence();
		System.out.println(ai.getPosition());//new int[4][4][4], 5, 1000));
        /*this.setContentView(R.layout.activity_main);
        glView = (GameView) findViewById(R.id.game_view);       // Allocate a GLSurfaceView
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
				glView.back();
            }
        });
		TextView textView = (TextView) findViewById(R.id.turn_text);
		glView.setTurnText(textView);
		*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
