package skypixeldev.gamescene.scene.pub;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Door;
import org.bukkit.metadata.FixedMetadataValue;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.export.ControlCooldown;

public class UnlockGateHandler implements Listener {
    private final static String KEY_UNLOCKED_DOOR = "PlayerDoorUnlocked";
    private final static String KEY_UNLOCKED_TIME = "PlayerDoorUnlockedTimeout";

    public static void markDoorAvailable(Player pl, Block doorPart1, int durationSeconds){
        pl.sendActionBar("§a开锁成功! 您将在接下来的 §e" + durationSeconds + "s §a内暂时获得该扇门的控制权");
        pl.setMetadata(KEY_UNLOCKED_DOOR, new FixedMetadataValue(Bootstrap.getInstance(), doorPart1));
        pl.setMetadata(KEY_UNLOCKED_TIME, new FixedMetadataValue(Bootstrap.getInstance(), System.currentTimeMillis()+1000*durationSeconds));
    }

    @EventHandler
    public void onInteractDoor(PlayerInteractEvent evt){
        if(evt.hasBlock() && evt.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(evt.getClickedBlock().getType() == Material.IRON_DOOR_BLOCK) {
                final Block doorPart1 = evt.getClickedBlock();
                Block doorPart2 = evt.getClickedBlock().getRelative(BlockFace.DOWN);
                Block doorBase = doorPart2;
                if (doorPart2.getType() != Material.IRON_DOOR_BLOCK) {
                    doorPart2 = evt.getClickedBlock().getRelative(BlockFace.UP);
                    doorBase = doorPart1;
                }
                if (doorPart2.getType() != Material.IRON_DOOR_BLOCK) {
                    return;
                }

                Player player = evt.getPlayer();

                // GCD
                if(ControlCooldown.isCooldown(player)){
                    return;
                }
                ControlCooldown.addCooldown(player, System.currentTimeMillis()+1000);

                boolean unlocked = false;
                if (!player.hasMetadata(KEY_UNLOCKED_DOOR)) {
                    unlocked = false;
                } else if(player.getMetadata(KEY_UNLOCKED_DOOR).get(0).value() instanceof Block){
                    Block block = (Block) player.getMetadata(KEY_UNLOCKED_DOOR).get(0).value();
                    if(block.getLocation().toBlockLocation() == doorPart1.getLocation().toBlockLocation() || block.getLocation().toBlockLocation() == doorPart2.getLocation().toBlockLocation()) {
                        if (player.hasMetadata(KEY_UNLOCKED_TIME)) {
                            long timeout = player.getMetadata(KEY_UNLOCKED_TIME).get(0).asLong();
                            if (System.currentTimeMillis() < timeout) {
                                unlocked = true;
                            }
                        }
                    }
                }
                
                if(unlocked){
                    BlockState part1 = doorPart1.getState();
                    BlockState part2 = doorPart2.getState();
                    Door door1 = (Door) doorPart1.getState().getData();
                    Door door2 = (Door) doorPart2.getState().getData();
                    if(door1.isOpen() || door2.isOpen()){
                        door1.setOpen(false);
                        door2.setOpen(false);
                    }else{
                        door1.setOpen(true);
                        door2.setOpen(true);
                    }
                    part1.setData(door1);
                    part2.setData(door2);
                    part1.update(true);
                    part2.update(true);
                    doorPart1.getWorld().playSound(doorPart1.getLocation(),  (door1.isOpen() ? Sound.BLOCK_IRON_DOOR_OPEN : Sound.BLOCK_IRON_DOOR_CLOSE) , 1f, 1f);
                }else if(Bootstrap.getGlobalSettings().getBoolean("Automatic-Unlocking-IronDoors")){
                    int difficulty = 3;
                    player.removeMetadata(KEY_UNLOCKED_TIME, Bootstrap.getInstance());
                    switch (doorBase.getRelative(BlockFace.DOWN).getType()){
                        case GOLD_BLOCK: difficulty = 5; break;
                        case DIAMOND_BLOCK: difficulty = 8; break;
                        case EMERALD_BLOCK: difficulty = 10; break;
                        case BEDROCK: difficulty = 100; break;
                        default: break;
                    }
                    UnlockMenu.openUnlocking(player, difficulty, pl -> {
                        pl.sendActionBar("§a开锁成功! 您将在接下来的 §e60s §a内暂时获得该扇门的控制权");
                        pl.setMetadata(KEY_UNLOCKED_DOOR, new FixedMetadataValue(Bootstrap.getInstance(), doorPart1));
                        pl.setMetadata(KEY_UNLOCKED_TIME, new FixedMetadataValue(Bootstrap.getInstance(), System.currentTimeMillis()+1000*60));
                    }, pl -> pl.sendActionBar("§c开锁失败, 请重试!"));
                }


//                if (!unlocked) {
//                    player.removeMetadata(KEY_UNLOCKED_TIME, Bootstrap.instance)
//                    UnlockMenu.openUnlockingMenu(player, when(doorBase.getRelative(BlockFace.DOWN).getType()){
//                        Material.GOLD_BLOCK -> 5
//                        Material.DIAMOND_BLOCK -> 8
//                        Material.EMERALD_BLOCK -> 10
//                        Material.BEDROCK -> 100
//                        else -> 3
//                    }, { pl ->
//                            pl.sendActionBar("§a开锁成功! 您将在接下来的 §e60s §a内暂时获得该扇门的控制权")
//                        pl.setMetadata(KEY_UNLOCKED_DOOR, FixedMetadataValue(Bootstrap.instance, doorPart1))
//                        pl.setMetadata(KEY_UNLOCKED_TIME, FixedMetadataValue(Bootstrap.instance, System.currentTimeMillis()+1000*60))
//                    }, { pl -> pl.sendActionBar("§c开锁失败, 请重试!") })
//                }else{
//                    val part1 = doorPart1.getState()
//                    val part2 = doorPart2.getState()
//                    val door1 = doorPart1.getState().getData() as Door
//                    val door2 = doorPart2.getState().getData() as Door
//                    if(door1.isOpen || door2.isOpen){
//                        door1.isOpen = false
//                        door2.isOpen = false
//                    }else{
//                        door1.isOpen = true
//                        door2.isOpen = true
//                    }
//                    part1.getData() = door1
//                    part2.getData() = door2
//                    part1.update(true)
//                    part2.update(true)
//                    doorPart1.world.playSound(doorPart1.getLocation(), if (door1.isOpen) Sound.BLOCK_IRON_DOOR_OPEN else Sound.BLOCK_IRON_DOOR_CLOSE , 1f, 1f)
//                }
            }
        }
    }
}
