package com.mcjty.signtastic.datagen;

import com.mcjty.signtastic.modules.signs.SignsModule;
import mcjty.lib.datagen.BaseRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shaped(SignsModule.SQUARE_SIGN.get())
                        .define('S', ItemTags.SIGNS)
                        .unlockedBy("paper", has(net.minecraft.world.item.Items.PAPER)),
                "ppp", "pSp", "ppp");
        build(consumer, ShapedRecipeBuilder.shaped(SignsModule.BLOCK_SIGN.get())
                        .define('S', ItemTags.SIGNS)
                        .define('P', ItemTags.PLANKS)
                        .unlockedBy("paper", has(net.minecraft.world.item.Items.PAPER)),
                "pPp", "PSP", "pPp");
        build(consumer, ShapedRecipeBuilder.shaped(SignsModule.SLAB_SIGN.get())
                        .define('S', ItemTags.SIGNS)
                        .define('P', ItemTags.PLANKS)
                        .unlockedBy("paper", has(net.minecraft.world.item.Items.PAPER)),
                "   ", "PSP", "pPp");
        build(consumer, ShapedRecipeBuilder.shaped(SignsModule.SIGN_CONFIGURATOR.get())
                        .define('S', SignsModule.SQUARE_SIGN.get())
                        .unlockedBy("iron", has(net.minecraft.world.item.Items.IRON_INGOT)),
                "S  ", " i ", "  i");
    }

}
