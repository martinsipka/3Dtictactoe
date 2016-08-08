package sk.martin.tictactoe.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

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

    private int difficulty;
    private int turn = MyGLRenderer.TURN_RED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGameActivity(this);
        if (turn == MyGLRenderer.TURN_RED) {
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
                        if(turn == MyGLRenderer.TURN_RED){
                            turn = MyGLRenderer.TURN_BLUE;
                        }else {
                            turn = MyGLRenderer.TURN_RED;
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
        return DIFFICULTY_HARD;
    }

    public int getTurn(){
        return turn;
    }
}
