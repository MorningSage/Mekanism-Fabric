package mekanism.tools.client;

import mekanism.api.providers.IItemProvider;
import mekanism.client.ClientRegistrationUtil;
import mekanism.tools.common.MekanismTools;
import mekanism.tools.common.registries.ToolsItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.impl.client.texture.SpriteRegistryCallbackHolder;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

public class ToolsClientRegistration implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        addShieldPropertyOverrides(MekanismTools.rl("blocking"),
                (stack, world, entity) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F,
                ToolsItems.BRONZE_SHIELD, ToolsItems.LAPIS_LAZULI_SHIELD, ToolsItems.OSMIUM_SHIELD, ToolsItems.REFINED_GLOWSTONE_SHIELD,
                ToolsItems.REFINED_OBSIDIAN_SHIELD, ToolsItems.STEEL_SHIELD);

        SpriteRegistryCallbackHolder.EVENT_GLOBAL.register((atlasTexture, registry) -> {
            if (atlasTexture.getId().equals(SpriteAtlasTexture.PARTICLE_ATLAS_TEX)) {
                for (ShieldTextures textures : ShieldTextures.values()) {
                    registry.register(textures.getBase().getTextureId());
                }
            }
        });
    }

    private static void addShieldPropertyOverrides(Identifier override, ModelPredicateProvider propertyGetter, IItemProvider... shields) {
        for (IItemProvider shield : shields) {
            ClientRegistrationUtil.setPropertyOverride(shield, override, propertyGetter);
        }
    }
}