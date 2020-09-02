package mekanism.api;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mekanism.api._helpers_pls_remove.FluidStack;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.inputs.chemical.ChemicalIngredientDeserializer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SerializerHelper {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static FloatingLong getFloatingLong(@Nonnull JsonObject json, @Nonnull String key) {
        if (!json.has(key)) {
            throw new JsonSyntaxException("Missing '" + key + "', expected to find an object");
        }
        JsonElement jsonElement = json.get(key);
        if (!jsonElement.isJsonPrimitive()) {
            throw new JsonSyntaxException("Expected '" + key + "' to be a json primitive representing a FloatingLong");
        }
        try {
            return FloatingLong.parseFloatingLong(jsonElement.getAsNumber().toString(), true);
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException("Expected '" + key + "' to be a valid FloatingLong (positive decimal number)");
        }
    }

    private static void validateKey(@Nonnull JsonObject json, @Nonnull String key) {
        if (!json.has(key)) {
            throw new JsonSyntaxException("Missing '" + key + "', expected to find an object");
        }
        if (!json.get(key).isJsonObject()) {
            throw new JsonSyntaxException("Expected '" + key + "' to be an object");
        }
    }

    public static ChemicalType getChemicalType(@Nonnull JsonObject json) {
        if (!json.has(JsonConstants.CHEMICAL_TYPE)) {
            throw new JsonSyntaxException("Missing '" + JsonConstants.CHEMICAL_TYPE + "', expected to find a string");
        }
        JsonElement element = json.get(JsonConstants.CHEMICAL_TYPE);
        if (!element.isJsonPrimitive()) {
            throw new JsonSyntaxException("Expected '" + JsonConstants.CHEMICAL_TYPE + "' to be a json primitive representing a string");
        }
        String name = element.getAsString();
        ChemicalType chemicalType = ChemicalType.fromString(name);
        if (chemicalType == null) {
            throw new JsonSyntaxException("Invalid chemical type '" + name + "'.");
        }
        return chemicalType;
    }

    public static ItemStack getItemStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        return ShapedRecipe.getItemStack(JsonHelper.getObject(json, key));
    }

    public static FluidStack getFluidStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        return deserializeFluid(JsonHelper.getObject(json, key));
    }

    public static ChemicalStack<?> getBoxedChemicalStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        JsonObject jsonObject = JsonHelper.getObject(json, key);
        ChemicalType chemicalType = getChemicalType(jsonObject);
        if (chemicalType == ChemicalType.GAS) {
            return deserializeGas(jsonObject);
        } else if (chemicalType == ChemicalType.INFUSION) {
            return deserializeInfuseType(jsonObject);
        } else if (chemicalType == ChemicalType.PIGMENT) {
            return deserializePigment(jsonObject);
        } else if (chemicalType == ChemicalType.SLURRY) {
            return deserializeSlurry(jsonObject);
        } else {
            throw new IllegalStateException("Unknown chemical type");
        }
    }

    public static GasStack getGasStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        return deserializeGas(JsonHelper.getObject(json, key));
    }

    public static InfusionStack getInfusionStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        return deserializeInfuseType(JsonHelper.getObject(json, key));
    }

    public static PigmentStack getPigmentStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        return deserializePigment(JsonHelper.getObject(json, key));
    }

    public static SlurryStack getSlurryStack(@Nonnull JsonObject json, @Nonnull String key) {
        validateKey(json, key);
        return deserializeSlurry(JsonHelper.getObject(json, key));
    }

    public static GasStack deserializeGas(@Nonnull JsonObject json) {
        return ChemicalIngredientDeserializer.GAS.deserializeStack(json);
    }

    public static FluidStack deserializeFluid(@Nonnull JsonObject json) {
        if (!json.has(JsonConstants.AMOUNT)) {
            throw new JsonSyntaxException("Expected to receive a amount that is greater than zero");
        }
        JsonElement count = json.get(JsonConstants.AMOUNT);
        if (!JsonHelper.isNumber(count)) {
            throw new JsonSyntaxException("Expected amount to be a number greater than zero.");
        }
        int amount = count.getAsJsonPrimitive().getAsInt();
        if (amount < 1) {
            throw new JsonSyntaxException("Expected amount to be greater than zero.");
        }
        Identifier resourceLocation = new Identifier(JsonHelper.getString(json, JsonConstants.FLUID));

        Fluid fluid = Registry.FLUID.get(resourceLocation);
        if (/* // Vanilla's is a DefaultedRegistry -- fluid == null ||*/ fluid == Fluids.EMPTY) {
            throw new JsonSyntaxException("Invalid fluid type '" + resourceLocation + "'");
        }
        CompoundTag nbt = null;
        if (json.has(JsonConstants.NBT)) {
            JsonElement jsonNBT = json.get(JsonConstants.NBT);
            try {
                if (jsonNBT.isJsonObject()) {
                    nbt = StringNbtReader.parse(GSON.toJson(jsonNBT));
                } else {
                    nbt = StringNbtReader.parse(JsonHelper.asString(jsonNBT, JsonConstants.NBT));
                }
            } catch (CommandSyntaxException e) {
                throw new JsonSyntaxException("Invalid NBT entry for fluid '" + resourceLocation + "'");
            }
        }
        return new FluidStack(fluid, amount, nbt);
    }

    public static InfusionStack deserializeInfuseType(@Nonnull JsonObject json) {
        return ChemicalIngredientDeserializer.INFUSION.deserializeStack(json);
    }

    public static PigmentStack deserializePigment(@Nonnull JsonObject json) {
        return ChemicalIngredientDeserializer.PIGMENT.deserializeStack(json);
    }

    public static SlurryStack deserializeSlurry(@Nonnull JsonObject json) {
        return ChemicalIngredientDeserializer.SLURRY.deserializeStack(json);
    }

    public static JsonElement serializeItemStack(@Nonnull ItemStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty(JsonConstants.ITEM, stack.getItem().getRegistryName().toString());
        if (stack.getCount() > 1) {
            json.addProperty(JsonConstants.COUNT, stack.getCount());
        }
        if (stack.hasTag()) {
            json.addProperty(JsonConstants.NBT, stack.getTag().toString());
        }
        return json;
    }

    public static JsonElement serializeFluidStack(@Nonnull FluidStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty(JsonConstants.FLUID, stack.getFluid().getRegistryName().toString());
        json.addProperty(JsonConstants.AMOUNT, stack.getAmount());
        if (stack.hasTag()) {
            json.addProperty(JsonConstants.NBT, stack.getTag().toString());
        }
        return json;
    }

    public static JsonElement serializeBoxedChemicalStack(@Nonnull BoxedChemicalStack stack) {
        JsonObject json;
        ChemicalType chemicalType = stack.getChemicalType();
        if (chemicalType == ChemicalType.GAS) {
            json = serializeGasStack((GasStack) stack.getChemicalStack());
        } else if (chemicalType == ChemicalType.INFUSION) {
            json = serializeInfusionStack((InfusionStack) stack.getChemicalStack());
        } else if (chemicalType == ChemicalType.PIGMENT) {
            json = serializePigmentStack((PigmentStack) stack.getChemicalStack());
        } else if (chemicalType == ChemicalType.SLURRY) {
            json = serializeSlurryStack((SlurryStack) stack.getChemicalStack());
        } else {
            throw new IllegalStateException("Unknown chemical type");
        }
        json.addProperty(JsonConstants.CHEMICAL_TYPE, chemicalType.getString());
        return json;
    }

    public static JsonObject serializeGasStack(@Nonnull GasStack stack) {
        return ChemicalIngredientDeserializer.GAS.serializeStack(stack);
    }

    public static JsonObject serializeInfusionStack(@Nonnull InfusionStack stack) {
        return ChemicalIngredientDeserializer.INFUSION.serializeStack(stack);
    }

    public static JsonObject serializePigmentStack(@Nonnull PigmentStack stack) {
        return ChemicalIngredientDeserializer.PIGMENT.serializeStack(stack);
    }

    public static JsonObject serializeSlurryStack(@Nonnull SlurryStack stack) {
        return ChemicalIngredientDeserializer.SLURRY.serializeStack(stack);
    }

    public static ChemicalIngredientDeserializer<?, ?, ?> getDeserializerForType(ChemicalType chemicalType) {
        if (chemicalType == ChemicalType.GAS) {
            return ChemicalIngredientDeserializer.GAS;
        } else if (chemicalType == ChemicalType.INFUSION) {
            return ChemicalIngredientDeserializer.INFUSION;
        } else if (chemicalType == ChemicalType.PIGMENT) {
            return ChemicalIngredientDeserializer.PIGMENT;
        } else if (chemicalType == ChemicalType.SLURRY) {
            return ChemicalIngredientDeserializer.SLURRY;
        } else {
            throw new IllegalStateException("Unknown Chemical Type");
        }
    }
}