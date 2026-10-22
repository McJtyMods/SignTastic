package com.mcjty.signtastic.modules.signs.blocks;

import com.mcjty.signtastic.modules.signs.data.SignData;
import com.mcjty.signtastic.modules.signs.data.SignSettings;
import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.TextureType;
import com.mojang.datafixers.util.Pair;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.*;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Function;

import static com.mcjty.signtastic.setup.Registration.*;

public abstract class AbstractSignTileEntity extends GenericTileEntity {

    @Cap(type = CapType.CONTAINER)
    private static final Function<AbstractSignTileEntity, MenuProvider> SCREEN_HANDLER =
            be -> new DefaultContainerProvider<GenericContainer>("Sign")
                    .containerSupplier(DefaultContainerProvider.empty(SignsModule.CONTAINER_SIGN, be));

    public AbstractSignTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
//        registerAttachment(SIGNDATA.get(), SignData.STREAM_CODEC);
//        registerAttachment(SIGNSETTINGS.get(), SignSettings.STREAM_CODEC);
    }

    public void setLines(List<String> lines) {
        SignData data = new SignData(lines);
        setData(SIGNDATA, data);
        markDirtyClient();
    }

    public List<String> getLines() {
        return getData(SIGNDATA).lines();
    }

    public void setSettings(SignSettings settings) {
        setData(SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public abstract int getLinesSupported();

    public abstract float getRenderOffset();

    @Override
    public void loadClientDataFromNBT(CompoundTag tag, HolderLookup.Provider provider) {
        setData(SIGNDATA, NbtOps.INSTANCE.withDecoder(SignData.CODEC).apply(tag.get("data")).result().map(Pair::getFirst).orElse(SignData.EMPTY));
        setData(SIGNSETTINGS, NbtOps.INSTANCE.withDecoder(SignSettings.CODEC).apply(tag.get("settings")).result().map(Pair::getFirst).orElse(SignSettings.EMPTY));
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tag, HolderLookup.Provider provider) {
        NbtOps.INSTANCE.withEncoder(SignData.CODEC).apply(getData(SIGNDATA)).result().ifPresent(nbt -> tag.put("data", nbt));
        NbtOps.INSTANCE.withEncoder(SignSettings.CODEC).apply(getData(SIGNSETTINGS)).result().ifPresent(nbt -> tag.put("settings", nbt));
    }

    @Override
    protected void applyImplicitComponents(net.minecraft.core.component.DataComponentGetter input) {
        super.applyImplicitComponents(input);
        var data = input.get(ITEM_SIGNDATA);
        if (data != null) {
            setData(SIGNDATA, data);
        }
        var settings = input.get(ITEM_SIGNSETTINGS);
        if (settings != null) {
            setData(SIGNSETTINGS, settings);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        var data = getData(SIGNDATA);
        builder.set(ITEM_SIGNDATA, data);
        var settings = getData(SIGNSETTINGS);
        builder.set(ITEM_SIGNSETTINGS, settings);
    }

//    @Override
//    protected void loadInfo(CompoundTag tagCompound) {
//        super.loadInfo(tagCompound);
//        loadClientDataFromNBT(tagCompound);
//    }

//    @Override
//    protected void saveInfo(CompoundTag tagCompound) {
//        super.saveInfo(tagCompound);
//        saveClientDataToNBT(tagCompound);
//    }

    public int getSize() {
        return 1;
    }

    public SignSettings getSettings() {
        return getData(SIGNSETTINGS);
    }

    public void setIconIndex(int iconIndex) {
        SignSettings settings = getData(SIGNSETTINGS);
        settings = settings.setIconIndex(iconIndex);
        setData(SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public void setBackColor(Integer backColor) {
        SignSettings settings = getData(SIGNSETTINGS);
        settings = settings.setBackColor(backColor);
        setData(SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public void setTextColor(int textColor) {
        SignSettings settings = getData(SIGNSETTINGS);
        settings = settings.setTextColor(textColor);
        setData(SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public void setBright(boolean bright) {
        SignSettings settings = getData(SIGNSETTINGS);
        settings = settings.setBright(bright);
        setData(SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public void setLarge(boolean large) {
        SignSettings settings = getData(SIGNSETTINGS);
        settings = settings.setLarge(large);
        setData(SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public void setTransparent(boolean transparent) {
        SignSettings settings = getData(SIGNSETTINGS);
        settings = settings.setTransparent(transparent);
        setData(SIGNSETTINGS, settings);
        markDirtyClient();
    }

    public void setTextureType(TextureType type) {
        SignSettings settings = getData(SIGNSETTINGS);
        settings = settings.setTextureType(type);
        setData(SIGNSETTINGS, settings);
        markDirtyClient();
    }
}