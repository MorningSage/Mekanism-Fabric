package mekanism.api.mixin.accessors;

import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemTags.class)
public interface ItemTagsAccessor {
    @Invoker("register")
    static Tag.Identified<Item> register(String id) {
        throw new Error("Failed to register item tag");
    }
}
