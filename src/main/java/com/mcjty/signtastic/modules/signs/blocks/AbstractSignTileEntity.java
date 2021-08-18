package com.mcjty.signtastic.modules.signs.blocks;

import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.TextureType;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.EmptyContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSignTileEntity extends GenericTileEntity {

    private boolean transparent = false;
    private Integer backColor = null;       // Color of background
    private int textColor = 0xffffff;       // Color of text
    private boolean bright = false;         // True if the screen contents is full bright
    private TextureType type = TextureType.OAK;
    private boolean large = false;

    private List<String> lines = new ArrayList<>();

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(EmptyContainerFactory::new);
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Builder")
            .containerSupplier((windowId, player) -> new GenericContainer(SignsModule.CONTAINER_SIGN.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), AbstractSignTileEntity.this))
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
    protected void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        if (tagCompound.contains("Info")) {
            CompoundNBT info = tagCompound.getCompound("Info");
            transparent = info.getBoolean("transparent");
            if (info.contains("backColor")) {
                backColor = info.getInt("backColor");
            } else {
                backColor = null;
            }
            textColor = info.getInt("textColor");
            bright = info.getBoolean("bright");
            large = info.getBoolean("large");
            type = TextureType.getByName(info.getString("ttype"));
            if (type == null) {
                type = TextureType.OAK;
            }
            ListNBT linesTag = info.getList("lines", Constants.NBT.TAG_STRING);
            lines.clear();
            for (INBT tag : linesTag) {
                lines.add(tag.getAsString());
            }
        }
    }

    @Override
    protected void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putBoolean("transparent", transparent);
        if (backColor != null) {
            info.putInt("backColor", backColor);
        }
        info.putInt("textColor", textColor);
        info.putBoolean("bright", bright);
        info.putBoolean("large", large);
        info.putString("ttype", type.name().toLowerCase());
        ListNBT linesTag = new ListNBT();
        for (String line : lines) {
            linesTag.add(StringNBT.valueOf(line));
        }
        info.put("lines", linesTag);
    }

    public int getSize() {
        return 1;
    }

    public Integer getBackColor() {
        return backColor;
    }

    public void setBackColor(Integer backColor) {
        this.backColor = backColor;
        markDirtyClient();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        markDirtyClient();
    }

    public boolean isBright() {
        return bright;
    }

    public void setBright(boolean bright) {
        this.bright = bright;
        markDirtyClient();
    }

    public boolean isLarge() {
        return large;
    }

    public void setLarge(boolean large) {
        this.large = large;
        markDirtyClient();
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
        markDirtyClient();
    }

    public boolean isTransparent() {
        return transparent;
    }

    public TextureType getTextureType() {
        return type;
    }

    public void setTextureType(TextureType type) {
        this.type = type;
        markDirtyClient();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
