package sk.martin.tictactoe.activities;

import android.os.Bundle;

import sk.martin.tictactoe.AI.AsyncAILoader;

/**
 * Created by Martin on 21. 9. 2015.
 */
public class BotModeActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGameActivity(this);
    }

    @Override
    public void nextMove(){
        setWaiting();
        new AsyncAILoader(this).execute(getPlayBoard());
    }

}
