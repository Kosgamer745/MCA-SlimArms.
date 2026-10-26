package com.kos745.slimarms.client;

import net.conczin.mca.client.model.VillagerEntityModelMCA;
import net.conczin.mca.client.render.layer.ClothingLayer;
import net.conczin.mca.client.render.layer.SkinLayer;
import net.conczin.mca.entity.VillagerLike;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Mob;

import java.util.List;

public final class SlimLayers {
    private SlimLayers() {
    }

    /**
     * Replaces MCA's skin and clothing layers with a switch that uses a slim-arm
     * copy for female villagers and keeps the original (wide) layer otherwise.
     */
    public static <T extends Mob & VillagerLike<T>> void apply(
            LivingEntityRenderer<T, VillagerEntityModelMCA<T>> renderer,
            List<RenderLayer<T, VillagerEntityModelMCA<T>>> layers,
            String clothingVariant,
            float clothingDilation) {
        for (int i = 0; i < layers.size(); i++) {
            RenderLayer<T, VillagerEntityModelMCA<T>> layer = layers.get(i);
            if (layer instanceof SkinLayer<?, ?>) {
                RenderLayer<T, VillagerEntityModelMCA<T>> slim = new SkinLayer<T, VillagerEntityModelMCA<T>>(
                        renderer,
                        SlimLayers.<T>model(VillagerEntityModelMCA.bodyData(CubeDeformation.NONE, true)).hideWears());
                layers.set(i, new SlimSwitchLayer<T, VillagerEntityModelMCA<T>>(renderer, layer, slim, SlimSwitchLayer::isFemale));
            } else if (layer instanceof ClothingLayer<?, ?>) {
                RenderLayer<T, VillagerEntityModelMCA<T>> slim = new ClothingLayer<T, VillagerEntityModelMCA<T>>(
                        renderer,
                        SlimLayers.<T>model(VillagerEntityModelMCA.bodyData(new CubeDeformation(clothingDilation), true)),
                        clothingVariant);
                layers.set(i, new SlimSwitchLayer<T, VillagerEntityModelMCA<T>>(renderer, layer, slim, SlimSwitchLayer::isFemale));
            }
        }
    }

    private static <T extends Mob & VillagerLike<T>> VillagerEntityModelMCA<T> model(MeshDefinition data) {
        return new VillagerEntityModelMCA<>(LayerDefinition.create(data, 64, 64).bakeRoot());
    }
}
