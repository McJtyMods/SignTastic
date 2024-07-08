package com.mcjty.signtastic.modules.signs.data;

import com.mcjty.signtastic.modules.signs.TextureType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class SignSettings {

    private boolean transparent = false;
    private Integer backColor = null;       // Color of background
    private int textColor = 0xffffff;       // Color of text
    private boolean bright = false;         // True if the screen contents is full bright
    private TextureType type = TextureType.OAK;
    private boolean large = false;
    private int iconIndex = 0;

    public static final Codec<SignSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("transparent").forGetter(SignSettings::isTransparent),
            Codec.INT.fieldOf("backColor").forGetter(SignSettings::getBackColor),
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
    }

    public SignSettings(boolean transparent, Integer backColor, int textColor, boolean bright, boolean large, int iconIndex, TextureType type) {
        this.transparent = transparent;
        this.backColor = backColor;
        this.textColor = textColor;
        this.bright = bright;
        this.large = large;
        this.iconIndex = iconIndex;
        this.type = type;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public Integer getBackColor() {
        return backColor;
    }

    public void setBackColor(Integer backColor) {
        this.backColor = backColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public boolean isBright() {
        return bright;
    }

    public void setBright(boolean bright) {
        this.bright = bright;
    }

    public TextureType getTextureType() {
        return type;
    }

    public void setTextureType(TextureType type) {
        this.type = type;
    }

    public boolean isLarge() {
        return large;
    }

    public void setLarge(boolean large) {
        this.large = large;
    }

    public int getIconIndex() {
        return iconIndex;
    }

    public void setIconIndex(int iconIndex) {
        this.iconIndex = iconIndex;
    }
}
