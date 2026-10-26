package com.kos745.slimarms.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.conczin.mca.client.model.PlayerEntityExtendedModel;
import net.conczin.mca.client.model.VillagerEntityModelMCA;
import net.conczin.mca.client.render.layer.ClothingLayer;
import net.conczin.mca.client.render.layer.SkinLayer;
import net.conczin.mca.client.render.layer.VillagerLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class PlayerSlimLayers {
    /** Original (wide) MCA layer -> its slim-arm copy. */
    private static final Map<VillagerLayer<?, ?>, VillagerLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> SLIM_BY_WIDE = new WeakHashMap<>();

    private PlayerSlimLayers() {
    }

    /** Third person: wraps MCA's skin and clothing layers so female characters use the slim copy. */
    public static void apply(
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer,
            List<RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> layers) {
        for (int i = 0; i < layers.size(); i++) {
            RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> layer = layers.get(i);
            VillagerLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> slim = null;

            if (layer instanceof SkinLayer<?, ?>) {
                slim = new SkinLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(
                        renderer, model(VillagerEntityModelMCA.bodyData(CubeDeformation.NONE, true)));
            } else if (layer instanceof ClothingLayer<?, ?>) {
                slim = new ClothingLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(
                        renderer, model(VillagerEntityModelMCA.bodyData(new CubeDeformation(0.0625F), true)), "normal");
            }

            if (slim != null) {
                SLIM_BY_WIDE.put((VillagerLayer<?, ?>) layer, slim);
                layers.set(i, new SlimSwitchLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(
                        renderer, layer, slim, SlimSwitchLayer::isFemale));
            }
        }
    }

    public static VillagerLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> slimFor(VillagerLayer<?, ?> wide) {
        return SLIM_BY_WIDE.get(wide);
    }

    /** First person: same as MCA's own arm rendering, but with the given layer. */
    public static void renderArm(
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            AbstractClientPlayer player,
            VillagerLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> layer,
            boolean right,
            ModelPart sourceArm) {
        ResourceLocation skin = layer.getSkin(player);
        if (skin == null || !layer.canUse(skin)) {
            return;
        }

        PlayerModel<AbstractClientPlayer> visibleModel = layer.model;
        ModelPart arm = right ? visibleModel.rightArm : visibleModel.leftArm;
        ModelPart sleeve = right ? visibleModel.rightSleeve : visibleModel.leftSleeve;
        arm.copyFrom(sourceArm);
        sleeve.copyFrom(arm);

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(skin));
        int color = layer.getColor(player, 0.0f);
        arm.render(matrices, buffer, light, OverlayTexture.NO_OVERLAY, color);
        sleeve.render(matrices, buffer, light, OverlayTexture.NO_OVERLAY, color);
    }

    private static PlayerEntityExtendedModel<AbstractClientPlayer> model(MeshDefinition data) {
        return new PlayerEntityExtendedModel<>(LayerDefinition.create(data, 64, 64).bakeRoot());
    }
}
