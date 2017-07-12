package net.orb15.yafvt.character;

public enum ArmorLevel {

    NONE(0),
    LIGHT(1),
    MEDIUM(2),
    HEAVY(3);

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
