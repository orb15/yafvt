package net.orb15.yafvt.character;

public enum Skill {

    PHYSICAL_DEFENSE("Physical_Defense", "PhyDef"),
    MENTAL_DEFENSE ("Mental_Defense", "MenDef"),

    LIGHT_WEAPON("Light_Weapon", "LtWep"),
    MEDIUM_WEAPON("Medium_Weapon", "MedWep"),
    HEAVY_WEAPON("Heavy_Weapon", "HvyWep"),

    BOW_WEAPON("Bow", "Bow"),
    THROWN_WEAPON("Thrown", "Thrown"),

    TOUGHNESS("Toughness", "Tough"),
    CARRY("Carry", "Carry"),
    SIZE("Size", "Size");

    private String name;
    private String abbrev;

    Skill(String name, String abbrev) {
        this.name = name;
        this.abbrev = abbrev;
    }

    public String getName() {
        return name;
    }

    public String getAbbrev() {
        return abbrev;
    }

    static Skill ciValueOf(String s) {
        return valueOf(s.toUpperCase());
    }

}
