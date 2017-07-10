package net.orb15.yafvt.console;

import net.orb15.yafvt.arena.Arena;
import net.orb15.yafvt.arena.ArenaManager;
import net.orb15.yafvt.util.PropertyFileLoader;
import net.orb15.yafvt.util.ScriptFileLoader;
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
    private ScriptFileLoader scriptFileLoader;
    private ArenaManager arenaManager;

    private Map<Command, BiConsumer<Matcher, String>> cliMap;

    @Autowired
    CLIManager(SystemState state, PropertyFileLoader propertyFileLoader,
               ScriptFileLoader scriptFileLoader, ArenaManager arenaManager) {
        this.state = state;
        this.propertyFileLoader = propertyFileLoader;
        this.scriptFileLoader = scriptFileLoader;
        this.arenaManager = arenaManager;
        loadMap();
    }

    private void loadMap() {

        cliMap = new HashMap<>();
        Command c = null;

        c = new Command("load char (.*) as (.*)", "load char {propfile} as {char name}\tLoad a character from prop file");
        cliMap.put(c, this::loadCharacter);

        c = new Command("show chars", "show chars\t\t\t\tShow all loaded characters");
        cliMap.put(c, this::characterList);

        c = new Command("show char (.*)", "show chars {char name}\t\t\tShow the details for the given character");
        cliMap.put(c, this::showCharacter);

        c = new Command("load arena (.*) as (.*)", "load arena {propfile} as {arena name}\tLoad an arena from prop file");
        cliMap.put(c, this::loadArena);

        c = new Command("show arenas", "show arenas\t\t\t\tShow all loaded arenas");
        cliMap.put(c, this::arenaList);

        c = new Command("show arena (.*)", "show arena {arena name}\t\t\tShow the details for the given arena");
        cliMap.put(c, this::showArena);

        c = new Command("h([0-9]+)", "h{x}\t\t\t\t\tShow the last x commands");
        cliMap.put(c, this::showHistory);

        c = new Command("l", "l\t\t\t\t\tRe-run the last command executed");
        cliMap.put(c, this::rerunLast);

        c = new Command("help", "help\t\t\t\t\tShow this help text");
        cliMap.put(c, this::showHelp);

        c = new Command("\\?", "?\t\t\t\t\tShow this help text");
        cliMap.put(c, this::showHelp);

        c = new Command("fight (.*) (.*) in (.*)", "fight {char1} {char2} in {arena}\tBegin a series of battles in an arena");
        cliMap.put(c, this::fight);

        c = new Command("run script (.*)", "run script {scriptfile}\t\tLoad and run a script file");
        cliMap.put(c, this::loadAndRunScript);

        c = new Command("state kill all", "state kill all\t\t\t\tCompletely reset system state");
        cliMap.put(c, this::killAllState);
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
            System.out.println();
            cliMap.get(c).accept(m,cmd);
            System.out.println();
        }

        return cmdMatched;
    }

    private void killAllState(Matcher m, String cmd) {

        System.out.print("\tThis will DESTROY all state data! Are you sure [NO]? ");
        String response = System.console().readLine().trim();

        if(response.compareTo("YES") == 0) {
            state.clearAll();
            cmdHistory.add(0, cmd);
            System.out.println("System state cleared.");
        }
    }

    private void showHelp(Matcher m, String cmd) {

        Set<Command> keys = cliMap.keySet();
        keys.stream()
                .sorted( (c1, c2) -> c1.getHelpText().compareToIgnoreCase(c2.getHelpText()))
                .forEach(e -> System.out.println(e.getHelpText()));

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

    private void showArena(Matcher m, String cmd) {

        String arenaName = m.group(1);

        Optional<Arena> optArena = state.getArena(arenaName);
        if(optArena.isPresent()) {

            System.out.println(optArena.get().toString());

        } else {
            System.out.println(String.format("No arena named: %s exists", arenaName));
        }

        cmdHistory.add(0,cmd);
    }

    private void characterList(Matcher m, String cmd) {

        state.getCharacterNames().stream()
                .forEach(s -> System.out.println(s));

        cmdHistory.add(0,cmd);
    }

    private void arenaList(Matcher m, String cmd) {

        state.getArenaNames().stream()
                .forEach(s -> System.out.println(s));

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

    private void rerunLast(Matcher m, String cmd) {

        int size = cmdHistory.size();

        if(size == 0)
            return;

        handleCommand(cmdHistory.get(0));
    }

    private void loadCharacter(Matcher m, String cmd) {

        String propFileName = m.group(1);
        String charName = m.group(2);

        Optional<Properties> propsOpt = propertyFileLoader.load(propFileName,
                PropertyFileLoader.PropertyFileType.CHARACTER);

        if(propsOpt.isPresent()) {
            Character character = Character.fromProperties(propsOpt.get(), propFileName, charName);
            state.storeCharacter(character);
            System.out.println("Character loaded");
        } else {
            System.out.println("Character NOT loaded");
        }

        cmdHistory.add(0,cmd);

    }

    private void loadArena(Matcher m, String cmd) {

        String propFileName = m.group(1);
        String arenaName = m.group(2);

        Optional<Properties> propsOpt = propertyFileLoader.load(propFileName,
                PropertyFileLoader.PropertyFileType.ARENA);

        if(propsOpt.isPresent()) {
            Arena arena = Arena.fromProperties(propsOpt.get(), propFileName, arenaName);
            state.storeArena(arena);
            System.out.println("Arena loaded");
        } else {
            System.out.println("Arena NOT loaded");
        }

        cmdHistory.add(0,cmd);
    }

    private void fight(Matcher m, String cmd) {

        String char1Name = m.group(1);
        String char2Name = m.group(2);
        String arenaName = m.group(3);

        Optional<Character> char1Opt = prepareCharacter(char1Name);
        Optional<Character> char2Opt = prepareCharacter(char2Name);
        Optional<Arena> arenaOpt = prepareArena(arenaName);

        if(char1Opt.isPresent() && char2Opt.isPresent() && arenaOpt.isPresent()) {

            arenaManager.runArena(arenaOpt.get(), char1Opt.get(), char2Opt.get());

            System.out.println("fight started");
        }

        cmdHistory.add(0,cmd);
    }

    private void loadAndRunScript(Matcher m, String cmd) {

        String scriptFile = m.group(1);
        List<String> commandList = scriptFileLoader.loadScriptFile(scriptFile);

        int cmdCount = commandList.size();
        if(cmdCount == 0)
            System.out.println("Script file has no valid lines to execute");
        else {
            System.out.println("Loaded " + commandList.size() + " valid lines.  Executing...");
            commandList.stream().forEach(s -> handleCommand(s));
        }

        cmdHistory.add(0,cmd);
    }

    private Optional<Character> prepareCharacter(String charName) {

        Character character = null;

        Optional<Character> charOpt = state.getCharacter(charName);
        if(charOpt.isPresent()) {

            character = charOpt.get();
            if(character.isInUse()) {

                LOG.debug("Character {} is already in use and cannot be used right now",
                        charName);
                System.out.println("Character: " + charName + "is already busy in a battle");
            }

        } else {

            LOG.debug("Character {} does not exist and cannot be used in a battle.",
                    charName);
            System.out.println("Character: " + charName + "has not been loaded");
        }

        return Optional.ofNullable(character);
    }

    private Optional<Arena> prepareArena(String arenaName) {

        Arena arena = null;

        Optional<Arena> arenaOpt = state.getArena(arenaName);
        if(arenaOpt.isPresent()) {

            arena = arenaOpt.get();
            if(arena.isInUse()) {

                LOG.debug("Arena {} is already in use and cannot be used right now",
                        arenaName);
                System.out.println("Arena: " + arenaName + "is already busy in a battle");
            }

        } else {

            LOG.debug("Arena {} does not exist and cannot be used in a battle.",
                    arenaName);
            System.out.println("Arena: " + arenaName + "has not been loaded");
        }

        return Optional.ofNullable(arena);
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
