package sk.martin.tictactoe.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;

import java.io.IOException;

import info.hoang8f.android.segmented.SegmentedGroup;
import sk.martin.tictactoe.R;
import sk.martin.tictactoe.backend.Achievements;
import sk.martin.tictactoe.backend.gameutils.BaseGameUtils;
import sk.martin.tictactoe.frontend.GameView;


/**
 * Created by Martin on 21. 9. 2015.
 */
public class MenuActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String LEVEL_PREF = "progress";
    public static final String DIFFICULTY_PREF = "difficulty";
    public static final String TUTORIAL_PREF = "tutorial";
    public static final String BEGIN = "begin";
    public static final int REQUEST_LEADERBOARD = 454;
    public static final int REQUEST_ACHIEVEMENTS = 474;


    private int[][][] tutorialBoard = new int[4][4][4];
    private int progress;
    private boolean humanSet = true, youBeginBoolean;
    private TextView difficultyText, rating;
    private RadioButton youBegin, botBegin;
    private AppCompatSeekBar seekBar;
    private ImageView firstLock, secondLock, firstStar, secondStar;
    private int level = 0, difficulty;
    private SharedPreferences prefs;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure;
    private boolean mAutoStartSignInFlow = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean(TUTORIAL_PREF, false)) {

            tutorial(null);


        }

        Log.d("TAG", prefs.getInt(Achievements.OFFLINE_GAME_ACHIEVEMENT, 0) + "");

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.game_menu);
        GameView glView = (GameView) findViewById(R.id.game_view);

        TutorialActivity.createTutorialBoard(tutorialBoard);
        glView.renderer.setPlayBoard(tutorialBoard);
        glView.renderer.startRotation();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        seekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar);
        difficultyText = (TextView) findViewById(R.id.difficultyText);
        rating = (TextView) findViewById(R.id.rating);
        updateRating();

        setText(seekBar.getProgress());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (humanSet) {
                    progress = seekBar.getProgress();
                    progress = roundProgress(progress);
                    showLocks(level, progressToLevel(progress));
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
                progress = seekBar.getProgress();
                progress = roundProgress(progress);
                showLocks(level, progressToLevel(progress));
            }
        });

        youBegin = (RadioButton) findViewById(R.id.player_begin);
        botBegin = (RadioButton) findViewById(R.id.bot_begin);
        firstLock = (ImageView) findViewById(R.id.first_lock);
        secondLock = (ImageView) findViewById(R.id.second_lock);
        firstStar = (ImageView) findViewById(R.id.first_star);
        secondStar = (ImageView) findViewById(R.id.second_star);

        youBegin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    prefs.edit().putBoolean(BEGIN, true).apply();
                }
            }
        });

        botBegin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    prefs.edit().putBoolean(BEGIN, false).apply();
                }
                Log.d("togg", "toggling");
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            checkAchievements();
        } else {
            mGoogleApiClient.connect();
        }


        level = prefs.getInt(LEVEL_PREF, 0);
        Log.d("bot level", Integer.toString(level));

        difficulty = prefs.getInt(DIFFICULTY_PREF, 0);

        youBeginBoolean = prefs.getBoolean(BEGIN, true);
        if (youBeginBoolean) {
            youBegin.toggle();
        } else {
            botBegin.toggle();
        }

        seekBar.setProgress(difficulty);
        showLocks(level, progressToLevel(difficulty));

        updateRating();
    }

    public void playOnline(View v) {
        Intent intent = new Intent(getBaseContext(), OnlineModeActivity.class);
        startActivity(intent);
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
        if (level >= BotModeActivity.levelDifficulty(progress, youBegin.isChecked())) {
            Intent intent = new Intent(getBaseContext(), BotModeActivity.class);
            intent.putExtra(BEGIN, youBegin.isChecked());
            intent.putExtra(DIFFICULTY_PREF, seekBar.getProgress());
            startActivity(intent);
        } else {
            Toast.makeText(this, "not unlocked", Toast.LENGTH_SHORT).show();
        }
    }

    public void seeLeaderboard(View v) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    OnlineModeActivity.LEADERBOARD_ID), REQUEST_LEADERBOARD);
        } else {
                Toast.makeText(this, "not connected", Toast.LENGTH_SHORT).show();
        }

    }

    public void seeAchievements(View v) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                    REQUEST_ACHIEVEMENTS);
        } else {
            Toast.makeText(this, "not connected", Toast.LENGTH_SHORT).show();
        }
    }

    private int roundProgress(int progress) {
        if (progress < 17)
            return 0;
        if (progress < 51)
            return 33;
        if (progress < 83)
            return 66;
        return 100;

    }

    private int progressToLevel(int progress) {
        if (progress < 17)
            return 0;
        if (progress < 51)
            return 2;
        if (progress < 83)
            return 4;
        return 6;
    }

    private void showLocks(int level, int difficulty) {

        Log.d("showing fucking locks", level + " "  + difficulty);

        firstStar.setVisibility(View.INVISIBLE);
        secondStar.setVisibility(View.INVISIBLE);

        if (level < difficulty) {
            firstLock.setVisibility(View.VISIBLE);
            secondLock.setVisibility(View.VISIBLE);
        } else if (level == difficulty) {
            firstLock.setVisibility(View.INVISIBLE);
            secondLock.setVisibility(View.VISIBLE);
        } else {
            if (level == difficulty + 1) {
                firstStar.setVisibility(View.VISIBLE);
            } else {
                firstStar.setVisibility(View.VISIBLE);
                secondStar.setVisibility(View.VISIBLE);
            }
            firstLock.setVisibility(View.INVISIBLE);
            secondLock.setVisibility(View.INVISIBLE);
        }
    }

    private void setText(int progress) {
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

    private void checkAchievements() {

        if (prefs.getBoolean(Achievements.NEW_ACHIEVEMENT, false)) {
            for (String s : Achievements.achievements) {
                if (prefs.getInt(s, Achievements.INVISIBLE) == Achievements.COMPLETED) {
                    Games.Achievements.unlock(mGoogleApiClient, s);
                    prefs.edit().putInt(s, Achievements.SENT).apply();
                }
            }
            prefs.edit().putBoolean(Achievements.NEW_ACHIEVEMENT, false).apply();
        }

    }

    public void quit(View v) {
        finish();
    }

    private void updateRating(){
        int ratingScore = prefs.getInt(OnlineModeActivity.MULTIPLAYER_SCORE, 100);
        rating.setText(getString(R.string.rating) + Integer.toString(ratingScore));
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);

        switch (requestCode) {
            case OnlineModeActivity.RC_SIGN_IN:
                Log.d("tag", "onActivityResult with requestCode == RC_SIGN_IN, responseCode="
                        + responseCode + ", intent=" + intent);
                mResolvingConnectionFailure = false;
                if (responseCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else if (responseCode == RESULT_CANCELED) {
                    Log.d("tag", "canceled dont know why");
                    mGoogleApiClient.connect();
                } else {
                    BaseGameUtils.showActivityResultError(this, requestCode, responseCode, R.string.add_id);
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkAchievements();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d("rag", "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d("tag", "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, OnlineModeActivity.RC_SIGN_IN, "problem with sign in");
        }
    }
}
