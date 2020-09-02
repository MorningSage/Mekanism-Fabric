package mekanism.api.chemical.infuse;

import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.MekanismAPI;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.providers.IInfuseTypeProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InfusionStack extends ChemicalStack<InfuseType> {

    public static final InfusionStack EMPTY = new InfusionStack(MekanismAPI.EMPTY_INFUSE_TYPE, 0);

    /**
     * Creates a new InfusionStack with a defined infusion type and quantity.
     *
     * @param infuseTypeProvider - provides the infusion type of the stack
     * @param amount             - amount of the infusion type to be referenced in this InfusionStack
     */
    public InfusionStack(IInfuseTypeProvider infuseTypeProvider, long amount) {
        super(infuseTypeProvider.getChemical(), amount);
    }

    public InfusionStack(InfusionStack stack, long amount) {
        this(stack.getType(), amount);
    }

    @Override
    protected IRegistryDelegate<InfuseType> getDelegate(InfuseType infuseType) {
        if (MekanismAPI.infuseTypeRegistry().getId(infuseType) == null) {
            MekanismAPI.logger.fatal("Failed attempt to create a InfusionStack for an unregistered InfuseType {} (type {})", infuseType.getRegistryName(),
                  infuseType.getClass().getName());
            throw new IllegalArgumentException("Cannot create a InfusionStack from an unregistered infusion type");
        }
        return infuseType.delegate;
    }

    @Override
    protected InfuseType getEmptyChemical() {
        return MekanismAPI.EMPTY_INFUSE_TYPE;
    }

    /**
     * Returns the InfusionStack stored in the defined tag compound, or null if it doesn't exist.
     *
     * @param nbtTags - tag compound to read from
     *
     * @return InfusionStack stored in the tag compound
     */
    public static InfusionStack readFromNBT(@Nullable CompoundTag nbtTags) {
        if (nbtTags == null || nbtTags.isEmpty()) {
            return EMPTY;
        }
        InfuseType type = InfuseType.readFromNBT(nbtTags);
        if (type.isEmptyType()) {
            return EMPTY;
        }
        long amount = nbtTags.getLong(NBTConstants.AMOUNT);
        if (amount <= 0) {
            return EMPTY;
        }
        return new InfusionStack(type, amount);
    }

    public static InfusionStack readFromPacket(PacketByteBuf buf) {
        InfuseType infuseType = MekanismAPI.infuseTypeRegistry().get(buf.readIdentifier());
        long amount = buf.readVarLong();
        if (infuseType == null || infuseType.isEmptyType()) {
            return EMPTY;
        }
        return new InfusionStack(infuseType, amount);
    }

    /**
     * Returns a copied form of this InfusionStack.
     *
     * @return copied InfusionStack
     */
    @Override
    public InfusionStack copy() {
        return new InfusionStack(this, getAmount());
    }

    /**
     * Default equality comparison for a InfusionStack. Same functionality as isTypeEqual().
     *
     * This is included for use in data structures.
     */
    @Override
    public final boolean equals(Object o) {
        return o instanceof InfusionStack && isTypeEqual((InfusionStack) o);
    }
}