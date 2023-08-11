package com.yalt.skinwalker.init;

import com.yalt.skinwalker.Skinwalker;
import com.yalt.skinwalker.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Skinwalker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreativeTabInit {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            Skinwalker.MODID);

    public static RegistryObject<CreativeModeTab> SKINWALKER_TAB = CREATIVE_MODE_TABS.register("skinwalker_tab", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SKIN_WALKER_EGG.get()))
                    .title(Component.translatable("itemGroup.skinwalker_tab")).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
