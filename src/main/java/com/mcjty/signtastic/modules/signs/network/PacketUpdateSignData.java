package com.mcjty.signtastic.modules.signs.network;

import com.mcjty.signtastic.modules.signs.TextureType;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketUpdateSignData {

    private final BlockPos pos;
    private final Integer backColor;
    private final int textColor;
    private final boolean bright;
    private final boolean transparent;
    private final List<String> lines;
    private final TextureType textureType;

    public PacketUpdateSignData(BlockPos pos, List<String> lines, Integer backColor, int textColor, boolean bright, boolean transparent, TextureType textureType) {
        this.pos = pos;
        this.lines = lines;
        this.backColor = backColor;
        this.textColor = textColor;
        this.bright = bright;
        this.transparent = transparent;
        this.textureType = textureType;
    }

    public PacketUpdateSignData(PacketBuffer buf) {
        pos = buf.readBlockPos();
        if (buf.readBoolean()) {
            backColor = buf.readInt();
        } else {
            backColor = null;
        }
        textColor = buf.readInt();
        bright = buf.readBoolean();
        transparent = buf.readBoolean();
        textureType = TextureType.values()[buf.readInt()];
        int s = buf.readInt();
        lines = new ArrayList<>();
        for (int i = 0; i < s; i++) {
            lines.add(buf.readUtf(32767));
        }
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        if (backColor != null) {
            buf.writeBoolean(true);
            buf.writeInt(backColor);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeInt(textColor);
        buf.writeBoolean(bright);
        buf.writeBoolean(transparent);
        buf.writeInt(textureType.ordinal());
        buf.writeInt(lines.size());
        lines.forEach(buf::writeUtf);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            TileEntity te = ctx.getSender().getLevel().getBlockEntity(pos);
            if (te instanceof AbstractSignTileEntity) {
                AbstractSignTileEntity square = (AbstractSignTileEntity) te;
                square.setLines(lines);
                square.setBackColor(backColor);
                square.setTextColor(textColor);
                square.setBright(bright);
                square.setTransparent(transparent);
                square.setTextureType(textureType);
            }
        });
        return true;
    }
}
