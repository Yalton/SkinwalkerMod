package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ethereal.Ethereal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

import java.util.List;
import java.util.Random;

public class SabotageGoal extends EtherealGoal {
    public SabotageGoal(Ethereal ethereal) {
        super(ethereal);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && ethereal.hasTarget();
    }

    @Override
    public void start() {
        super.start();

        var r = rearrangeItemsInContainers();
        System.out.println("rearrangeItemsInContainers(): " + r);
    }

    private boolean replacePaintings() {
        List<Painting> paintings = this.ethereal.level().getEntitiesOfClass(Painting.class, this.ethereal.getBoundingBox().inflate(5));
        for (Painting painting : paintings) {
            painting.remove(Entity.RemovalReason.DISCARDED);
            Painting newPainting = new Painting((EntityType<? extends Painting>) painting.getType(), painting.level());
            newPainting.setPos(painting.getX(), painting.getY(), painting.getZ());
            this.ethereal.level().addFreshEntity(newPainting);
        }

        return true;
    }

    private boolean toggleDoors() {
        System.out.println("Sabotaging Doors");

        var blocks = ethereal.getBlocks();
        if (blocks == null) {
            return false;
        }

        System.out.println("Doors stream");
        var doors = blocks
                .filter(e -> e.getBlockState().getBlock() instanceof DoorBlock)
                .toList();
        System.out.println("Doors: " + doors.size());
        if (doors.isEmpty()) {
            return false;
        }

        var rand = new Random().nextInt(doors.size());
        var door = doors.get(rand);

        var block = (DoorBlock) door.getBlockState().getBlock();
        var is_open = door.getBlockState().getValue(DoorBlock.OPEN);
        var state = door.getBlockState().setValue(DoorBlock.OPEN, is_open);

        // TODO: Open door.
        block.setOpen(null, door.getLevel(), state, door.getBlockPos(), !is_open);
        return true;

    }

    private boolean enableJukebox() {
        var blocks = ethereal.getBlocks();
        if (blocks == null) {
            return false;
        }

        var jukeboxes = blocks
                .filter(e -> e instanceof JukeboxBlockEntity)
                .map(e -> ((JukeboxBlockEntity) e))
                .filter(e -> !e.isRecordPlaying())
                .toList();
        if (jukeboxes.isEmpty()) {
            return false;
        }

        var rand = new Random().nextInt(jukeboxes.size());
        var jukebox = jukeboxes.get(rand);

        var record = (RecordItem) Items.MUSIC_DISC_CAT;
        jukebox.setItem(0, record.getDefaultInstance());
        return true;
    }


    private boolean rearrangeItemsInContainers() {
        System.out.println("Sabotaging Chests");

        var blocks = ethereal.getBlocks();
        if (blocks == null) {
            return false;
        }

        System.out.println("Chest stream");
        var doors = blocks.filter(e -> e instanceof ChestBlockEntity)
                .toList();
        System.out.println("Chests: " + doors.size());
        if (doors.isEmpty()) {
            return false;
        }

        // Code to rearrange items in containers
        return true;
    }

//    private void unleashOrRenameAnimals() {
//        List<Animal> animals = this.ethereal.level().getEntitiesOfClass(Animal.class, this.ethereal.getBoundingBox().inflate(5));
//        for (Animal animal : animals) {
//            animal.setCustomName(new TextComponent("Custom Name")); // Set custom name
//        }
//    }

//    private void stealItemsFromContainers() {
//        // Code to steal items from containers
//    }

}
