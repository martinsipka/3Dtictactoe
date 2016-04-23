package com.example.dtictactoe.frontend.animations;

import android.opengl.Matrix;

import com.example.dtictactoe.frontend.MyGLRenderer;

/**
 * Created by martin on 23.4.16.
 */
public class DismemberCubeAnimation implements Animation {
    @Override
    public boolean perform(MyGLRenderer renderer) {
        int sign = 1;
        if (renderer.rotate) {
            Matrix.setIdentityM(renderer.mAccumulatedRotation, 0);
            //rotateAnimation();
            renderer.rotate = false;
        }
        if (renderer.dismem && !renderer.rotate) {
            for (int i = 0; i < 4; i++) {
                if (i % 2 == 1)
                    sign = -1 * sign;
                renderer.floorCords[i][0] += sign * MyGLRenderer.animSpeed;
                if (i % 3 == 0)
                    sign = -sign;
                renderer.floorCords[i][1] += sign * MyGLRenderer.animSpeed;
                sign = renderer.state;

                if (renderer.floorCords[3][1] > 1.15f) {

                    renderer.dismem = false;


                }
            }
        } else if (!renderer.rotate) {

            for (int j = 0; j < 4; j++) {
                renderer.floorCords[j][2] -= (j + 3.0f) * MyGLRenderer.animSpeed;
                if (renderer.floorCords[j][2] < -3.0f) {
                    for(int p = 0; p < 4; p++){
                        renderer.floorCords[p][2] = -3.0f;
                    }
                    renderer.state = MyGLRenderer.STATE_FLOORS;
                    return true;
                }
            }
        }
        return false;
    }
}
