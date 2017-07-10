package net.orb15.yafvt.arena;

import net.orb15.yafvt.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ArenaStats {

    private Pair<String, String> char1Info;
    private Pair<String, String> char2Info;
    private Pair<String, String> arenaInfo;

    private List<BattleSummary> battleSummaries;

    public ArenaStats(String c1Name, String c1PropsName, String c2Name, String c2PropsName,
                      String arenaName, String arenaPropsName) {

        char1Info = new Pair<>(c1Name, c1PropsName);
        char2Info = new Pair<>(c2Name, c2PropsName);
        arenaInfo = new Pair<>(arenaName, arenaPropsName);

        battleSummaries = new ArrayList<>();

    }

    public void addBattleSummary(BattleSummary battleSummary) {
        battleSummaries.add(battleSummary);
    }

    public ArenaStatsSummary getStatsSummary() {

        ArenaStatsSummary statsSummary = new ArenaStatsSummary();
        statsSummary.setBattleCount(battleSummaries.size());

        statsSummary = countWinners(statsSummary);
        statsSummary = averageWinnerWounds(statsSummary);

        return statsSummary;
    }

    private ArenaStatsSummary countWinners(ArenaStatsSummary statsSummary) {

        long char1WinsCounter =  battleSummaries.stream()
                .filter(s -> s.getWinnerName().compareTo(char1Info.getT()) == 0).count();

        long char2WinsCounter =  battleSummaries.stream()
                .filter(s -> s.getWinnerName().compareTo(char2Info.getT()) == 0).count();

        statsSummary.setChar1WinCount((int)char1WinsCounter);
        statsSummary.setChar2WinCount((int)char2WinsCounter);

        return statsSummary;
    }

    private ArenaStatsSummary averageWinnerWounds(ArenaStatsSummary statsSummary) {

        Double d = battleSummaries.stream().collect(Collectors.averagingInt(s -> s.getRoundsTaken()));
        statsSummary.setAverageRoundsTaken(d);

        return statsSummary;
    }

}
