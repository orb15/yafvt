package net.orb15.yafvt.arena;

import net.orb15.yafvt.util.Usable;
import net.orb15.yafvt.character.Character;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Optional;
import java.util.Properties;

public class Arena extends Usable {

    private static final Logger LOG = LoggerFactory.getLogger(Arena.class);

    private static final String DEFAULT_MAX_ROUNDS = "12";

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

        String battlesToExecuteRaw= getConfigItem("iterations").orElse(DEFAULT_MAX_ROUNDS);

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

            battlesToExecute = Integer.parseInt(DEFAULT_MAX_ROUNDS);
        }

        int battlesCompleted = 0;
        do {

            executeBattle(char1, char2);
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

    private void executeBattle(Character char1, Character char2) {

    }
}
