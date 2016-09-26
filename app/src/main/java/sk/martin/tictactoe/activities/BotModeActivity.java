package sk.martin.tictactoe.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.games.multiplayer.Participant;

import sk.martin.tictactoe.AI.AsyncAILoader;
import sk.martin.tictactoe.R;
import sk.martin.tictactoe.backend.Achievements;
import sk.martin.tictactoe.frontend.LineFloor;
import sk.martin.tictactoe.frontend.MyGLRenderer;

/**
 * Created by Martin on 21. 9. 2015.
 */
public class BotModeActivity extends GameActivity {

    public static final int DIFFICULTY_DUMMY = 10;
    public static final int DIFFICULTY_EASY = 500;
    public static final int DIFFICULTY_MEDIUM = 10000;
    public static final int DIFFICULTY_HARD = 30000;
    public static final int DIFFICULTY_VERY_HARD = 60000;

    private int difficulty;
    private int playouts;
    private boolean youBegin = true;
    private int myColor;
    private int turn = MyGLRenderer.TURN_RED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGameActivity(this);
        Intent intent = getIntent();

        youBegin = intent.getBooleanExtra(MenuActivity.BEGIN, true);
        difficulty = intent.getIntExtra(MenuActivity.DIFFICULTY_PREF, 0);

        playouts = decodeDifficulty(difficulty);

        myColor = MyGLRenderer.TURN_RED;
        if (!youBegin) {
            nextMove(false);
            myColor = MyGLRenderer.TURN_BLUE;
            setNames(getString(R.string.bot_name), getString(R.string.player_name));
        } else {
            setNames(getString(R.string.player_name), getString(R.string.bot_name));
        }
        redTime.setVisibility(View.GONE);
        blueTime.setVisibility(View.GONE);
    }

    @Override
    public void nextMove(boolean winningMove){
        if(!winningMove) {
            setWaiting();
            new AsyncAILoader(this).execute(getPlayBoard());
        }
    }

    @Override
    public void setWinner(int winner){
        if(winner == myColor + LineFloor.WIN){
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int currentLevel = prefs.getInt(MenuActivity.LEVEL_PREF, 0);

            Log.d("level: ", Integer.toString(difficulty) + "begin " + youBegin);
            if(currentLevel == levelDifficulty(difficulty, youBegin)){
                currentLevel++;
                prefs.edit().putInt(MenuActivity.LEVEL_PREF, currentLevel).commit();

            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    decideNext();
                }

            }, 5000);

        } else {
            super.setWinner(winner);
            //decideBegin();
        }
    }

    @Override
    public void rematch(){
        super.rematch();
        decideBegin();
    }


    private void decideNext(){
        Log.d(TAG, "" + difficulty);
        if(difficulty == 100 && !youBegin){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.getInt(Achievements.ALL_BOTS_ACHIEVEMENT, Achievements.INVISIBLE) <
                    Achievements.COMPLETED){
                prefs.edit().putInt(Achievements.ALL_BOTS_ACHIEVEMENT,
                        Achievements.COMPLETED).apply();
                prefs.edit().putBoolean(Achievements.NEW_ACHIEVEMENT, true).apply();
            }
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.congrats))
                    .setMessage(getString(R.string.all_bots_down))
                    .setPositiveButton(R.string.play_again, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            rematch();
                        }
                    })
                    .setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.getInt(Achievements.EASY_BOT_ACHIEVEMENT, Achievements.INVISIBLE) <
                    Achievements.COMPLETED){
                prefs.edit().putInt(Achievements.EASY_BOT_ACHIEVEMENT,
                        Achievements.COMPLETED).apply();
                prefs.edit().putBoolean(Achievements.NEW_ACHIEVEMENT, true).apply();
            }
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.congrats))
                    .setMessage(getString(R.string.decide_continue))
                    .setPositiveButton(R.string.cont, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (youBegin) {
                                youBegin = false;
                            } else {
                                youBegin = true;
                                difficulty++;
                                playouts = decodeDifficulty(difficulty);
                            }
                            rematch();
                        }
                    })
                    .setNegativeButton(R.string.rematch, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            rematch();
                        }
                    })
                    .show();
        }
    }

    public int getDifficulty(){
        Log.d("dif", Integer.toString(difficulty));
        return playouts;
    }

    public int getTurn(){
        return turn;
    }

    private int decodeDifficulty(int difficulty){
        //return DIFFICULTY_DUMMY;
        if(difficulty == 0)
            return DIFFICULTY_EASY;
        if(difficulty == 33)
            return DIFFICULTY_MEDIUM;
        if(difficulty == 66){
            return DIFFICULTY_HARD;
        }
        return DIFFICULTY_VERY_HARD;
    }

    public static int levelDifficulty(int difficulty, boolean youBegin){
        if(difficulty == 0)
            return youBegin ? 0 : 1;
        if(difficulty == 33)
            return youBegin ? 2 : 3;
        if(difficulty == 66){
            return youBegin ? 4 : 5;
        }
        return youBegin ? 6 : 7;
    }

    private void decideBegin(){
        if (youBegin) {
            if(myColor == MyGLRenderer.TURN_RED){
                turnRed();
                glView.renderer.turn = MyGLRenderer.TURN_RED;
            } else {
                turnBlue();
                glView.renderer.turn = MyGLRenderer.TURN_BLUE;
            }
        } else {
            if(myColor == MyGLRenderer.TURN_RED){
                turnBlue();
                glView.renderer.turn = MyGLRenderer.TURN_BLUE;
            } else {
                turnRed();
                glView.renderer.turn = MyGLRenderer.TURN_RED;
            }
            nextMove(false);
        }
    }
}
