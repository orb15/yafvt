package net.orb15.yafvt.algorithm;

import java.util.Optional;
import java.util.function.BiFunction;

import net.orb15.dicebag.DiceBag;
import net.orb15.yafvt.character.ArmorLevel;
import net.orb15.yafvt.character.Character;
import net.orb15.yafvt.character.Skill;
import net.orb15.yafvt.character.WoundLevel;
import net.orb15.yafvt.util.YafvtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatAlgorithm {

    private static final Logger LOG = LoggerFactory.getLogger(CombatAlgorithm.class);

    public enum AlgorithmType {

        DEFAULT,
        USE_SHIELD,
        NEW_WEAPON;
    }

    public static BiFunction<Character, Character, WoundLevel> getCombatAlgorithm(AlgorithmType type) {

        switch(type) {

            case DEFAULT:
                return defaultCombatAlgorithm();

            case USE_SHIELD:
                return useShieldsCombatAlgorithm();

            case NEW_WEAPON:
                return newWeaponCombatAlgorithm();

            default:
                throw new YafvtException("AlgorithmType: " + type + " is not supported");
        }

    }

    private static BiFunction<Character, Character, WoundLevel> defaultCombatAlgorithm() {

        return (att, def) -> {

            Skill attWeaponInHand = att.getCurrentWeapon();
            int attSkillLevel = att.getSkillValue(attWeaponInHand).get();

            int defSkillLevel = def.getSkillValue(Skill.PHYSICAL_DEFENSE).get();
            ArmorLevel defArmorLevel = def.getCurrentArmor();

            int attTotal = DiceBag.dF.roll() + attSkillLevel;
            int defTotal = DiceBag.dF.roll() + defSkillLevel;

            int net = attTotal - defTotal;
            LOG.trace("Net roll: {}", net);

            if(net < 0)
                return def.getCurrentWoundLevel();

            //limit total net hits by attacker weapon
            int weapNet;
            switch(attWeaponInHand) {

                case LIGHT_WEAPON:
                case THROWN_WEAPON:
                    weapNet = Math.min(net, 2);
                    break;

                case MEDIUM_WEAPON:
                    weapNet = Math.min(net, 3);
                    break;

                default: //bows and heavy weapons have no limit
                    weapNet = net;
            }

            LOG.trace("Net roll after weapon limit: {}", weapNet);

            //deal with armor's DR
            int finalNet = Math.max(weapNet - defArmorLevel.getValue(), 0);
            LOG.trace("Net roll after DR: {}", finalNet);

            WoundLevel defWounds = def.applyDamage(finalNet);
            LOG.trace("Final net: {} with defender {} wounds now at: {}",
                    finalNet, def.getName(), defWounds);

            return defWounds;
        };
    }

    private static BiFunction<Character, Character, WoundLevel> useShieldsCombatAlgorithm() {

        return (att, def) -> {

            Skill attWeaponInHand = att.getCurrentWeapon();
            int attSkillLevel = att.getSkillValue(attWeaponInHand).get();

            int defSkillLevel = def.getSkillValue(Skill.PHYSICAL_DEFENSE).get();
            ArmorLevel defArmorLevel = def.getCurrentArmor();

            int attTotal = DiceBag.dF.roll() + attSkillLevel;
            int defTotal = DiceBag.dF.roll() + defSkillLevel;

            int net = attTotal - defTotal;
            LOG.trace("Net roll: {}", net);

            if(net < 0)
                return def.getCurrentWoundLevel();

            //limit total net hits by attacker weapon
            int weapNet;
            switch(attWeaponInHand) {

                case LIGHT_WEAPON:
                case THROWN_WEAPON:
                    weapNet = Math.min(net, 2);
                    break;

                case MEDIUM_WEAPON:
                    weapNet = Math.min(net, 3);
                    break;

                default: //bows and heavy weapons have no limit
                    weapNet = net;
            }

            LOG.trace("Net roll after weapon limit: {}", weapNet);

            //deal with armor's DR
            int finalNet = Math.max(weapNet - defArmorLevel.getValue(), 0);
            LOG.trace("Net roll after DR: {}", finalNet);

            //deal with shield, if any
            if(def.hasShield()) {
                int roll = DiceBag.d3.roll();
                if(roll >= 2) { //same as a blank or + on single dF die

                    finalNet = Math.max(finalNet - 1, 0);
                    LOG.trace("Net roll after Shield: {}", finalNet);
                }
            } else {
                LOG.trace("Defender has no shield");
            }

            WoundLevel defWounds = def.applyDamage(finalNet);
            LOG.trace("Final net: {} with defender {} wounds now at: {}",
                    finalNet, def.getName(), defWounds);

            return defWounds;
        };
    }

    private static BiFunction<Character, Character, WoundLevel> newWeaponCombatAlgorithm() {

        return (att, def) -> {

            Skill attWeaponInHand = att.getCurrentWeapon();
            int attSkillLevel = att.getSkillValue(attWeaponInHand).get();

            int defSkillLevel = def.getSkillValue(Skill.PHYSICAL_DEFENSE).get();
            ArmorLevel defArmorLevel = def.getCurrentArmor();

            int attTotal = DiceBag.dF.roll() + attSkillLevel;
            int defTotal = DiceBag.dF.roll() + defSkillLevel;

            int net = attTotal - defTotal;
            LOG.trace("Net roll: {}", net);

            if(net < 0)
                return def.getCurrentWoundLevel();

            //limit total net hits by attacker weapon
            int weapNet;
            switch(attWeaponInHand) {

                case LIGHT_WEAPON:
                case THROWN_WEAPON:
                    weapNet = Math.min(net, 1);
                    break;

                case MEDIUM_WEAPON:
                    weapNet = net;
                    break;

                default: //bows and heavy weapons are + 1
                    weapNet = net + 1;
            }

            LOG.trace("Net roll after weapon limit: {}", weapNet);

            //deal with armor's DR
            int finalNet = Math.max(weapNet - defArmorLevel.getValue(), 0);
            LOG.trace("Net roll after DR: {}", finalNet);

            //deal with shield, if any
            if(def.hasShield()) {
                int roll = DiceBag.d3.roll();
                if(roll >= 2) { //same as a blank or + on single dF die

                    finalNet = Math.max(finalNet - 1, 0);
                    LOG.trace("Net roll after Shield: {}", finalNet);
                }
            } else {
                LOG.trace("Defender has no shield");
            }

            WoundLevel defWounds = def.applyDamage(finalNet);
            LOG.trace("Final net: {} with defender {} wounds now at: {}",
                    finalNet, def.getName(), defWounds);

            return defWounds;
        };
    }

}
