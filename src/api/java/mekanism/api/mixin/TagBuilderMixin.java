package mekanism.api.mixin;

import com.google.gson.JsonObject;
import mekanism.api.mixin.accessors.TagBuilderAccessor;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Tag.Builder.class)
public final class TagBuilderMixin implements TagBuilderAccessor {
    private boolean replace = false;

    @Override
    public Tag.Builder replace(boolean replace) {
        this.replace = replace;
        return (Tag.Builder) (Object) this;
    }

    @Inject(
        at = @At("RETURN"),
        method = "toJson"
    )
    public void toJson(CallbackInfoReturnable<JsonObject> callbackInfo) {
        callbackInfo.getReturnValue().addProperty("replace", replace);
    }
}
