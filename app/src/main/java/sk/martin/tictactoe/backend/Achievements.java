package sk.martin.tictactoe.backend;

import java.nio.FloatBuffer;

/**
 * Created by martin on 2.9.16.
 */
public interface Achievements {

    String NEW_ACHIEVEMENT = "new_achievement";

    String TUTORIAL_ACHIEVEMENT = "CgkI7uOS38EHEAIQBg";
    String EASY_BOT_ACHIEVEMENT = "CgkI7uOS38EHEAIQAg";
    String FIRST_ONLINE_ACHIEVEMENT = "CgkI7uOS38EHEAIQAw";
    String OFFLINE_GAME_ACHIEVEMENT = "CgkI7uOS38EHEAIQBQ";
    String ALL_BOTS_ACHIEVEMENT = "CgkI7uOS38EHEAIQBA";

    String[] achievements = { TUTORIAL_ACHIEVEMENT, EASY_BOT_ACHIEVEMENT, FIRST_ONLINE_ACHIEVEMENT,
            OFFLINE_GAME_ACHIEVEMENT, ALL_BOTS_ACHIEVEMENT};

    int INVISIBLE = 1;
    int VISIBLE = 2;
    int COMPLETED = 3;
    int SENT = 4;
}
