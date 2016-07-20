package sk.martin.tictactoe.frontend.animations;

import sk.martin.tictactoe.frontend.MyGLRenderer;

/**
 * Created by Martin on 21. 9. 2015.
 */
public interface Animation {

    /**
     * Perform animation and return if it finished
     * @param renderer MyGlRenderer that contains all necessary information and variables
     * @return returns true if animation finished and should be thrown away.
     */
    boolean perform(MyGLRenderer renderer);

}
