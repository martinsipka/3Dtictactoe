package sk.martin.tictactoe.frontend.animations;

import sk.martin.tictactoe.frontend.MyGLRenderer;

/**
 * Created by martin on 23.4.16.
 */
public class ZoomOutAnimation implements Animation {

    public static final int STEPS = 30;
    private NiceInterpolator zoomInterpolator;
    private int step;

    public ZoomOutAnimation(){
        zoomInterpolator = new NiceInterpolator(1.0f, 0.0f);
        step = 0;
    }

    @Override
    public boolean perform(MyGLRenderer renderer) {
        step++;
        float value = zoomInterpolator.interpolate((float) step/STEPS);
        renderer.cPosX = renderer.zoomX * value;
        renderer.cPosY = renderer.zoomY * value;
        renderer.cPosZ = value;
        if (step == STEPS) {
            renderer.state = MyGLRenderer.STATE_FLOORS;
            return true;
        }
        return false;
    }
}
