package net.whiterm.claysoldiersrebornmissfeatures.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ClayNexusBlockEntity;

import java.util.*;

import static net.whiterm.claysoldiersrebornmissfeatures.block.entity.ClayNexusBlockEntity.nexuses;

public class ModEvents {

    public static void registerEvents() {

        //Repair Nexus with Clay Ball
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.getBlockEntity(hitResult.getBlockPos()) instanceof ClayNexusBlockEntity blockEntity){
                if (player.isSneaking() && player.getMainHandStack().isOf(Items.CLAY_BALL)) {
                    if (!world.isClient()) {
                        blockEntity.repair();
                        blockEntity.updateHealthBar(world, blockEntity.getPos(), blockEntity.backgroundColorHealth, blockEntity.backgroundColorText);
                    } else {
                        if (hand == Hand.MAIN_HAND) {
                            player.swingHand(hand);
                        }
                    }
                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
        });

        //Nexus Soldier collecting system
        ServerTickEvents.END_WORLD_TICK.register((world) -> {
            if (nexuses == null || nexuses.isEmpty()) return;
            int posDiffInt = 32;
            Vec3i posDiffVec3i = new Vec3i(posDiffInt, posDiffInt, posDiffInt);
            ArrayList<Item> colors = new ArrayList<>();
            Map<Double, Map<ClayNexusBlockEntity, ItemEntity>> distanceNexusItem = new HashMap<>();

            //Get distances and BlockE and Item "relation"
            for (var nexus : nexuses) {
                //Do it only when Nexus is active
                if (!nexus.getNexusActive()) continue;
                BlockPos pos = nexus.getPos();
                Item color = nexus.getSoldierItem();
                //no duplicates and air
                if (color != Items.AIR) {
                    if (!colors.contains(color)) {
                        colors.add(color);
                    }
                }
                List<ItemEntity> droppedSoldiers = world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class),
                        new Box(pos.subtract(posDiffVec3i), pos.add(posDiffVec3i)),
                        itemEntity -> itemEntity.getStack().getItem() == color);
                if (!droppedSoldiers.isEmpty()) {
                    for (var item : droppedSoldiers) {
                        //Maximum capacity cannot be exceeded (filters all itemStacks which's count would exceed the capacity)
                        if (nexus.withdrawSoldiersCount() > nexus.withdrawMaxCapacity - item.getStack().getCount()) continue;
                        //Write distances between items and nexus's
                        double distance = pos.getSquaredDistance(item.getPos());
                        Map<ClayNexusBlockEntity, ItemEntity> nexusItem = new HashMap<>();
                            nexusItem.put(nexus, item);
                        distanceNexusItem.put(distance, nexusItem);
                    }
                }
            }

            if (!distanceNexusItem.isEmpty()) {
                //For each color detected
                for (int i = 0; i < colors.toArray().length; i++) {
                    //Get min distance and get the corresponding pair in the Map
                    double minDistance = getMinDistance(colors, i, distanceNexusItem);
                    if (minDistance == Double.MAX_VALUE) continue;
                    Map<ClayNexusBlockEntity, ItemEntity> pair = distanceNexusItem.get(minDistance);
                    //Add item to the closest nexus
                    for (var entry : pair.entrySet()) {
                        ItemEntity itemEntity = entry.getValue();
                        ItemStack itemStack = itemEntity.getStack();
                        int itemCount = itemStack.getCount();
                        ClayNexusBlockEntity blockEntity = entry.getKey();
                        blockEntity.addItemToWithdrawButton(itemStack.getItem(), itemCount);
                        itemEntity.setDespawnImmediately();
                    }
                }
            }
        });
    }

    private static double getMinDistance(ArrayList<Item> colors, int i, Map<Double, Map<ClayNexusBlockEntity, ItemEntity>> distanceNexusItem) {
        //Add all distances from Map to array (by color)
        ArrayList<Double> distancesByColor = new ArrayList<>();
        Item color = colors.get(i);
        for (var entry : distanceNexusItem.entrySet()) {
            ClayNexusBlockEntity be = null;
            for (var innerEntry : entry.getValue().entrySet()) {
                be = innerEntry.getKey();
            }
            Item soldierItem = be.getSoldierItem();
            if (color == soldierItem) {
                distancesByColor.add(entry.getKey());
            } else {
                //If the item does not correspond to the nexus, nothing happens
                return Double.MAX_VALUE;
            }
        }
        return Collections.min(distancesByColor);
    }
}