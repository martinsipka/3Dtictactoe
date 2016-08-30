package sk.martin.tictactoe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sk.martin.tictactoe.R;
import sk.martin.tictactoe.backend.Move;
import sk.martin.tictactoe.backend.gameutils.BaseGameUtils;
import sk.martin.tictactoe.backend.gameutils.GameCountDown;
import sk.martin.tictactoe.frontend.LineFloor;
import sk.martin.tictactoe.frontend.MyGLRenderer;

/**
 * Created by Martin on 29. 9. 2015.
 */
public class OnlineModeActivity extends GameActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        RoomUpdateListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener {

    public static final byte TURN_START = 0;
    public static final byte POSITION_UPDATE = 1;
    public static final byte REMATCH_REQUEST = 2;
    public static final byte TIME_OVER = 3;

    public static final int PLAYBOARD_OFFSET = 5;
    public static final int TIME_OFFSET = 100;

    public static final int MY_TURN = 1;
    public static final int ENEMY_TURN = 2;

    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;

    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;

    public static final long GAME_TIME = 300000L;

    public static final String TAG = "ONLINE tag";

    private boolean mResolvingConnectionFailure, mSignInClicked, mAutoStartSignInFlow = true;

    private String mRoomId;

    private List<Participant> participants = new ArrayList<>();

    private String mMyId;

    private boolean firstMatch = true;

    private boolean wantRematch = false;

    private RelativeLayout connectingLayout;
    private int redScore, blueScore;
    private TextView redScoreBoard, blueScoreBoard;
    private Button rematch;

    private GoogleApiClient mGoogleApiClient;

    private int startTurn;
    private int myTurnColor;
    private int enemyTurnColor;
    private byte myRandom = 0;
    private byte attempt = 0;

    private GameCountDown myCountDown, enemyCountDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGameActivity(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        connectingLayout = (RelativeLayout) findViewById(R.id.connecting_layout);
        connectingLayout.setVisibility(View.VISIBLE);

        redScoreBoard = (TextView) findViewById(R.id.red_score);
        blueScoreBoard = (TextView) findViewById(R.id.blue_score);

        redScoreBoard.setVisibility(View.VISIBLE);
        blueScoreBoard.setVisibility(View.VISIBLE);

        rematch = (Button) findViewById(R.id.rematch);

    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.w(TAG,
                    "GameHelper: client was already connected on onStart()");
        } else {
            Log.d(TAG, "Connecting client.");
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    public void nextMove() {

        byte[] message = new byte[200];
        message[0] = POSITION_UPDATE;
        BaseGameUtils.createByteArray(message, getPlayBoard(), PLAYBOARD_OFFSET);
        BaseGameUtils.longToBytes(myCountDown.getTime(), message, TIME_OFFSET);

        for (Participant p : participants) {
            if (!p.getParticipantId().equals(mMyId)) {
                Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, message,
                        mRoomId, p.getParticipantId());
            }
        }
        setWaiting();
        myCountDown.pause();
        enemyCountDown.resume();
        Log.d(TAG, "send");

    }

