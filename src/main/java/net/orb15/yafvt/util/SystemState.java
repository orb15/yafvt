package net.orb15.yafvt.util;

import net.orb15.yafvt.arena.Arena;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.orb15.yafvt.character.Character;

@Component
public class SystemState {

    private Map<String, Character> characters = new HashMap<>();
    private Map<String, Arena> arenas = new HashMap<>();

    public void storeCharacter(Character c) {
        characters.put(c.getName(), c);
    }

    public Optional<Character> getCharacter(String name) {
        return Optional.ofNullable(characters.get(name));
    }

    public List<String> getCharacterNames() {
        return characters.keySet().stream().collect(Collectors.toList());
    }

    public void storeArena(Arena a) {
        arenas.put(a.getName(), a);
    }

    public Optional<Arena> getArena(String name) {
        return Optional.ofNullable(arenas.get(name));
    }

    public List<String> getArenaNames() {
        return arenas.keySet().stream().collect(Collectors.toList());
    }

    public void clearAll() {
        characters.clear();
        arenas.clear();
    }
}
