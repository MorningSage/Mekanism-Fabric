package mekanism.api._helpers_pls_remove;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public interface IConditionSerializer<T extends ICondition>
{
    void write(JsonObject json, T value);

    T read(JsonObject json);

    Identifier getID();

    default JsonObject getJson(T value)
    {
        JsonObject json = new JsonObject();
        this.write(json, value);
        json.addProperty("type", value.getID().toString());
        return json;
    }
}