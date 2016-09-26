package sk.martin.tictactoe.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import sk.martin.tictactoe.R;
import sk.martin.tictactoe.backend.Achievements;
import sk.martin.tictactoe.backend.gameutils.GameCountDown;
import sk.martin.tictactoe.frontend.MyGLRenderer;

/**
 * Created by Martin on 21. 9. 2015.
 */
public class VersusModeActivity extends GameActivity {

    public static final long GAME_TIME = 300000L;

    private GameCountDown redCountDown, blueCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGameActivity(this);
        setNames(getString(R.string.red), getString(R.string.blue));
        redCountDown = new GameCountDown(GAME_TIME, this, MyGLRenderer.TURN_RED, true);
        blueCountDown = new GameCountDown(GAME_TIME, this, MyGLRenderer.TURN_BLUE, true);
        updateTime(GAME_TIME, MyGLRenderer.TURN_RED);
        updateTime(GAME_TIME, MyGLRenderer.TURN_BLUE);
        redCountDown.resume();
    }

    @Override
    public void nextMove(boolean winningMove) {

        if (glView.renderer.turn == MyGLRenderer.TURN_RED) {
            blueCountDown.pause();
            redCountDown.resume();
        } else if (glView.renderer.turn == MyGLRenderer.TURN_BLUE) {
            redCountDown.pause();
            blueCountDown.resume();
        }

    }

    @Override
    public void setWinner(int winner) {
        redCountDown.reset();
        blueCountDown.reset();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getInt(Achievements.OFFLINE_GAME_ACHIEVEMENT, Achievements.INVISIBLE) <
                Achievements.COMPLETED){
            prefs.edit().putInt(Achievements.OFFLINE_GAME_ACHIEVEMENT,
                    Achievements.COMPLETED).apply();
            prefs.edit().putBoolean(Achievements.NEW_ACHIEVEMENT, true).apply();
        }

        super.setWinner(winner);
    }

    @Override
    public void rematch() {
        updateTime(GAME_TIME, MyGLRenderer.TURN_RED);
        updateTime(GAME_TIME, MyGLRenderer.TURN_BLUE);
        redCountDown.resume();
        super.rematch();
    }
}
