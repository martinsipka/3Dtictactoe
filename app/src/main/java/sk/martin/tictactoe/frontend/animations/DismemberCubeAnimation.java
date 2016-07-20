package sk.martin.tictactoe.frontend.animations;

import sk.martin.tictactoe.backend.AlgebraObjects.Quaternion;
import sk.martin.tictactoe.frontend.MyGLRenderer;

/**
 * Created by martin on 23.4.16.
 */
public class DismemberCubeAnimation implements Animation {

    public static final int ROTATION_STEPS = 60;
    public static final int DISMEM_STEPS = 100;
    public static final int DOWN_STEPS = 40;

    private boolean rotate = true;
    private boolean dismem = false;
    private int step = 0;
    private Quaternion identity = new Quaternion();
    private Quaternion startPosition;
    private NiceInterpolator rotateInterpolator;
    private NiceInterpolator dismemInterpolator;
    private NiceInterpolator[] downInterpolators = new NiceInterpolator[4];

    public DismemberCubeAnimation(){
        rotateInterpolator = new NiceInterpolator(0, 1);
        dismemInterpolator = new NiceInterpolator(0, 1.15f);
        for(int i = 0; i < 4; i++){
            downInterpolators[i] = new NiceInterpolator(-0.75f + 0.5f*i, -3.0f);
        }
    }

    @Override
    public boolean perform(MyGLRenderer renderer) {
        int sign = 1;

        if(startPosition == null){
            startPosition = new Quaternion(renderer.currentRotation);
        }

        if (rotate) {
            step++;
            //renderer.currentRotation = Quaternion.slerp((float) step/STEPS, startPosition, identity);
            if(renderer.currentRotation.equals(identity)){
                step = ROTATION_STEPS;
            }
            renderer.currentRotation = new Quaternion(startPosition).slerp(identity,
                    rotateInterpolator.interpolate((float) step/ROTATION_STEPS));

            if(step == ROTATION_STEPS) {
                dismem = true;
                rotate = false;
                step = 0;
                renderer.currentRotation = new Quaternion();
            }
        } else if (dismem) {
            for (int i = 0; i < 4; i++) {
                step++;
                float currentValue = dismemInterpolator.interpolate((float) step/DISMEM_STEPS);
                if (i % 2 == 1)
                    sign = -1 * sign;
                renderer.floorCords[i][0] = sign * currentValue;
                if (i % 3 == 0)
                    sign = -sign;
                renderer.floorCords[i][1] = sign * currentValue;
                sign = 1;

                if (step == DISMEM_STEPS) {

                    dismem = false;
                    step = 0;
                }
            }
        } else  {
            step++;
            for (int j = 0; j < 4; j++) {
                renderer.floorCords[j][2] = downInterpolators[j].interpolate((float) step/DOWN_STEPS);
            }
            if (step == DOWN_STEPS) {
                renderer.state = MyGLRenderer.STATE_FLOORS;
                return true;
            }
        }
        return false;
    }

}
