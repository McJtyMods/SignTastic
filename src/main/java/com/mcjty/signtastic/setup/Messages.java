package com.mcjty.signtastic.setup;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.network.PacketUpdateSignData;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import static mcjty.lib.network.PlayPayloadContext.wrap;

public class Messages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void registerMessages(String name) {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(SignTastic.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.registerMessage(id(), PacketUpdateSignData.class, PacketUpdateSignData::write, PacketUpdateSignData::create, wrap(PacketUpdateSignData::handle));
    }

    public static <T> void sendToServer(T packet) {
        INSTANCE.sendToServer(packet);
    }
}
