package com.mcjty.signtastic.modules.signs.client;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.data.SignSettings;
import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.TextureType;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignBlock;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import com.mcjty.signtastic.setup.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class SignRenderer implements BlockEntityRenderer<AbstractSignTileEntity> {

    public static final ResourceLocation SIGNS = ResourceLocation.fromNamespaceAndPath(SignTastic.MODID, "block/signs");

    public SignRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AbstractSignTileEntity tileEntity, float v, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn) {
        renderInternal(tileEntity, matrixStack, buffer, packedLightIn, packedOverlayIn);
    }

    // @todo 1.19.3
    public static void renderInternal(AbstractSignTileEntity tileEntity, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn, int packedOverlayIn) {
        Direction facing, horizontalFacing;
        BlockState state = Minecraft.getInstance().level.getBlockState(tileEntity.getBlockPos());
        if (state.getBlock() instanceof AbstractSignBlock) {
            facing = state.getValue(BlockStateProperties.FACING);
            horizontalFacing = state.getValue(AbstractSignBlock.HORIZ_FACING);
        } else {
            return;
        }

        matrixStack.pushPose();

        float yRotation = switch (horizontalFacing) {
            case NORTH -> -180.0F;
            case WEST -> -90.0F;
            case EAST -> 90.0F;
            default -> 0;
        };
        float xRotation = switch (facing) {
            case DOWN -> 90.0F;
            case UP -> -90.0F;
            default -> 0;
        };

        matrixStack.translate(0.5F, 0.5F, 0.5F);
        RenderHelper.rotateYP(matrixStack, yRotation);
        RenderHelper.rotateXP(matrixStack, xRotation);
        matrixStack.translate(0.0F, 0.0F, -0.4375F);

        SignSettings settings = tileEntity.getSettings();

        if (!settings.isTransparent()) {
            renderScreenBoard(matrixStack, buffer, settings.getTextureType(),
                    tileEntity.getRenderOffset(),
                    settings.getBackColor(), packedLightIn);
        }

        if (settings.getIconIndex() > 0) {
            renderImage(tileEntity, matrixStack, buffer, settings.isBright() ? 0xf000f0 : packedLightIn, settings.getIconIndex());
        }

        Font fontrenderer = Minecraft.getInstance().font;
        renderText(matrixStack, buffer, fontrenderer, tileEntity, settings.isLarge(), packedLightIn);

        matrixStack.popPose();
    }

    private static void renderImage(AbstractSignTileEntity tileEntity, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn,
                                    int idx) {
        int cols = Config.HORIZONTAL_ICONS.get();
        int rows = Config.VERTICAL_ICONS.get();
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(SIGNS);
        VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
        float dim = .46f;
        float offs = -0.01f - tileEntity.getRenderOffset();

        int sx = idx % cols;
        int sy = idx / rows;

        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        float u1 = sprite.getU1();

        float du = (u1-u0)/cols;
        float dv = (v1-v0)/rows;

        u0 += sx * du;
        u1 = u0 + du;
        v0 += sy * dv;
        v1 = v0 + dv;

        RenderHelper.vt(builder, matrixStack, -.46f, dim, offs, u0, v1, packedLightIn);
        RenderHelper.vt(builder, matrixStack, dim, dim, offs, u1, v1, packedLightIn);
        RenderHelper.vt(builder, matrixStack, dim, -.46f, offs, u1, v0, packedLightIn);
        RenderHelper.vt(builder, matrixStack, -.46f, -.46f, offs, u0, v0, packedLightIn);
        matrixStack.popPose();
    }

    private static void renderText(PoseStack matrixStack, MultiBufferSource buffer, Font fontrenderer, AbstractSignTileEntity tileEntity, boolean large, int lightmapValue) {
        SignSettings settings = tileEntity.getSettings();
        float factor = 2.0f + (large ? 2 : 0);
        int currenty = 9 - (large ? 4 : 0);

        float f = 0.005F;

        matrixStack.pushPose();
        matrixStack.translate(-0.5F, 0.5F, 0.02F + tileEntity.getRenderOffset());
        matrixStack.scale(f * factor, -1.0f * f * factor, f);
        int l = 0;
        int linesSupported = tileEntity.getLinesSupported();
        if (settings.isLarge()) {
            linesSupported /= 2;
        }
        for (String line : tileEntity.getLines()) {
            RenderHelper.renderText(fontrenderer, line, 5, currenty, 0xff000000 | settings.getTextColor(), matrixStack, buffer,
                    settings.isBright() ? 0xf000f0 : lightmapValue);
            currenty += 10;
            l++;
            if (l >= linesSupported) {
                break;
            }
        }
        matrixStack.popPose();
    }


    private static void renderScreenBoard(PoseStack matrixStack, @Nullable MultiBufferSource buffer,
                                          TextureType textureType, float renderOffset,
                                          Integer color, int packedLight) {
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(
                textureType.getId()
        );

        VertexConsumer builder = buffer.getBuffer(RenderType.solid());

        float dim = .46f;
        float zback = .05f;
        float zfront = -renderOffset;
        float ss = .5f;

        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();
        float su = (u1 - u0) * (.1f+renderOffset);
        float sv = (v1 - v0) * (.1f+renderOffset);

        // BACK
        RenderHelper.vt(builder, matrixStack, -ss, -ss, zback, u0, v0, packedLight);
        RenderHelper.vt(builder, matrixStack, ss, -ss, zback, u1, v0, packedLight);
        RenderHelper.vt(builder, matrixStack, ss, ss, zback, u1, v1, packedLight);
        RenderHelper.vt(builder, matrixStack, -ss, ss, zback, u0, v1, packedLight);

        // FRONT
        RenderHelper.vt(builder, matrixStack, -ss, ss, zfront, u0, v0, packedLight);
        RenderHelper.vt(builder, matrixStack, ss, ss, zfront, u1, v0, packedLight);
        RenderHelper.vt(builder, matrixStack, ss, -ss, zfront, u1, v1, packedLight);
        RenderHelper.vt(builder, matrixStack, -ss, -ss, zfront, u0, v1, packedLight);

        // DOWN
        RenderHelper.vt(builder, matrixStack, -ss, ss, zback, u0, v0, packedLight);
        RenderHelper.vt(builder, matrixStack, ss, ss, zback, u1, v0, packedLight);
        RenderHelper.vt(builder, matrixStack, ss, ss, zfront, u1, v0+sv, packedLight);
        RenderHelper.vt(builder, matrixStack, -ss, ss, zfront, u0, v0+sv, packedLight);

        // UP
        RenderHelper.vt(builder, matrixStack, -ss, -ss, zfront, u0, v0, packedLight);
        RenderHelper.vt(builder, matrixStack, ss, -ss, zfront, u1, v0, packedLight);
        RenderHelper.vt(builder, matrixStack, ss, -ss, zback, u1, v0+sv, packedLight);
        RenderHelper.vt(builder, matrixStack, -ss, -ss, zback, u0, v0+sv, packedLight);

        // LEFT
        RenderHelper.vt(builder, matrixStack, -ss, -ss, zfront, u0, v0, packedLight);
        RenderHelper.vt(builder, matrixStack, -ss, -ss, zback, u0+su, v0, packedLight);
        RenderHelper.vt(builder, matrixStack, -ss, ss, zback, u0+su, v1, packedLight);
        RenderHelper.vt(builder, matrixStack, -ss, ss, zfront, u0, v1, packedLight);

        // RIGHT
        RenderHelper.vt(builder, matrixStack, ss, ss, zfront, u0, v0, packedLight);
        RenderHelper.vt(builder, matrixStack, ss, ss, zback, u0+su, v0, packedLight);
        RenderHelper.vt(builder, matrixStack, ss, -ss, zback, u0+su, v1, packedLight);
        RenderHelper.vt(builder, matrixStack, ss, -ss, zfront, u0, v1, packedLight);


        if (color != null) {
            builder = buffer.getBuffer(CustomRenderTypes.QUADS_NOTEXTURE);
            float r = ((color & 16711680) >> 16) / 255.0F;
            float g = ((color & 65280) >> 8) / 255.0F;
            float b = ((color & 255)) / 255.0F;
            float offs = -0.01f - renderOffset;
            RenderHelper.vt(builder, matrixStack, -.46f, dim, offs, r, g, b, packedLight);
            RenderHelper.vt(builder, matrixStack, dim, dim, offs, r, g, b, packedLight);
            RenderHelper.vt(builder, matrixStack, dim, -.46f, offs, r, g, b, packedLight);
            RenderHelper.vt(builder, matrixStack, -.46f, -.46f, offs, r, g, b, packedLight);
        }
        matrixStack.popPose();
    }

    public static void register() {
        BlockEntityRenderers.register(SignsModule.TYPE_SQUARE_SIGN.get(), SignRenderer::new);
        BlockEntityRenderers.register(SignsModule.TYPE_BLOCK_SIGN.get(), SignRenderer::new);
        BlockEntityRenderers.register(SignsModule.TYPE_SLAB_SIGN.get(), SignRenderer::new);
    }

    @Override
    public AABB getRenderBoundingBox(AbstractSignTileEntity be) {
        int xCoord = be.getBlockPos().getX();
        int yCoord = be.getBlockPos().getY();
        int zCoord = be.getBlockPos().getZ();
        int size = 1;
        return new AABB(xCoord - size - 1, yCoord - size - 1, zCoord - size - 1, xCoord + size + 1, yCoord + size + 1, zCoord + size + 1); // TODO see if we can shrink this
    }

}
