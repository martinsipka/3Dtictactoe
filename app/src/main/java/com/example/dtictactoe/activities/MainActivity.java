package com.example.dtictactoe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dtictactoe.R;
import com.example.dtictactoe.frontend.GameView;

public class MainActivity extends Activity {

	GameView glView;
    int localState = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
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

		
		
		/*setContentView(R.layout.activity_main);
		Button b = (Button) findViewById(R.id.button1);
		final EditText et = (EditText) findViewById(R.id.editText1);
		final TextView tv = (TextView) findViewById(R.id.textView1);
		final ScoreCheck sc = new ScoreCheck();
		tv.setText("mozes hrat");
		b.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String s = et.getText().toString();
				int x = Integer.parseInt(s.subSequence(0, 1).toString());
				int y = Integer.parseInt(s.subSequence(1, 2).toString());
				int z = Integer.parseInt(s.subSequence(2, 3).toString());
				
				if(sc.check(x, y, z, 1)==1){
					tv.setText("dakto vzhral");
				}
			}
			
		});
		
		Button b2 = (Button) findViewById(R.id.button2);
		b2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sc.nulboard();
				tv.setText("mozes hrat");
			}
			
			
		});
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
