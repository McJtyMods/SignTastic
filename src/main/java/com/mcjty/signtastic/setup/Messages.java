package com.mcjty.signtastic.setup;
import com.mcjty.signtastic.modules.signs.network.PacketUpdateSignData;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import mcjty.lib.network.Networking;
public final class Messages {
    public static void registerMessages() {
        PayloadTypeRegistry.serverboundPlay().register(PacketUpdateSignData.TYPE, PacketUpdateSignData.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketUpdateSignData.TYPE, (packet, context) -> packet.handle(context.player()));
    }
    public static <T extends CustomPacketPayload> void sendToServer(T packet) { Networking.sendToServer(packet); }
}
