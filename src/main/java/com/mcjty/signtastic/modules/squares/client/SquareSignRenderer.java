package com.mcjty.signtastic.modules.squares.client;

import com.mcjty.signtastic.modules.squares.SquaresModule;
import com.mcjty.signtastic.modules.squares.blocks.SquareSignBlock;
import com.mcjty.signtastic.modules.squares.blocks.SquareSignTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.client.CustomRenderTypes;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nullable;

public class SquareSignRenderer extends TileEntityRenderer<SquareSignTileEntity> {


    public SquareSignRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SquareSignTileEntity tileEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn, int packedOverlayIn) {
        renderInternal(tileEntity, matrixStack, buffer, packedLightIn, packedOverlayIn);
    }

    public static void renderInternal(SquareSignTileEntity tileEntity, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn, int packedOverlayIn) {
        float xRotation = 0.0F, yRotation = 0.0F;

        Direction facing = Direction.SOUTH, horizontalFacing = Direction.SOUTH;
        BlockState state = Minecraft.getInstance().level.getBlockState(tileEntity.getBlockPos());
        if (state.getBlock() instanceof SquareSignBlock) {
            facing = state.getValue(BlockStateProperties.FACING);
            horizontalFacing = state.getValue(SquareSignBlock.HORIZ_FACING);
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
            renderScreenBoard(matrixStack, buffer, tileEntity.getSize(), tileEntity.getColor(), packedLightIn, packedOverlayIn);
        }

        FontRenderer fontrenderer = Minecraft.getInstance().font;

        renderText(matrixStack, buffer, fontrenderer, tileEntity, tileEntity.getSize(), packedLightIn);

        matrixStack.popPose();
    }

    private static void renderText(MatrixStack matrixStack, IRenderTypeBuffer buffer, FontRenderer fontrenderer, SquareSignTileEntity tileEntity, int size, int lightmapValue) {
        float f3;
        float factor = size + 1.0f;
        int currenty = 7;

        float f = 0.0075F;
        float minf3 = -1.0f;
        MatrixStack stack = matrixStack;

    // FOR EACH LINE
        stack.pushPose();
        stack.translate(-0.5F, 0.5F, 0.03F);
        stack.scale(f * factor, minf3 * f * factor, f);
        renderScaled(fontrenderer, matrixStack, buffer, "Testing", 0, currenty, 0xffffff, lightmapValue);
        currenty += 10;
        stack.popPose();
    }

    private static void renderScaled(FontRenderer renderer, MatrixStack matrixStack, IRenderTypeBuffer buffer, String text, int x, int y, int color, int lightmapValue) {
        renderer.drawInBatch(text, x, y, 0xff000000 | color, false, matrixStack.last().pose(), buffer, false, 0, lightmapValue);
    }


    private static void renderScreenBoard(MatrixStack matrixStack, @Nullable IRenderTypeBuffer buffer, int size, int color, int packedLightIn, int packedOverlayIn) {
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);

        Matrix4f matrix = matrixStack.last().pose();

        IVertexBuilder builder = buffer.getBuffer(CustomRenderTypes.QUADS_NOTEXTURE);

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
        float zfront = 0f;//10f;//-.00f;

        float ss = .5f;//50;//.5f;

        // BACK
        builder.vertex(matrix, -ss, -ss, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, -ss, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, ss + s, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, ss + s, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();

        // FRONT
        builder.vertex(matrix, -ss, ss + s, zfront).color(fr * .8f, fg * .8f, fb * .8f, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, ss + s, zfront).color(fr * .8f, fg * .8f, fb * .8f, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, -ss, zfront).color(fr * .8f, fg * .8f, fb * .8f, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, -ss, zfront).color(fr * .8f, fg * .8f, fb * .8f, 1f).uv2(packedLightIn).endVertex();

        // DOWN
        builder.vertex(matrix, -ss, ss + s, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, ss + s, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, ss + s, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, ss + s, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();

        // UP
        builder.vertex(matrix, -ss, -ss, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, -ss, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, -ss, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, -ss, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();

        // LEFT
        builder.vertex(matrix, -ss, -ss, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, -ss, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, ss + s, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -ss, ss + s, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();

        // RIGHT
        builder.vertex(matrix, ss + s, ss + s, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, ss + s, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, -ss, zback).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, ss + s, -ss, zfront).color(fr, fg, fb, 1f).uv2(packedLightIn).endVertex();


        float r = ((color & 16711680) >> 16) / 255.0F;
        float g = ((color & 65280) >> 8) / 255.0F;
        float b = ((color & 255)) / 255.0F;
        builder.vertex(matrix, -.46f, dim, -0.01f).color(r, g, b, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, dim, dim, -0.01f).color(r, g, b, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, dim, -.46f, -0.01f).color(r, g, b, 1f).uv2(packedLightIn).endVertex();
        builder.vertex(matrix, -.46f, -.46f, -0.01f).color(r, g, b, 1f).uv2(packedLightIn).endVertex();

        matrixStack.popPose();
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(SquaresModule.TYPE_SQUARE_SIGN.get(), SquareSignRenderer::new);
    }
}
