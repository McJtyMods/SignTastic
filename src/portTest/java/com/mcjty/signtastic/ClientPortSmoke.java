package com.mcjty.signtastic;

import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.TextureType;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignBlock;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import com.mcjty.signtastic.modules.signs.client.SignGui;
import com.mcjty.signtastic.modules.signs.data.SignSettings;
import com.mcjty.signtastic.modules.signs.network.PacketUpdateSignData;
import com.mcjty.signtastic.setup.Messages;
import com.mojang.blaze3d.platform.InputConstants;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.ColorSelector;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.ToggleButton;
import mcjty.lib.gui.widgets.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/** Runs only in the disposable signtastic-port-smoke world with -PclientSmoke. */
@EventBusSubscriber(modid = SignTastic.MODID, value = Dist.CLIENT)
public final class ClientPortSmoke {
    private static int ticks;
    private static CompletableFuture<Void> pending = CompletableFuture.completedFuture(null);
    private static final BlockPos EDITED = new BlockPos(0, 4, 0);
    private static final SignSettings GUI_SETTINGS = new SignSettings(true, Integer.valueOf(0x335577),
            0xabcdef, false, true, 7, TextureType.SPRUCE);

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
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
                        sign.setLines(List.of("SignTastic", "26.2", "café", "Line four", "five", "six", "seven", "eight"));
                        sign.setSettings(new SignSettings(d == 2, d == 1 ? Integer.valueOf(0x335577) : null,
                                d == 1 ? 0xffcc33 : 0xffffff, d == 3, d == 4, d >= 2 ? 1 + d : 0,
                                TextureType.values()[f * 3 + d]));
                    }
                }
                for (var player : server.getPlayerList().getPlayers()) {
                    player.teleportTo(server.overworld(), 4.5, 5, 9.5, java.util.Set.of(), 180, 0, false);
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
            Messages.sendToServer(PacketUpdateSignData.create(EDITED, List.of("Network edit", "26.2 works"),
                    0x223344, 0xffdd00, true, false, false, TextureType.DARK_OAK, 7));
        }
        if (tick == 150) {
            screenshot(mc, "signtastic-signs.png");
        }
        if (tick == 180) {
            var clientSign = (AbstractSignTileEntity) mc.level.getBlockEntity(EDITED);
            if (!clientSign.getLines().equals(List.of("Network edit", "26.2 works")) || clientSign.getSettings().getIconIndex() != 7) {
                throw new AssertionError("Server edit did not sync to the client");
            }
            var playerId = mc.player.getUUID();
            pending = CompletableFuture.runAsync(() -> {
                var sign = (AbstractSignTileEntity) server.overworld().getBlockEntity(EDITED);
                if (!sign.getLines().equals(List.of("Network edit", "26.2 works"))) throw new AssertionError("Edit did not reach server");
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
            var gui = (SignGui) mc.gui.screen();
            click(gui, iconChoice(gui), InputConstants.MOUSE_BUTTON_LEFT);
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
            var gui = (SignGui) mc.gui.screen();
            click(gui, iconChoice(gui), InputConstants.MOUSE_BUTTON_RIGHT);
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
            var text = widgets(gui, TextField.class).getFirst();
            gui.getWindow().setTextFocus(text);
            text.text("GUI edit");
            text.charTyped('!');
            // Several input events can arrive before the server's next block update.
            click(gui, toggle(gui, "Large"), InputConstants.MOUSE_BUTTON_LEFT);
            widgets(gui, ColorSelector.class).get(0).currentColor(GUI_SETTINGS.getBackColor());
            widgets(gui, ColorSelector.class).get(1).currentColor(GUI_SETTINGS.getTextColor());
            widgets(gui, ChoiceLabel.class).getFirst().choice("spruce");
            click(gui, toggle(gui, "Transp"), InputConstants.MOUSE_BUTTON_LEFT);
            click(gui, toggle(gui, "Bright"), InputConstants.MOUSE_BUTTON_LEFT);
            var clientSign = (AbstractSignTileEntity) mc.level.getBlockEntity(EDITED);
            if (!clientSign.getSettings().equals(GUI_SETTINGS)) throw new AssertionError("Client did not immediately apply GUI settings");
        }
        if (tick == 290) {
            var clientSign = (AbstractSignTileEntity) mc.level.getBlockEntity(EDITED);
            if (!clientSign.getLines().getFirst().equals("GUI edit!") || !clientSign.getSettings().equals(GUI_SETTINGS))
                throw new AssertionError("GUI edit did not update the client sign: " + clientSign.getLines() + " " + clientSign.getSettings());
            mc.player.closeContainer();
        }
        if (tick == 300) {
            var playerId = mc.player.getUUID();
            pending = CompletableFuture.runAsync(() -> {
                var sign = (AbstractSignTileEntity) server.overworld().getBlockEntity(EDITED);
                if (!sign.getLines().getFirst().equals("GUI edit!") || !sign.getSettings().equals(GUI_SETTINGS))
                    throw new AssertionError("GUI edit did not persist on server");
                sign.getBlockState().useWithoutItem(server.overworld(), server.getPlayerList().getPlayer(playerId),
                        new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(EDITED), Direction.SOUTH, EDITED, false));
            }, server);
        }
        if (tick == 320) {
            if (!(mc.gui.screen() instanceof SignGui gui)) throw new AssertionError("Editor did not reopen");
            var text = widgets(gui, TextField.class).getFirst();
            if (!text.getText().equals("GUI edit!")) throw new AssertionError("Reopened editor lost text");
            if (toggle(gui, "Bright").isPressed() || !toggle(gui, "Large").isPressed() || !toggle(gui, "Transp").isPressed()
                    || !widgets(gui, ChoiceLabel.class).getFirst().getCurrentChoice().equals("spruce")
                    || !Objects.equals(widgets(gui, ColorSelector.class).get(0).getCurrentColor(), GUI_SETTINGS.getBackColor())
                    || widgets(gui, ColorSelector.class).get(1).getCurrentColor() != GUI_SETTINGS.getTextColor()
                    || iconChoice(gui).getCurrentChoiceIndex() != 7)
                throw new AssertionError("Reopened editor lost settings");
            screenshot(mc, "signtastic-editor-reopened.png");
            System.out.println("SIGNTASTIC CLIENT SMOKE PASSED: rendering, item models, packet sync, icon cycling, rapid GUI edits, and close/reopen persistence");
            mc.stop();
        }
    }

    private static ImageChoiceLabel iconChoice(SignGui gui) {
        return widgets(gui, ImageChoiceLabel.class).getFirst();
    }

    private static <T> List<T> widgets(SignGui gui, Class<T> type) {
        return ((Panel) gui.getWindow().getToplevel()).getChildren().stream().filter(type::isInstance).map(type::cast).toList();
    }

    private static ToggleButton toggle(SignGui gui, String text) {
        return widgets(gui, ToggleButton.class).stream().filter(b -> b.getText().equals(text)).findFirst().orElseThrow();
    }

    private static void click(SignGui gui, Widget<?> widget, int button) {
        var bounds = widget.getBounds();
        var mouse = new MouseButtonEvent(gui.getLeftPos() + bounds.x + 2, gui.getTopPos() + bounds.y + 2,
                new MouseButtonInfo(button, 0));
        gui.mouseClicked(mouse, false);
        gui.mouseReleased(mouse);
    }

    private static void screenshot(Minecraft mc, String name) {
        net.minecraft.client.Screenshot.grab(mc.gameDirectory, name, mc.gameRenderer.mainRenderTarget(), 1,
                message -> System.out.println(message.getString()));
    }
}
