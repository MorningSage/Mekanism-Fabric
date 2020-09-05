package mekanism.api._helpers_pls_remove;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CraftingHelper {
    private static final Map<Identifier, IConditionSerializer<?>> conditions = new HashMap<>();

    public static <T extends ICondition> JsonObject serialize(T condition) {
        @SuppressWarnings("unchecked")
        IConditionSerializer<T> serializer = (IConditionSerializer<T>) conditions.get(condition.getID());
        if (serializer == null)
            throw new JsonSyntaxException("Unknown condition type: " + condition.getID().toString());
        return serializer.getJson(condition);
    }
}
