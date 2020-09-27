package mekanism.common.inventory.container;

import mekanism.api.text.ILangEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerProvider implements NamedScreenHandlerFactory {

    private final Text displayName;
    private final ScreenHandlerFactory provider;

    public ContainerProvider(ILangEntry translationHelper, ScreenHandlerFactory provider) {
        this(translationHelper.translate(), provider);
    }

    public ContainerProvider(Text displayName, ScreenHandlerFactory provider) {
        this.displayName = displayName;
        this.provider = provider;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, @Nonnull PlayerInventory inv, @Nonnull PlayerEntity player) {
        return provider.createMenu(i, inv, player);
    }

    @Nonnull
    @Override
    public Text getDisplayName() {
        return displayName;
    }
}