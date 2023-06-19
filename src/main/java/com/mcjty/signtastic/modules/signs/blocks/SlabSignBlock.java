package com.mcjty.signtastic.modules.signs.blocks;

import mcjty.lib.builder.BlockBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class SlabSignBlock extends AbstractSignBlock {

    public SlabSignBlock(BlockEntityType.BlockEntitySupplier<BlockEntity> supplier) {
        super(new BlockBuilder()
                .properties(Properties.of().strength(1.0F).sound(SoundType.STONE).noOcclusion())
//                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.signtastic.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(supplier));
    }

    public static final VoxelShape BLOCK_AABB = Shapes.box(0F, 0F, 0F, 1F, 1F, 1F);
    public static final VoxelShape NORTH_AABB = Shapes.box(.01F, .01F, 8F / 16f, .99F, .99F, 1F);
    public static final VoxelShape SOUTH_AABB = Shapes.box(.01F, .01F, 0F, .99F, .99F, 8F / 16f);
    public static final VoxelShape WEST_AABB = Shapes.box(8F / 16f, .01F, .01F, 1F, .99F, .99F);
    public static final VoxelShape EAST_AABB = Shapes.box(0F, .01F, .01F, 8F / 16f, .99F, .99F);
    public static final VoxelShape UP_AABB = Shapes.box(.01F, 0F, .01F, 1F, 8F / 16f, .99F);
    public static final VoxelShape DOWN_AABB = Shapes.box(.01F, 8F / 16f, .01F, .99F, 1F, .99F);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return switch (facing) {
            case NORTH -> NORTH_AABB;
            case SOUTH -> SOUTH_AABB;
            case WEST -> WEST_AABB;
            case EAST -> EAST_AABB;
            case UP -> UP_AABB;
            case DOWN -> DOWN_AABB;
        };
    }

}
