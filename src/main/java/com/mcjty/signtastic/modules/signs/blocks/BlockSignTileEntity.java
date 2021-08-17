package com.mcjty.signtastic.modules.signs.blocks;

import com.mcjty.signtastic.modules.signs.SignsModule;

public class BlockSignTileEntity extends AbstractSignTileEntity {

    public BlockSignTileEntity() {
        super(SignsModule.TYPE_BLOCK_SIGN.get());
    }

    @Override
    public int getLinesSupported() {
        return 8;
    }

    @Override
    public float getRenderOffset() {
        return .95f;
    }
}
