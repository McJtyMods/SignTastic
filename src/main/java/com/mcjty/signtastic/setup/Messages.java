package com.mcjty.signtastic.setup;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.network.PacketUpdateSignData;
import mcjty.lib.network.Networking;

public class Messages {

    private static IPayloadRegistrar registrar;

    public static void registerMessages() {
        registrar = Networking.registrar(SignTastic.MODID)
                .versioned("1.0")
                .optional();

        registrar.play(PacketUpdateSignData.class, PacketUpdateSignData::create, handler -> handler.server(PacketUpdateSignData::handle));
    }

    public static <T> void sendToServer(T packet) {
        registrar.getChannel().sendToServer(packet);
    }
}
