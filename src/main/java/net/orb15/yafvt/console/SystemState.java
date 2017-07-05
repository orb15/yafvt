package net.orb15.yafvt.console;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    List<String> getCharacterNames() {
        return characters.keySet().stream().collect(Collectors.toList());
    }
}
