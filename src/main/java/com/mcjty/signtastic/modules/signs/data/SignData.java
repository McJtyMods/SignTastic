package com.mcjty.signtastic.modules.signs.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record SignData(List<String> lines) {

    public static final SignData EMPTY = new SignData(new ArrayList<>());

    public static final Codec<SignData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(Codec.STRING).fieldOf("lines").forGetter(SignData::lines)
    ).apply(instance, SignData::new));

    public static final StreamCodec<ByteBuf, SignData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), SignData::lines,
            SignData::new);

    public SignData() {
        this(new ArrayList<>());
    }
}
