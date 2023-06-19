package com.mcjty.signtastic.setup;

import mcjty.lib.setup.DefaultModSetup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        Messages.registerMessages("signtastic");
    }

    @Override
    protected void setupModCompat() {
    }
}
