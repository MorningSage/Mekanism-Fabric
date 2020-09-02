package mekanism.api.chemical;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.chemical.attribute.ChemicalAttribute;
import net.minecraft.util.Identifier;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChemicalBuilder<CHEMICAL extends Chemical<CHEMICAL>, BUILDER extends ChemicalBuilder<CHEMICAL, BUILDER>> {

    private final Map<Class<? extends ChemicalAttribute>, ChemicalAttribute> attributeMap = new Object2ObjectOpenHashMap<>();
    private final Identifier texture;
    private int color = 0xFFFFFF;
    private boolean hidden;

    protected ChemicalBuilder(Identifier texture) {
        this.texture = texture;
    }

    public BUILDER with(ChemicalAttribute attribute) {
        attributeMap.put(attribute.getClass(), attribute);
        return getThis();
    }

    public Map<Class<? extends ChemicalAttribute>, ChemicalAttribute> getAttributeMap() {
        return attributeMap;
    }

    public Identifier getTexture() {
        return texture;
    }

    public BUILDER color(int color) {
        this.color = color;
        return getThis();
    }

    public BUILDER hidden() {
        this.hidden = true;
        return getThis();
    }

    public int getColor() {
        return color;
    }

    public boolean isHidden() {
        return hidden;
    }

    @SuppressWarnings("unchecked")
    public BUILDER getThis() {
        return (BUILDER) this;
    }
}