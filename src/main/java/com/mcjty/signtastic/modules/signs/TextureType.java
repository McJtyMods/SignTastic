package com.mcjty.signtastic.modules.signs;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.HashMap;
import java.util.Map;

public enum TextureType implements StringRepresentable {

    OAK("minecraft", "oak_planks"),
    ACACIA("minecraft", "acacia_planks"),
    SPRUCE("minecraft", "spruce_planks"),
    BIRCH("minecraft", "birch_planks"),
    JUNGLE("minecraft", "jungle_planks"),
    DARK_OAK("minecraft", "dark_oak_planks"),
    STONE("minecraft", "stone"),
    ANDESITE("minecraft", "andesite"),
    GRANITE("minecraft", "granite"),
    DIORITE("minecraft", "diorite"),
    WHITE("minecraft", "white_concrete"),
    BLACK("minecraft", "black_concrete"),
    IRON("minecraft", "iron_block"),
    ;

    private final ResourceLocation id;

    public static final Codec<TextureType> CODEC = StringRepresentable.fromEnum(TextureType::values);
    public static final StreamCodec<FriendlyByteBuf, TextureType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(TextureType.class);

    private static final Map<String, TextureType> MAP = new HashMap<>();
    static {
        for (TextureType type : TextureType.values()) {
            MAP.put(type.name().toLowerCase(), type);
        }
    }

    TextureType(String modid, String txt) {
        this.id = ResourceLocation.fromNamespaceAndPath(modid, "block/" + txt);
    }

    public ResourceLocation getId() {
        return id;
    }

    public static TextureType getByName(String name) {
        return MAP.get(name.toLowerCase());
    }


    @Override
    public String getSerializedName() {
        return this.name();
    }
}
