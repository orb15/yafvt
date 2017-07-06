package net.orb15.yafvt.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.orb15.yafvt.character.Character;

@Component
public class SystemState {

    private Map<String, Character> characters;

    SystemState() {
        characters = new HashMap<>();
    }

    public void storeCharacter(Character c) {
        characters.put(c.getName(), c);
    }

    public Optional<Character> getCharacter(String name) {
        return Optional.ofNullable(characters.get(name));
    }

    public List<String> getCharacterNames() {
        return characters.keySet().stream().collect(Collectors.toList());
    }

    public void clearAll() {
        characters.clear();
    }
}
