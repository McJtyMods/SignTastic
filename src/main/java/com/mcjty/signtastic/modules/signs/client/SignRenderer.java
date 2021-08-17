package com.mcjty.signtastic.modules.signs.client;

import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.TextureType;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignBlock;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
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
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nullable;

public class SignRenderer extends TileEntityRenderer<AbstractSignTileEntity> {

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

        // TileEntity can be null if this is used for an item renderer.
        matrixStack.translate(0.5F, 0.5F, 0.5F);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(yRotation));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(xRotation));
        matrixStack.translate(0.0F, 0.0F, -0.4375F);

        if (!tileEntity.isTransparent()) {
            renderScreenBoard(matrixStack, buffer, tileEntity.getTextureType(),
                    tileEntity.getRenderOffset(),
                    tileEntity.getSize(),
                    tileEntity.getBackColor(), packedLightIn, packedOverlayIn);
        }

        FontRenderer fontrenderer = Minecraft.getInstance().font;
        renderText(matrixStack, buffer, fontrenderer, tileEntity, tileEntity.getSize(), packedLightIn);

        matrixStack.popPose();
    }

    private static void renderText(MatrixStack matrixStack, IRenderTypeBuffer buffer, FontRenderer fontrenderer, AbstractSignTileEntity tileEntity, int size, int lightmapValue) {
        float factor = size + 1.0f;
        int currenty = 9;

        float f = 0.005F;

        matrixStack.pushPose();
        matrixStack.translate(-0.5F, 0.5F, 0.02F + tileEntity.getRenderOffset());
        matrixStack.scale(f * factor, -1.0f * f * factor, f);
        for (String line : tileEntity.getLines()) {
            fontrenderer.drawInBatch(line, 5, currenty, 0xff000000 | tileEntity.getTextColor(), false, matrixStack.last().pose(), buffer, false, 0,
                    tileEntity.isBright() ? 0xf000f0 : lightmapValue);
            currenty += 10;
        }
        matrixStack.popPose();
    }


    private static void renderScreenBoard(MatrixStack matrixStack, @Nullable IRenderTypeBuffer buffer,
                                          TextureType textureType, float renderOffset,
                                          int size, Integer color, int packedLight, int packedOverlay) {
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);

        Matrix4f matrix = matrixStack.last().pose();

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(
                textureType.getId()
        );


        IVertexBuilder builder = buffer.getBuffer(RenderType.solid());

        float dim;
        float s;
//        if (size == ScreenTileEntity.SIZE_HUGE) {
//            dim = 2.46f;
//            s = 2;
//        } else if (size == ScreenTileEntity.SIZE_LARGE) {
//            dim = 1.46f;
//            s = 1;
//        } else {
            dim = .46f;
            s = 0;
//        }


        float fr = 0.5f;
        float fg = 0.5f;
        float fb = 0.5f;

        float zback = .05f;
        float zfront = 0f - renderOffset;

        float ss = .5f;//50;//.5f;

        // BACK
        vt(builder, matrix, -ss, -ss, zback, sprite.getU0(), sprite.getV0(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, ss + s, -ss, zback, sprite.getU1(), sprite.getV0(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, ss + s, ss + s, zback, sprite.getU1(), sprite.getV1(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, -ss, ss + s, zback, sprite.getU0(), sprite.getV1(), packedLight, packedOverlay, fr, fg, fb, 1.0f);

        // FRONT
        vt(builder, matrix, -ss, ss + s, zfront, sprite.getU0(), sprite.getV0(), packedLight, packedOverlay, fr * .8f, fg * .8f, fb * .8f, 1.0f);
        vt(builder, matrix, ss + s, ss + s, zfront, sprite.getU1(), sprite.getV0(), packedLight, packedOverlay, fr * .8f, fg * .8f, fb * .8f, 1.0f);
        vt(builder, matrix, ss + s, -ss, zfront, sprite.getU1(), sprite.getV1(), packedLight, packedOverlay, fr * .8f, fg * .8f, fb * .8f, 1.0f);
        vt(builder, matrix, -ss, -ss, zfront, sprite.getU0(), sprite.getV1(), packedLight, packedOverlay, fr * .8f, fg * .8f, fb * .8f, 1.0f);

        // DOWN
        vt(builder, matrix, -ss, ss + s, zback, sprite.getU0(), sprite.getV0(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, ss + s, ss + s, zback, sprite.getU1(), sprite.getV0(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, ss + s, ss + s, zfront, sprite.getU1(), sprite.getV1(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, -ss, ss + s, zfront, sprite.getU0(), sprite.getV1(), packedLight, packedOverlay, fr, fg, fb, 1.0f);

        // UP
        vt(builder, matrix, -ss, -ss, zfront, sprite.getU0(), sprite.getV0(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, ss + s, -ss, zfront, sprite.getU1(), sprite.getV0(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, ss + s, -ss, zback, sprite.getU1(), sprite.getV1(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, -ss, -ss, zback, sprite.getU0(), sprite.getV1(), packedLight, packedOverlay, fr, fg, fb, 1.0f);

        // LEFT
        vt(builder, matrix, -ss, -ss, zfront, sprite.getU0(), sprite.getV0(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, -ss, -ss, zback, sprite.getU1(), sprite.getV0(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, -ss, ss + s, zback, sprite.getU1(), sprite.getV1(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, -ss, ss + s, zfront, sprite.getU0(), sprite.getV1(), packedLight, packedOverlay, fr, fg, fb, 1.0f);

        // RIGHT
        vt(builder, matrix, ss + s, ss + s, zfront, sprite.getU0(), sprite.getV0(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, ss + s, ss + s, zback, sprite.getU1(), sprite.getV0(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, ss + s, -ss, zback, sprite.getU1(), sprite.getV1(), packedLight, packedOverlay, fr, fg, fb, 1.0f);
        vt(builder, matrix, ss + s, -ss, zfront, sprite.getU0(), sprite.getV1(), packedLight, packedOverlay, fr, fg, fb, 1.0f);


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
                          int packedLight, int packedOverlay, float r, float g, float b, float a) {
        renderer.vertex(matrix, x, y, z).color(r, g, b, a).uv(u, v).uv2(packedLight).normal(1.0F, 0.0F, 0.0F).endVertex();
    }

}
