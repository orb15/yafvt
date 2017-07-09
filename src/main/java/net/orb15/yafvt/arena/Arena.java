package net.orb15.yafvt.arena;

import java.util.*;

public class Arena {

    private String name;
    private String propertySource;
    private Map<String, String> configMap = new HashMap<>();

    protected Arena(String name, String propertySource, Map<String, String> configMap) {
        this.name = name;
        this.propertySource = propertySource;
        this.configMap = configMap;
    }


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
}
