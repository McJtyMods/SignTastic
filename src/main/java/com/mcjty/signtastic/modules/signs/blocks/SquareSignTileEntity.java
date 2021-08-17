package com.mcjty.signtastic.modules.signs.blocks;

import com.mcjty.signtastic.modules.signs.SignsModule;

public class SquareSignTileEntity extends AbstractSignTileEntity {

    public SquareSignTileEntity() {
        super(SignsModule.TYPE_SQUARE_SIGN.get());
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
