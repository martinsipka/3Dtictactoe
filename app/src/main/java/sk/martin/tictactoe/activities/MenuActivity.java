package sk.martin.tictactoe.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import info.hoang8f.android.segmented.SegmentedGroup;
import sk.martin.tictactoe.R;
import sk.martin.tictactoe.frontend.GameView;


/**
 * Created by Martin on 21. 9. 2015.
 */
public class MenuActivity extends AppCompatActivity {

    public static final String DIFFICULTY_PREF = "difficulty";
    public static final String BEGIN = "begin";

    private int[][][] tutorialBoard = new int[4][4][4];
    private int progress;
    private boolean humanSet = true;
    private TextView difficultyText;
    private RadioButton youBegin, botBegin;
    private AppCompatSeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.game_menu);
        GameView glView = (GameView) findViewById(R.id.game_view);

        final SharedPreferences prefs = this.getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);

        TutorialActivity.createTutorialBoard(tutorialBoard);
        glView.renderer.setPlayBoard(tutorialBoard);
        glView.renderer.startRotation();

        seekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar);
        difficultyText = (TextView) findViewById(R.id.difficultyText);

        seekBar.setProgress(prefs.getInt(DIFFICULTY_PREF, 0));
        setText(seekBar.getProgress());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(humanSet) {
                    progress = seekBar.getProgress();
                    progress = roundProgress(progress);
                    setText(progress);
                } else {
                    humanSet = true;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = roundProgress(seekBar.getProgress());
                seekBar.setProgress(progress);
                humanSet = false;
                prefs.edit().putInt(DIFFICULTY_PREF, progress).apply();
            }
        });

        youBegin = (RadioButton) findViewById(R.id.player_begin);
        botBegin = (RadioButton) findViewById(R.id.bot_begin);

        boolean youBeginBoolean = prefs.getBoolean(BEGIN, true);

        if(youBeginBoolean){
            youBegin.toggle();
        }else {
            botBegin.toggle();
        }

        youBegin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    prefs.edit().putBoolean(BEGIN, true).apply();
                }
            }
        });

        botBegin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    prefs.edit().putBoolean(BEGIN, false).apply();
                }
                Log.d("togg", "toggling");
            }
        });

    }

    public void tutorial(View v) {
        Intent intent = new Intent(getBaseContext(), TutorialActivity.class);
        startActivity(intent);
    }

    public void playVersus(View v) {
        Intent intent = new Intent(getBaseContext(), VersusModeActivity.class);
        startActivity(intent);
    }

    public void playBot(View v) {
        Intent intent = new Intent(getBaseContext(), BotModeActivity.class);
        intent.putExtra(BEGIN, youBegin.isChecked());
        intent.putExtra(DIFFICULTY_PREF, seekBar.getProgress());
        startActivity(intent);
    }

    private int roundProgress(int progress){
        if(progress < 17)
            return 0;
        if(progress < 51)
            return 33;
        if(progress < 83)
            return 66;
        return 100;

    }

    private void setText(int progress){
        switch (progress) {
            case 0:
                difficultyText.setText(getResources().getString(R.string.easy));
                break;
            case 33:
                difficultyText.setText(getResources().getString(R.string.medium));
                break;
            case 66:
                difficultyText.setText(getResources().getString(R.string.hard));
                break;
            case 100:
                difficultyText.setText(getResources().getString(R.string.very_hard));
                break;
            default:
                throw new RuntimeException("Wrong arguments int progress");

        }
    }

    public void quit(View v) {
        finish();
    }

}
