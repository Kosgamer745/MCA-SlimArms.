package com.kos745.slimarms.mixin;

import com.kos745.slimarms.client.PlayerSlimLayers;
import com.kos745.slimarms.client.SlimSwitchLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.conczin.mca.client.render.layer.VillagerLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// A higher priority number makes this mixin's injections run after MCA's own (default 1000).
@Mixin(value = PlayerRenderer.class, priority = 2000)
public abstract class MixinPlayerRenderer extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    protected MixinPlayerRenderer(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    // Third person: runs after MCA has added its layers in its own constructor injection.
    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V", at = @At("TAIL"))
    private void mcaslimarms$init(EntityRendererProvider.Context ctx, boolean slim, CallbackInfo ci) {
        PlayerSlimLayers.apply(this, layers);
    }

    // First person: MCA renders the arm through this method, always with the wide layers.
    @Inject(method = "mca$renderArmLayer", at = @At("HEAD"), cancellable = true, remap = false)
    private void mcaslimarms$renderArmLayer(
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            AbstractClientPlayer player,
            VillagerLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> layer,
            boolean right,
            ModelPart sourceArm,
            CallbackInfo ci) {
        if (!SlimSwitchLayer.isFemale(player)) {
            return;
        }
        VillagerLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> slim = PlayerSlimLayers.slimFor(layer);
        if (slim != null) {
            PlayerSlimLayers.renderArm(matrices, vertexConsumers, light, player, slim, right, sourceArm);
            ci.cancel();
        }
    }
}
