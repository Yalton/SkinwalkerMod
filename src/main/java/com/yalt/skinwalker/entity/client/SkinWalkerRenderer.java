package com.yalt.skinwalker.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yalt.skinwalker.entity.custom.SkinWalkerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.HashMap;
import java.util.Map;

public class SkinWalkerRenderer extends GeoEntityRenderer<SkinWalkerEntity> {
//    private GeoRenderLayer eyesRenderLayer;

    private final Map<EntityType<?>, EntityRenderer<?>> transformationRenderers = new HashMap<>();


    public SkinWalkerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SkinWalkerModel());
        this.shadowRadius = 0.5F;

        // Populate transformationRenderers with renderers for each passive mob
//        for (EntityType<?> entityType : SkinWalkerEntity.TRANSFORMABLE_MOBS) {
//            transformationRenderers.put(entityType, renderManager.getResourceManager(entityType));
//        }
    }

    public ResourceLocation getTextureLocation(SkinWalkerEntity entity) {
        if (entity.getCurrentTransformation() != null) {
            // Return texture based on current transformation
        }
        return new ResourceLocation("skinwalker", "textures/entity/skinwalker_texture.png");
    }

    public void render(SkinWalkerEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        EntityType<?> transformation = entity.getCurrentTransformation();
//        if (transformation != null) {
//            // Render using the renderer for the current transformation
//            EntityRenderer<?> renderer = transformationRenderers.get(transformation);
//            renderer.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
//        } else {
            // Regular rendering
            poseStack.scale(2F, 3F, 3F);
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
//        }
    }
}
