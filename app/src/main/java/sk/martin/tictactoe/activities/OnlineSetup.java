package sk.martin.tictactoe.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import sk.martin.tictactoe.R;
import sk.martin.tictactoe.frontend.GameView;

/**
 * Created by martin on 23.8.16.
 */
public class OnlineSetup extends AppCompatActivity {

    private int[][][] tutorialBoard = new int[4][4][4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.online_menu);

        GameView glView = (GameView) findViewById(R.id.game_view);

        TutorialActivity.createTutorialBoard(tutorialBoard);
        glView.renderer.setPlayBoard(tutorialBoard);
        glView.renderer.startRotation();

    }


    void startGame(boolean multiplayer) {

        //Intent intent = new Intent(getBaseContext(), OnlineActivity.class);
        //startActivity(intent);

    }

}