    @Override
    public void setWinner(int winner){
        nextMove();
        myCountDown.reset();
        enemyCountDown.reset();
        if (firstMatch) {
            winText.setText("Switching sides...");
            if(winner == LineFloor.RED_WIN){
                redScore++;
            } else if(winner == LineFloor.BLUE_WIN){
                blueScore++;
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if(startTurn == MY_TURN){
                        startTurn = ENEMY_TURN;
                        setWaiting();
                    } else {
                        startTurn = MY_TURN;
                        setReady();
                    }
                    glView.newGame();
                }

            }, 5000);
            firstMatch = false;
            updateScore();
            Log.d(TAG, "first match over");
        } else {
            if (redScore > blueScore) {
                super.setWinner(LineFloor.RED_WIN);
            } else if (redScore < blueScore) {
                super.setWinner(LineFloor.BLUE_WIN);
            } else {
                super.setWinner(56985470);
            }
        }
    }

    @Override
    public void timeOver(int turn){
        byte[] msg = new byte[64];

        msg[0] = TIME_OVER;

        for (Participant p : participants) {
            if (!p.getParticipantId().equals(mMyId)) {
                Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, msg,
                        mRoomId, p.getParticipantId());
            }
        }
        timeOver(myTurnColor);
        Log.d(TAG, "send");
    }

    @Override
    public void rematch() {

        byte[] msg = new byte[64];

        msg[0] = REMATCH_REQUEST;

        for (Participant p : participants) {
            if (!p.getParticipantId().equals(mMyId)) {
                Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, msg,
                        mRoomId, p.getParticipantId());
            }
        }
        Log.d(TAG, "send");

        if(wantRematch){
            rematchAccepted();
        }

    }

    private void rematchAccepted(){
        winText.setVisibility(View.INVISIBLE);
        rematch.setVisibility(View.INVISIBLE);
        redScore = 0;
        blueScore = 0;
        updateScore();
        glView.newGame();
        if(startTurn == MY_TURN){
            startTurn = ENEMY_TURN;
            setWaiting();
        } else {
            startTurn = MY_TURN;
        }
        firstMatch = false;
        wantRematch = false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startQuickGame();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);

        switch (requestCode) {
            case RC_SELECT_PLAYERS:
                // we got the result from the "select players" UI -- ready to create the room
                //handleSelectPlayersResult(responseCode, intent);
                break;
            case RC_INVITATION_INBOX:
                // we got the result from the "select invitation" UI (invitation inbox). We're
                // ready to accept the selected invitation:
                //handleInvitationInboxResult(responseCode, intent);
                break;
            case RC_WAITING_ROOM:
                // we got the result from the "waiting room" UI.
                if (responseCode == Activity.RESULT_OK) {
                    // ready to decideTurn playing
                    Log.d(TAG, "Starting game (waiting room returned OK).");
                    //startGame(true);
                } else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player indicated that they want to leave the room
                    //leaveRoom();
                    finish();
                } else if (responseCode == Activity.RESULT_CANCELED) {
                    finish();
                }
                break;
            case RC_SIGN_IN:
                Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, responseCode="
                        + responseCode + ", intent=" + intent);
                mSignInClicked = false;
                mResolvingConnectionFailure = false;
                if (responseCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else if (responseCode == RESULT_CANCELED) {
                    Log.d(TAG, "canceled dont know why");
                    mGoogleApiClient.connect();
                } else {
                    BaseGameUtils.showActivityResultError(this, requestCode, responseCode, R.string.add_id);
                }
                break;
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Log.d(TAG, "connection failed " + connectionResult.getErrorCode());
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, "problem with sign in");
        }

        //switchToScreen(R.id.screen_sign_in);
    }

    // create a RoomConfigBuilder that's appropriate for your implementation
    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            //TODO showGameError();
            return;
        }

        // save room ID so we can leave cleanly before the game starts.
        mRoomId = room.getRoomId();

        // show the waiting room UI
        showWaitingRoom(room);
    }

    @Override
    public void onJoinedRoom(int i, Room room) {

    }

    @Override
    public void onLeftRoom(int i, String s) {

    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            //TODO showGameError();
            return;
        }
        participants = room.getParticipants();
        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));
        decideTurn();
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        Log.d(TAG, "received");
        byte[] message = realTimeMessage.getMessageData();

        switch (message[0]) {
            case TURN_START:

                synchronized (this) {
                    if (message[1] > attempt) {
                        attempt++;
                        sendStarterShake();
                        Log.e(TAG, "FATAL LOGIC ERROR HIGHER ATTEMPT");
                    }

                    if (message[2] == myRandom) {
                        attempt++;
                        sendStarterShake();
                    } else {
                        startTurn = myRandom > message[2] ? MY_TURN : ENEMY_TURN;
                        if(startTurn == MY_TURN){
                            myTurnColor = MyGLRenderer.TURN_RED;
                            enemyTurnColor = MyGLRenderer.TURN_BLUE;
                            for (Participant p : participants) {
                                if (!p.getParticipantId().equals(mMyId)) {
                                    blueName.setText(p.getDisplayName());
                                } else {
                                    redName.setText(p.getDisplayName());
                                }
                            }
                        } else {
                            myTurnColor = MyGLRenderer.TURN_BLUE;
                            enemyTurnColor = MyGLRenderer.TURN_RED;
                            for (Participant p : participants) {
                                if (!p.getParticipantId().equals(mMyId)) {
                                    redName.setText(p.getDisplayName());
                                } else {
                                    blueName.setText(p.getDisplayName());
                                }
                            }
                        }
                        startMatch();
                        myCountDown = new GameCountDown(GAME_TIME, this, myTurnColor, true);
                        enemyCountDown = new GameCountDown(GAME_TIME, this, enemyTurnColor, false);
                        updateTime(GAME_TIME, MyGLRenderer.TURN_RED);
                        updateTime(GAME_TIME, MyGLRenderer.TURN_BLUE);
                    }

                }

                break;
            case TIME_OVER:
                timeOver(enemyTurnColor);
                break;
            case POSITION_UPDATE:
                int[][][] newBoard = BaseGameUtils.createIntArray(message, PLAYBOARD_OFFSET);
                Move move = BaseGameUtils.findNew(getPlayBoard(), newBoard, startTurn);
                enemyCountDown.updateTime(BaseGameUtils.bytesToLong(message, TIME_OFFSET));
                Log.d(TAG, "" + markSquare(move));
                setReady();
                myCountDown.resume();
                enemyCountDown.pause();
                break;
            case REMATCH_REQUEST:
                if(wantRematch){
                    rematchAccepted();
                }
                //show rematch request
                break;
        }
    }

    @Override
    public void onRoomConnecting(Room room) {

    }

    @Override
    public void onRoomAutoMatching(Room room) {

    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {

    }

    @Override
    public void onPeerDeclined(Room room, List<String> list) {

    }

    @Override
    public void onPeerJoined(Room room, List<String> list) {

    }

    @Override
    public void onPeerLeft(Room room, List<String> list) {

    }

    @Override
    public void onConnectedToRoom(Room room) {

    }

    @Override
    public void onDisconnectedFromRoom(Room room) {

    }

    @Override
    public void onPeersConnected(Room room, List<String> list) {

    }

    @Override
    public void onPeersDisconnected(Room room, List<String> list) {

    }

    @Override
    public void onP2PConnected(String s) {

    }

    @Override
    public void onP2PDisconnected(String s) {

    }

    void startQuickGame() {

        Log.d(TAG, "starting quick game");
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        keepScreenOn();
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
    }

    void showWaitingRoom(Room room) {

        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    void decideTurn() {

        synchronized (this) {
            if (attempt == 0) {
                sendStarterShake();
            }
        }
    }

    void sendStarterShake() {

        byte[] msg = new byte[64];
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        byte randomNum = (byte) rand.nextInt(256);
        myRandom = randomNum;
        msg[0] = TURN_START;
        msg[1] = attempt;
        msg[2] = randomNum;

        for (Participant p : participants) {
            if (!p.getParticipantId().equals(mMyId)) {
                Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, msg,
                        mRoomId, p.getParticipantId());
            }
        }
        Log.d(TAG, "send");
    }

    void startMatch(){
        connectingLayout.setVisibility(View.INVISIBLE);
        if(startTurn == ENEMY_TURN){
            setWaiting();
            enemyCountDown.resume();
        }
        myCountDown.resume();
    }

    void updateScore(){
        redScoreBoard.setText(Integer.toString(redScore));
        blueScoreBoard.setText(Integer.toString(blueScore));
    }

    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
