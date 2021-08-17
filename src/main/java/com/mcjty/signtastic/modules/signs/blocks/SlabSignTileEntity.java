package com.mcjty.signtastic.modules.signs.blocks;

import com.mcjty.signtastic.modules.signs.SignsModule;

public class SlabSignTileEntity extends AbstractSignTileEntity {

    public SlabSignTileEntity() {
        super(SignsModule.TYPE_SLAB_SIGN.get());
    }

    @Override
    public int getLinesSupported() {
        return 8;
    }

    @Override
    public float getRenderOffset() {
        return .45f;
    }
}
