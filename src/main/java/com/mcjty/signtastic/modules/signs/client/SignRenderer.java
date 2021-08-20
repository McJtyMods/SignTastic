package com.mcjty.signtastic.modules.signs.client;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.SignSettings;
import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.TextureType;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignBlock;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import com.mcjty.signtastic.setup.Config;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.client.CustomRenderTypes;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nullable;

public class SignRenderer extends TileEntityRenderer<AbstractSignTileEntity> {

    public static final ResourceLocation SIGNS = new ResourceLocation(SignTastic.MODID, "varia/signs");

    public SignRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(AbstractSignTileEntity tileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn, int packedOverlayIn) {
        renderInternal(tileEntity, matrixStack, buffer, packedLightIn, packedOverlayIn);
    }

    public static void renderInternal(AbstractSignTileEntity tileEntity, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn, int packedOverlayIn) {
        float xRotation = 0.0F, yRotation = 0.0F;

        Direction facing = Direction.SOUTH, horizontalFacing = Direction.SOUTH;
        BlockState state = Minecraft.getInstance().level.getBlockState(tileEntity.getBlockPos());
        if (state.getBlock() instanceof AbstractSignBlock) {
            facing = state.getValue(BlockStateProperties.FACING);
            horizontalFacing = state.getValue(AbstractSignBlock.HORIZ_FACING);
        } else {
            return;
        }

        matrixStack.pushPose();

        switch (horizontalFacing) {
            case NORTH:
                yRotation = -180.0F;
                break;
            case WEST:
                yRotation = -90.0F;
                break;
            case EAST:
                yRotation = 90.0F;
        }
        switch (facing) {
            case DOWN:
                xRotation = 90.0F;
                break;
            case UP:
                xRotation = -90.0F;
        }

        matrixStack.translate(0.5F, 0.5F, 0.5F);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(yRotation));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(xRotation));
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

        FontRenderer fontrenderer = Minecraft.getInstance().font;
        renderText(matrixStack, buffer, fontrenderer, tileEntity, settings.isLarge(), packedLightIn);

        matrixStack.popPose();
    }

    private static void renderImage(AbstractSignTileEntity tileEntity, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn,
                                    int idx) {
        int cols = Config.HORIZONTAL_ICONS.get();
        int rows = Config.VERTICAL_ICONS.get();
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(SIGNS);
        IVertexBuilder builder = buffer.getBuffer(RenderType.cutout());
        Matrix4f matrix = matrixStack.last().pose();
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

        vt(builder, matrix, -.46f, dim, offs, u0, v1, packedLightIn);
        vt(builder, matrix, dim, dim, offs, u1, v1, packedLightIn);
        vt(builder, matrix, dim, -.46f, offs, u1, v0, packedLightIn);
        vt(builder, matrix, -.46f, -.46f, offs, u0, v0, packedLightIn);
        matrixStack.popPose();
    }

    private static void renderText(MatrixStack matrixStack, IRenderTypeBuffer buffer, FontRenderer fontrenderer, AbstractSignTileEntity tileEntity, boolean large, int lightmapValue) {
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
            fontrenderer.drawInBatch(line, 5, currenty, 0xff000000 | settings.getTextColor(), false, matrixStack.last().pose(), buffer, false, 0,
                    settings.isBright() ? 0xf000f0 : lightmapValue);
            currenty += 10;
            l++;
            if (l >= linesSupported) {
                break;
            }
        }
        matrixStack.popPose();
    }


    private static void renderScreenBoard(MatrixStack matrixStack, @Nullable IRenderTypeBuffer buffer,
                                          TextureType textureType, float renderOffset,
                                          Integer color, int packedLight) {
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);

        Matrix4f matrix = matrixStack.last().pose();

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(
                textureType.getId()
        );

        IVertexBuilder builder = buffer.getBuffer(RenderType.solid());

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
        vt(builder, matrix, -ss, -ss, zback, u0, v0, packedLight);
        vt(builder, matrix, ss, -ss, zback, u1, v0, packedLight);
        vt(builder, matrix, ss, ss, zback, u1, v1, packedLight);
        vt(builder, matrix, -ss, ss, zback, u0, v1, packedLight);

        // FRONT
        vt(builder, matrix, -ss, ss, zfront, u0, v0, packedLight);
        vt(builder, matrix, ss, ss, zfront, u1, v0, packedLight);
        vt(builder, matrix, ss, -ss, zfront, u1, v1, packedLight);
        vt(builder, matrix, -ss, -ss, zfront, u0, v1, packedLight);

        // DOWN
        vt(builder, matrix, -ss, ss, zback, u0, v0, packedLight);
        vt(builder, matrix, ss, ss, zback, u1, v0, packedLight);
        vt(builder, matrix, ss, ss, zfront, u1, v0+sv, packedLight);
        vt(builder, matrix, -ss, ss, zfront, u0, v0+sv, packedLight);

        // UP
        vt(builder, matrix, -ss, -ss, zfront, u0, v0, packedLight);
        vt(builder, matrix, ss, -ss, zfront, u1, v0, packedLight);
        vt(builder, matrix, ss, -ss, zback, u1, v0+sv, packedLight);
        vt(builder, matrix, -ss, -ss, zback, u0, v0+sv, packedLight);

        // LEFT
        vt(builder, matrix, -ss, -ss, zfront, u0, v0, packedLight);
        vt(builder, matrix, -ss, -ss, zback, u0+su, v0, packedLight);
        vt(builder, matrix, -ss, ss, zback, u0+su, v1, packedLight);
        vt(builder, matrix, -ss, ss, zfront, u0, v1, packedLight);

        // RIGHT
        vt(builder, matrix, ss, ss, zfront, u0, v0, packedLight);
        vt(builder, matrix, ss, ss, zback, u0+su, v0, packedLight);
        vt(builder, matrix, ss, -ss, zback, u0+su, v1, packedLight);
        vt(builder, matrix, ss, -ss, zfront, u0, v1, packedLight);


        if (color != null) {
            builder = buffer.getBuffer(CustomRenderTypes.QUADS_NOTEXTURE);
            float r = ((color & 16711680) >> 16) / 255.0F;
            float g = ((color & 65280) >> 8) / 255.0F;
            float b = ((color & 255)) / 255.0F;
            float offs = -0.01f - renderOffset;
            builder.vertex(matrix, -.46f, dim, offs).color(r, g, b, 1f).uv2(packedLight).endVertex();
            builder.vertex(matrix, dim, dim, offs).color(r, g, b, 1f).uv2(packedLight).endVertex();
            builder.vertex(matrix, dim, -.46f, offs).color(r, g, b, 1f).uv2(packedLight).endVertex();
            builder.vertex(matrix, -.46f, -.46f, offs).color(r, g, b, 1f).uv2(packedLight).endVertex();
        }
        matrixStack.popPose();
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(SignsModule.TYPE_SQUARE_SIGN.get(), SignRenderer::new);
        ClientRegistry.bindTileEntityRenderer(SignsModule.TYPE_BLOCK_SIGN.get(), SignRenderer::new);
        ClientRegistry.bindTileEntityRenderer(SignsModule.TYPE_SLAB_SIGN.get(), SignRenderer::new);
    }

    public static void vt(IVertexBuilder renderer, Matrix4f matrix, float x, float y, float z, float u, float v,
                          int packedLight) {
        renderer.vertex(matrix, x, y, z).color(1f, 1f, 1f, 1f).uv(u, v).uv2(packedLight).normal(1.0F, 0.0F, 0.0F).endVertex();
    }

}
