package com.mcjty.signtastic.setup;

import mcjty.lib.setup.DefaultModSetup;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        NeoForge.EVENT_BUS.register(new ForgeEventHandlers());
        Messages.registerMessages();
    }

    @Override
    protected void setupModCompat() {
    }
}
