package sk.martin.tictactoe.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;

import com.google.android.gms.common.api.GoogleApiClient;

import sk.martin.tictactoe.R;
import sk.martin.tictactoe.frontend.MyGLRenderer;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by martin on 24.7.16.
 */
public class TutorialActivity extends GameActivity {

    public static final String SHOWCASE_ID = "tutorial";

    private int[][][] tutorialBoard = new int[4][4][4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGameActivity(this);
        //createTutorialBoard(tutorialBoard);
        createTutorialBoard(tutorialBoard);

        glView.renderer.setPlayBoard(tutorialBoard);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        MaterialShowcaseView board = new MaterialShowcaseView.Builder(this)
                .renderOverNavigationBar()
                .setShapePadding(0)
                .withoutShape()
                .setDismissText("GOT IT")
                .setContentText("This is a playboard! Rotate it freely to see the game" +
                        " from another perespective.").build();

        MaterialShowcaseView winOne = new MaterialShowcaseView.Builder(this)
                .renderOverNavigationBar()
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                        clear(tutorialBoard);
                        winCaseOne(tutorialBoard);
                        glView.requestRender();
                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {

                    }
                })
                .setShapePadding(0)
                .setDelay(3000)
                .withoutShape()
                .setDismissText("GOT IT")
                .setContentText("Win by placing four cubes in a line like this for example").build();

        MaterialShowcaseView winTwo = new MaterialShowcaseView.Builder(this)
                .renderOverNavigationBar()
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                        clear(tutorialBoard);
                        winCaseTwo(tutorialBoard);
                        glView.requestRender();
                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {

                    }
                })
                .setShapePadding(0)
                .setDelay(3000)
                .withoutShape()
                .setDismissText("GOT IT")
                .setContentText("Or like this...").build();

        MaterialShowcaseView winThree = new MaterialShowcaseView.Builder(this)
                .renderOverNavigationBar()
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                        clear(tutorialBoard);
                        winCaseThree(tutorialBoard);
                        glView.requestRender();
                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {

                    }
                })
                .setShapePadding(0)
                .setDelay(3000)
                .withoutShape()
                .setDismissText("GOT IT")
                .setContentText("Even this is fine.").build();



        MaterialShowcaseView cube = new MaterialShowcaseView.Builder(this)
                .setTargetTouchable(true)
                .renderOverNavigationBar()
                .setTarget(findViewById(R.id.cube_view))
                .setShapePadding(40)
                .setDelay(4000)
                .setContentText("Use this button to change from CubeView to FloorsView." +
                        " Only in FloorsView you can make moves").build();

        MaterialShowcaseView first = new MaterialShowcaseView.Builder(this)
                .setTargetTouchable(true)
                .renderOverNavigationBar()
                .setTarget(findViewById(R.id.first))
                .setShapePadding(width/3)
                //.withRectangleShape(false)
                .setDelay(2000)
                .setDismissText("GOT IT")
                .setContentText("First floor of the cube").build();

        MaterialShowcaseView second = new MaterialShowcaseView.Builder(this)
                .setTargetTouchable(true)
                .renderOverNavigationBar()
                .setTarget(findViewById(R.id.second))
                .setShapePadding(width/3)
                //.withRectangleShape(false)
                .setDismissText("GOT IT")
                .setContentText("Second floor of the cube").build();

        MaterialShowcaseView third = new MaterialShowcaseView.Builder(this)
                .setTargetTouchable(true)
                .renderOverNavigationBar()
                .setTarget(findViewById(R.id.third))
                .setShapePadding(width/3)
                //.withRectangleShape(false)
                .setDismissText("GOT IT")
                .setContentText("Third floor of the cube").build();

        MaterialShowcaseView fourth = new MaterialShowcaseView.Builder(this)
                .setTargetTouchable(true)
                .renderOverNavigationBar()
                .setTarget(findViewById(R.id.fourth))
                .setShapePadding(width/3)
                //.withRectangleShape(false)
                .setDismissText("GOT IT")
                .setContentText("Fourth floor of the cube").build();

        MaterialShowcaseView showNow = new MaterialShowcaseView.Builder(this)
                .renderOverNavigationBar()
                .setShapePadding(0)
                .withoutShape()
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                        clear(tutorialBoard);
                        testCaseOne(tutorialBoard);
                        glView.requestRender();
                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {

                    }
                })
                .setDismissText("GOT IT")
                .setDelay(1500)
                .setContentText("Good. Now win with a single turn.").build();




        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.addSequenceItem(board);
        sequence.addSequenceItem(winOne);
        sequence.addSequenceItem(winTwo);
        sequence.addSequenceItem(winThree);
        sequence.addSequenceItem(cube);
        sequence.addSequenceItem(first);
        sequence.addSequenceItem(second);
        sequence.addSequenceItem(third);
        sequence.addSequenceItem(fourth);
        sequence.addSequenceItem(showNow);
        sequence.start();



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
        board[0][0][0] = MyGLRenderer.TURN_RED;
        board[0][0][1] = MyGLRenderer.TURN_BLUE;
        board[0][3][1] = MyGLRenderer.TURN_RED;
        board[1][2][0] = MyGLRenderer.TURN_BLUE;
        board[3][0][3] = MyGLRenderer.TURN_RED;
        board[0][3][0] = MyGLRenderer.TURN_BLUE;
        board[2][2][2] = MyGLRenderer.TURN_RED;
        board[1][0][1] = MyGLRenderer.TURN_BLUE;

    }

    public static void winCaseOne(int[][][] board){
        board[2][2][0] = MyGLRenderer.TURN_RED;
        board[3][2][1] = MyGLRenderer.TURN_BLUE;
        board[2][2][1] = MyGLRenderer.TURN_RED;
        board[2][0][3] = MyGLRenderer.TURN_BLUE;
        board[2][2][2] = MyGLRenderer.TURN_RED;
        board[0][0][0] = MyGLRenderer.TURN_BLUE;
        board[2][2][3] = MyGLRenderer.TURN_RED;

    }

    public static void winCaseTwo(int[][][] board){
        board[1][3][0] = MyGLRenderer.TURN_RED;
        board[3][2][1] = MyGLRenderer.TURN_BLUE;
        board[1][2][1] = MyGLRenderer.TURN_RED;
        board[2][0][3] = MyGLRenderer.TURN_BLUE;
        board[1][1][2] = MyGLRenderer.TURN_RED;
        board[0][0][0] = MyGLRenderer.TURN_BLUE;
        board[1][0][3] = MyGLRenderer.TURN_RED;

    }

    public static void winCaseThree(int[][][] board){
        board[0][3][0] = MyGLRenderer.TURN_RED;
        board[3][2][1] = MyGLRenderer.TURN_BLUE;
        board[1][2][1] = MyGLRenderer.TURN_RED;
        board[2][0][3] = MyGLRenderer.TURN_BLUE;
        board[2][1][2] = MyGLRenderer.TURN_RED;
        board[0][0][0] = MyGLRenderer.TURN_BLUE;
        board[3][0][3] = MyGLRenderer.TURN_RED;

    }

    public static void testCaseOne(int[][][] board){
        board[0][0][0] = MyGLRenderer.TURN_RED;
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

    }

    public static void testCaseTwo(int[][][] board){
        board[0][0][0] = MyGLRenderer.TURN_RED;
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
        board[0][0][0] = MyGLRenderer.TURN_RED;
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

}
