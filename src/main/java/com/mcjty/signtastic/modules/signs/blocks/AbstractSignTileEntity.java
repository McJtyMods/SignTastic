package com.mcjty.signtastic.modules.signs.blocks;

import com.mcjty.signtastic.modules.signs.SignSettings;
import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.TextureType;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSignTileEntity extends GenericTileEntity {

    private SignSettings settings = new SignSettings();

    private List<String> lines = new ArrayList<>();

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Builder")
            .containerSupplier(DefaultContainerProvider.empty(SignsModule.CONTAINER_SIGN, this))
    );

    public AbstractSignTileEntity(TileEntityType<?> type) {
        super(type);
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
    public AxisAlignedBB getRenderBoundingBox() {
        int xCoord = getBlockPos().getX();
        int yCoord = getBlockPos().getY();
        int zCoord = getBlockPos().getZ();
        int size = 1;
        return new AxisAlignedBB(xCoord - size - 1, yCoord - size - 1, zCoord - size - 1, xCoord + size + 1, yCoord + size + 1, zCoord + size + 1); // TODO see if we can shrink this
    }

    @Override
    public void loadClientDataFromNBT(CompoundNBT tagCompound) {
        if (tagCompound.contains("Info")) {
            CompoundNBT info = tagCompound.getCompound("Info");
            settings.read(info);
            ListNBT linesTag = info.getList("lines", Constants.NBT.TAG_STRING);
            lines.clear();
            for (INBT tag : linesTag) {
                lines.add(tag.getAsString());
            }
        }
    }

    @Override
    public void saveClientDataToNBT(CompoundNBT tagCompound) {
        CompoundNBT info = getOrCreateInfo(tagCompound);
        settings.write(info);
        ListNBT linesTag = new ListNBT();
        for (String line : lines) {
            linesTag.add(StringNBT.valueOf(line));
        }
        info.put("lines", linesTag);
    }

    @Override
    protected void loadInfo(CompoundNBT tagCompound) {
        super.loadInfo(tagCompound);
        loadClientDataFromNBT(tagCompound);
    }

    @Override
    protected void saveInfo(CompoundNBT tagCompound) {
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