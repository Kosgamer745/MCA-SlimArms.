package com.kos745.slimarms.mixin;

import com.kos745.slimarms.client.SlimLayers;
import net.conczin.mca.client.model.VillagerEntityModelMCA;
import net.conczin.mca.client.render.VillagerLikeEntityMCARenderer;
import net.conczin.mca.client.render.ZombieVillagerEntityMCARenderer;
import net.conczin.mca.entity.ZombieVillagerEntityMCA;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieVillagerEntityMCARenderer.class)
public abstract class MixinZombieVillagerEntityMCARenderer extends VillagerLikeEntityMCARenderer<ZombieVillagerEntityMCA> {
    protected MixinZombieVillagerEntityMCARenderer(EntityRendererProvider.Context ctx, VillagerEntityModelMCA<ZombieVillagerEntityMCA> model) {
        super(ctx, model);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mcaslimarms$init(EntityRendererProvider.Context ctx, CallbackInfo ci) {
        SlimLayers.apply(this, layers, "zombie", 0.075F);
    }
}
