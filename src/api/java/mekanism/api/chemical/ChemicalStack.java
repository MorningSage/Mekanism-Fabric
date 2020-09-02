package mekanism.api.chemical;

import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.attribute.ChemicalAttribute;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.IHasTranslationKey;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ChemicalStack<CHEMICAL extends Chemical<CHEMICAL>> implements IHasTextComponent, IHasTranslationKey {

    private boolean isEmpty;
    private long amount;
    @Nonnull
    private final IRegistryDelegate<CHEMICAL> chemicalDelegate;

    protected ChemicalStack(CHEMICAL chemical, long amount) {
        this.chemicalDelegate = getDelegate(chemical);
        this.amount = amount;
        updateEmpty();
    }

    /**
     * Used for checking the chemical is valid and registered.
     */
    protected abstract IRegistryDelegate<CHEMICAL> getDelegate(CHEMICAL chemical);

    protected abstract CHEMICAL getEmptyChemical();

    public abstract ChemicalStack<CHEMICAL> copy();

    public final CHEMICAL getType() {
        return isEmpty ? getEmptyChemical() : getRaw();
    }

    /**
     * Whether or not this ChemicalStack's chemical type is equal to the other defined ChemicalStack.
     *
     * @param stack - ChemicalStack to check
     *
     * @return if the ChemicalStacks contain the same chemical type
     */
    public boolean isTypeEqual(ChemicalStack<CHEMICAL> stack) {
        return isTypeEqual(stack.getType());
    }

    public boolean isTypeEqual(CHEMICAL chemical) {
        return getType() == chemical;
    }

    /**
     * Helper to retrieve the registry name of the stored chemical. This is equivalent to calling {@code getType().getRegistryName()}
     *
     * @return The registry name of the stored chemical.
     */
    public Identifier getTypeRegistryName() {
        return getType().getRegistryName();
    }

    /**
     * Helper to get the tint of the stored chemical. This is equivalent to calling {@code getType().getTint()}
     *
     * @return The tint of the stored chemical.
     *
     * @apiNote Does not have any special handling for when the stack is empty.
     */
    public int getChemicalTint() {
        return getType().getTint();
    }

    public final CHEMICAL getRaw() {
        return chemicalDelegate.get();
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    protected void updateEmpty() {
        isEmpty = getRaw().isEmptyType() || amount <= 0;
    }

    public long getAmount() {
        return isEmpty ? 0 : amount;
    }

    public void setAmount(long amount) {
        if (getRaw().isEmptyType()) {
            throw new IllegalStateException("Can't modify the empty stack.");
        }
        this.amount = amount;
        updateEmpty();
    }

    /**
     * Grows this stack's amount by the given amount.
     *
     * @param amount The amount to grow this stack by.
     *
     * @apiNote Negative values are valid and will instead shrink the stack.
     * @implNote No checks are made to ensure that the long does not overflow.
     */
    public void grow(long amount) {
        setAmount(this.amount + amount);
    }

    /**
     * Shrinks this stack's amount by the given amount.
     *
     * @param amount The amount to shrink this stack by.
     *
     * @apiNote Negative values are valid and will instead grow the stack.
     * @implNote No checks are made to ensure that the long does not underflow.
     */
    public void shrink(long amount) {
        setAmount(this.amount - amount);
    }

    /**
     * Whether this stack's chemical has an attribute of a certain type.
     *
     * @param type attribute type to check
     *
     * @return if this chemical has the attribute
     */
    public boolean has(Class<? extends ChemicalAttribute> type) {
        return getType().has(type);
    }

    /**
     * Gets the attribute instance of a certain type, or null if it doesn't exist.
     *
     * @param type attribute type to get
     *
     * @return attribute instance
     */
    @Nullable
    public <T extends ChemicalAttribute> T get(Class<T> type) {
        return getType().get(type);
    }

    /**
     * Gets all attribute instances associated with this chemical's type.
     *
     * @return collection of attribute instances
     */
    public Collection<ChemicalAttribute> getAttributes() {
        return getType().getAttributes();
    }

    /**
     * Gets all attribute types associated with this chemical's type.
     *
     * @return collection of attribute types
     */
    public Collection<Class<? extends ChemicalAttribute>> getAttributeTypes() {
        return getType().getAttributeTypes();
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + getType().hashCode();
        code = 31 * code + Long.hashCode(getAmount());
        return code;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public String toString() {
        return "[" + getType() + ", " + amount + "]";
    }

    @Override
    public Text getTextComponent() {
        //Wrapper to get display name of the chemical type easier
        return getType().getTextComponent();
    }

    @Override
    public String getTranslationKey() {
        //Wrapper to get translation key of the chemical type easier
        return getType().getTranslationKey();
    }

    /**
     * Determines if the Chemicals are equal and this stack is larger.
     *
     * @return true if this ChemicalStack contains the other ChemicalStack (same chemical and >= amount)
     */
    public boolean contains(ChemicalStack<CHEMICAL> other) {
        return isTypeEqual(other) && amount >= other.amount;
    }

    /**
     * Determines if the chemicals and amounts are all equal.
     *
     * @param other - the ChemicalStack for comparison
     *
     * @return true if the two ChemicalStacks are exactly the same
     */
    public boolean isStackIdentical(ChemicalStack<CHEMICAL> other) {
        return isTypeEqual(other) && amount == other.amount;
    }

    /**
     * Writes this ChemicalStack to a defined tag compound.
     *
     * @param nbtTags - tag compound to write to
     *
     * @return tag compound with this GasStack's data
     */
    public CompoundTag write(CompoundTag nbtTags) {
        getType().write(nbtTags);
        nbtTags.putLong(NBTConstants.AMOUNT, getAmount());
        return nbtTags;
    }

    public void writeToPacket(PacketByteBuf buf) {
        buf.writeRegistryId(getType());
        buf.writeVarLong(getAmount());
    }
}