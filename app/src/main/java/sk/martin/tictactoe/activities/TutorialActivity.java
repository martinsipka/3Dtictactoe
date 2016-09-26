package sk.martin.tictactoe.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import java.util.Stack;

import sk.martin.tictactoe.R;
import sk.martin.tictactoe.backend.Achievements;
import sk.martin.tictactoe.frontend.LineFloor;
import sk.martin.tictactoe.frontend.MyGLRenderer;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * Created by martin on 24.7.16.
 */
public class TutorialActivity extends GameActivity {

    public static final String TAG = "tutorial tag";

    private int[][][] tutorialBoard = new int[4][4][4];
    private int testCase = 1;
    private Button gotIt, help;
    MaterialShowcaseView game = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGameActivity(this);
        //createTutorialBoard(tutorialBoard);
        createTutorialBoard(tutorialBoard);

        glView.renderer.setPlayBoard(tutorialBoard);

        gotIt = (Button) findViewById(R.id.got_it);
        help = (Button) findViewById(R.id.instructions);

        hideControls();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        MaterialShowcaseView start = new MaterialShowcaseView.Builder(this)
                .renderOverNavigationBar()
                .setShapePadding(0)
                .withoutShape()
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        gotIt.setVisibility(View.VISIBLE);
                    }
                })
                .setDismissText(getResources().getString(R.string.next))
                .setContentText(getResources().getString(R.string.intro)).build();

        start.show(this);

        MaterialShowcaseView winOne = new MaterialShowcaseView.Builder(this)
                .renderOverNavigationBar()
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                        glView.renderer.gameOver = true;
                        gotIt.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        fadeOut(1);
                    }
                })
                .setShapePadding(0)
                .withoutShape()
                .setDismissText(getResources().getString(R.string.next))
                .setContentText(getResources().getString(R.string.win_one)).build();

        MaterialShowcaseView winTwo = new MaterialShowcaseView.Builder(this)
                .renderOverNavigationBar()
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                        gotIt.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        fadeOut(2);
                    }
                })
                .setShapePadding(0)
                .withoutShape()
                .setDismissText(getResources().getString(R.string.next))
                .setContentText(getResources().getString(R.string.win_two)).build();

        MaterialShowcaseView winThree = new MaterialShowcaseView.Builder(this)
                .renderOverNavigationBar()
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                        gotIt.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        fadeOut(3);

                    }
                })
                .setShapePadding(0)
                .withoutShape()
                .setDismissText(getResources().getString(R.string.next))
                .setContentText(getResources().getString(R.string.win_three)).build();



        MaterialShowcaseView cube = new MaterialShowcaseView.Builder(this)
                .setTargetTouchable(true)
                .renderOverNavigationBar()
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                        cubeView.setVisibility(View.VISIBLE);
                        glView.renderer.gameOver = false;
                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {

                    }
                })
                .setTarget(findViewById(R.id.cube_view))
                .setShapePadding(40)
                .setContentText(getResources().getString(R.string.cube_button)).build();

        MaterialShowcaseView first = new MaterialShowcaseView.Builder(this)
                .setTargetTouchable(true)
                .renderOverNavigationBar()
                .setTarget(findViewById(R.id.first))
                .setShapePadding(width/3)
                .setDelay(2000)
                .setDismissText(getResources().getString(R.string.next))
                .setContentText(getResources().getString(R.string.first_floor)).build();

        MaterialShowcaseView second = new MaterialShowcaseView.Builder(this)
                .setTargetTouchable(true)
                .renderOverNavigationBar()
                .setTarget(findViewById(R.id.second))
                .setShapePadding(width/3)
                .setDismissText(getResources().getString(R.string.next))
                .setContentText(getResources().getString(R.string.second_floor)).build();

        MaterialShowcaseView third = new MaterialShowcaseView.Builder(this)
                .setTargetTouchable(true)
                .renderOverNavigationBar()
                .setTarget(findViewById(R.id.third))
                .setShapePadding(width/3)
                .setDismissText(getResources().getString(R.string.next))
                .setContentText(getResources().getString(R.string.third_floor)).build();

        MaterialShowcaseView fourth = new MaterialShowcaseView.Builder(this)
                .setTargetTouchable(true)
                .renderOverNavigationBar()
                .setTarget(findViewById(R.id.fourth))
                .setShapePadding(width/3)
                .setDismissText(getResources().getString(R.string.next))
                .setContentText(getResources().getString(R.string.fourth_floor)).build();

        buildGame();

        final Stack<MaterialShowcaseView> showStack = new Stack<MaterialShowcaseView>();
        showStack.push(winThree);
        showStack.push(winTwo);
        showStack.push(winOne);

        final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.addSequenceItem(cube);
        sequence.addSequenceItem(first);
        sequence.addSequenceItem(second);
        sequence.addSequenceItem(third);
        sequence.addSequenceItem(fourth);
        sequence.addSequenceItem(game);

        cubeView.setVisibility(View.INVISIBLE);

        gotIt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!showStack.isEmpty()) {
                    showStack.pop().show(TutorialActivity.this);
                } else {
                    sequence.start();
                    gotIt.setVisibility(View.INVISIBLE);
                }
            }

        });



    }

    private void buildGame(){
        game = new MaterialShowcaseView.Builder(this)
                .renderOverNavigationBar()
                .setShapePadding(0)
                .withoutShape()
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        fadeOut(4);
                        help.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                buildGame();
                                game.show(TutorialActivity.this);
                            }
                        });
                        help.setVisibility(View.VISIBLE);
                    }
                })
                .setDismissText(getResources().getString(R.string.next))
                .setContentText(getResources().getString(R.string.assisted_win)).build();
    }

    public static void clear(int[][][] board){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                for(int k = 0; k < 4; k++){
                    board[i][j][k] = 0;
                }
            }
        }
    }

    public static void createTutorialBoard(int[][][] board) {
        board[1][0][0] = MyGLRenderer.TURN_RED;
        board[0][0][1] = MyGLRenderer.TURN_BLUE;
        board[0][3][1] = MyGLRenderer.TURN_RED;
        board[1][2][0] = MyGLRenderer.TURN_BLUE;
        board[3][0][3] = MyGLRenderer.TURN_RED;
        board[0][3][0] = MyGLRenderer.TURN_BLUE;
        board[2][2][2] = MyGLRenderer.TURN_RED;
        board[1][0][1] = MyGLRenderer.TURN_BLUE;

    }

    public static void winCaseOne(int[][][] board){
        board[2][2][0] = LineFloor.RED_WIN;
        board[3][2][1] = MyGLRenderer.TURN_BLUE;
        board[2][2][1] = LineFloor.RED_WIN;
        board[2][0][3] = MyGLRenderer.TURN_BLUE;
        board[2][2][2] = LineFloor.RED_WIN;
        board[0][2][0] = MyGLRenderer.TURN_BLUE;
        board[2][2][3] = LineFloor.RED_WIN;

    }

    public static void winCaseTwo(int[][][] board){
        board[1][3][0] = LineFloor.RED_WIN;
        board[3][2][1] = MyGLRenderer.TURN_BLUE;
        board[1][2][1] = LineFloor.RED_WIN;
        board[2][0][3] = MyGLRenderer.TURN_BLUE;
        board[1][1][2] = LineFloor.RED_WIN;
        board[0][1][0] = MyGLRenderer.TURN_BLUE;
        board[1][0][3] = LineFloor.RED_WIN;

    }

    public static void winCaseThree(int[][][] board){
        board[0][3][0] = LineFloor.RED_WIN;
        board[3][2][1] = MyGLRenderer.TURN_BLUE;
        board[1][2][1] = LineFloor.RED_WIN;
        board[2][0][3] = MyGLRenderer.TURN_BLUE;
        board[2][1][2] = LineFloor.RED_WIN;
        board[0][1][0] = MyGLRenderer.TURN_BLUE;
        board[3][0][3] = LineFloor.RED_WIN;

    }

    public static void testCaseOne(int[][][] board){
        board[1][0][0] = MyGLRenderer.TURN_RED;
        board[0][0][1] = MyGLRenderer.TURN_BLUE;
        board[0][3][1] = MyGLRenderer.TURN_RED;
        board[1][2][0] = MyGLRenderer.TURN_BLUE;
        board[2][0][3] = MyGLRenderer.TURN_RED;
        board[0][3][0] = MyGLRenderer.TURN_BLUE;
        board[1][2][2] = MyGLRenderer.TURN_RED;
        board[1][0][1] = MyGLRenderer.TURN_BLUE;
        board[2][1][2] = MyGLRenderer.TURN_RED;
        board[3][2][2] = MyGLRenderer.TURN_BLUE;
        board[3][0][2] = MyGLRenderer.TURN_RED;
        board[0][2][3] = MyGLRenderer.TURN_BLUE;
        board[0][3][2] = LineFloor.TUTORIAL_HIGHLIGHT;

    }

    public static void testCaseTwo(int[][][] board){
        board[1][0][0] = MyGLRenderer.TURN_RED;
        board[0][0][1] = MyGLRenderer.TURN_BLUE;
        board[0][3][1] = MyGLRenderer.TURN_RED;
        board[1][2][0] = MyGLRenderer.TURN_BLUE;
        board[2][0][3] = MyGLRenderer.TURN_RED;
        board[0][3][0] = MyGLRenderer.TURN_BLUE;
        board[2][2][1] = MyGLRenderer.TURN_RED;
        board[1][0][1] = MyGLRenderer.TURN_BLUE;
        board[2][1][2] = MyGLRenderer.TURN_RED;
        board[1][2][2] = MyGLRenderer.TURN_BLUE;
        board[2][0][2] = MyGLRenderer.TURN_RED;
        board[0][2][3] = MyGLRenderer.TURN_BLUE;

    }

    public static void testCaseThree(int[][][] board){
        board[0][3][0] = MyGLRenderer.TURN_RED;
        board[3][2][1] = MyGLRenderer.TURN_BLUE;
        board[1][2][1] = MyGLRenderer.TURN_RED;
        board[2][0][3] = MyGLRenderer.TURN_BLUE;
        board[2][1][2] = MyGLRenderer.TURN_RED;
        board[0][0][0] = MyGLRenderer.TURN_BLUE;
    }

    @Override
    public void nextMove(boolean winningMove){
        if(!winningMove) {
            if (testCase == 1) {

                game = new MaterialShowcaseView.Builder(this)
                        .renderOverNavigationBar()
                        .setShapePadding(0)
                        .withoutShape()
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                glView.renderer.gameOver = false;
                                glView.renderer.turn = MyGLRenderer.TURN_RED;
                                fadeOut(4);
                                help.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        nextMove(false);
                                    }
                                });
                            }
                        })
                        .setDismissText(getResources().getString(R.string.next))
                        .setContentText(getResources().getString(R.string.try_again)).build();
                game.show(this);
                testCase = 1;
            } else if (testCase == 2) {

                game = new MaterialShowcaseView.Builder(this)
                        .renderOverNavigationBar()
                        .setShapePadding(0)
                        .withoutShape()
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                glView.renderer.gameOver = false;
                                glView.renderer.turn = MyGLRenderer.TURN_RED;
                                fadeOut(5);
                                help.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        game.resetSingleUse();
                                        game.show(TutorialActivity.this);
                                    }
                                });
                            }
                        })
                        .setDismissText(getResources().getString(R.string.next))
                        .setContentText(getResources().getString(R.string.try_again)).build();
                game.show(this);
                testCase = 2;
            } else {

                game = new MaterialShowcaseView.Builder(this)
                        .renderOverNavigationBar()
                        .setShapePadding(0)
                        .withoutShape()
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                glView.renderer.gameOver = false;
                                glView.renderer.turn = MyGLRenderer.TURN_RED;
                                fadeOut(6);
                                help.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        game.resetSingleUse();
                                        game.show(TutorialActivity.this);
                                    }
                                });
                            }
                        })
                        .setDismissText(getResources().getString(R.string.next))
                        .setContentText(getResources().getString(R.string.try_again)).build();
                game.show(this);
                testCase = 3;
            }
        }
    }

    @Override
    public void setWinner(int winner){
        if(testCase == 1) {
            game = new MaterialShowcaseView.Builder(this)
                    .renderOverNavigationBar()
                    .setShapePadding(0)
                    .withoutShape()
                    .setDelay(0)
                    .setListener(new IShowcaseListener() {
                        @Override
                        public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                        }

                        @Override
                        public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                            glView.renderer.gameOver = false;
                            glView.renderer.turn = MyGLRenderer.TURN_RED;
                            fadeOut(5);
                            help.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    testCase = 1;
                                    setWinner(0);
                                }
                            });
                        }
                    })
                    .setDismissText(getResources().getString(R.string.next))
                    .setContentText(getResources().getString(R.string.second_win)).build();
            game.show(this);
            testCase = 2;
            glView.renderer.turn = MyGLRenderer.TURN_RED;
        } else if (testCase == 2){

            game = new MaterialShowcaseView.Builder(this)
                    .renderOverNavigationBar()
                    .setShapePadding(0)
                    .withoutShape()
                    .setDelay(0)
                    .setListener(new IShowcaseListener() {
                        @Override
                        public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                        }

                        @Override
                        public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                            glView.renderer.gameOver = false;
                            glView.renderer.turn = MyGLRenderer.TURN_RED;
                            fadeOut(6);
                            help.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    testCase = 2;
                                    setWinner(0);
                                }
                            });
                        }
                    })
                    .setDismissText(getResources().getString(R.string.next))
                    .setContentText(getResources().getString(R.string.third_win)).build();
            game.show(this);
            testCase = 3;


        } else {
            showCompletedShowCase();
        }
    }

    private void showCompletedShowCase(){

        game = new MaterialShowcaseView.Builder(this)
                .renderOverNavigationBar()
                .setShapePadding(0)
                .withoutShape()
                .setDelay(1500)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                        SharedPreferences prefs = PreferenceManager
                                .getDefaultSharedPreferences(TutorialActivity.this);
                        if (!prefs.getBoolean(MenuActivity.TUTORIAL_PREF, false)){
                            prefs.edit().putInt(Achievements.TUTORIAL_ACHIEVEMENT,
                                    Achievements.COMPLETED).apply();
                            prefs.edit().putBoolean(MenuActivity.TUTORIAL_PREF,
                                    true).apply();
                            prefs.edit().putBoolean(Achievements.NEW_ACHIEVEMENT, true).apply();
                        }
                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        finish();
                    }
                })
                .setDismissText(getResources().getString(R.string.next))
                .setContentText(getResources().getString(R.string.tutorial_end)).build();
        game.show(this);

    }

    @Override
    void transition(int transitionID){
        clear(tutorialBoard);
        switch(transitionID){
            case 1 :
                winCaseOne(tutorialBoard);
                gotIt.setVisibility(View.VISIBLE);
                break;
            case 2 :
                winCaseTwo(tutorialBoard);
                gotIt.setVisibility(View.VISIBLE);
                break;
            case 3 :
                winCaseThree(tutorialBoard);
                gotIt.setVisibility(View.VISIBLE);
                break;
            case 4 :
                testCaseOne(tutorialBoard);
                break;
            case 5 :
                testCaseTwo(tutorialBoard);
                break;
            case 6 :
                testCaseThree(tutorialBoard);
                break;
        }
        glView.requestRender();

    }

}
