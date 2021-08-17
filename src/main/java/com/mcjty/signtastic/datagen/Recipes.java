package com.mcjty.signtastic.datagen;

import com.mcjty.signtastic.modules.signs.SignsModule;
import mcjty.lib.datagen.BaseRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
//        add('F', VariousModule.MACHINE_FRAME.get());
//        add('A', VariousModule.MACHINE_BASE.get());
//        add('s', VariousModule.DIMENSIONALSHARD.get());
//        add('S', SpawnerModule.SYRINGE.get());
//        add('Z', Tags.Items.DYES_BLACK);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shaped(SignsModule.SQUARE_SIGN.get())
                        .define('S', ItemTags.SIGNS)
                        .unlockedBy("paper", has(Items.PAPER)),
                "ppp", "pSp", "ppp");
        build(consumer, ShapedRecipeBuilder.shaped(SignsModule.BLOCK_SIGN.get())
                        .define('S', ItemTags.SIGNS)
                        .define('P', ItemTags.PLANKS)
                        .unlockedBy("paper", has(Items.PAPER)),
                "pPp", "PSP", "pPp");
        build(consumer, ShapedRecipeBuilder.shaped(SignsModule.SLAB_SIGN.get())
                        .define('S', ItemTags.SIGNS)
                        .define('P', ItemTags.PLANKS)
                        .unlockedBy("paper", has(Items.PAPER)),
                "   ", "PSP", "pPp");
    }

}
