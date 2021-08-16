package com.mcjty.signtastic;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SignTastic.MODID)
public class SignTastic {

    public static final String MODID = "signtastic";

    public static final Logger LOGGER = LogManager.getLogger();

    public SignTastic() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SignTastic::init);
    }

    public static void init(final FMLCommonSetupEvent event) {
    }
}
