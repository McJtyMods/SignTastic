package com.mcjty.signtastic.modules.signs.blocks;

import mcjty.lib.builder.BlockBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class SquareSignBlock extends AbstractSignBlock implements EntityBlock {

    public SquareSignBlock() {
        super(new BlockBuilder()
                .properties(Properties.of(Material.STONE).strength(1.0F).sound(SoundType.STONE).noOcclusion())
//                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.signtastic.shiftmessage"))
                .infoShift(header()));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SquareSignTileEntity(pos, state);
    }

    public static final VoxelShape BLOCK_AABB = Shapes.box(0F, 0F, 0F, 1F, 1F, 1F);
    public static final VoxelShape NORTH_AABB = Shapes.box(.01F, .01F, 15F / 16f, .99F, .99F, 1F);
    public static final VoxelShape SOUTH_AABB = Shapes.box(.01F, .01F, 0F, .99F, .99F, 1F / 16f);
    public static final VoxelShape WEST_AABB = Shapes.box(15F / 16f, .01F, .01F, 1F, .99F, .99F);
    public static final VoxelShape EAST_AABB = Shapes.box(0F, .01F, .01F, 1F / 16f, .99F, .99F);
    public static final VoxelShape UP_AABB = Shapes.box(.01F, 0F, .01F, 1F, .99F / 16f, .99F);
    public static final VoxelShape DOWN_AABB = Shapes.box(.01F, 15F / 16f, .01F, .99F, 1F, .99F);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if (facing == Direction.NORTH) {
            return NORTH_AABB;
        } else if (facing == Direction.SOUTH) {
            return SOUTH_AABB;
        } else if (facing == Direction.WEST) {
            return WEST_AABB;
        } else if (facing == Direction.EAST) {
            return EAST_AABB;
        } else if (facing == Direction.UP) {
            return UP_AABB;
        } else if (facing == Direction.DOWN) {
            return DOWN_AABB;
        } else {
            return BLOCK_AABB;
        }
    }

}
