package net.orb15.yafvt.character;

public enum WoundLevel {

    NONE(0),
    LIGHT(1),
    MODERATE(2),
    HEAVY(3),
    SEVERE(4),
    INCAPACITATED(5);

    private int value;

    WoundLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int compare(WoundLevel rhs) {

        if(this.value == rhs.value)
            return 0;
        else if(this.value > rhs.value)
            return 1;
        else
            return -1;
    }

}
