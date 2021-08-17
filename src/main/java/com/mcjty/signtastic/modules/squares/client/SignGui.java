package com.mcjty.signtastic.modules.squares.client;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.squares.SquaresModule;
import com.mcjty.signtastic.modules.squares.blocks.SquareSignTileEntity;
import com.mcjty.signtastic.setup.Messages;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.GuiItemScreen;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.Widgets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class SignGui extends GenericGuiContainer<SquareSignTileEntity, GenericContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(SignTastic.MODID, "textures/gui/signgui.png");
    public static final int WIDTH = 215;
    public static final int HEIGHT = 152;

    private TextField label1;

    public SignGui(SquareSignTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, ManualEntry.EMPTY);

        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        label1 = Widgets.textfield(10, 10, 205, 16);
        Panel toplevel = Widgets.positional().background(BACKGROUND).children(label1);
        toplevel.bounds(leftPos, topPos, WIDTH, HEIGHT);

        window = new Window(this, toplevel);
    }

    @Override
    public void render(MatrixStack matrixStack, int xSize_lo, int ySize_lo, float par3) {
        super.render(matrixStack, xSize_lo, ySize_lo, par3);
        drawWindow(matrixStack);
    }

    public static void register() {
        register(SquaresModule.CONTAINER_SQUARE_SIGN.get(), SignGui::new);
    }
}
