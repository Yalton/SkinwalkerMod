package com.yalt.skinwalker.sound;

import com.yalt.skinwalker.Skinwalker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS;
    public static final RegistryObject<SoundEvent> SKINWALKER_BAIT1;
    public static final RegistryObject<SoundEvent> SKINWALKER_BAIT2;
    public static final RegistryObject<SoundEvent> SKINWALKER_SOUND1;
    public static final RegistryObject<SoundEvent> SKINWALKER_SOUND2;
    public static final RegistryObject<SoundEvent> SKINWALKER_SOUND3;
    public static final RegistryObject<SoundEvent> SKINWALKER_TALKING1;
    public static final RegistryObject<SoundEvent> SKINWALKER_TALKING2;
    public static final RegistryObject<SoundEvent> SKINWALKER_TALKING3;
    public static final RegistryObject<SoundEvent> SKINWALKER_TALKING4;

    public ModSounds() {
    }

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(Skinwalker.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

    static {
        SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Skinwalker.MODID);
        SKINWALKER_BAIT2 = registerSoundEvent("bait2");
        SKINWALKER_SOUND1 = registerSoundEvent("skinwalker_sound1");
        SKINWALKER_BAIT1 = registerSoundEvent("bait1");
        SKINWALKER_SOUND2 = registerSoundEvent("skinwalker_sound2");
        SKINWALKER_SOUND3 = registerSoundEvent("skinwalker_sound3");
        SKINWALKER_TALKING1 = registerSoundEvent("skinwalker_talking1");
        SKINWALKER_TALKING2 = registerSoundEvent("skinwalker_talking2");
        SKINWALKER_TALKING3 = registerSoundEvent("skinwalker_talking3");
        SKINWALKER_TALKING4 = registerSoundEvent("skinwalker_talking4");
    }
}
