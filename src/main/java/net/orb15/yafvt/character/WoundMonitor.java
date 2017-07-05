package net.orb15.yafvt.character;

import net.orb15.yafvt.util.Pair;
import net.orb15.yafvt.util.YafvtException;

public class WoundMonitor {

    private Pair<Integer, Integer> scratches;
    private Pair<Integer, Integer> lightWounds;
    private Pair<Integer, Integer> moderateWounds;
    private Pair<Integer, Integer> heavyWounds;

    private WoundLevel woundLevel;
    private boolean incapacitated;

    protected WoundMonitor(Pair s, Pair l, Pair m, Pair h) {
        this.scratches = s;
        this.lightWounds = l;
        this.moderateWounds = m;
        this.heavyWounds = h;

        this.woundLevel = WoundLevel.NONE;
        this.incapacitated = false;
    }
    public static WoundMonitor defaultValued() {
        return WoundMonitor.forToughness(1);
    }

    public static WoundMonitor forToughness(int toughness) {

        switch(toughness) {

            case 1: {
                Pair<Integer, Integer> scratch = new Pair<>(3, 0);
                Pair<Integer, Integer> light = new Pair<>(1, 0);
                Pair<Integer, Integer> mod = new Pair<>(1, 0);
                Pair<Integer, Integer> heavy = new Pair<>(1, 0);
                return new WoundMonitor(scratch, light, mod, heavy);
            }

            case 2: {
                Pair<Integer, Integer> scratch = new Pair<>(3, 0);
                Pair<Integer, Integer> light = new Pair<>(2, 0);
                Pair<Integer, Integer> mod = new Pair<>(1, 0);
                Pair<Integer, Integer> heavy = new Pair<>(1, 0);
                return new WoundMonitor(scratch, light, mod, heavy);
            }

            case 3: {
                Pair<Integer, Integer> scratch = new Pair<>(3, 0);
                Pair<Integer, Integer> light = new Pair<>(3, 0);
                Pair<Integer, Integer> mod = new Pair<>(1, 0);
                Pair<Integer, Integer> heavy = new Pair<>(1, 0);
                return new WoundMonitor(scratch, light, mod, heavy);
            }

            case 4: {
                Pair<Integer, Integer> scratch = new Pair<>(3, 0);
                Pair<Integer, Integer> light = new Pair<>(3, 0);
                Pair<Integer, Integer> mod = new Pair<>(2, 0);
                Pair<Integer, Integer> heavy = new Pair<>(1, 0);
                return new WoundMonitor(scratch, light, mod, heavy);
            }

            case 5: {
                Pair<Integer, Integer> scratch = new Pair(3, 0);
                Pair<Integer, Integer> light = new Pair(3, 0);
                Pair<Integer, Integer> mod = new Pair(3, 0);
                Pair<Integer, Integer> heavy = new Pair(1, 0);
                return new WoundMonitor(scratch, light, mod, heavy);
            }

            case 6: {
                Pair<Integer, Integer> scratch = new Pair(3, 0);
                Pair<Integer, Integer> light = new Pair(3, 0);
                Pair<Integer, Integer> mod = new Pair(3, 0);
                Pair<Integer, Integer> heavy = new Pair(2, 0);
                return new WoundMonitor(scratch, light, mod, heavy);
            }

            case 7: {
                Pair<Integer, Integer> scratch = new Pair(3, 0);
                Pair<Integer, Integer> light = new Pair(3, 0);
                Pair<Integer, Integer> mod = new Pair(3, 0);
                Pair<Integer, Integer> heavy = new Pair(3, 0);
                return new WoundMonitor(scratch, light, mod, heavy);
            }
        }

        throw new YafvtException("Unsupported Toughness: %i", toughness);
    }

    public void healAllDamage() {
        scratches.setN(scratches.getM());
        lightWounds.setN(scratches.getM());
        moderateWounds.setN(scratches.getM());
        heavyWounds.setN(scratches.getM());
    }
    
    public WoundLevel applyNetDamage(int netDamage) {

        if(netDamage <= 0)
            return woundLevel;

        switch(netDamage) {

            case 1:
                return applyScratch();
            
            case 2:
            case 3:
                return applyLight();
            
            case 4:
                return applyModerate();

            default:
                return applyHeavy();
        }

    }

    public WoundLevel determineWoundLevel() {

        if(incapacitated)
            return WoundLevel.INCAPACITATED;

        int now = heavyWounds.getN();
        if(now > 0)
            return WoundLevel.HEAVY;

        now = moderateWounds.getN();
        if(now > 0)
            return WoundLevel.MODERATE;

        now = lightWounds.getN();
        if(now > 0)
            return WoundLevel.LIGHT;

        now = scratches.getN();
        if(now > 0)
            return WoundLevel.SCRATCHED;

        return WoundLevel.NONE;
    }

    
    private WoundLevel applyScratch() {

        int now = scratches.getN();
        int max = scratches.getM();
        
        if(now == max) {
            return applyLight();
        }
        
        now++;
        scratches.setN(now);
        return determineWoundLevel();
    }

    private WoundLevel applyLight() {

        int now = lightWounds.getN();
        int max = lightWounds.getM();

        if(now == max) {
            return applyModerate();
        }

        now++;
        lightWounds.setN(now);
        return determineWoundLevel();
    }

    private WoundLevel applyModerate() {

        int now = moderateWounds.getN();
        int max = moderateWounds.getM();

        if(now == max) {
            return applyHeavy();
        }

        now++;
        moderateWounds.setN(now);
        return determineWoundLevel();
    }

    private WoundLevel applyHeavy() {

        int now = heavyWounds.getN();
        int max = heavyWounds.getM();

        if(now == max) {
            incapacitated = true;
            return WoundLevel.INCAPACITATED;
        } else {
            now++;
            heavyWounds.setN(now);
            return WoundLevel.HEAVY;
        }
    }

}
