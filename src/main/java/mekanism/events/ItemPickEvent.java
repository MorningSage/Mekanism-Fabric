package mekanism.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;

public interface ItemPickEvent {
    Event<ItemPickEvent> EVENT = EventFactory.createArrayBacked(ItemPickEvent.class,
        listeners -> (defaultStack, hitResult) -> {
            for (ItemPickEvent stationsUpdating : listeners) {
                stationsUpdating.onItemPicked(defaultStack, hitResult);
            }
        }
    );

    void onItemPicked(ItemStack defaultStack, HitResult hitResult);
}
