package com.yalt.skinwalker.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SkinWalkerSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "skinwalker");

    public static final RegistryObject<SoundEvent>[] BAIT_SOUNDS = new RegistryObject[2];
    public static final RegistryObject<SoundEvent>[] SKINWALKER_SOUNDS = new RegistryObject[19];
    public static final RegistryObject<SoundEvent>[] TALKING_SOUNDS = new RegistryObject[8];

    static {
        // Registering bait sounds
        for (int i = 1; i <= BAIT_SOUNDS.length; i++) {
            BAIT_SOUNDS[i - 1] = registerSoundEvent("bait" + i);
        }

        // Registering skinwalker sounds
        for (int i = 1; i <= SKINWALKER_SOUNDS.length; i++) {
            SKINWALKER_SOUNDS[i - 1] = registerSoundEvent("Skinwalker sounds" + i);
        }

        // Registering talking sounds
        for (int i = 1; i <= TALKING_SOUNDS.length; i++) {
            TALKING_SOUNDS[i - 1] = registerSoundEvent("SkinwalkerTalking" + (i != 8 ? i : ""));
        }
    }

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation("skinwalker", name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id)); // This line is unchanged
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}