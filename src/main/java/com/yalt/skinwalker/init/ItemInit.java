package com.yalt.skinwalker.init;

import com.yalt.skinwalker.Skinwalker;
import com.yalt.skinwalker.entity.ModEntityTypes;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Skinwalker.MODID);


    public static final RegistryObject<Item> SKIN_WALKER_SPAWN_EGG = ITEMS.register("skin_walker_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.SKIN_WALKER, 0xD57E36, 0x1D0D00,
                    new Item.Properties()));

//    public static final RegistryObject<ForgeSpawnEggItem> EXAMPLE_SPAWN_EGG = addToTab(ITEMS.register("skinwalker_spawn_egg",
//            () -> new ForgeSpawnEggItem(ModEntityTypes.SKIN_WALKER, 0xF0ABD1, 0xAE4C82, new Item.Properties())));

}
