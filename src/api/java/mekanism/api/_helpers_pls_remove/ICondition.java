package mekanism.api._helpers_pls_remove;

import net.minecraft.util.Identifier;

public interface ICondition
{
    Identifier getID();

    boolean test();
}
