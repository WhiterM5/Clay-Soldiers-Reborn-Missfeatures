package net.whiterm.claysoldiersrebornmissfeatures.network.packet.S2C;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ClayNexusBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class NexusWithdrawSoldiersCountS2CPacket {
    private static final Logger log = LogManager.getLogger(NexusWithdrawSoldiersCountS2CPacket.class);

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        Map<ItemStack, Integer> forPlayerItemSoldiers = buf.readMap(PacketByteBuf::readItemStack, PacketByteBuf::readInt);
        BlockPos pos = buf.readBlockPos();
        client.execute(() -> {
            ClayNexusBlockEntity blockEntity = (ClayNexusBlockEntity) handler.getWorld().getBlockEntity(pos);
            for (var entry : forPlayerItemSoldiers.entrySet()) {
                ItemStack soldierItem = entry.getKey();
                Integer soldierCount = entry.getValue();
                blockEntity.forPlayerItemSoldiers.put(soldierItem.getItem(), soldierCount);
            }
        });
    }
}
