package com.mcjty.signtastic.modules.signs.blocks;

import mcjty.lib.builder.BlockBuilder;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class BlockSignBlock extends AbstractSignBlock {

    public BlockSignBlock(BlockEntityType.BlockEntitySupplier<BlockEntity> supplier) {
        super(new BlockBuilder()
                .properties(Properties.of(Material.STONE).strength(1.0F).sound(SoundType.STONE).noOcclusion())
//                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.signtastic.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(supplier));
    }
}
