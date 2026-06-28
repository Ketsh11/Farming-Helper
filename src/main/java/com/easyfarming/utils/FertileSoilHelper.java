package com.easyfarming.utils;

import com.easyfarming.EasyFarmingConfig;
import net.runelite.api.gameval.ItemID;

import java.util.Map;

public final class FertileSoilHelper {
    private FertileSoilHelper() {}

    public static boolean usesFertileSoil(EasyFarmingConfig config) {
        if (config == null) {
            return false;
        }
        EasyFarmingConfig.OptionEnumCompost compost = config.enumConfigCompost();
        return compost == EasyFarmingConfig.OptionEnumCompost.Fertile_Soil
                || compost == EasyFarmingConfig.OptionEnumCompost.Fertile_Soil_Ash_Covered_Tome;
    }

    public static boolean usesVolcanicAsh(EasyFarmingConfig config) {
        return config != null
                && config.enumConfigCompost() == EasyFarmingConfig.OptionEnumCompost.Fertile_Soil_Ash_Covered_Tome;
    }

    public static boolean useSpellbookSwap(EasyFarmingConfig config) {
        return usesFertileSoil(config)
                && config.fertileSoilTeleportMode() == EasyFarmingConfig.OptionEnumFertileSoilTeleportMode.Use_Spellbook_Swap;
    }

    public static boolean avoidStandardSpellbookTeleports(EasyFarmingConfig config) {
        return usesFertileSoil(config) && !useSpellbookSwap(config);
    }

    public static EasyFarmingConfig.OptionEnumHouseTele effectiveHouseTeleport(EasyFarmingConfig config) {
        if (config == null) {
            return EasyFarmingConfig.OptionEnumHouseTele.Law_air_earth_runes;
        }
        EasyFarmingConfig.OptionEnumHouseTele selected = config.enumConfigHouseTele();
        if (avoidStandardSpellbookTeleports(config)
                && selected == EasyFarmingConfig.OptionEnumHouseTele.Law_air_earth_runes) {
            return EasyFarmingConfig.OptionEnumHouseTele.Teleport_To_House;
        }
        return selected;
    }

    public static void mergeSpellbookSwapRunes(Map<Integer, Integer> requirements, int casts) {
        if (requirements == null || casts <= 0) {
            return;
        }
        requirements.merge(ItemID.ASTRALRUNE, Constants.SPELLBOOK_SWAP_ASTRAL_RUNE_COUNT * casts, Integer::sum);
        requirements.merge(ItemID.COSMICRUNE, Constants.SPELLBOOK_SWAP_COSMIC_RUNE_COUNT * casts, Integer::sum);
        requirements.merge(ItemID.LAWRUNE, Constants.SPELLBOOK_SWAP_LAW_RUNE_COUNT * casts, Integer::sum);
    }
}
