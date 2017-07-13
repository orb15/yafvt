package net.orb15.yafvt.character;

import net.orb15.yafvt.util.Pair;
import net.orb15.yafvt.util.YafvtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WoundMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(WoundMonitor.class);

    private Pair<Integer, Integer> lightWounds;
    private Pair<Integer, Integer> moderateWounds;
    private Pair<Integer, Integer> heavyWounds;
    private Pair<Integer, Integer> severeWounds;

    private WoundLevel woundLevel;
    private boolean incapacitated;

    private WoundMonitor(Pair<Integer, Integer> l, Pair<Integer, Integer> m,
                         Pair<Integer, Integer> h, Pair<Integer, Integer> s) {
        this.lightWounds = l;
        this.moderateWounds = m;
        this.heavyWounds = h;
        this.severeWounds = s;

        this.woundLevel = WoundLevel.NONE;
        this.incapacitated = false;
    }

    public static WoundMonitor defaultValued() {
        return WoundMonitor.forToughness(1);
    }

    public static WoundMonitor forToughness(int toughness) {

        switch(toughness) {

            case 1: {
                Pair<Integer, Integer> light = new Pair<>(0, 0);
                Pair<Integer, Integer> mod = new Pair<>(0, 0);
                Pair<Integer, Integer> heavy = new Pair<>(0, 0);
                Pair<Integer, Integer> severe = new Pair<>(1, 0);
                return new WoundMonitor(light, mod, heavy, severe);
            }

            case 2: {
                Pair<Integer, Integer> light = new Pair<>(0, 0);
                Pair<Integer, Integer> mod = new Pair<>(0, 0);
                Pair<Integer, Integer> heavy = new Pair<>(1, 0);
                Pair<Integer, Integer> severe = new Pair<>(1, 0);
                return new WoundMonitor(light, mod, heavy, severe);
            }

            case 3: {
                Pair<Integer, Integer> light = new Pair<>(0, 0);
                Pair<Integer, Integer> mod = new Pair<>(1, 0);
                Pair<Integer, Integer> heavy = new Pair<>(1, 0);
                Pair<Integer, Integer> severe = new Pair<>(1, 0);
                return new WoundMonitor(light, mod, heavy, severe);
            }

            case 4: {
                Pair<Integer, Integer> light = new Pair<>(1, 0);
                Pair<Integer, Integer> mod = new Pair<>(1, 0);
                Pair<Integer, Integer> heavy = new Pair<>(1, 0);
                Pair<Integer, Integer> severe = new Pair<>(1, 0);
                return new WoundMonitor(light, mod, heavy, severe);
            }

            case 5: {
                Pair<Integer, Integer> light = new Pair<>(2, 0);
                Pair<Integer, Integer> mod = new Pair<>(2, 0);
                Pair<Integer, Integer> heavy = new Pair<>(1, 0);
                Pair<Integer, Integer> severe = new Pair<>(1, 0);
                return new WoundMonitor(light, mod, heavy, severe);
            }

            case 6: {
                Pair<Integer, Integer> light = new Pair<>(3, 0);
                Pair<Integer, Integer> mod = new Pair<>(3, 0);
                Pair<Integer, Integer> heavy = new Pair<>(1, 0);
                Pair<Integer, Integer> severe = new Pair<>(1, 0);
                return new WoundMonitor(light, mod, heavy, severe);
            }

            case 7: {
                Pair<Integer, Integer> light = new Pair<>(3, 0);
                Pair<Integer, Integer> mod = new Pair<>(3, 0);
                Pair<Integer, Integer> heavy = new Pair<>(2, 0);
                Pair<Integer, Integer> severe = new Pair<>(1, 0);
                return new WoundMonitor(light, mod, heavy, severe);
            }

            case 8: {
                Pair<Integer, Integer> light = new Pair<>(3, 0);
                Pair<Integer, Integer> mod = new Pair<>(3, 0);
                Pair<Integer, Integer> heavy = new Pair<>(3, 0);
                Pair<Integer, Integer> severe = new Pair<>(1, 0);
                return new WoundMonitor(light, mod, heavy, severe);
            }

            case 9: {
                Pair<Integer, Integer> light = new Pair<>(3, 0);
                Pair<Integer, Integer> mod = new Pair<>(3, 0);
                Pair<Integer, Integer> heavy = new Pair<>(3, 0);
                Pair<Integer, Integer> severe = new Pair<>(2, 0);
                return new WoundMonitor(light, mod, heavy, severe);
            }

            case 10: {
                Pair<Integer, Integer> light = new Pair<>(3, 0);
                Pair<Integer, Integer> mod = new Pair<>(3, 0);
                Pair<Integer, Integer> heavy = new Pair<>(3, 0);
                Pair<Integer, Integer> severe = new Pair<>(3, 0);
                return new WoundMonitor(light, mod, heavy, severe);
            }
        }

        throw new YafvtException("Unsupported Toughness: %i", toughness);
    }

    public void healAllDamage() {
        lightWounds.setR(0);
        moderateWounds.setR(0);
        heavyWounds.setR(0);
        severeWounds.setR(0);
        incapacitated = false;
    }
    
    public WoundLevel applyNetDamage(int netDamage) {

        if(netDamage < 0)
            return woundLevel;

        switch(netDamage) {

            case 0:
                LOG.debug("applying  wound: LIGHT");
                return applyLight();
            
            case 1:
                LOG.debug("applying  wound: MODERATE");
                return applyModerate();
            
            case 2:
                LOG.debug("applying  wound: HEAVY");
                return applyHeavy();

            default:
                LOG.debug("applying  wound: SEVERE");
                return applySevere();
        }

    }

    public WoundLevel determineWoundLevel() {

        if(incapacitated)
            return WoundLevel.INCAPACITATED;

        int now = severeWounds.getR();
        if(now > 0)
            return WoundLevel.SEVERE;

        now = heavyWounds.getR();
        if(now > 0)
            return WoundLevel.HEAVY;

        now = moderateWounds.getR();
        if(now > 0)
            return WoundLevel.MODERATE;

        now = lightWounds.getR();
        if(now > 0)
            return WoundLevel.LIGHT;

        return WoundLevel.NONE;
    }


    private WoundLevel applyLight() {

        int now = lightWounds.getR();
        int max = lightWounds.getT();

        if(now == max) {
            LOG.debug("Escalating wound to level: MODERATE");
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
            LOG.debug("Escalating wound to level: HEAVY");
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
            LOG.debug("Escalating wound to level: SEVERE");
            return applySevere();
        }

        now++;
        heavyWounds.setR(now);
        return determineWoundLevel();
    }

    private WoundLevel applySevere() {

        int now = severeWounds.getR();
        int max = severeWounds.getT();

        if(now == max) {
            incapacitated = true;
            LOG.debug("Setting wounds to: INCAPACITATED after severe damage exceeded");
            return WoundLevel.INCAPACITATED;
        } else {
            now++;
            severeWounds.setR(now);
            return WoundLevel.SEVERE;
        }
    }

    @Override
    public String toString() {
        return "WoundMonitor{" +
                ", lightWounds=" + lightWounds +
                ", moderateWounds=" + moderateWounds +
                ", heavyWounds=" + heavyWounds +
                "severeWounds=" + severeWounds +
                ", woundLevel=" + woundLevel +
                ", incapacitated=" + incapacitated +
                '}';
    }

}
