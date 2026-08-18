package net.whiterm.claysoldiersrebornmissfeatures.item;

import com.matthewperiut.clay.registry.TabsRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.whiterm.claysoldiersrebornmissfeatures.ClaySoldiersRebornMissfeatures;
import net.whiterm.claysoldiersrebornmissfeatures.block.ModBlocks;

public class ModItems {
    //public static final Item RUBY = registerItem("ruby", new Item(new FabricItemSettings()));

    private static void addItemsToClayItemGroup(FabricItemGroupEntries entries){
        //entries.add(RUBY);
        entries.add(ModBlocks.CLAY_NEXUS);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, name), item);
    }

    public static void registerModItems (){
        ClaySoldiersRebornMissfeatures.LOGGER.info("Registering modded items for " + ClaySoldiersRebornMissfeatures.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(TabsRegistry.CLAY_MISC_GROUP.getKey()).register(ModItems::addItemsToClayItemGroup);
    }
}
