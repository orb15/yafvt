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

    protected WoundMonitor(Pair<Integer, Integer> s, Pair<Integer, Integer> l,
                           Pair<Integer, Integer> m, Pair<Integer, Integer> h) {
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
                Pair<Integer, Integer> scratch = new Pair<>(3, 0);
                Pair<Integer, Integer> light = new Pair<>(3, 0);
                Pair<Integer, Integer> mod = new Pair<>(3, 0);
                Pair<Integer, Integer> heavy = new Pair<>(1, 0);
                return new WoundMonitor(scratch, light, mod, heavy);
            }

            case 6: {
                Pair<Integer, Integer> scratch = new Pair<>(3, 0);
                Pair<Integer, Integer> light = new Pair<>(3, 0);
                Pair<Integer, Integer> mod = new Pair<>(3, 0);
                Pair<Integer, Integer> heavy = new Pair<>(2, 0);
                return new WoundMonitor(scratch, light, mod, heavy);
            }

            case 7: {
                Pair<Integer, Integer> scratch = new Pair<>(3, 0);
                Pair<Integer, Integer> light = new Pair<>(3, 0);
                Pair<Integer, Integer> mod = new Pair<>(3, 0);
                Pair<Integer, Integer> heavy = new Pair<>(3, 0);
                return new WoundMonitor(scratch, light, mod, heavy);
            }
        }

        throw new YafvtException("Unsupported Toughness: %i", toughness);
    }

    public void healAllDamage() {
        scratches.setR(scratches.getT());
        lightWounds.setR(scratches.getT());
        moderateWounds.setR(scratches.getT());
        heavyWounds.setR(scratches.getT());
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

        int now = heavyWounds.getR();
        if(now > 0)
            return WoundLevel.HEAVY;

        now = moderateWounds.getR();
        if(now > 0)
            return WoundLevel.MODERATE;

        now = lightWounds.getR();
        if(now > 0)
            return WoundLevel.LIGHT;

        now = scratches.getR();
        if(now > 0)
            return WoundLevel.SCRATCHED;

        return WoundLevel.NONE;
    }

    
    private WoundLevel applyScratch() {

        int now = scratches.getR();
        int max = scratches.getT();
        
        if(now == max) {
            return applyLight();
        }
        
        now++;
        scratches.setR(now);
        return determineWoundLevel();
    }

    private WoundLevel applyLight() {

        int now = lightWounds.getR();
        int max = lightWounds.getT();

        if(now == max) {
            return applyModerate();
        }

        now++;
        lightWounds.setR(now);
        return determineWoundLevel();
    }

    private WoundLevel applyModerate() {

        int now = moderateWounds.getR();
        int max = moderateWounds.getT();

        if(now == max) {
            return applyHeavy();
        }

        now++;
        moderateWounds.setR(now);
        return determineWoundLevel();
    }

    private WoundLevel applyHeavy() {

        int now = heavyWounds.getR();
        int max = heavyWounds.getT();

        if(now == max) {
            incapacitated = true;
            return WoundLevel.INCAPACITATED;
        } else {
            now++;
            heavyWounds.setR(now);
            return WoundLevel.HEAVY;
        }
    }

    @Override
    public String toString() {
        return "WoundMonitor{" +
                "scratches=" + scratches +
                ", lightWounds=" + lightWounds +
                ", moderateWounds=" + moderateWounds +
                ", heavyWounds=" + heavyWounds +
                ", woundLevel=" + woundLevel +
                ", incapacitated=" + incapacitated +
                '}';
    }

}
