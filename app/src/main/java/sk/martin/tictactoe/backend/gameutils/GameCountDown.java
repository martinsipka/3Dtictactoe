package sk.martin.tictactoe.backend.gameutils;

import android.os.CountDownTimer;

import sk.martin.tictactoe.activities.GameActivity;

/**
 * Created by martin on 30.8.16.
 */
public class GameCountDown {

    public static final long INTERVAL = 1000L;

    private GameActivity activity;
    private long initialTime;
    private long timeLeft;
    private CountDownTimer countDown;
    private int turn;
    private boolean killGame;

    public GameCountDown(long millis, GameActivity activityCallback, int turn, boolean killGame){
        timeLeft = millis;
        initialTime = millis;
        activity = activityCallback;
        this.turn = turn;
        this.killGame = killGame;
        //createTimer();

    }

    public void pause(){
        if(countDown != null)
        countDown.cancel();
    }

    public void resume(){
        createTimer();
    }

    public void reset(){
        pause();
        timeLeft = initialTime;
        activity.updateTime(timeLeft, turn);
    }

    public void updateTime(long time){
        timeLeft = time;
    }

    public long getTime(){
        return timeLeft;
    }

    private void createTimer(){

        if(countDown != null) {
            countDown.cancel();
        }

        countDown = new CountDownTimer(timeLeft, INTERVAL){

            @Override
            public void onTick(long l) {
                timeLeft = l;
                activity.updateTime(timeLeft, turn);
            }

            @Override
            public void onFinish() {
                if(killGame) {
                    activity.timeOver(turn);
                }
            }
        }.start();
    }

}
