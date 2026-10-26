package com.kos745.slimarms.mixin;

import com.kos745.slimarms.client.SlimLayers;
import net.conczin.mca.client.model.VillagerEntityModelMCA;
import net.conczin.mca.client.render.VillagerEntityMCARenderer;
import net.conczin.mca.client.render.VillagerLikeEntityMCARenderer;
import net.conczin.mca.entity.VillagerEntityMCA;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntityMCARenderer.class)
public abstract class MixinVillagerEntityMCARenderer extends VillagerLikeEntityMCARenderer<VillagerEntityMCA> {
    protected MixinVillagerEntityMCARenderer(EntityRendererProvider.Context ctx, VillagerEntityModelMCA<VillagerEntityMCA> model) {
        super(ctx, model);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mcaslimarms$init(EntityRendererProvider.Context ctx, CallbackInfo ci) {
        SlimLayers.apply(this, layers, "normal", 0.0625F);
    }
}
