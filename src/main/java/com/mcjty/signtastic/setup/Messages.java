package com.mcjty.signtastic.setup;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.network.PacketUpdateSignData;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Messages {

    public static void registerMessages(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(SignTastic.MODID)
                .versioned("1.0")
                .optional();

        registrar.playToServer(PacketUpdateSignData.TYPE, PacketUpdateSignData.CODEC, PacketUpdateSignData::handle);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        PacketDistributor.sendToServer(packet);
    }
}
