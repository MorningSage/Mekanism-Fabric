package mekanism.common.inventory.container.entity.robit;

import javax.annotation.Nonnull;
import mekanism.common.entity.EntityRobit;
import mekanism.common.inventory.container.entity.IEntityContainer;
import mekanism.common.inventory.container.entity.MekanismEntityContainer;
import mekanism.common.registries.MekanismContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class CraftingRobitContainer extends CraftingScreenHandler implements IEntityContainer<EntityRobit> {

    private final EntityRobit entity;

    public CraftingRobitContainer(int id, PlayerInventory inv, EntityRobit robit) {
        super(id, inv, robit.getWorldPosCallable());
        this.entity = robit;
    }

    public CraftingRobitContainer(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv, MekanismEntityContainer.getEntityFromBuf(buf, EntityRobit.class));
    }

    @Override
    public boolean canUse(@Nonnull PlayerEntity player) {
        return entity.isAlive();
    }

    @Override
    public EntityRobit getEntity() {
        return entity;
    }

    @Nonnull
    @Override
    public ScreenHandlerType<?> getType() {
        return MekanismContainerTypes.CRAFTING_ROBIT.getContainerType();
    }
}