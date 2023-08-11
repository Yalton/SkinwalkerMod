package com.yalt.skinwalker.item;

import com.yalt.skinwalker.Skinwalker;
import com.yalt.skinwalker.entity.ModEntityTypes;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Skinwalker.MODID);
    public static final RegistryObject<Item> SKIN_WALKER_EGG = ITEMS.register("skin_walker_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.SKIN_WALKER, 0xD57E36, 0x1D0D00,
                    new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
