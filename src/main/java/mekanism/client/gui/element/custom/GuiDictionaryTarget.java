package mekanism.client.gui.element.custom;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.GuiRelativeElement;
import mekanism.client.gui.item.GuiDictionary.DictionaryTagType;
import mekanism.client.jei.interfaces.IJEIGhostTarget;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.FluidType;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.base.TagCache;
import mekanism.common.util.StackUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidStack;

public class GuiDictionaryTarget extends GuiRelativeElement implements IJEIGhostTarget {

    private final Map<DictionaryTagType, List<String>> tags = new EnumMap<>(DictionaryTagType.class);
    private final Consumer<Set<DictionaryTagType>> tagSetter;
    @Nullable
    private Object target;

    public GuiDictionaryTarget(IGuiWrapper gui, int x, int y, Consumer<Set<DictionaryTagType>> tagSetter) {
        super(gui, x, y, 16, 16);
        this.tagSetter = tagSetter;
        playClickSound = true;
    }

    public boolean hasTarget() {
        return target != null;
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (target instanceof ItemStack) {
            guiObj.renderItem(matrix, (ItemStack) target, x, y);
        } else if (target instanceof FluidStack) {
            FluidStack stack = (FluidStack) this.target;
            MekanismRenderer.color(stack);
            drawTiledSprite(matrix, x, y, height, width, height, MekanismRenderer.getFluidTexture(stack, FluidType.STILL));
            MekanismRenderer.resetColor();
        } else if (target instanceof ChemicalStack) {
            ChemicalStack<?> stack = (ChemicalStack<?>) this.target;
            MekanismRenderer.color(stack);
            drawTiledSprite(matrix, x, y, height, width, height, MekanismRenderer.getChemicalTexture(stack.getType()));
            MekanismRenderer.resetColor();
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (Screen.hasShiftDown()) {
            setTargetSlot(null, false);
        } else {
            ItemStack stack = minecraft.player.inventory.getItemStack();
            if (!stack.isEmpty()) {
                setTargetSlot(stack, false);
            }
        }
    }

    public List<String> getTags(DictionaryTagType type) {
        return tags.getOrDefault(type, Collections.emptyList());
    }

    public void setTargetSlot(Object newTarget, boolean playSound) {
        //Clear cached tags
        tags.clear();
        if (newTarget == null) {
            target = null;
        } else if (newTarget instanceof ItemStack) {
            ItemStack itemStack = (ItemStack) newTarget;
            if (itemStack.isEmpty()) {
                target = null;
            } else {
                ItemStack stack = StackUtils.size(itemStack, 1);
                target = stack;
                Item item = stack.getItem();
                tags.put(DictionaryTagType.ITEM, TagCache.getItemTags(stack));
                if (item instanceof BlockItem) {
                    tags.put(DictionaryTagType.BLOCK, TagCache.getBlockTags(stack));
                }
                //TODO: Support other types of things?
            }
        } else if (newTarget instanceof FluidStack) {
            FluidStack fluidStack = (FluidStack) newTarget;
            if (fluidStack.isEmpty()) {
                target = null;
            } else {
                target = fluidStack.copy();
                tags.put(DictionaryTagType.FLUID, TagCache.getFluidTags((FluidStack) target));
            }
        } else if (newTarget instanceof ChemicalStack) {
            ChemicalStack<?> chemicalStack = (ChemicalStack<?>) newTarget;
            if (chemicalStack.isEmpty()) {
                target = null;
            } else {
                target = chemicalStack.copy();
                if (target instanceof GasStack) {
                    tags.put(DictionaryTagType.GAS, TagCache.getChemicalTags((GasStack) target));
                } else if (target instanceof InfusionStack) {
                    tags.put(DictionaryTagType.INFUSE_TYPE, TagCache.getChemicalTags((InfusionStack) target));
                } else if (target instanceof PigmentStack) {
                    tags.put(DictionaryTagType.PIGMENT, TagCache.getChemicalTags((PigmentStack) target));
                } else if (target instanceof SlurryStack) {
                    tags.put(DictionaryTagType.SLURRY, TagCache.getChemicalTags((SlurryStack) target));
                }
            }
        } else {
            Mekanism.logger.warn("Unable to get tags for unknown type: {}", newTarget);
            return;
        }
        //Update the list being viewed
        tagSetter.accept(tags.keySet());
        if (playSound) {
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }

    @Override
    public boolean hasPersistentData() {
        return true;
    }

    @Override
    public void syncFrom(GuiElement element) {
        super.syncFrom(element);
        GuiDictionaryTarget old = (GuiDictionaryTarget) element;
        target = old.target;
        tags.putAll(old.tags);
    }

    @Nullable
    @Override
    public IGhostIngredientConsumer getGhostHandler() {
        return new IGhostIngredientConsumer() {
            @Override
            public boolean supportsIngredient(Object ingredient) {
                if (ingredient instanceof ItemStack) {
                    return !((ItemStack) ingredient).isEmpty();
                } else if (ingredient instanceof FluidStack) {
                    return !((FluidStack) ingredient).isEmpty();
                } else if (ingredient instanceof ChemicalStack) {
                    return !((ChemicalStack<?>) ingredient).isEmpty();
                }
                return false;
            }

            @Override
            public void accept(Object ingredient) {
                setTargetSlot(ingredient, true);
            }
        };
    }
}