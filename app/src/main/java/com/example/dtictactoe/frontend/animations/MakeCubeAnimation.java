package com.example.dtictactoe.frontend.animations;

import com.example.dtictactoe.frontend.MyGLRenderer;

/**
 * Created by Martin on 21. 9. 2015.
 */
public class MakeCubeAnimation implements Animation {

    @Override
    public boolean perform(MyGLRenderer renderer) {
        int sign = -1;
        if (renderer.dismem) {
            for (int i = 0; i < 4; i++) {
                if (i % 2 == 1)
                    sign = -1 * sign;
                renderer.floorCords[i][0] += sign * MyGLRenderer.animSpeed;
                if (i % 3 == 0)
                    sign = -sign;
                renderer.floorCords[i][1] += sign * MyGLRenderer.animSpeed;
                sign = renderer.state;

                if (renderer.floorCords[3][1] < 0.0f) {
                    for (int j = 0; j < 4; j++) {
                        renderer.floorCords[j][0] = 0.0f;
                        renderer.floorCords[j][1] = 0.0f;
                        renderer.floorCords[j][2] = -0.75f + 0.5f * j;
                    }
                    renderer.state = MyGLRenderer.STATE_CUBE;
                    renderer.rotate = true;
                    return true;
                }
            }
        } else {

            for (int j = 0; j < 4; j++) {
                renderer.floorCords[j][2] += (j + 3.0f) * 0.01f;
                if (renderer.floorCords[j][2] > 0.75f) {
                    for (int i = 0; i < 4; i++) {
                        renderer.floorCords[i][2] = -0.75f + 0.5f * i;
                    }
                    renderer.dismem = true;
                }
            }

        }
        return false;
    }
}
