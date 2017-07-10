package net.orb15.yafvt.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ScriptFileLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptFileLoader.class);

    public List<String> loadScriptFile(String filename) {

        filename = "scripts/" + filename + ".script";

        List<String> commandList = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(filename))) {

            commandList = stream
                    .filter( l -> !l.startsWith("#"))
                    .map(String::toLowerCase)
                    .map(String::trim)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            LOG.error("Can't find or load the script file: {}", filename, e);
            System.out.println("Unable to load file: " + filename);
        }

        return commandList;

    }
}
