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
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class ConsoleBean implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleBean.class);

    private final Pattern loadCharPattern = Pattern.compile("load (.*) as (.*)");
    private final Pattern historyPattern = Pattern.compile("h([0-9]+)");
    private final Pattern charListPattern = Pattern.compile("show chars");
    private final Pattern showCharPattern = Pattern.compile("show char (.*)");
    private final Pattern showHelpPattern = Pattern.compile("help");
    private final Pattern killStatePattern = Pattern.compile("state kill");

    private List<String> cmdHistory = new ArrayList<>();
    private SystemState state;

    private Map<Pattern, BiConsumer<Matcher, String>> cliMap;

    @Autowired
    ConsoleBean(SystemState state) {
        this.state = state;

        loadMap();
    }

    @Override
    public void run(String... strings) throws Exception {

        LOG.debug("Command line application starting...");

        System.out.println();
        executePrompt();
        System.out.println();
    }

    private void loadMap() {

        cliMap = new HashMap<>();

        cliMap.put(loadCharPattern, this::loadCharacter);
        cliMap.put(historyPattern, this::showHistory);
        cliMap.put(charListPattern, this::characterList);
        cliMap.put(showCharPattern, this::showCharacter);
        cliMap.put(showHelpPattern, this::showHelp);
        cliMap.put(killStatePattern, this::killState);
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

        boolean cmdMatched = false;

        Set<Pattern> keys = cliMap.keySet();
        Optional<Pattern> optPattern = keys.stream().filter(p -> p.matcher(cmd).matches()).findFirst();
        if(optPattern.isPresent()) {
            cmdMatched = true;
            Pattern p = optPattern.get();
            Matcher m = p.matcher(cmd);
            m.matches();
            cliMap.get(p).accept(m,cmd);
        }

        return cmdMatched;
    }

    private void killState(Matcher m, String cmd) {

        state.clearAll();
        cmdHistory.add(0,cmd);
    }

    private void showHelp(Matcher m, String cmd) {

        System.out.println("Load a character from prop file: " + loadCharPattern.toString());
        System.out.println("Show command history: " + historyPattern.toString());
        System.out.println("List loaded characters: " + charListPattern.toString());
        System.out.println("Show a given character: " + showCharPattern.toString());
        System.out.println("Clear the current system state: " + killStatePattern.toString());
    }

    private void showCharacter(Matcher m, String cmd) {

        String charName = m.group(1);

        Optional<Character> optChar = state.getCharacter(charName);
        if(optChar.isPresent()) {

            System.out.println(optChar.get().toString());

        } else {
            System.out.println(String.format("No character named: %s exists", charName));
        }

        cmdHistory.add(0,cmd);
    }

    private void characterList(Matcher m, String cmd) {

        state.getCharacterNames().stream().forEach(s -> System.out.println(s));
        cmdHistory.add(0,cmd);
    }

    private void showHistory(Matcher m, String cmd) {

        int size = cmdHistory.size();

        if(size == 0)
            return;

        int count = Integer.parseInt(m.group(1));

        System.out.println(String.format("%d most recent commands:", count));

        int shown = 0;
        while(shown < count && shown < size ) {

            System.out.println(cmdHistory.get(shown));
            shown++;
        }
    }

    private void loadCharacter(Matcher m, String cmd) {

        InputStream input = null;
        Properties props = new Properties();
        Character character = null;

        String propFileName = m.group(1);
        String charName = m.group(2);

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
        }
    }
}
