package com.mcjty.signtastic.modules.signs.network;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.TextureType;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public record PacketUpdateSignData(BlockPos pos, List<String> lines, Integer backColor, Integer textColor,
                                   Boolean bright, Boolean transparent, Boolean large, Integer imageIndex,
                                   TextureType textureType) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(SignTastic.MODID, "update_sign_data");

    public static PacketUpdateSignData create(BlockPos pos, List<String> lines, Integer backColor, int textColor,
                                boolean bright, boolean transparent, boolean large, TextureType textureType,
                                int imageIndex) {
        return new PacketUpdateSignData(pos, lines, backColor, textColor, bright, transparent, large, imageIndex, textureType);
    }

    public static PacketUpdateSignData create(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Integer backColor;
        if (buf.readBoolean()) {
            backColor = buf.readInt();
        } else {
            backColor = null;
        }
        Integer textColor = buf.readInt();
        Boolean bright = buf.readBoolean();
        Boolean transparent = buf.readBoolean();
        Boolean large = buf.readBoolean();
        TextureType textureType = TextureType.values()[buf.readInt()];
        Integer imageIndex = buf.readInt();
        int s = buf.readInt();
        List<String>lines = new ArrayList<>();
        for (int i = 0; i < s; i++) {
            lines.add(buf.readUtf(32767));
        }
        return new PacketUpdateSignData(pos, lines, backColor, textColor, bright, transparent, large, imageIndex, textureType);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
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
        buf.writeBoolean(large);
        buf.writeInt(textureType.ordinal());
        buf.writeInt(imageIndex);
        buf.writeInt(lines.size());
        lines.forEach(buf::writeUtf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                BlockEntity te = player.level().getBlockEntity(pos);
                if (te instanceof AbstractSignTileEntity sign) {
                    sign.setLines(lines);
                    sign.setBackColor(backColor);
                    sign.setTextColor(textColor);
                    sign.setBright(bright);
                    sign.setLarge(large);
                    sign.setTransparent(transparent);
                    sign.setTextureType(textureType);
                    sign.setIconIndex(imageIndex);
                }
            });
        });
    }
}
