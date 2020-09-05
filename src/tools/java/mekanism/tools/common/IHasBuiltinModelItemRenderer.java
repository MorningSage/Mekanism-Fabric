package mekanism.tools.common;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface IHasBuiltinModelItemRenderer<T> {
    Supplier<Callable<BuiltinModelItemRenderer>> getBuiltinModelItemRenderer();

    T setBuiltinModelItemRenderer(Supplier<Callable<BuiltinModelItemRenderer>> builtinModelItemRenderer);
}
