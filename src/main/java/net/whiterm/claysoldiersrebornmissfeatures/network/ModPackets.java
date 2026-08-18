package net.whiterm.claysoldiersrebornmissfeatures.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.whiterm.claysoldiersrebornmissfeatures.ClaySoldiersRebornMissfeatures;
import net.whiterm.claysoldiersrebornmissfeatures.network.packet.NexusWithdrawSoldiersC2SPacket;

public class ModPackets {
    public static final Identifier NEXUS_WITHDRAW_SOLDIERS_ID = new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, "nexus_withdraw_soldiers");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(NEXUS_WITHDRAW_SOLDIERS_ID, NexusWithdrawSoldiersC2SPacket::receive);
    }
}
