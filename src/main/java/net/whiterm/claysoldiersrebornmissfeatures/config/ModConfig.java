//Code copied from Kaupenjoe tutorial | For more info visit ModConfigProvider class
//Edited by WhiterM

package net.whiterm.claysoldiersrebornmissfeatures.config;

import com.mojang.datafixers.util.Pair;
import net.whiterm.claysoldiersrebornmissfeatures.ClaySoldiersRebornMissfeatures;

public class ModConfig {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static int NEXUS_HEALTH;
    public static int nexusHealth = 160;
    private static final String nexusHealthKeyName = "ClayNexusHealth";
    public static int MAX_SOLDIERS_COUNT;
    public static int maxSoldiersCount = 10;
    private static final String maxSoldiersCountKeyName = "MaxSoldiersCount";
    public static int NEXUS_SPAWN_DELAY;
    public static int nexusSpawnDelay = 5;
    private static final String nexusSpawnDelayKeyName = "NexusSpawnDelay";
    public static int SOLDIER_ATTACK_DELAY;
    public static int soldierAttackDelay = 20;
    private static final String soldierAttackDelayKeyName = "SoldierAttackDelay";


    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(ClaySoldiersRebornMissfeatures.MOD_ID + "-server").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>(nexusHealthKeyName, nexusHealth), "Set the max health of the Clay Nexus");
        configs.addKeyValuePair(new Pair<>(maxSoldiersCountKeyName, maxSoldiersCount), "Number of soldiers Clay Nexus will maintain when active. E.g if the value is 10, and on the battlefield there are 4 soldiers, Nexus will spawn 6 new soldiers");
        configs.addKeyValuePair(new Pair<>(nexusSpawnDelayKeyName, nexusSpawnDelay), "How fast the Nexus spawns Soldiers in ticks (20ticks = 1s). The higher the value, the slower the Nexus spawns Soldiers");
        configs.addKeyValuePair(new Pair<>(soldierAttackDelayKeyName, soldierAttackDelay), "Defines delay between soldier attacks in ticks (20ticks = 1s). The higher the value, the slower the Nexus health will decrease");
    }

    private static void assignConfigs() {
        NEXUS_HEALTH = CONFIG.getOrDefault(nexusHealthKeyName, nexusHealth);
        MAX_SOLDIERS_COUNT = CONFIG.getOrDefault(maxSoldiersCountKeyName, maxSoldiersCount);
        NEXUS_SPAWN_DELAY = CONFIG.getOrDefault(nexusSpawnDelayKeyName, nexusSpawnDelay);
        SOLDIER_ATTACK_DELAY = CONFIG.getOrDefault(soldierAttackDelayKeyName, soldierAttackDelay);

        System.out.println("All " + ClaySoldiersRebornMissfeatures.MOD_ID + "'s " + configs.getConfigsList().size() + " config entries have been set properly");
    }
}
