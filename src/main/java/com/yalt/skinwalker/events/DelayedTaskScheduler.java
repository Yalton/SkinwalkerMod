package com.yalt.skinwalker.events;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber
public class DelayedTaskScheduler {
    private static final Map<Runnable, Integer> TASKS = new WeakHashMap<>();

    public static void scheduleTask(Runnable task, int delay) {
        TASKS.put(task, delay);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        Iterator<Map.Entry<Runnable, Integer>> iterator = TASKS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Runnable, Integer> entry = iterator.next();
            if (entry.getValue() <= 0) {
                entry.getKey().run();
                iterator.remove();
            } else {
                entry.setValue(entry.getValue() - 1);
            }
        }
    }
}