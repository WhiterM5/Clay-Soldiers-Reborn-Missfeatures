/* ---------------------------CREDITS-------------------------------
 * This file was created by Kaupenjoe
 * https://github.com/Tutorials-By-Kaupenjoe/Fabric-Tutorial-1.17.1/blob/28-simpleConfigs/src/main/java/net/tutorialsbykaupenjoe/tutorialmod/config/ModConfigProvider.java
 * ----------------------------------------------------------------- */

package net.whiterm.claysoldiersrebornmissfeatures.config;

import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ModConfigProvider implements SimpleConfig.DefaultConfig {

    private String configContents = "";

    public List<Pair> getConfigsList() {
        return configsList;
    }

    private final List<Pair> configsList = new ArrayList<>();

    public void addKeyValuePair(Pair<String, ?> keyValuePair, String comment) {
        configsList.add(keyValuePair);
        configContents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " #"
                + comment + " | default value: " + keyValuePair.getSecond() + "\n";
    }

    @Override
    public String get(String namespace) {
        return configContents;
    }
}