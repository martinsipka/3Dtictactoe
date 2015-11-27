package com.example.dtictactoe.AI;

import android.os.AsyncTask;

import com.example.dtictactoe.activities.BotModeActivity;
import com.example.dtictactoe.backend.Move;

/**
 * Created by Martin on 20. 9. 2015.
 */
public class AsyncAILoader extends AsyncTask<int[][][], Object, Integer> {

    BotModeActivity gameActivity;

    public AsyncAILoader(BotModeActivity gameActivity){
        this.gameActivity = gameActivity;
    }

    @Override
    protected Integer doInBackground(int[][][]... params) {
        ArtificialIntelligence ai = new ArtificialIntelligence();
        System.out.println(params[0][0][0][0]);
        int newTurn  = ai.getPosition(params[0]);
        return newTurn;
    }

    @Override
    protected void onPostExecute(Integer newTurn) {
        super.onPostExecute(newTurn);
        int xCoor = newTurn/16;
        int left = newTurn % 16;
        int yCoor = left / 4;
        int zCoor = left % 4;
        if(!gameActivity.markSquare(new Move(xCoor, yCoor, zCoor, -1)))
            throw new AIException();
        gameActivity.setReady();
    }

}
