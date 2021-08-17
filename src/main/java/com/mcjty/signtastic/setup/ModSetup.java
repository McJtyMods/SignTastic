package com.mcjty.signtastic.setup;

import com.mcjty.signtastic.modules.squares.SquaresModule;
import mcjty.lib.setup.DefaultModSetup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("signtastic", () -> new ItemStack(SquaresModule.SQUARE_SIGN_ITEM.get()));
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        Messages.registerMessages("signtastic");
    }

    @Override
    protected void setupModCompat() {
    }
}
