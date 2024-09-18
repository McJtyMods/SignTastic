package com.mcjty.signtastic;

import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.setup.Config;
import com.mcjty.signtastic.setup.Messages;
import com.mcjty.signtastic.setup.ModSetup;
import com.mcjty.signtastic.setup.Registration;
import io.netty.channel.PreferHeapByteBufAllocator;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import mcjty.lib.tileentity.AnnotationHolder;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(SignTastic.MODID)
public class SignTastic {

    public static final String MODID = "signtastic";

    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();
    private Modules modules = new Modules();
    public static SignTastic instance;

    public SignTastic(ModContainer container, IEventBus bus, Dist dist) {
        instance = this;
        Config.register(container);
        setupModules(bus, dist);
        Registration.register(bus);

        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(this::onDataGen);
        bus.addListener(Messages::registerMessages);
        bus.addListener(this::onRegisterCapabilities);

        if (dist.isClient()) {
            bus.addListener(modules::initClient);
        }
    }

    public static <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        return instance.setup.tab(supplier);
    }

    private void onDataGen(GatherDataEvent event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen);
        datagen.generate();
    }

    private void setupModules(IEventBus bus, Dist dist) {
        modules.register(new SignsModule(bus, dist));
    }

    private void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        // @todo 1.21 let McJtyLib handle this
        for (Map.Entry<Class<? extends GenericTileEntity>, AnnotationHolder> entry : Registration.RBLOCKS.getHolders().entrySet()) {
            AnnotationHolder holder = entry.getValue();
            for (int i = 0 ; i < holder.getCapSize() ; i++) {
                var hd = holder.getCapHolder(i);
                var bc = hd.capability();
                var function = hd.function();
                event.registerBlock(bc, new IBlockCapabilityProvider<>() {
                    @Nullable
                    @Override
                    public Object getCapability(Level level, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, Object o) {
                        if (blockEntity instanceof GenericTileEntity be) {
                            return function.apply(be);
                        }
                        return null;
                    }
                }, hd.block().get());
            }
        }
    }
}
