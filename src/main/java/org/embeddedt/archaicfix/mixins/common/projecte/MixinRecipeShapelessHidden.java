package org.embeddedt.archaicfix.mixins.common.projecte;

import com.google.common.collect.ImmutableSet;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.embeddedt.archaicfix.ArchaicLogger;
import org.embeddedt.archaicfix.FixHelper;
import org.embeddedt.archaicfix.ducks.IAcceleratedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Set;

@Mixin(RecipeShapelessHidden.class)
public class MixinRecipeShapelessHidden implements IAcceleratedRecipe {
    @Shadow(remap = false)
    private ArrayList<Object> input;
    private Set<Item> allPossibleInputs = null;

    @SuppressWarnings("unchecked")
    private void genMatchCache() {
        allPossibleInputs = null;
        ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
        for(Object o : input) {
            if(o instanceof ItemStack) {
                if(((ItemStack)o).getItem() != null)
                    builder.add(((ItemStack) o).getItem());
            } else if(o instanceof ArrayList) {
                for(ItemStack stack : ((ArrayList<ItemStack>)o)) {
                    if(stack.getItem() != null)
                        builder.add(stack.getItem());
                }
            } else if(o != null) {
                ArchaicLogger.LOGGER.warn("Couldn't optimize input value: " + o);
                return;
            }
        }
        allPossibleInputs = builder.build();
        FixHelper.recipesHoldingPotentialItems.add(this);
    }

    @Override
    public Set<Item> getPotentialItems() {
        if(allPossibleInputs == null)
            genMatchCache();
        return allPossibleInputs;
    }

    @Override
    public void invalidatePotentialItems() {
        allPossibleInputs = null;
    }
}
