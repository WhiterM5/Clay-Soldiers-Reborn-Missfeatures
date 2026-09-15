package net.whiterm.claysoldiersrebornmissfeatures.network.packet.C2S;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ClayNexusBlockEntity;
import net.whiterm.claysoldiersrebornmissfeatures.network.ModPackets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class NexusSendWithdrawSoldiersCountC2SPacket {
    private static final Logger log = LogManager.getLogger(NexusSendWithdrawSoldiersCountC2SPacket.class);

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        server.execute(() -> {
            ClayNexusBlockEntity blockEntity = (ClayNexusBlockEntity) player.getWorld().getBlockEntity(pos);

            if (blockEntity != null) {
                if (!blockEntity.forPlayerItemSoldiers.isEmpty()) {
                    Map<ItemStack, Integer> forPlayerItemSoldiers = new HashMap<>();
                    for (var entry : blockEntity.forPlayerItemSoldiers.entrySet()) {
                        Item soldierItem = entry.getKey();
                        Integer soldierCount = entry.getValue();
                        forPlayerItemSoldiers.put(soldierItem.getDefaultStack(), soldierCount);
                    }
                    PacketByteBuf response = PacketByteBufs.create();
                    response.writeMap(forPlayerItemSoldiers, PacketByteBuf::writeItemStack, PacketByteBuf::writeInt);
                    response.writeBlockPos(pos);
                    ServerPlayNetworking.send(player, ModPackets.NEXUS_WITHDRAW_SOLDIERS_COUNT_ID, response);
                }
            } else {
                log.error("Block Entity is null - failed to send fallen soldiers count to the Client");
            }
        });
    }
}
