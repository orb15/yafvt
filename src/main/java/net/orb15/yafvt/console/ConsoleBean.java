package net.orb15.yafvt.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import net.orb15.yafvt.character.Character;

import java.io.Console;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class ConsoleBean implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleBean.class);

    private static final Pattern loadCharPattern = Pattern.compile("load (.*) as (.*)");
    private static final Pattern historyPattern = Pattern.compile("h([0-9]+)");
    private static final Pattern charListPattern = Pattern.compile("chars");

    private List<String> cmdHistory = new ArrayList<>();
    private SystemState state;

    @Autowired
    ConsoleBean(SystemState state) {
        this.state = state;
    }

    @Override
    public void run(String... strings) throws Exception {

        LOG.debug("Command line application starting...");

        System.out.println();
        executePrompt();
        System.out.println();
    }

    private void executePrompt() {

        Console console = System.console();

        boolean keepRunning = true;

        while(keepRunning) {

            System.out.print("yafvt> ");
            String command = console.readLine().toLowerCase().trim();

            if(command.length() == 0)
                continue;

            if(command.compareTo("exit") == 0 || command.compareTo("quit") == 0)
                keepRunning = false;

            if(!handleCommand(command)) {
                System.out.println("Invalid command: " + command);
            }
        }
    }

    private boolean handleCommand(String cmd) {

        Matcher m;

        m = loadCharPattern.matcher(cmd);
        if(m.matches()) {

            String propFileName = m.group(1);
            String charName = m.group(2);

            return loadCharacter(propFileName, charName, cmd);
        }

        m = historyPattern.matcher(cmd);
        if(m.matches()) {

            int count = Integer.parseInt(m.group(1));
            return showHistory(count);
        }

        m = charListPattern.matcher(cmd);
        if(m.matches()) {
            return characterList();
        }

        return false;

    }

    private boolean characterList() {

        state.getCharacterNames().stream().forEach(s -> System.out.println(s));
        return true;
    }

    private boolean showHistory(int count) {

        int size = cmdHistory.size();

        if(size == 0)
            return true;

        System.out.println(String.format("%d most recent commands:", count));

        int shown = 0;
        while(shown < count && shown < size ) {

            System.out.println(cmdHistory.get(shown));
            shown++;
        }

        return true;
    }

    private boolean loadCharacter(String propFileName, String charName, String cmd) {

        InputStream input = null;
        Properties props = new Properties();
        Character character = null;

        try {

            input = new FileInputStream("characters/" + propFileName + ".properties");
            props.load(input);

            character = Character.fromProperties(props, propFileName, charName);
            state.storeCharacter(character);
            System.out.println("properties file loaded");
            cmdHistory.add(0,cmd);

        } catch (Exception ex) {
            LOG.error("Can't find or load the properties file: {}", propFileName, ex);
            System.out.println("Unable to load file: " + propFileName);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception ex) {
                    LOG.error("Can't close the properties file: {}", propFileName, ex);
                }
            }

            return true;
        }
    }
}
