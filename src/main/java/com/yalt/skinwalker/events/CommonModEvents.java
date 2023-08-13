package com.yalt.skinwalker.events;

import com.yalt.skinwalker.Skinwalker;
import com.yalt.skinwalker.entity.ModEntityTypes;
import com.yalt.skinwalker.entity.ethereal.Ethereal;
import com.yalt.skinwalker.entity.walker.SkinWalkerEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Skinwalker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {
    @SubscribeEvent
    public static void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.SKIN_WALKER.get(), SkinWalkerEntity.setAttributes());
        event.put(ModEntityTypes.ETHEREAL_ENTITY.get(), Ethereal.setAttributes());

    }

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(
                ModEntityTypes.SKIN_WALKER.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.WORLD_SURFACE,
                SkinWalkerEntity::canSpawn,
                SpawnPlacementRegisterEvent.Operation.OR
        );
        event.register(
                ModEntityTypes.ETHEREAL_ENTITY.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.WORLD_SURFACE,
                Ethereal::canSpawn,
                SpawnPlacementRegisterEvent.Operation.OR
        );
    }
}
