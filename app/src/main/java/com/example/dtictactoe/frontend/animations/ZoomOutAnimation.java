package com.example.dtictactoe.frontend.animations;

import com.example.dtictactoe.frontend.MyGLRenderer;

/**
 * Created by martin on 23.4.16.
 */
public class ZoomOutAnimation implements Animation {

    @Override
    public boolean perform(MyGLRenderer renderer) {
        renderer.cPosX -= renderer.zoomX * MyGLRenderer.animSpeed;
        renderer.cPosY -= renderer.zoomY * MyGLRenderer.animSpeed;
        renderer.cPosZ -= renderer.animSpeed;
        if (renderer.cPosX * renderer.zoomX < 0.0f){
            renderer.state = MyGLRenderer.STATE_FLOORS;
            renderer.cPosX = 0.0f;
            renderer.cPosY = 0.0f;
            renderer.cPosZ = 0.0f;
            return true;
        }
        return false;
    }
}
