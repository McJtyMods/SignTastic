package com.mcjty.signtastic.modules.squares.blocks;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.squares.SquaresModule;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.EmptyContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.mcjty.signtastic.modules.squares.SquaresModule.TYPE_SQUARE_SIGN;

public class SquareSignTileEntity extends GenericTileEntity {

    private boolean transparent = false;
    private int color = 0;                  // Color of the screen.
    private boolean bright = false;         // True if the screen contents is full bright

    private List<String> lines = new ArrayList<>();

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(EmptyContainerFactory::new);
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Builder")
            .containerSupplier((windowId, player) -> new GenericContainer(SquaresModule.CONTAINER_SQUARE_SIGN.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), SquareSignTileEntity.this))
            .dataListener(new DataListener(this))
    );

    private static class DataListener implements IContainerDataListener {
        private final SquareSignTileEntity te;
        private List<String> lines = new ArrayList<>();

        public DataListener(SquareSignTileEntity te) {
            this.te = te;
        }

        @Override
        public ResourceLocation getId() {
            return new ResourceLocation(SignTastic.MODID, "signdata");
        }

        @Override
        public boolean isDirtyAndClear() {
            if (lines.size() != te.lines.size()) {
                lines = new ArrayList<>(te.lines);
                return true;
            }
            for (int i = 0 ; i < lines.size() ; i++) {
                if (!lines.get(i).equals(te.lines.get(i))) {
                    lines = new ArrayList<>(te.lines);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void toBytes(PacketBuffer buf) {
            buf.writeInt(lines.size());
            lines.forEach(buf::writeUtf);
        }

        @Override
        public void readBuf(PacketBuffer buf) {
            int s = buf.readInt();
            lines.clear();
            for (int i = 0 ; i < s ; i++) {
                lines.add(buf.readUtf(32767));
            }
        }
    }

    public SquareSignTileEntity() {
        super(TYPE_SQUARE_SIGN.get());
    }

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
            color = info.getInt("color");
            bright = info.getBoolean("bright");
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
        info.putInt("color", color);
        info.putBoolean("bright", bright);
        ListNBT linesTag = new ListNBT();
        for (String line : lines) {
            linesTag.add(StringNBT.valueOf(line));
        }
        info.put("lines", linesTag);
    }

    public int getSize() {
        return 1;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        markDirtyClient();
    }

    public boolean isBright() {
        return bright;
    }

    public void setBright(boolean bright) {
        this.bright = bright;
        markDirtyClient();
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
        markDirtyClient();
    }

    public boolean isTransparent() {
        return transparent;
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
