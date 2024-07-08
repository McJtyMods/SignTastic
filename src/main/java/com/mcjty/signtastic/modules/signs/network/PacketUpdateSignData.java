package com.mcjty.signtastic.modules.signs.network;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.TextureType;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import mcjty.lib.varia.CompositeStreamCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record PacketUpdateSignData(BlockPos pos, List<String> lines, Integer backColor, Integer textColor,
                                   Boolean bright, Boolean transparent, Boolean large, Integer imageIndex,
                                   TextureType textureType) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(SignTastic.MODID, "update_sign_data");
    public static final CustomPacketPayload.Type<PacketUpdateSignData> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketUpdateSignData> CODEC = CompositeStreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketUpdateSignData::pos,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), PacketUpdateSignData::lines,
            ByteBufCodecs.INT.apply(ByteBufCodecs::optional), s -> Optional.ofNullable(s.backColor),
            ByteBufCodecs.INT, PacketUpdateSignData::textColor,
            ByteBufCodecs.BOOL, PacketUpdateSignData::bright,
            ByteBufCodecs.BOOL, PacketUpdateSignData::transparent,
            ByteBufCodecs.BOOL, PacketUpdateSignData::large,
            ByteBufCodecs.INT, PacketUpdateSignData::imageIndex,
            TextureType.STREAM_CODEC, PacketUpdateSignData::textureType,
            PacketUpdateSignData::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public PacketUpdateSignData(BlockPos pos, List<String> lines, Optional<Integer> backColor, Integer textColor,
                                Boolean bright, Boolean transparent, Boolean large, Integer imageIndex,
                                TextureType textureType) {
        this(pos, lines, backColor.orElse(null), textColor, bright, transparent, large, imageIndex, textureType);
    }

    public static PacketUpdateSignData create(BlockPos pos, List<String> lines, Integer backColor, int textColor,
                                              boolean bright, boolean transparent, boolean large, TextureType textureType,
                                              int imageIndex) {
        return new PacketUpdateSignData(pos, lines, backColor, textColor, bright, transparent, large, imageIndex, textureType);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
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
    }
}
