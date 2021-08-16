package com.mcjty.signtastic.datagen;

import mcjty.lib.datagen.BaseRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;

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
//        build(consumer, ShapedRecipeBuilder.shaped(CrafterModule.CRAFTER1.get())
//                        .define('C', Blocks.CRAFTING_TABLE)
//                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
//                " T ", "CFC", " T ");
    }

}
