package mekanism.api.providers;

import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.IHasTranslationKey;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public interface IBaseProvider extends IHasTextComponent, IHasTranslationKey {

    Identifier getRegistryName();

    default String getName() {
        return getRegistryName().getPath();
    }

    @Override
    default Text getTextComponent() {
        return new TranslatableText(getTranslationKey());
    }
}