package net.orb15.yafvt.arena;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ArenaManager {

    @Async
    public void runArena() {

        System.out.println(Thread.currentThread().getName());
    }
}
