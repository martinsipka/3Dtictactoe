package sk.martin.tictactoe.frontend.animations;

import android.util.Log;

import sk.martin.tictactoe.backend.AlgebraObjects.Quaternion;
import sk.martin.tictactoe.frontend.MyGLRenderer;

/**
 * Created by martin on 17.8.16.
 */
public class RotateAnimation implements Animation {

    private static final float ANIMATION_SPEED = 0.005f;

    @Override
    public boolean perform(MyGLRenderer renderer) {
        renderer.currentRotation.mulInplace(new Quaternion(0.0f, 1.0f, 0.0f, -ANIMATION_SPEED));
        renderer.currentRotation.mulInplace(new Quaternion(1.0f, 0.0f, 0.0f, -ANIMATION_SPEED*2));
        return false;
    }

}
