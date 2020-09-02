package mekanism.api._helpers_pls_remove;

import mekanism.api.math.FloatingLong;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

// Only a placeholder until Fabric actually decides to do something
// ToDo: Literally everything
public class FluidStack {
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0, new CompoundTag());

    private final Fluid fluid;
    private final int amount;
    private final CompoundTag nbt;

    public FluidStack(Fluid fluid, int amount, CompoundTag nbt) {
        this.fluid = fluid;
        this.amount = amount;
        this.nbt = nbt;
    }

    public FluidStack(FluidStack fluidStack, int amount) {
        this(fluidStack.fluid, amount, new CompoundTag());
    }

    public FluidStack(Fluid fluid, int amount) {
        this(fluid, amount, new CompoundTag());
    }

    public static FluidStack readFromPacket(PacketByteBuf buffer) {
        return EMPTY;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public int getAmount() {
        return amount;
    }

    public boolean hasTag() {
        return false;
    }

    public CompoundTag getTag() {
        return nbt;
    }

    public boolean isEmpty() {
        return amount <= 0;
    }

    public boolean isFluidEqual(FluidStack fluidStack) {
        return false;
    }

    public void grow(int amount) {

    }

    public String getTranslationKey() {
        return "None";
    }

    public void writeToPacket(PacketByteBuf buffer) {

    }
}
