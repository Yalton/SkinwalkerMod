package com.yalt.skinwalker.entity.ethereal.ai;

import com.yalt.skinwalker.entity.ethereal.Ethereal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SabotageGoal extends EtherealGoal {
    public SabotageGoal(Ethereal ethereal) {
        super(ethereal);
    }

    private static final int COOLDOWN_DURATION = 2 * 60 * 1000; // 5 minutes in milliseconds
    private long lastUsageTime = 0;
    private static final int SABOTAGE_COST = -3;
    final int RANDOM_RANGE = 10; // Define the range for random number generation
    final int RANDOM_OFFSET = 5; // Define the offset for random number generation
    public boolean usedAbility = false;

    @Override
    public boolean canUse() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUsageTime < COOLDOWN_DURATION && !usedAbility) {
            return false;
        }
        return super.canUse() && ethereal.hasPossessedEntity && ethereal.hasTarget();
    }

    @Override
    public void start() {
        this.usedAbility = false;
        lastUsageTime = System.currentTimeMillis();
        Random random = new Random();
        int randomNumber = random.nextInt(7) + 1; // Increase the range to include the new methods

        switch (randomNumber) {
            case 1 -> {
                this.usedAbility = this.replacePaintings();
                break;
            }
            case 2 -> {
                this.usedAbility = this.toggleDoors();
                break;
            }
            case 3 -> {
                this.usedAbility = this.enableJukebox();
                break;
            }
            case 4 -> {
                this.usedAbility = this.rearrangeItemsInContainers();
                break;
            }
            case 5 -> {
                this.usedAbility = this.breakTorches();
                break;
            }
            case 6 -> {
                this.usedAbility = this.createPit();
                break;
            }
            case 7 -> {
                this.usedAbility = this.spawnLightning();
                break;
            }
        }

        if (this.usedAbility) {
            lastUsageTime = System.currentTimeMillis();
            ethereal.updateBudget(SABOTAGE_COST);
            this.playNoise();
            return;
        }
        else{
            this.start();
        }
    }

    private boolean replacePaintings() {
        System.out.println("Sabotaging Paintings");
        List<Painting> paintings = this.ethereal.level().getEntitiesOfClass(Painting.class,
                this.ethereal.getBoundingBox().inflate(5));
        for (Painting painting : paintings) {
            painting.remove(Entity.RemovalReason.DISCARDED);
            Optional<Painting> newPaintingOpt = Painting.create(painting.level(), painting.blockPosition(),
                    painting.getDirection());
            if (newPaintingOpt.isPresent()) {
                this.ethereal.level().addFreshEntity(newPaintingOpt.get());
            }
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
        BlockState blockState = door.getBlockState();

        // Open or close the door based on its current state.
        block.setOpen(ethereal, ethereal.level(), blockState, door.getBlockPos(), !is_open);
        return true;
    }

    private boolean enableJukebox() {
        System.out.println("Sabotaging Jukebox");
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

    public boolean createPit() {
        System.out.println("Creating Pit");
        Player player = this.ethereal.getTarget();
        if (player != null) {
            BlockPos playerPos = player.blockPosition();
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    for (int dy = 0; dy < 3; dy++) {
                        BlockPos pos = playerPos.offset(dx, -dy, dz);
                        ethereal.level().removeBlock(pos, false);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean spawnLightning() {
        System.out.println("Spawning Lightning");
        Player player = this.ethereal.getTarget();
        if (player != null) {
            // Generate a random position near the player to spawn the lightning
            Random rand = new Random();

            double offsetX = rand.nextInt(RANDOM_RANGE) - RANDOM_OFFSET;
            double offsetZ = rand.nextInt(RANDOM_RANGE) - RANDOM_OFFSET;

            BlockPos lightningPosition = new BlockPos((int) (player.getX() + offsetX), (int) player.getY(),
                    (int) (player.getZ() + offsetZ));

            // Spawn the lightning bolt
            if (ethereal.level().isLoaded(lightningPosition)) { // Check if the block position is loaded and valid
                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, ethereal.level());
                lightningBolt.setPos(lightningPosition.getX(), lightningPosition.getY(), lightningPosition.getZ());
                ethereal.level().addFreshEntity(lightningBolt);
                ethereal.updateBudget(SABOTAGE_COST + 2);
            }
            return true;
        }
        return false;
    }

    private boolean breakTorches() {
        System.out.println("Breaking Torches");

        Level level = ethereal.level();
        BlockPos etherealPos = ethereal.blockPosition();
        int radius = 5; // Set the radius within which to break torches

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = etherealPos.offset(dx, dy, dz);
                    BlockState blockState = level.getBlockState(pos);
                    if (blockState.getBlock() == Blocks.TORCH) {
                        level.removeBlock(pos, true);
                    }
                }
            }
        }
        return true;
    }


    // private void unleashOrRenameAnimals() {
    // List<Animal> animals = this.ethereal.level().getEntitiesOfClass(Animal.class,
    // this.ethereal.getBoundingBox().inflate(5));
    // for (Animal animal : animals) {
    // animal.setCustomName(new TextComponent("Custom Name")); // Set custom name
    // }
    // }

    // private void stealItemsFromContainers() {
    // // Code to steal items from containers
    // }

}
