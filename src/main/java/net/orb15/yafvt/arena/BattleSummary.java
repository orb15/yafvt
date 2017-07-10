package net.orb15.yafvt.arena;

import net.orb15.yafvt.character.WoundLevel;

public class BattleSummary {

    private String winnerName;
    private WoundLevel winnerDamage;
    private String firstBloodiedName;
    private int roundsTaken;

    public BattleSummary(String winnerName, WoundLevel winnerDamage, String firstBloodiedName, int roundsTaken) {
        this.winnerName = winnerName;
        this.winnerDamage = winnerDamage;
        this.firstBloodiedName = firstBloodiedName;
        this.roundsTaken = roundsTaken;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public WoundLevel getWinnerDamage() {
        return winnerDamage;
    }

    public String getFirstBloodiedName() {
        return firstBloodiedName;
    }

    public int getRoundsTaken() {
        return roundsTaken;
    }

}
