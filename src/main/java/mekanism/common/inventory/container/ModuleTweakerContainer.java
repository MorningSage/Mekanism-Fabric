package mekanism.common.inventory.container;

import javax.annotation.Nonnull;
import mekanism.common.inventory.container.slot.ArmorSlot;
import mekanism.common.inventory.container.slot.HotBarSlot;
import mekanism.common.inventory.container.slot.OffhandSlot;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.util.EnumUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class ModuleTweakerContainer extends MekanismContainer {

    public ModuleTweakerContainer(int id, PlayerInventory inv) {
        super(MekanismContainerTypes.MODULE_TWEAKER, id, inv);
        addSlotsAndOpen();
    }

    public ModuleTweakerContainer(int id, PlayerInventory inv, PacketBuffer buf) {
        this(id, inv);
    }

    @Override
    protected void addInventorySlots(@Nonnull PlayerInventory inv) {
        for (int index = 0; index < inv.armorInventory.size(); index++) {
            EquipmentSlotType slotType = EnumUtils.EQUIPMENT_SLOT_TYPES[2 + inv.armorInventory.size() - index - 1];
            addSlot(new ArmorSlot(inv, 36 + slotType.ordinal() - 2, 8, 8 + index * 18, slotType) {
                @Override
                public boolean canTakeStack(@Nonnull PlayerEntity player) {
                    return false;
                }

                @Override
                public boolean isItemValid(@Nonnull ItemStack stack) {
                    return false;
                }
            });
        }
        for (int slotY = 0; slotY < 9; slotY++) {
            addSlot(new HotBarSlot(inv, slotY, 43 + slotY * 18, 161) {
                @Override
                public boolean canTakeStack(@Nonnull PlayerEntity player) {
                    return false;
                }

                @Override
                public boolean isItemValid(@Nonnull ItemStack stack) {
                    return false;
                }
            });
        }
        addSlot(new OffhandSlot(inv, 40, 8, 16 + 18 * 4) {
            @Override
            public boolean canTakeStack(@Nonnull PlayerEntity player) {
                return false;
            }

            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                return false;
            }
        });
    }
}
