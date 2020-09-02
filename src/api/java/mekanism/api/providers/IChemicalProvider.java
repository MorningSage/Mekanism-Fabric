package mekanism.api.providers;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;

public interface IChemicalProvider<CHEMICAL extends Chemical<CHEMICAL>> extends IBaseProvider {

    @Nonnull
    CHEMICAL getChemical();

    @Nonnull
    ChemicalStack<CHEMICAL> getStack(long size);

    @Override
    default Identifier getRegistryName() {
        return getChemical().getRegistryName();
    }

    @Override
    default Text getTextComponent() {
        return getChemical().getTextComponent();
    }

    @Override
    default String getTranslationKey() {
        return getChemical().getTranslationKey();
    }
}