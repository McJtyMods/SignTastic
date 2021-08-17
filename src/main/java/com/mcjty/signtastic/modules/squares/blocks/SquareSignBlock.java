package com.mcjty.signtastic.modules.squares.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static net.minecraft.state.properties.BlockStateProperties.FACING;

public class SquareSignBlock extends BaseBlock {

    public static final DirectionProperty HORIZ_FACING = DirectionProperty.create("horizfacing", Direction.Plane.HORIZONTAL);

    public SquareSignBlock(Supplier<TileEntity> supplier) {
        super(new BlockBuilder()
//                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.signtastic.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(supplier));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context).setValue(HORIZ_FACING, context.getPlayer().getDirection().getOpposite());
    }

    private static long lastTime = 0;

    public ActionResultType activate(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        return use(state, world, pos, player, hand, result);
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation rot) {
        // Doesn't make sense to rotate a potentially 3x3 screen,
        // and is incompatible with our special wrench actions.
        return state;
    }


    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZ_FACING);
    }

    @Override
    protected boolean openGui(World world, int x, int y, int z, PlayerEntity player) {
        ItemStack itemStack = player.getItemInHand(Hand.MAIN_HAND);
        if (!itemStack.isEmpty() && itemStack.getItem() == Items.BLACK_DYE) {   // @Todo 1.14, use tags to get all dyes
            int damage = itemStack.getDamageValue(); // @todo 1.14 don't use damage!
            if (damage < 0) {
                damage = 0;
            } else if (damage > 15) {
                damage = 15;
            }
            DyeColor color = DyeColor.byId(damage);
            TileEntity te = world.getBlockEntity(new BlockPos(x, y, z));
            if (te instanceof SquareSignTileEntity) {
                SquareSignTileEntity squareSignTileEntity = (SquareSignTileEntity) te;
                squareSignTileEntity.setColor(color.getMaterialColor().col); // @todo 1.14
            }
            return true;
        }
        return openGui(world, x, y, z, player);
    }

    public static final VoxelShape BLOCK_AABB = VoxelShapes.box(0F, 0F, 0F, 1F, 1F, 1F);
    public static final VoxelShape NORTH_AABB = VoxelShapes.box(.01F, .01F, 15F / 16f, .99F, .99F, 1F);
    public static final VoxelShape SOUTH_AABB = VoxelShapes.box(.01F, .01F, 0F, .99F, .99F, 1F / 16f);
    public static final VoxelShape WEST_AABB = VoxelShapes.box(15F / 16f, .01F, .01F, 1F, .99F, .99F);
    public static final VoxelShape EAST_AABB = VoxelShapes.box(0F, .01F, .01F, 1F / 16f, .99F, .99F);
    public static final VoxelShape UP_AABB = VoxelShapes.box(.01F, 0F, .01F, 1F, .99F / 16f, .99F);
    public static final VoxelShape DOWN_AABB = VoxelShapes.box(.01F, 15F / 16f, .01F, .99F, 1F, .99F);

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
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

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
