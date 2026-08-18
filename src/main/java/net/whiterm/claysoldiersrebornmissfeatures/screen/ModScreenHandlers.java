package net.whiterm.claysoldiersrebornmissfeatures.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.whiterm.claysoldiersrebornmissfeatures.ClaySoldiersRebornMissfeatures;

public class ModScreenHandlers {
    public static final ScreenHandlerType<ClayNexusScreenHandler> CLAY_NEXUS_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, "clay_nexus"),
                    new ExtendedScreenHandlerType<>(ClayNexusScreenHandler::new));

    public static void registerScreenHandlers(){
        ClaySoldiersRebornMissfeatures.LOGGER.info("Registering screen handlers for " + ClaySoldiersRebornMissfeatures.MOD_ID);
    }
}
