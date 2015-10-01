package com.example.dtictactoe.activities;

import android.os.Bundle;

import com.example.dtictactoe.GameActivity;

/**
 * Created by Martin on 21. 9. 2015.
 */
public class VersusModeActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGameActivity(this);
    }

    @Override
    public void nextMove() {

    }
}
