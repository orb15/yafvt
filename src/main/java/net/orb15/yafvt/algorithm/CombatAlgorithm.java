package net.orb15.yafvt.algorithm;

import java.util.Optional;
import java.util.function.BiFunction;

import net.orb15.dicebag.DiceBag;
import net.orb15.yafvt.character.ArmorLevel;
import net.orb15.yafvt.character.Character;
import net.orb15.yafvt.character.Skill;
import net.orb15.yafvt.character.WoundLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatAlgorithm {

    private static final Logger LOG = LoggerFactory.getLogger(CombatAlgorithm.class);

    public enum AlgorithmType {

        DEFAULT;
    }

    public static BiFunction<Character, Character, WoundLevel> getCombatAlgorithm(AlgorithmType type) {

        switch(type) {

            case DEFAULT:
                return defaultCombatAlgorithm();

            default:
                throw new UnsupportedOperationException("AlgorithmType: " + type + " is not supported");
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

            if(net <= 0)
                return def.getCurrentWoundLevel();

            //attacker hit, deal with armor's DR
            int drNet = (int) (Math.ceil( (double)net / (double)(defArmorLevel.getValue())));
            LOG.trace("Net roll after DR: {}", drNet);

            //limit total net hits by attacker weapon
            int finalNet;
            switch(attWeaponInHand) {

                case LIGHT_WEAPON:
                case THROWN_WEAPON:
                    finalNet = Math.min(drNet, 2);
                    break;

                case MEDIUM_WEAPON:
                    finalNet = Math.min(drNet, 4);
                    break;

                default: //bows and heavy weapons have no limit
                    finalNet = drNet;
            }

            WoundLevel defWounds = def.applyDamage(finalNet);
            LOG.trace("Final net after weapon limiter: {} with defender {} wounds now at: {}",
                    finalNet, def.getName(), defWounds);

            return defWounds;
        };
    }
}
