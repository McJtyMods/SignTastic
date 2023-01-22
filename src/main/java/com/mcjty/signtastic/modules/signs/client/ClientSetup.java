package com.mcjty.signtastic.modules.signs.client;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class ClientSetup {

    public static List<ResourceLocation> onTextureStitch() {
        return Collections.singletonList(SignRenderer.SIGNS);
    }
}
