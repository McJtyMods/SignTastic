package com.mcjty.signtastic.modules.signs.blocks;

import com.mcjty.signtastic.modules.signs.SignSettings;
import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.TextureType;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSignTileEntity extends GenericTileEntity {

    private SignSettings settings = new SignSettings();

    private List<String> lines = new ArrayList<>();

    @Cap(type = CapType.CONTAINER)
    private final Lazy<MenuProvider> screenHandler = Lazy.of(() -> new DefaultContainerProvider<GenericContainer>("Builder")
            .containerSupplier(DefaultContainerProvider.empty(SignsModule.CONTAINER_SIGN, this))
    );

    public AbstractSignTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setLines(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        markDirtyClient();
    }

    public List<String> getLines() {
        return lines;
    }

    public abstract int getLinesSupported();

    public abstract float getRenderOffset();

    @Override
    public AABB getRenderBoundingBox() {
        int xCoord = getBlockPos().getX();
        int yCoord = getBlockPos().getY();
        int zCoord = getBlockPos().getZ();
        int size = 1;
        return new AABB(xCoord - size - 1, yCoord - size - 1, zCoord - size - 1, xCoord + size + 1, yCoord + size + 1, zCoord + size + 1); // TODO see if we can shrink this
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound) {
        if (tagCompound.contains("Info")) {
            CompoundTag info = tagCompound.getCompound("Info");
            settings.read(info);
            ListTag linesTag = info.getList("lines", Tag.TAG_STRING);
            lines.clear();
            for (Tag tag : linesTag) {
                lines.add(tag.getAsString());
            }
        }
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound) {
        CompoundTag info = getOrCreateInfo(tagCompound);
        settings.write(info);
        ListTag linesTag = new ListTag();
        for (String line : lines) {
            linesTag.add(StringTag.valueOf(line));
        }
        info.put("lines", linesTag);
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
        return settings;
    }

    public void setIconIndex(int iconIndex) {
        settings.setIconIndex(iconIndex);
        markDirtyClient();
    }

    public void setBackColor(Integer backColor) {
        settings.setBackColor(backColor);
        markDirtyClient();
    }

    public void setTextColor(int textColor) {
        settings.setTextColor(textColor);
        markDirtyClient();
    }

    public void setBright(boolean bright) {
        settings.setBright(bright);
        markDirtyClient();
    }

    public void setLarge(boolean large) {
        settings.setLarge(large);
        markDirtyClient();
    }

    public void setTransparent(boolean transparent) {
        settings.setTransparent(transparent);
        markDirtyClient();
    }

    public void setTextureType(TextureType type) {
        settings.setTextureType(type);
        markDirtyClient();
    }
}