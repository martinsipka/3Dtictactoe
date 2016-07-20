package sk.martin.tictactoe.frontend.animations;

import sk.martin.tictactoe.frontend.MyGLRenderer;

/**
 * Created by Martin on 21. 9. 2015.
 */
public class MakeCubeAnimation implements Animation {

    public static final int DISMEM_STEPS = 100;
    public static final int DOWN_STEPS = 40;

    private boolean dismem;
    private int step;

    private NiceInterpolator dismemInterpolator;
    private NiceInterpolator[] downInterpolators = new NiceInterpolator[4];

    public MakeCubeAnimation(){
        dismemInterpolator = new NiceInterpolator(1.15f, 0.0f);
        for(int i = 0; i < 4; i++){
            downInterpolators[i] = new NiceInterpolator(-3.0f, -0.75f + 0.5f*i);
        }
        step = 0;
        dismem = false;
    }

    @Override
    public boolean perform(MyGLRenderer renderer) {
        int sign = 1;
        if (dismem) {

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
                    renderer.state = MyGLRenderer.STATE_CUBE;
                    return true;
                }
            }
        } else {

            step++;
            for (int j = 0; j < 4; j++) {
                renderer.floorCords[j][2] = downInterpolators[j].interpolate((float) step/DOWN_STEPS);
            }
            if (step == DOWN_STEPS) {
                step = 0;
                dismem = true;

            }

        }
        return false;
    }
}
