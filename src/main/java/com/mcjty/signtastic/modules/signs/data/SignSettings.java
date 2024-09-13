package com.mcjty.signtastic.modules.signs.data;

import com.mcjty.signtastic.modules.signs.TextureType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.Optional;

public record SignSettings(boolean transparent, Integer backColor, int textColor, boolean bright, boolean large, int iconIndex, TextureType type) {

    public static final SignSettings EMPTY = new SignSettings();

    public static final Codec<SignSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("transparent").forGetter(SignSettings::isTransparent),
            Codec.INT.optionalFieldOf("backColor").forGetter(s -> Optional.ofNullable(s.getBackColor())),
            Codec.INT.fieldOf("textColor").forGetter(SignSettings::getTextColor),
            Codec.BOOL.fieldOf("bright").forGetter(SignSettings::isBright),
            Codec.BOOL.fieldOf("large").forGetter(SignSettings::isLarge),
            Codec.INT.fieldOf("image").forGetter(SignSettings::getIconIndex),
            TextureType.CODEC.fieldOf("ttype").forGetter(SignSettings::getTextureType)
    ).apply(instance, SignSettings::new));

    public static final StreamCodec<FriendlyByteBuf, SignSettings> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            ByteBufCodecs.BOOL, SignSettings::isTransparent,
            ByteBufCodecs.INT, SignSettings::getBackColor,
            ByteBufCodecs.INT, SignSettings::getTextColor,
            ByteBufCodecs.BOOL, SignSettings::isBright,
            ByteBufCodecs.BOOL, SignSettings::isLarge,
            ByteBufCodecs.INT, SignSettings::getIconIndex,
            TextureType.STREAM_CODEC, SignSettings::getTextureType,
            SignSettings::new);

    public SignSettings() {
        this(false, Optional.empty(), 0xffffff, false, false, 0, TextureType.OAK);
    }

    public SignSettings(boolean transparent, Optional<Integer> backColor, int textColor, boolean bright, boolean large, int iconIndex, TextureType type) {
        this(transparent, backColor.orElse(null), textColor, bright, large, iconIndex, type);
    }

    public boolean isTransparent() {
        return transparent;
    }

    public Integer getBackColor() {
        return backColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public boolean isBright() {
        return bright;
    }

    public TextureType getTextureType() {
        return type;
    }

    public boolean isLarge() {
        return large;
    }

    public int getIconIndex() {
        return iconIndex;
    }

    // Setters that return a new instance of SignSettings with the updated value
    public SignSettings setTransparent(boolean transparent) {
        return new SignSettings(transparent, backColor, textColor, bright, large, iconIndex, type);
    }

    public SignSettings setBackColor(Integer backColor) {
        return new SignSettings(transparent, backColor, textColor, bright, large, iconIndex, type);
    }

    public SignSettings setTextColor(int textColor) {
        return new SignSettings(transparent, backColor, textColor, bright, large, iconIndex, type);
    }

    public SignSettings setBright(boolean bright) {
        return new SignSettings(transparent, backColor, textColor, bright, large, iconIndex, type);
    }

    public SignSettings setLarge(boolean large) {
        return new SignSettings(transparent, backColor, textColor, bright, large, iconIndex, type);
    }

    public SignSettings setIconIndex(int iconIndex) {
        return new SignSettings(transparent, backColor, textColor, bright, large, iconIndex, type);
    }

    public SignSettings setTextureType(TextureType type) {
        return new SignSettings(transparent, backColor, textColor, bright, large, iconIndex, type);
    }
}
