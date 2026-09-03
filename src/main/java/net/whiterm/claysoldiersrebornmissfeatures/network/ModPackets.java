package net.whiterm.claysoldiersrebornmissfeatures.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.whiterm.claysoldiersrebornmissfeatures.ClaySoldiersRebornMissfeatures;
import net.whiterm.claysoldiersrebornmissfeatures.network.packet.C2S.NexusWithdrawSoldiersC2SPacket;
import net.whiterm.claysoldiersrebornmissfeatures.network.packet.S2C.NexusWithdrawSoldiersCountS2CPacket;

public class ModPackets {
    public static final Identifier NEXUS_WITHDRAW_SOLDIERS_ID = new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, "nexus_withdraw_soldiers");
    public static final Identifier NEXUS_WITHDRAW_SOLDIERS_COUNT_ID = new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, "nexus_withdraw_soldiers_count");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(NEXUS_WITHDRAW_SOLDIERS_ID, NexusWithdrawSoldiersC2SPacket::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(NEXUS_WITHDRAW_SOLDIERS_COUNT_ID, NexusWithdrawSoldiersCountS2CPacket::receive);
    }
}
