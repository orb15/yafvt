package net.orb15.yafvt.arena;

public class ArenaStatsSummary {

    private int battleCount;
    private int char1WinCount;
    private int char2WinCount;


    private double averageRoundsTaken;

    @Override
    public String toString() {
        return "ArenaStatsSummary{" +
                "battleCount=" + battleCount +
                ", char1WinCount=" + char1WinCount +
                ", char2WinCount=" + char2WinCount +
                ", averageRoundsTaken=" + averageRoundsTaken +
                "}";
    }

    public int getBattleCount() {
        return battleCount;
    }

    public void setBattleCount(int battleCount) {
        this.battleCount = battleCount;
    }

    public int getChar1WinCount() {
        return char1WinCount;
    }

    public void setChar1WinCount(int char1WinCount) {
        this.char1WinCount = char1WinCount;
    }

    public int getChar2WinCount() {
        return char2WinCount;
    }

    public void setChar2WinCount(int char2WinCount) {
        this.char2WinCount = char2WinCount;
    }

    public double getAverageRoundsTaken() {
        return averageRoundsTaken;
    }

    public void setAverageRoundsTaken(double averageRoundsTaken) {
        this.averageRoundsTaken = averageRoundsTaken;
    }
}
