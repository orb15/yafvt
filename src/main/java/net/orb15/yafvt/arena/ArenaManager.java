package net.orb15.yafvt.arena;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import net.orb15.yafvt.character.Character;

@Component
public class ArenaManager {

    @Async
    public void runArena(Arena arena, Character char1, Character char2) {
        arena.runArena(char1, char2);
    }
}
