package com.mcjty.signtastic.modules.signs.blocks;

import com.mcjty.signtastic.modules.signs.SignsModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SquareSignTileEntity extends AbstractSignTileEntity {

    public SquareSignTileEntity(BlockPos pos, BlockState state) {
        super(SignsModule.TYPE_SQUARE_SIGN.get(), pos, state);
    }

    @Override
    public int getLinesSupported() {
        return 8;
    }

    @Override
    public float getRenderOffset() {
        return 0f;
    }
}
