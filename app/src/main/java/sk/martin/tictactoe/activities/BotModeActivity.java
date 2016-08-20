package sk.martin.tictactoe.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import sk.martin.tictactoe.AI.AsyncAILoader;
import sk.martin.tictactoe.frontend.MyGLRenderer;

/**
 * Created by Martin on 21. 9. 2015.
 */
public class BotModeActivity extends GameActivity {

    public static final int DIFFICULTY_DUMMY = 10;
    public static final int DIFFICULTY_EASY = 5000;
    public static final int DIFFICULTY_MEDIUM = 15000;
    public static final int DIFFICULTY_HARD = 30000;
    public static final int DIFFICULTY_VERY_HARD = 60000;

    private int difficulty;
    private boolean youBegin = true;
    private int turn = MyGLRenderer.TURN_RED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGameActivity(this);
        Intent intent = getIntent();

        youBegin = intent.getBooleanExtra(MenuActivity.BEGIN, true);
        difficulty = intent.getIntExtra(MenuActivity.DIFFICULTY_PREF, 0);

        difficulty = decodeDifficulty(difficulty);

        if (!youBegin) {
           nextMove();
        }
    }

    @Override
    public void nextMove(){
        setWaiting();
        new AsyncAILoader(this).execute(getPlayBoard());
    }

    @Override
    public void chooseSide(){
        new AlertDialog.Builder(this)
                .setTitle("Switch sides?")
                .setMessage("Do you want to switch sides?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(youBegin){
                            youBegin = false;
                        }else {
                            youBegin = true;
                        }
                        rematch();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        rematch();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public int getDifficulty(){
        Log.d("dif", Integer.toString(difficulty));
        return difficulty;
    }

    public int getTurn(){
        return turn;
    }

    private int decodeDifficulty(int difficulty){
        if(difficulty == 0)
            return DIFFICULTY_EASY;
        if(difficulty == 33)
            return DIFFICULTY_MEDIUM;
        if(difficulty == 66){
            return DIFFICULTY_HARD;
        }
        return DIFFICULTY_VERY_HARD;
    }
}
