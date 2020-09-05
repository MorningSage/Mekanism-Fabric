package mekanism.api._helpers_pls_remove;

public enum FluidAction {
    EXECUTE, SIMULATE;

    public boolean execute() {
        return this == EXECUTE;
    }

    public boolean simulate() {
        return this == SIMULATE;
    }
}