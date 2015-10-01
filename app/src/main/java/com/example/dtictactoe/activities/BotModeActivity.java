package com.example.dtictactoe.activities;

import android.os.Bundle;

import com.example.dtictactoe.AI.AsyncAILoader;
import com.example.dtictactoe.GameActivity;
import com.example.dtictactoe.backend.Move;

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
