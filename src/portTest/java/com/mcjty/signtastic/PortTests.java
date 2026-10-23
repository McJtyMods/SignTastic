package com.mcjty.signtastic;

import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.TextureType;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignBlock;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import com.mcjty.signtastic.modules.signs.data.SignData;
import com.mcjty.signtastic.modules.signs.data.SignSettings;
import com.mcjty.signtastic.modules.signs.network.PacketUpdateSignData;
import com.mcjty.signtastic.setup.Registration;
import io.netty.buffer.Unpooled;
import mcjty.lib.api.container.CapabilityContainerProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@EventBusSubscriber(modid = SignTastic.MODID)
public final class PortTests {
    private static final List<String> LINES = List.of("SignTastic 26.2", "Unicode: café", "", "Fourth line", "5", "6", "7", "8");
    private static final Map<String, Consumer<GameTestHelper>> TESTS = Map.of(
            "save_and_sync", PortTests::saveAndSync,
            "drops_and_configurator", PortTests::dropsAndConfigurator,
            "live_updates", PortTests::liveUpdates,
            "packet_codecs", PortTests::packetCodecs);

    private static Identifier id(String name) { return Identifier.fromNamespaceAndPath(SignTastic.MODID, name); }

    @SubscribeEvent
    public static void functions(RegisterEvent event) {
        TESTS.forEach((name, test) -> event.register(Registries.TEST_FUNCTION, id(name), () -> test));
    }

