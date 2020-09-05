package mekanism.tools.client.render.item;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;

import java.util.concurrent.Callable;

//This class is used to prevent class loading issues on the server without having to use OnlyIn hacks
public class ToolsISTERProvider {

    public static Callable<BuiltinModelItemRenderer> shield() {
        return RenderMekanismShieldItem::new;
    }
}