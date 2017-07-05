package net.orb15.yafvt.character;

import java.util.*;
import java.util.stream.Collectors;

public class Character {

    private Map<Skill, Integer> skillMap = new HashMap<>();
    private WoundMonitor wounds = WoundMonitor.defaultValued();

    private Skill currentWeapon;
    private ArmorLevel currentArmor;
    private String name;
    private String propertySource;

    protected Character(Map<Skill, Integer> skillMap,
                        WoundMonitor wounds,
                        Skill currentWeapon,
                        ArmorLevel currentArmor,
                        String name,
                        String propertySource) {
        this.skillMap = skillMap;
        this.wounds = wounds;
        this.currentWeapon = currentWeapon;
        this.currentArmor = currentArmor;
        this.name = name;
        this.propertySource = propertySource;
    }

    public Optional<Integer> getSkillValue(Skill skill) {
        return Optional.of(skillMap.get(skill));
    }

    public WoundLevel applyDamage(int damage) {
        return wounds.applyNetDamage(damage);
    }

    public Skill getCurrentWeapon() {
        return currentWeapon;
    }

    public ArmorLevel getCurrentArmor() {
        return currentArmor;
    }

    public WoundLevel getCurrentWoundLevel() {
        return wounds.determineWoundLevel();
    }

    public String getName() {
        return name;
    }

    public String getPropertySource() {
        return propertySource;
    }

    public void healAllDamage() {
        wounds.healAllDamage();
    }

    public static Character fromProperties(Properties props, String propFileName, String name) {

        //basics
        Skill currentWeapon = Skill.ciValueOf(props.getProperty("currentWeapon"));
        ArmorLevel currentArmor = ArmorLevel.ciValueOf(props.getProperty("currentArmor"));

        //Wounds
        WoundMonitor wounds = WoundMonitor.forToughness(Integer.parseInt(props.getProperty("toughness")));

        //Skills
        Map<Skill, Integer> skillMap = new HashMap<>();
        Set<String> propSet = props.stringPropertyNames();
        propSet.stream()
                .filter(p -> p.startsWith("skill."))
                .forEach(s -> {
                    skillMap.put(Skill.ciValueOf(s.substring(6)), Integer.parseInt(props.getProperty(s)));

                });

        return new Character (skillMap, wounds, currentWeapon, currentArmor,
                name, propFileName);

    }
}
