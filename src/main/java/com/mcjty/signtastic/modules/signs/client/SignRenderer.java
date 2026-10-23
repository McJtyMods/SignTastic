package com.mcjty.signtastic.modules.signs.client;

import com.mcjty.signtastic.SignTastic;
import com.mcjty.signtastic.modules.signs.SignsModule;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignBlock;
import com.mcjty.signtastic.modules.signs.blocks.AbstractSignTileEntity;
import com.mcjty.signtastic.modules.signs.data.SignSettings;
import com.mcjty.signtastic.setup.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class SignRenderer implements BlockEntityRenderer<AbstractSignTileEntity, SignRenderer.State> {
    private static final RenderPipeline UNLIT_ICON_PIPELINE =
            RenderPipeline.builder(RenderPipelines.ENTITY_EMISSIVE_SNIPPET)
                    .withLocation(Identifier.parse("signtastic:pipeline/unlit_icons"))
                    .withShaderDefine("NO_CARDINAL_LIGHTING")
                    .withBindGroupLayout(BindGroupLayouts.SAMPLER1)
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .build();
    private static final OitPipelineSet UNLIT_ICON_OIT_PIPELINES = OitPipelineSet.builder("signtastic_unlit_icons",
            RenderPipeline.builder(RenderPipelines.OIT_ENTITY_SNIPPET))
            .withAccumulateModifier(accumulate -> accumulate.withShaderDefine("EMISSIVE")
                    .withShaderDefine("NO_CARDINAL_LIGHTING")
                    .withBindGroupLayout(BindGroupLayouts.SAMPLER1))
            .build();
    public static final Identifier SIGNS = Identifier.fromNamespaceAndPath(SignTastic.MODID, "textures/block/signs.png");

    private static final RenderType UNLIT_ICONS =
            RenderType.create("signtastic_unlit_icons",
                    RenderSetup.builder(UNLIT_ICON_PIPELINE)
                            .setOitPipelines(UNLIT_ICON_OIT_PIPELINES)
                            .withTexture("Sampler0", SIGNS).useOverlay().sortOnUpload().createRenderSetup());

    public static class State extends BlockEntityRenderState {
        SignSettings settings = SignSettings.EMPTY;
        List<String> lines = List.of();
        Direction facing = Direction.NORTH;
        Direction horizontalFacing = Direction.NORTH;
        float offset;
        int linesSupported, columns, rows;
    }

    public SignRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(AbstractSignTileEntity sign, State state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(sign, state, partialTicks, cameraPosition, breakProgress);
        state.settings = sign.getSettings();
        state.lines = List.copyOf(sign.getLines());
        state.facing = sign.getBlockState().getValue(BlockStateProperties.FACING);
        state.horizontalFacing = sign.getBlockState().getValue(AbstractSignBlock.HORIZ_FACING);
        state.offset = sign.getRenderOffset();
        state.linesSupported = sign.getLinesSupported();
        state.columns = Config.HORIZONTAL_ICONS.get();
        state.rows = Config.VERTICAL_ICONS.get();
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        pose.pushPose();
        float yRotation = switch (state.horizontalFacing) {
            case NORTH -> -180;
            case WEST -> -90;
            case EAST -> 90;
            default -> 0;
        };
        float xRotation = switch (state.facing) {
            case DOWN -> 90;
            case UP -> -90;
            default -> 0;
        };
        pose.translate(.5F, .5F, .5F);
        RenderHelper.rotateYP(pose, yRotation);
        RenderHelper.rotateXP(pose, xRotation);
        pose.translate(0, 0, -.4375F);

        if (!state.settings.isTransparent()) renderBoard(state, pose, collector);
        if (state.settings.getIconIndex() > 0) renderIcon(state, pose, collector);
        renderText(state, pose, collector);
        pose.popPose();
    }

    private static void renderIcon(State state, PoseStack pose, SubmitNodeCollector collector) {
        int index = state.settings.getIconIndex();
        float u0 = (float) (index % state.columns) / state.columns;
        float v0 = (float) (index / state.columns) / state.rows;
        float u1 = u0 + 1F / state.columns;
        float v1 = v0 + 1F / state.rows;
        int light = state.settings.isBright() ? RenderHelper.MAX_BRIGHTNESS : state.lightCoords;
        var type = state.settings.isBright() ? UNLIT_ICONS : RenderTypes.entityCutout(SIGNS);
        pose.pushPose();
        pose.scale(1, -1, -1);
        collector.submitCustomGeometry(pose, type, (snapshot, vertices) ->
                quad(vertices, snapshot, -.46F, -.46F, .46F, .46F, -.01F - state.offset,
                        u0, v0, u1, v1, light));
        pose.popPose();
    }

    private static void renderText(State state, PoseStack pose, SubmitNodeCollector collector) {
        boolean large = state.settings.isLarge();
        float factor = large ? 4 : 2;
        int y = large ? 5 : 9;
        int limit = state.linesSupported / (large ? 2 : 1);
        int light = state.settings.isBright() ? RenderHelper.MAX_BRIGHTNESS : state.lightCoords;
        pose.pushPose();
        pose.translate(-.5F, .5F, .02F + state.offset);
        pose.scale(.005F * factor, -.005F * factor, .005F);
        for (int i = 0; i < Math.min(limit, state.lines.size()); i++) {
            RenderHelper.renderText(Minecraft.getInstance().font, state.lines.get(i), 5, y,
                    0xff000000 | state.settings.getTextColor(), pose, collector, light);
            y += 10;
        }
        pose.popPose();
    }

    private static void renderBoard(State state, PoseStack pose, SubmitNodeCollector collector) {
        Identifier texture = state.settings.getTextureType().getId();
        texture = Identifier.fromNamespaceAndPath(texture.getNamespace(), "textures/" + texture.getPath() + ".png");
        pose.pushPose();
        pose.scale(1, -1, -1);
        collector.submitCustomGeometry(pose, RenderTypes.entitySolid(texture), (snapshot, vertices) -> {
            float back = .05F, front = -state.offset;
            int light = state.lightCoords;
            // Preserve the sign's original board depth and texture coordinates.
            vertex(vertices, snapshot, -.5F, -.5F, back, 0, 0, light);
            vertex(vertices, snapshot, .5F, -.5F, back, 1, 0, light);
            vertex(vertices, snapshot, .5F, .5F, back, 1, 1, light);
            vertex(vertices, snapshot, -.5F, .5F, back, 0, 1, light);
            vertex(vertices, snapshot, -.5F, .5F, front, 0, 0, light);
            vertex(vertices, snapshot, .5F, .5F, front, 1, 0, light);
            vertex(vertices, snapshot, .5F, -.5F, front, 1, 1, light);
            vertex(vertices, snapshot, -.5F, -.5F, front, 0, 1, light);
            float uv = .1F + state.offset;
            vertex(vertices, snapshot, -.5F, .5F, back, 0, 0, light);
            vertex(vertices, snapshot, .5F, .5F, back, 1, 0, light);
            vertex(vertices, snapshot, .5F, .5F, front, 1, uv, light);
            vertex(vertices, snapshot, -.5F, .5F, front, 0, uv, light);
            vertex(vertices, snapshot, -.5F, -.5F, front, 0, 0, light);
            vertex(vertices, snapshot, .5F, -.5F, front, 1, 0, light);
            vertex(vertices, snapshot, .5F, -.5F, back, 1, uv, light);
            vertex(vertices, snapshot, -.5F, -.5F, back, 0, uv, light);
            vertex(vertices, snapshot, -.5F, -.5F, front, 0, 0, light);
            vertex(vertices, snapshot, -.5F, -.5F, back, uv, 0, light);
            vertex(vertices, snapshot, -.5F, .5F, back, uv, 1, light);
            vertex(vertices, snapshot, -.5F, .5F, front, 0, 1, light);
            vertex(vertices, snapshot, .5F, .5F, front, 0, 0, light);
            vertex(vertices, snapshot, .5F, .5F, back, uv, 0, light);
            vertex(vertices, snapshot, .5F, -.5F, back, uv, 1, light);
            vertex(vertices, snapshot, .5F, -.5F, front, 0, 1, light);
        });
        Integer color = state.settings.getBackColor();
        if (color != null) {
            collector.submitCustomGeometry(pose, CustomRenderTypes.QUADS_NOTEXTURE, (snapshot, vertices) -> {
                float z = -.01F - state.offset;
                int argb = 0xff000000 | color;
                vertices.addVertex(snapshot.pose(), -.46F, .46F, z).setColor(argb);
                vertices.addVertex(snapshot.pose(), .46F, .46F, z).setColor(argb);
                vertices.addVertex(snapshot.pose(), .46F, -.46F, z).setColor(argb);
                vertices.addVertex(snapshot.pose(), -.46F, -.46F, z).setColor(argb);
            });
        }
        pose.popPose();
    }

    private static void quad(VertexConsumer vertices, PoseStack.Pose pose, float x0, float y0, float x1, float y1,
                             float z, float u0, float v0, float u1, float v1, int light) {
        vertex(vertices, pose, x0, y1, z, u0, v1, light);
        vertex(vertices, pose, x1, y1, z, u1, v1, light);
        vertex(vertices, pose, x1, y0, z, u1, v0, light);
        vertex(vertices, pose, x0, y0, z, u0, v0, light);
    }

    private static void vertex(VertexConsumer vertices, PoseStack.Pose pose, float x, float y, float z, float u, float v, int light) {
        vertices.addVertex(pose.pose(), x, y, z).setColor(-1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 0, -1);
    }

    public static void register() {
        RenderPipelines.register(UNLIT_ICON_PIPELINE);
        RenderPipelines.register(UNLIT_ICON_OIT_PIPELINES);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(SignsModule.SQUARE_SIGN.be().get(), SignRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(SignsModule.BLOCK_SIGN.be().get(), SignRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(SignsModule.SLAB_SIGN.be().get(), SignRenderer::new);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        // A large sign can extend outside the block's own frustum bounds.
        return true;
    }
}
