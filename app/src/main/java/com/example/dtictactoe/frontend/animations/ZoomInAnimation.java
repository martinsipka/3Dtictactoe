package com.example.dtictactoe.frontend.animations;

import com.example.dtictactoe.frontend.MyGLRenderer;

/**
 * Created by martin on 23.4.16.
 */
public class ZoomInAnimation implements Animation {
    @Override
    public boolean perform(MyGLRenderer renderer) {
        renderer.cPosX += renderer.zoomX * MyGLRenderer.animSpeed;
        renderer.cPosY += renderer.zoomY * MyGLRenderer.animSpeed;
        renderer.cPosZ += renderer.animSpeed;
        if (renderer.cPosX * renderer.zoomX > 1.0f) {
            renderer.state = MyGLRenderer.STATE_ZOOMED_IN;
            renderer.cPosX = 1.0f * renderer.zoomX;
            renderer.cPosY = 1.0f * renderer.zoomY;
            renderer.cPosZ = 1.0f;
            return true;
        }
        return false;
    }
}
