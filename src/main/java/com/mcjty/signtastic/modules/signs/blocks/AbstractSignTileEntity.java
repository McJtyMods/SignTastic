package com.mcjty.signtastic.modules.signs.blocks;

import com.mcjty.signtastic.modules.signs.data.SignData;
import com.mcjty.signtastic.modules.signs.data.SignSettings;
import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.TextureType;
import com.mcjty.signtastic.setup.Registration;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractSignTileEntity extends GenericTileEntity {

//    private final Lazy<MenuProvider> screenHandler = Lazy.of(() -> new DefaultContainerProvider<GenericContainer>("Builder")
//            .containerSupplier(DefaultContainerProvider.empty(SignsModule.CONTAINER_SIGN, this))
//    );

    @Cap(type = CapType.CONTAINER)
    private final static Function<AbstractSignTileEntity, MenuProvider> SCREEN_HANDLER =
            be -> new DefaultContainerProvider<GenericContainer>("Sign")
                    .containerSupplier(DefaultContainerProvider.empty(SignsModule.CONTAINER_SIGN, be));

    public AbstractSignTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        registerAttachment(Registration.SIGNDATA.get(), SignData.STREAM_CODEC);
        registerAttachment(Registration.SIGNSETTINGS.get(), SignSettings.STREAM_CODEC);
    }

    public void setLines(List<String> lines) {
        SignData data = getData(Registration.SIGNDATA);
        data.lines().clear();
        data.lines().addAll(lines);
        markDirtyClient();
    }

    public List<String> getLines() {
        return getData(Registration.SIGNDATA).lines();
    }

    public void setSettings(SignSettings settings) {
        setData(Registration.SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public abstract int getLinesSupported();

    public abstract float getRenderOffset();

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        var data = input.get(Registration.ITEM_SIGNDATA);
        if (data != null) {
            setData(Registration.SIGNDATA, data);
        }
        var settings = input.get(Registration.ITEM_SIGNSETTINGS);
        if (settings != null) {
            setData(Registration.SIGNSETTINGS, settings);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        var data = getData(Registration.SIGNDATA);
        builder.set(Registration.ITEM_SIGNDATA, data);
        var settings = getData(Registration.SIGNSETTINGS);
        builder.set(Registration.ITEM_SIGNSETTINGS, settings);
    }

    @Override
    protected void loadInfo(CompoundTag tagCompound) {
        super.loadInfo(tagCompound);
        loadClientDataFromNBT(tagCompound);
    }

    @Override
    protected void saveInfo(CompoundTag tagCompound) {
        super.saveInfo(tagCompound);
        saveClientDataToNBT(tagCompound);
    }

    public int getSize() {
        return 1;
    }

    public SignSettings getSettings() {
        return getData(Registration.SIGNSETTINGS);
    }

    public void setIconIndex(int iconIndex) {
        SignSettings settings = getData(Registration.SIGNSETTINGS);
        settings = settings.setIconIndex(iconIndex);
        setData(Registration.SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public void setBackColor(Integer backColor) {
        SignSettings settings = getData(Registration.SIGNSETTINGS);
        settings = settings.setBackColor(backColor);
        setData(Registration.SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public void setTextColor(int textColor) {
        SignSettings settings = getData(Registration.SIGNSETTINGS);
        settings = settings.setTextColor(textColor);
        setData(Registration.SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public void setBright(boolean bright) {
        SignSettings settings = getData(Registration.SIGNSETTINGS);
        settings = settings.setBright(bright);
        setData(Registration.SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public void setLarge(boolean large) {
        SignSettings settings = getData(Registration.SIGNSETTINGS);
        settings = settings.setLarge(large);
        setData(Registration.SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public void setTransparent(boolean transparent) {
        SignSettings settings = getData(Registration.SIGNSETTINGS);
        settings = settings.setTransparent(transparent);
        setData(Registration.SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public void setTextureType(TextureType type) {
        SignSettings settings = getData(Registration.SIGNSETTINGS);
        settings = settings.setTextureType(type);
        setData(Registration.SIGNSETTINGS, settings);
        markDirtyClient();
    }
}