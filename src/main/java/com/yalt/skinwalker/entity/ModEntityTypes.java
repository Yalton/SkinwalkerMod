package com.yalt.skinwalker.entity;

import com.yalt.skinwalker.Skinwalker;
import com.yalt.skinwalker.entity.ethereal.Ethereal;
import com.yalt.skinwalker.entity.walker.SkinWalkerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Skinwalker.MODID);


    public static final RegistryObject<EntityType<SkinWalkerEntity>> SKIN_WALKER =
            ENTITY_TYPES.register("skin_walker",
                    () -> EntityType.Builder.of(SkinWalkerEntity::new, MobCategory.MONSTER)
                            .sized(1.5f, 1.75f)
                            .build(new ResourceLocation(Skinwalker.MODID, "skinwalker").toString()));


    public static final RegistryObject<EntityType<Ethereal>> ETHEREAL_ENTITY =
            ENTITY_TYPES.register("ethereal_entity",
                    () -> EntityType.Builder.of(Ethereal::new, MobCategory.MONSTER)
                            .sized(1.5f, 1.75f)
                            .build(new ResourceLocation(Skinwalker.MODID, "ethereal_entity").toString()));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
