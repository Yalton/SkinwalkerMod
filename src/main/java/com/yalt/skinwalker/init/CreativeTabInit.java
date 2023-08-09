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

    public static RegistryObject<CreativeModeTab> TUTORIAL_TAB = CREATIVE_MODE_TABS.register("tutorial_tab", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SKIN_WALKER_EGG.get()))
                    .title(Component.translatable("itemGroup.skinwalker_tab")).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
//    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Skinwalker.MODID);
//
//    public static final List<Supplier<? extends ItemLike>> EXAMPLE_TAB_ITEMS = new ArrayList<>();
//
//    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = TABS.register("example_tab",
//            () -> CreativeModeTab.builder()
//                    .title(Component.translatable("itemGroup.skinwalker_tab"))
//                    .icon(() -> ModItems.SKIN_WALKER_SPAWN_EGG.get().getDefaultInstance()) // Updated icon
//                    .displayItems((displayParams, output) ->
//                            EXAMPLE_TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get())))
//                    .withSearchBar()
//                    .build()
//    );
//
//    public static <T extends Item> RegistryObject<T> addToTab(RegistryObject<T> itemLike) {
//        EXAMPLE_TAB_ITEMS.add(itemLike);
//        return itemLike;
//    }
//
//
//    @SubscribeEvent
//    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
//        if(event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
//            event.getEntries().putAfter(Items.ACACIA_LOG.getDefaultInstance(), ModItems.SKIN_WALKER_SPAWN_EGG.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
//        }
//
//        if(event.getTab() == EXAMPLE_TAB.get()) {
//            event.accept(Items.CROSSBOW);
//        }
//    }
}
