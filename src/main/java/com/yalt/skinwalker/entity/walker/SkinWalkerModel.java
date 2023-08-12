package com.yalt.skinwalker.entity.walker;

import com.mojang.math.Constants;
import com.yalt.skinwalker.entity.walker.SkinWalkerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SkinWalkerModel extends GeoModel<SkinWalkerEntity> {
    private Constants Mth;

    public SkinWalkerModel() {
    }

    public ResourceLocation getModelResource(SkinWalkerEntity object) {
        return new ResourceLocation("skinwalker", "geo/skinwalker.geo.json");
    }

    public ResourceLocation getTextureResource(SkinWalkerEntity object) {
        return new ResourceLocation("skinwalker", "textures/entity/skinwalker_texture.png");
    }

    public ResourceLocation getAnimationResource(SkinWalkerEntity animatable) {
        return new ResourceLocation("skinwalker", "animations/skinwalker.animation.json");
    }

    @Override
    public void setCustomAnimations(SkinWalkerEntity animatable, long instanceId, AnimationState<SkinWalkerEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}

