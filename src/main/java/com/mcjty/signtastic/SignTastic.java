package com.mcjty.signtastic;

import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.setup.*;
import mcjty.lib.modules.Modules;
import net.fabricmc.api.ModInitializer;

public class SignTastic implements ModInitializer {
    public static final String MODID = "signtastic";
    public static final ModSetup setup = new ModSetup();
    public static final Modules modules = new Modules();
    public static SignTastic instance;
    @Override public void onInitialize() {
        instance = this;
        Config.register();
        modules.register(new SignsModule());
        Registration.register();
        setup.init();
        modules.init();
        setup.registerBlockCapabilities(Registration.RBLOCKS);
        Messages.registerMessages();
        FabricEventHandlers.register();
    }
}