    @SubscribeEvent
    public static void tests(RegisterGameTestsEvent event) {
        var environment = event.registerEnvironment(id("port"));
        TESTS.forEach((name, test) -> event.registerTest(id(name), new FunctionGameTestInstance(
                ResourceKey.create(Registries.TEST_FUNCTION, id(name)),
                new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true))));
    }

    private static void saveAndSync(GameTestHelper helper) {
        var world = helper.getLevel();
        var registries = world.registryAccess();
        var pos = helper.absolutePos(new BlockPos(0, 1, 0));
        for (var family : List.of(SignsModule.SQUARE_SIGN, SignsModule.BLOCK_SIGN, SignsModule.SLAB_SIGN)) {
            for (Direction facing : Direction.values()) {
                for (Direction horizontal : Direction.Plane.HORIZONTAL) {
                    var state = family.block().get().defaultBlockState().setValue(BlockStateProperties.FACING, facing)
                            .setValue(AbstractSignBlock.HORIZ_FACING, horizontal);
                    // Literal 1.21.1 attachment layout, without a newly introduced wrapper field.
                    var oldData = new CompoundTag();
                    var lines = new ListTag();
                    LINES.forEach(line -> lines.add(StringTag.valueOf(line)));
                    oldData.put("lines", lines);
                    var oldSettings = new CompoundTag();
                    oldSettings.putBoolean("transparent", true);
                    oldSettings.putInt("backColor", 0x335577);
                    oldSettings.putInt("textColor", 0xaabbcc);
                    oldSettings.putBoolean("bright", true);
                    oldSettings.putBoolean("large", true);
                    oldSettings.putInt("image", 17);
                    oldSettings.putString("ttype", "BIRCH");
                    var attachments = new CompoundTag();
                    attachments.put("signtastic:signdata", oldData);
                    attachments.put("signtastic:signsettings", oldSettings);
                    var legacy = new CompoundTag();
                    String blockId = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
                    legacy.putString("id", blockId);
                    legacy.put("neoforge:attachments", attachments);
                    var restored = (AbstractSignTileEntity) BlockEntity.loadStatic(pos, state, legacy, registries);
                    helper.assertTrue(restored != null, "Legacy sign loads");
                    helper.assertValueEqual(restored.getLines(), LINES, "Legacy text preserved");
                    var settings = new SignSettings(true, Integer.valueOf(0x335577), 0xaabbcc, true, true, 17, TextureType.BIRCH);
                    helper.assertValueEqual(restored.getSettings(), settings, "Legacy settings preserved");
                    helper.assertValueEqual(restored.getBlockState(), state, "Facing properties preserved");
                    var saved = restored.saveWithFullMetadata(registries);
                    var reloaded = (AbstractSignTileEntity) BlockEntity.loadStatic(pos, state, saved, registries);
                    helper.assertValueEqual(reloaded.getLines(), LINES, "Text survives save/load");
                    helper.assertValueEqual(reloaded.getSettings(), settings, "Settings survive save/load");
                    var client = (AbstractSignTileEntity) family.be().get().create(pos, state);
                    client.handleUpdateTag(TagValueInput.create(ProblemReporter.DISCARDING, registries, restored.getUpdateTag(registries)));
                    helper.assertValueEqual(client.getLines(), LINES, "Client receives text");
                    helper.assertValueEqual(client.getSettings(), settings, "Client receives settings");
                }
            }
        }
        helper.succeed();
    }

    private static void dropsAndConfigurator(GameTestHelper helper) {
        var world = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(0, 1, 0));
        var otherPos = helper.absolutePos(new BlockPos(2, 1, 0));
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var configurator = new ItemStack(SignsModule.SIGN_CONFIGURATOR.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, configurator);
        for (var family : List.of(SignsModule.SQUARE_SIGN, SignsModule.BLOCK_SIGN, SignsModule.SLAB_SIGN)) {
            var state = family.block().get().defaultBlockState();
            world.setBlock(pos, state, Block.UPDATE_ALL);
            world.setBlock(otherPos, state, Block.UPDATE_ALL);
            var sign = (AbstractSignTileEntity) world.getBlockEntity(pos);
            var target = (AbstractSignTileEntity) world.getBlockEntity(otherPos);
            var settings = new SignSettings(false, Integer.valueOf(0x112233), 0xffaa00, true, false, 9, TextureType.IRON);
            sign.setLines(LINES);
            sign.setSettings(settings);
            target.setLines(List.of("Keep this text"));
            player.setShiftKeyDown(false);
            player.setPose(net.minecraft.world.entity.Pose.STANDING);
            configurator.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false)));
            helper.assertValueEqual(configurator.get(Registration.ITEM_SIGNSETTINGS), settings, "Configurator copies settings");
            player.setShiftKeyDown(true);
            player.setPose(net.minecraft.world.entity.Pose.CROUCHING);
            configurator.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(otherPos), Direction.UP, otherPos, false)));
            helper.assertValueEqual(target.getSettings(), settings, "Configurator pastes settings");
            helper.assertValueEqual(target.getLines(), List.of("Keep this text"), "Paste preserves text");
            var drops = Block.getDrops(state, world, pos, sign, null, ItemStack.EMPTY);
            helper.assertTrue(drops.size() == 1, "Sign drops one item");
            var drop = drops.getFirst();
            helper.assertValueEqual(drop.get(Registration.ITEM_SIGNDATA), new SignData(LINES), "Drop keeps text");
            helper.assertValueEqual(drop.get(Registration.ITEM_SIGNSETTINGS), settings, "Drop keeps settings");
            var saved = ItemStack.CODEC.encodeStart(world.registryAccess().createSerializationContext(NbtOps.INSTANCE), drop).getOrThrow();
            var loaded = ItemStack.CODEC.parse(world.registryAccess().createSerializationContext(NbtOps.INSTANCE), saved).getOrThrow();
            var placed = (AbstractSignTileEntity) family.be().get().create(otherPos, state);
            placed.applyComponentsFromItemStack(loaded);
            helper.assertValueEqual(placed.getLines(), LINES, "Replaced sign keeps text");
            helper.assertValueEqual(placed.getSettings(), settings, "Replaced sign keeps settings");
            helper.assertTrue(world.getCapability(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY, pos, null) != null,
                    "Sign editing menu capability registered");
        }
        helper.succeed();
    }

    private static void liveUpdates(GameTestHelper helper) {
        var world = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(1, 1, 1));
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var context = (IPayloadContext) Proxy.newProxyInstance(IPayloadContext.class.getClassLoader(),
                new Class<?>[]{IPayloadContext.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "player" -> player;
                    case "enqueueWork" -> {
                        ((Runnable) args[0]).run();
                        yield CompletableFuture.completedFuture(null);
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        for (var family : List.of(SignsModule.SQUARE_SIGN, SignsModule.BLOCK_SIGN, SignsModule.SLAB_SIGN)) {
            var state = family.block().get().defaultBlockState();
            world.setBlock(pos, state, Block.UPDATE_ALL);
            var sign = (AbstractSignTileEntity) world.getBlockEntity(pos);
            var edit = PacketUpdateSignData.create(pos, LINES, 0x123456, 0xabcdef, true, true, true, TextureType.IRON, 7);
            edit.handle(context);
            helper.assertValueEqual(sign.getLines(), LINES, "GUI text reaches server");
            var expected = new SignSettings(true, Integer.valueOf(0x123456), 0xabcdef, true, true, 7, TextureType.IRON);
            helper.assertValueEqual(sign.getSettings(), expected, "GUI settings reach server");
            var packet = sign.getUpdatePacket();
            var client = (AbstractSignTileEntity) family.be().get().create(pos, state);
            client.onDataPacket(null, TagValueInput.create(ProblemReporter.DISCARDING, world.registryAccess(), packet.getTag()));
            helper.assertValueEqual(client.getLines(), LINES, "Live packet syncs GUI text");
            helper.assertValueEqual(client.getSettings(), expected, "Live packet syncs GUI settings");
        }
        helper.succeed();
    }

    private static void packetCodecs(GameTestHelper helper) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        try {
            for (var texture : TextureType.values()) {
                for (Integer color : new Integer[]{null, 0x123456}) {
                    var packet = PacketUpdateSignData.create(BlockPos.ZERO, LINES, color, 0xabcdef, true, false, true, texture, 23);
                    PacketUpdateSignData.CODEC.encode(buf, packet);
                    helper.assertValueEqual(PacketUpdateSignData.CODEC.decode(buf), packet, "Edit packet round trip");
                    var settings = new SignSettings(true, color, 0xabcdef, false, true, 3, texture);
                    SignSettings.STREAM_CODEC.encode(buf, settings);
                    helper.assertValueEqual(SignSettings.STREAM_CODEC.decode(buf), settings, "Settings component sync");
                }
            }
        } finally {
            buf.release();
        }
        helper.succeed();
    }
}
