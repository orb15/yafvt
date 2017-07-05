package net.orb15.yafvt.character;

public enum ArmorLevel {

    NONE(0),
    SHIELD(1),
    LIGHT(2),
    MEDIUM(3),
    HEAVY(4),
    VERY_HEAVY(5);

    private int value;

    ArmorLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    static ArmorLevel ciValueOf(String s) {
        return valueOf(s.toUpperCase());
    }
}
