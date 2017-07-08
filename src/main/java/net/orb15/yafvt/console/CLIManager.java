package net.orb15.yafvt.console;

import net.orb15.yafvt.arena.Arena;
import net.orb15.yafvt.arena.ArenaManager;
import net.orb15.yafvt.util.PropertyFileLoader;
import net.orb15.yafvt.util.SystemState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import net.orb15.yafvt.character.Character;

import java.io.Console;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class CLIManager implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CLIManager.class);

    private List<String> cmdHistory = new ArrayList<>();
    private SystemState state;
    private PropertyFileLoader propertyFileLoader;
    private ArenaManager arenaManager;

    private Map<Command, BiConsumer<Matcher, String>> cliMap;

    @Autowired
    CLIManager(SystemState state, PropertyFileLoader propertyFileLoader, ArenaManager arenaManager) {
        this.state = state;
        this.propertyFileLoader = propertyFileLoader;
        this.arenaManager = arenaManager;
        loadMap();
    }

    private void loadMap() {

        cliMap = new HashMap<>();
        Command c = null;

        c = new Command("load char (.*) as (.*)", "load {propfile} as {char name}\tLoad a character from prop file");
        cliMap.put(c, this::loadCharacter);

        c = new Command("h([0-9]+)", "h{x}\t\t\t\tShow the last x commands");
        cliMap.put(c, this::showHistory);

        c = new Command("show chars", "show chars\t\t\tShow all loaded characters");
        cliMap.put(c, this::characterList);

        c = new Command("show char (.*)", "show chars {char name}\t\tShow the details for the given character");
        cliMap.put(c, this::showCharacter);

        c = new Command("help", "help\t\t\t\tShow this help text");
        cliMap.put(c, this::showHelp);

        c = new Command("\\?", "?\t\t\t\tShow this help text");
        cliMap.put(c, this::showHelp);

        c = new Command("state kill", "state kill\t\t\tCompletely reset system state");
        cliMap.put(c, this::killState);
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

            if(command.compareTo("exit") == 0 || command.compareTo("quit") == 0) {
                keepRunning = false;
                arenaManager.runArena();
                continue;
            }

            if(!handleCommand(command)) {
                System.out.println("Invalid command: " + command);
            }
        }
    }

    private boolean handleCommand(String cmd) {

        boolean cmdMatched = false;

        Set<Command> keys = cliMap.keySet();
        Optional<Command> optCommand = keys.stream().
                filter(c -> c.getPattern().matcher(cmd).matches())
                .findFirst();

        if(optCommand.isPresent()) {
            cmdMatched = true;
            Command c = optCommand.get();
            Matcher m = c.getPattern().matcher(cmd);
            m.matches();
            cliMap.get(c).accept(m,cmd);
        }

        return cmdMatched;
    }

    private void killState(Matcher m, String cmd) {

        System.out.print("\tThis will DESTROY all state data! Are you sure [NO]? ");
        String response = System.console().readLine().trim();

        if(response.compareTo("YES") == 0) {
            state.clearAll();
            cmdHistory.add(0, cmd);
            System.out.println("\nSystem state cleared.\n");
        }
    }

    private void showHelp(Matcher m, String cmd) {

        System.out.println();

        Set<Command> keys = cliMap.keySet();
        keys.stream()
                .sorted( (c1, c2) -> c1.getHelpText().compareToIgnoreCase(c2.getHelpText()))
                .forEach(e -> System.out.println(e.getHelpText()));

        System.out.println();
    }

    private void showCharacter(Matcher m, String cmd) {

        System.out.println();

        String charName = m.group(1);

        Optional<Character> optChar = state.getCharacter(charName);
        if(optChar.isPresent()) {

            System.out.println(optChar.get().toString());

        } else {
            System.out.println(String.format("No character named: %s exists", charName));
        }

        System.out.println();


        cmdHistory.add(0,cmd);
    }

    private void characterList(Matcher m, String cmd) {

        System.out.println();

        state.getCharacterNames().stream()
                .forEach(s -> System.out.println(s));
        cmdHistory.add(0,cmd);

        System.out.println();
    }

    private void showHistory(Matcher m, String cmd) {

        int size = cmdHistory.size();

        if(size == 0)
            return;

        System.out.println();

        int count = Integer.parseInt(m.group(1));

        System.out.println(String.format("%d most recent commands:", count));

        int shown = 0;
        while(shown < count && shown < size ) {

            System.out.println(cmdHistory.get(shown));
            shown++;
        }

        System.out.println();
    }

    private void loadCharacter(Matcher m, String cmd) {

        String propFileName = m.group(1);
        String charName = m.group(2);

        Optional<Properties> propsOpt = propertyFileLoader.load(propFileName,
                PropertyFileLoader.PropertyFileType.CHARACTER);

        System.out.println();

        if(propsOpt.isPresent()) {
            Character character = Character.fromProperties(propsOpt.get(), propFileName, charName);
            state.storeCharacter(character);
            System.out.println("Character loaded");
        } else {
            System.out.println("Character NOT loaded");
        }

    }


    private class Command {

        private Pattern pattern;
        private String helpText;

        protected Command(String patternString, String helpText) {
            this.pattern = Pattern.compile(patternString);
            this.helpText = helpText;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public String getHelpText() {
            return helpText;
        }

    }
}
