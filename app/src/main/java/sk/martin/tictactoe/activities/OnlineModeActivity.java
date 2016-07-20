package sk.martin.tictactoe.activities;

import android.os.Bundle;

/**
 * Created by Martin on 29. 9. 2015.
 */
public class OnlineModeActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGameActivity(this);
    }

    @Override
    public void nextMove() {

    }

}
