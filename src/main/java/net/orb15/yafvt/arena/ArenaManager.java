package net.orb15.yafvt.arena;


import net.orb15.yafvt.util.YafvtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import net.orb15.yafvt.character.Character;

@Component
public class ArenaManager {

    private static final Logger LOG = LoggerFactory.getLogger(ArenaManager.class);

    @Async
    public void runArena(Arena arena, Character char1, Character char2) {

        try {
            arena.runArena(char1, char2);
        } catch (Exception e) {
            LOG.error("Error executing Arena code", e);
            throw new YafvtException("Error executing arena code", e);
        }
    }

}
