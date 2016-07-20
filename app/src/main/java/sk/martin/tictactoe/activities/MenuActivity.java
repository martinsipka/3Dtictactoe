package sk.martin.tictactoe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import sk.martin.tictactoe.R;

/**
 * Created by Martin on 21. 9. 2015.
 */
public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }

    public void playVersus(View v){
        Intent intent = new Intent(getBaseContext(), VersusModeActivity.class);
        startActivity(intent);
    }

    public void playBot(View v){
        Intent intent = new Intent(getBaseContext(), BotModeActivity.class);
        startActivity(intent);
    }

    public void quit(View v){
        finish();
    }
}
