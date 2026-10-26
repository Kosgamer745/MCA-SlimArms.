package com.kos745.slimarms.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.conczin.mca.client.model.CommonVillagerModel;
import net.conczin.mca.entity.ai.relationship.Gender;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

public class SlimSwitchLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final RenderLayer<T, M> wide;
    private final RenderLayer<T, M> slim;
    private final Predicate<T> useSlim;

    public SlimSwitchLayer(RenderLayerParent<T, M> parent, RenderLayer<T, M> wide, RenderLayer<T, M> slim, Predicate<T> useSlim) {
        super(parent);
        this.wide = wide;
        this.slim = slim;
        this.useSlim = useSlim;
    }

    public static boolean isFemale(Entity entity) {
        return CommonVillagerModel.getVillager(entity).getGenetics().getGender() == Gender.FEMALE;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        (useSlim.test(entity) ? slim : wide).render(pose, buffer, light, entity, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
    }
}
