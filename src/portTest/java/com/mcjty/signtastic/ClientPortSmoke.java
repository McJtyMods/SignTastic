package com.mcjty.signtastic;

import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.TextureType;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignBlock;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import com.mcjty.signtastic.modules.signs.client.SignGui;
import com.mcjty.signtastic.modules.signs.data.SignSettings;
import com.mcjty.signtastic.modules.signs.network.PacketUpdateSignData;
import com.mcjty.signtastic.setup.Messages;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.ToggleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Runs only in the disposable signtastic-port-smoke world with -PclientSmoke. */
public final class ClientPortSmoke implements net.fabricmc.api.ClientModInitializer {
    private static int ticks;
    private static CompletableFuture<Void> pending = CompletableFuture.completedFuture(null);
    private static final BlockPos EDITED = new BlockPos(0, 4, 0);

    @Override public void onInitializeClient() { net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> tick()); }

    public static void tick() {
        if (!Boolean.getBoolean("signtastic.clientSmoke")) return;
        var mc = Minecraft.getInstance();
        var server = mc.getSingleplayerServer();
        if (mc.level == null || mc.player == null || server == null) return;
        if (mc.gui.screen() != null && !(mc.gui.screen() instanceof SignGui)) return;
        if (!pending.isDone()) return;
        pending.join();
        int tick = ticks++;
        if (tick == 0) {
            pending = CompletableFuture.runAsync(() -> {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) server.overworld().getChunk(x, z);
                }
                var source = server.createCommandSourceStack();
                server.getCommands().performPrefixedCommand(source, "fill -2 3 -2 12 3 12 minecraft:stone");
                server.getCommands().performPrefixedCommand(source, "gamemode creative @a");
                var families = List.of(SignsModule.SQUARE_SIGN, SignsModule.SLAB_SIGN, SignsModule.BLOCK_SIGN);
                for (int f = 0; f < families.size(); f++) {
                    for (int d = 0; d < 6; d++) {
                        var pos = new BlockPos(f * 3 + d % 3, 4 + (d / 3) * 2, 0);
                        var state = families.get(f).block().get().defaultBlockState()
                                .setValue(BlockStateProperties.FACING, Direction.SOUTH)
                                .setValue(AbstractSignBlock.HORIZ_FACING, Direction.SOUTH);
                        server.overworld().setBlock(pos, state, 3);
                        var sign = (AbstractSignTileEntity) server.overworld().getBlockEntity(pos);
                        sign.setLines(List.of("SignTastic", "26.3", "café", "Line four", "five", "six", "seven", "eight"));
                        sign.setSettings(new SignSettings(d == 2, d == 1 ? Integer.valueOf(0x335577) : null,
                                d == 1 ? 0xffcc33 : 0xffffff, d == 3, d == 4, d >= 2 ? 1 + d : 0,
                                TextureType.values()[f * 3 + d]));
                    }
                }
                for (var player : server.getPlayerList().getPlayers()) {
                    // Stay within the edit packet's eight-block interaction limit.
                    player.teleportTo(server.overworld(), 4.5, 5, 6.5, java.util.Set.of(), 180, 0, false);
                    player.getInventory().setItem(0, new ItemStack(SignsModule.SQUARE_SIGN.item().get()));
                    player.getInventory().setItem(1, new ItemStack(SignsModule.SLAB_SIGN.item().get()));
                    player.getInventory().setItem(2, new ItemStack(SignsModule.BLOCK_SIGN.item().get()));
                    player.getInventory().setItem(3, new ItemStack(SignsModule.SIGN_CONFIGURATOR.get()));
                }
            }, server);
        }
        if (tick == 100) {
            for (var item : List.of(SignsModule.SQUARE_SIGN.item(), SignsModule.SLAB_SIGN.item(),
                    SignsModule.BLOCK_SIGN.item(), SignsModule.SIGN_CONFIGURATOR)) {
                var model = new net.minecraft.client.renderer.item.ItemStackRenderState();
                mc.getItemModelResolver().updateForTopItem(model, new ItemStack(item.get()),
                        net.minecraft.world.item.ItemDisplayContext.GUI, mc.level, mc.player, 0);
                if (model.isEmpty()) throw new AssertionError("Missing item model: " + item.get());
            }
            Messages.sendToServer(PacketUpdateSignData.create(EDITED, List.of("Network edit", "26.3 works"),
                    0x223344, 0xffdd00, true, false, false, TextureType.DARK_OAK, 7));
        }
        if (tick == 150) {
            screenshot(mc, "signtastic-signs.png");
        }
        if (tick == 180) {
            var clientSign = (AbstractSignTileEntity) mc.level.getBlockEntity(EDITED);
            if (!clientSign.getLines().equals(List.of("Network edit", "26.3 works")) || clientSign.getSettings().getIconIndex() != 7) {
                throw new AssertionError("Server edit did not sync to the client");
            }
            var playerId = mc.player.getUUID();
            pending = CompletableFuture.runAsync(() -> {
                var sign = (AbstractSignTileEntity) server.overworld().getBlockEntity(EDITED);
                if (!sign.getLines().equals(List.of("Network edit", "26.3 works"))) throw new AssertionError("Edit did not reach server");
                var player = server.getPlayerList().getPlayer(playerId);
                // Use the block's actual activation path to open its McJtyLib container.
                player.teleportTo(server.overworld(), 1, 5, 2, java.util.Set.of(), 180, 0, false);
                var state = sign.getBlockState();
                state.useWithoutItem(server.overworld(), player, new net.minecraft.world.phys.BlockHitResult(
                        net.minecraft.world.phys.Vec3.atCenterOf(EDITED), Direction.SOUTH, EDITED, false));
            }, server);
        }
        if (tick == 220) {
            if (!(mc.gui.screen() instanceof SignGui gui)) throw new AssertionError("Sign editor did not open");
            if (iconChoice(gui).getCurrentChoiceIndex() != 7) throw new AssertionError("Editor did not load the selected icon");
            screenshot(mc, "signtastic-editor.png");
        }
        if (tick == 221) {
            // Cross a sprite-sheet row using the widget's actual click handler.
            iconChoice((SignGui) mc.gui.screen()).mouseClick(0, 0, com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT);
        }
        if (tick == 240) {
            if (iconChoice((SignGui) mc.gui.screen()).getCurrentChoiceIndex() != 8) throw new AssertionError("Icon did not advance");
            screenshot(mc, "signtastic-editor-next-icon.png");
            pending = CompletableFuture.runAsync(() -> {
                var sign = (AbstractSignTileEntity) server.overworld().getBlockEntity(EDITED);
                if (sign.getSettings().getIconIndex() != 8) throw new AssertionError("Icon choice did not reach server");
            }, server);
        }
        if (tick == 241) {
            iconChoice((SignGui) mc.gui.screen()).mouseClick(0, 0, com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_RIGHT);
        }
        if (tick == 260) {
            if (iconChoice((SignGui) mc.gui.screen()).getCurrentChoiceIndex() != 7) throw new AssertionError("Icon did not go back");
            pending = CompletableFuture.runAsync(() -> {
                var sign = (AbstractSignTileEntity) server.overworld().getBlockEntity(EDITED);
                if (sign.getSettings().getIconIndex() != 7) throw new AssertionError("Previous icon did not reach server");
            }, server);
        }
        if (tick == 270) {
            var gui = (SignGui) mc.gui.screen();
            // Toggle Large before the server can echo the edited text back.
            textField(gui).text("Draft survives Large");
            ((Panel) gui.getWindow().getToplevel()).getChildren().stream()
                    .filter(ToggleButton.class::isInstance).map(ToggleButton.class::cast)
                    .filter(button -> button.getText().equals("Large")).findFirst().orElseThrow()
                    .mouseClick(0, 0, com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT);
            if (!textField(gui).getText().equals("Draft survives Large")) throw new AssertionError("Large reset draft text");
        }
        if (tick == 300) mc.player.closeContainer();
        if (tick == 330) {
            pending = CompletableFuture.runAsync(() -> {
                var sign = (AbstractSignTileEntity) server.overworld().getBlockEntity(EDITED);
                if (!sign.getLines().getFirst().equals("Draft survives Large") || !sign.getSettings().isLarge())
                    throw new AssertionError("Editor changes were not saved on the server");
                var player = server.getPlayerList().getPlayers().getFirst();
                sign.getBlockState().useWithoutItem(server.overworld(), player, new net.minecraft.world.phys.BlockHitResult(
                        net.minecraft.world.phys.Vec3.atCenterOf(EDITED), Direction.SOUTH, EDITED, false));
            }, server);
        }
        if (tick == 360) {
            if (!(mc.gui.screen() instanceof SignGui gui) || !textField(gui).getText().equals("Draft survives Large")
                    || !((AbstractSignTileEntity) gui.getBE()).getSettings().isLarge())
                throw new AssertionError("Editor changes did not survive closing and reopening");
            System.out.println("SIGNTASTIC CLIENT SMOKE PASSED: sign rendering, four item models, edit packet sync, McJtyLib editor, icon cycling, and saved GUI edits");
            mc.stop();
        }
    }

    private static TextField textField(SignGui gui) {
        return ((Panel) gui.getWindow().getToplevel()).getChildren().stream()
                .filter(TextField.class::isInstance).map(TextField.class::cast).findFirst().orElseThrow();
    }

    private static ImageChoiceLabel iconChoice(SignGui gui) {
        return ((Panel) gui.getWindow().getToplevel()).getChildren().stream()
                .filter(ImageChoiceLabel.class::isInstance).map(ImageChoiceLabel.class::cast)
                .findFirst().orElseThrow(() -> new AssertionError("Missing icon selector"));
    }

    private static void screenshot(Minecraft mc, String name) {
        net.minecraft.client.Screenshot.grab(mc.gameDirectory, name, mc.gameRenderer.mainRenderTarget(), 1,
                message -> System.out.println(message.getString()));
    }
}
