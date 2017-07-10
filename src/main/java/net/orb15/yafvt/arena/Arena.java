package net.orb15.yafvt.arena;

import net.orb15.yafvt.algorithm.CombatAlgorithm;
import net.orb15.yafvt.character.WoundLevel;
import net.orb15.yafvt.character.WoundMonitor;
import net.orb15.yafvt.util.Usable;
import net.orb15.yafvt.character.Character;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiFunction;

public class Arena extends Usable {

    private static final Logger LOG = LoggerFactory.getLogger(Arena.class);

    private static final String DEFAULT_MAX_ROUNDS = "12";
    private static final String DEFAULT_ITERATIONS = "1";
    private static final String DEFAULT_COMBAT_ALGORITHM = "DEFAULT";

    private String name;
    private String propertySource;
    private Map<String, String> configMap = new HashMap<>();

    private volatile boolean haltIterations;

    protected Arena(String name, String propertySource, Map<String, String> configMap) {

        super();
        this.name = name;
        this.propertySource = propertySource;
        this.configMap = configMap;
        this.haltIterations = false;
    }

    public void runArena(Character char1, Character char2) {

        char1.setInUse(true);
        char2.setInUse(true);
        setInUse(true);

        String battlesToExecuteRaw= getConfigItem("iterations").orElse(DEFAULT_ITERATIONS);
        int battlesToExecute = 0;
        try {

            battlesToExecute = Integer.parseInt(battlesToExecuteRaw);
            LOG.debug("Arena {} will execute {} battles", name, battlesToExecute);

        } catch(NumberFormatException nfe)
        {
            LOG.warn("Invalid 'iterations' Arena setting value in arena property file {}: {}",
                    propertySource, battlesToExecuteRaw, nfe);
            System.out.println("Invalid 'iterations' Arena setting value in arena property file: " +
                    propertySource + " value: " + battlesToExecuteRaw + "  Using default value");

            battlesToExecute = Integer.parseInt(DEFAULT_ITERATIONS);
        }

        String perRoundAlgoName = getConfigItem("combatRules").orElse(DEFAULT_COMBAT_ALGORITHM);
        CombatAlgorithm.AlgorithmType type = CombatAlgorithm.AlgorithmType.DEFAULT;
        try {
            type = CombatAlgorithm.AlgorithmType.valueOf(perRoundAlgoName.toUpperCase());
        } catch (IllegalArgumentException iae) {

            LOG.warn("Invalid 'combatRules' Arena setting value in arena property file {}",
                    propertySource, iae);
            System.out.println("Invalid 'combatRules' Arena setting value in arena property file: " +
                    propertySource + " Using DEFAULT algorithm.");
        }
        BiFunction<Character, Character, WoundLevel> perRoundAlgo = CombatAlgorithm.getCombatAlgorithm(type);


        String maxCombatRoundsRaw = getConfigItem("maxRounds").orElse(DEFAULT_MAX_ROUNDS);
        int maxCombatRounds = 0;
        try {

            maxCombatRounds = Integer.parseInt(maxCombatRoundsRaw);
            LOG.debug("Arena {} limit each combat to {} rounds", name, maxCombatRounds);

        } catch(NumberFormatException nfe)
        {
            LOG.warn("Invalid 'maxRounds' Arena setting value in arena property file {}: {}",
                    propertySource, maxCombatRoundsRaw, nfe);
            System.out.println("Invalid 'maxRounds' Arena setting value in arena property file: " +
                    propertySource + " value: " + maxCombatRoundsRaw + "  Using default value");

            maxCombatRounds = Integer.parseInt(DEFAULT_MAX_ROUNDS);
        }


        int battlesCompleted = 0;
        do {

            char1.healAllDamage();
            char2.healAllDamage();
            executeBattle(char1, char2, maxCombatRounds, perRoundAlgo);
            battlesCompleted++;

            LOG.debug("Arena: {} Battle: {} completed", name, battlesCompleted);

        } while(  (battlesCompleted < battlesToExecute) && !haltIterations );

        char1.setInUse(false);
        char2.setInUse(false);
        setInUse(false);

        LOG.debug("Arena: {} completed {} battles", name, battlesCompleted);

    }

    public void setHaltIterations(boolean haltIterations) { this.haltIterations = haltIterations;}

    public String getName() {
        return name;
    }

    public String getPropertySource() {
        return propertySource;
    }

    public Optional<String> getConfigItem(String item) {

        return Optional.of(configMap.get(item));
    }

    public static Arena fromProperties(Properties props, String propFileName, String name) {

        Map<String, String> configMap = new HashMap<>();

        Set<String> propSet = props.stringPropertyNames();
        propSet.stream().forEach( p -> configMap.put(p, props.getProperty(p)));


        Arena arena = new Arena(name, propFileName, configMap);

        return arena;
    }

    @Override
    public String toString() {
        return "configMap=" + configMap +
                "\nname=" + name +
                "\npropertySource=" + propertySource;
    }

    private void executeBattle(Character char1, Character char2,
                               int maxRoundsPerCombat, BiFunction<Character, Character, WoundLevel> perRoundAlgo) {

        int roundsElapsed = 0;
        WoundLevel defendersWounds = char2.getCurrentWoundLevel();

        while( roundsElapsed < maxRoundsPerCombat ) {

            defendersWounds = perRoundAlgo.apply(char1,char2);
            if(defendersWounds != WoundLevel.INCAPACITATED) {
                defendersWounds = perRoundAlgo.apply(char2,char1);
                if(defendersWounds == WoundLevel.INCAPACITATED) {
                    LOG.trace("Char 1 is dead!");
                    break;
                }
            } else {
                LOG.trace("Char 2 is dead!");
                break;
            }

            roundsElapsed++;
        }
    }
}
