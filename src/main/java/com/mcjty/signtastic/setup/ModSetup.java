package com.mcjty.signtastic.setup;

import com.mcjty.signtastic.modules.signs.SignsModule;
import mcjty.lib.setup.DefaultModSetup;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("signtastic", () -> new ItemStack(SignsModule.SQUARE_SIGN_ITEM.get()));
    }

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
