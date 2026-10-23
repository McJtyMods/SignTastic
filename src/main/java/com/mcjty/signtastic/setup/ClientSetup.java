package com.mcjty.signtastic.setup;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.client.SignGui;
import com.mcjty.signtastic.modules.signs.client.SignRenderer;
import net.fabricmc.api.ClientModInitializer;

public final class ClientSetup implements ClientModInitializer {
    @Override public void onInitializeClient() {
        SignTastic.modules.initClient();
        SignGui.register();
        SignRenderer.register();
    }
}
