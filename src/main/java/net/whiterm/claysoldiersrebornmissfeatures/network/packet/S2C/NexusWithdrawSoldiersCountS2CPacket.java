package net.whiterm.claysoldiersrebornmissfeatures.network.packet.S2C;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NexusWithdrawSoldiersCountS2CPacket {
    private static final Logger log = LogManager.getLogger(NexusWithdrawSoldiersCountS2CPacket.class);

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

    }
}
