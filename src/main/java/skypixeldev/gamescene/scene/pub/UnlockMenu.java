package skypixeldev.gamescene.scene.pub;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.export.itemMenus.ActionMenuItem;
import skypixeldev.gamescene.export.itemMenus.ItemMenu;

import java.util.Random;
import java.util.function.Consumer;

public class UnlockMenu {
    public static void openUnlocking(Player whom, int difficulty, Consumer<Player> whenDone, Consumer<Player> whenFailed){
        Random random = new Random(System.currentTimeMillis());
        ItemMenu menu = new ItemMenu("§f开锁 (" + difficulty + " 剩余)", ItemMenu.Size.THREE_LINE);
        int successSlot = random.nextInt(27);
        for(int i = 0; i < 27;i++){
            if(random.nextBoolean() || successSlot == i){
                continue;
            }
            menu.setItem(i, new ActionMenuItem("§e错误路径", event -> {
                event.setWillClose(true);
                Player player = event.getPlayer();
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE,1f,0.5f);
                player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1f, 0.5f);
                whenFailed.accept(player);
                player.updateInventory();
            }, new ItemStack(Material.TRIPWIRE_HOOK),"§f不要点击","§f若失误点击则§c开锁失败§f!"));

        }

        menu.setItem(successSlot, new ActionMenuItem("§a正确路径", event -> {
            Player player = event.getPlayer();
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN,1f,3f);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1f,3f);
            if(difficulty-1 <= 0){
                player.closeInventory();
                whenDone.accept(player);
                player.sendTitle("§c开锁成功", "§f您完成了所有正确的挑战", 0, 20 * 5, 0);
            }else {
                player.closeInventory();
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bootstrap.getInstance(), () -> UnlockMenu.openUnlocking(player, difficulty - 1, whenDone, whenFailed),4);
            }
        }, new ItemStack(Material.TRIPWIRE_HOOK),"§f点击!!!","§f点击该物品进入下一阶段开锁"));

        menu.open(whom);
        whom.playSound(whom.getLocation(), Sound.BLOCK_CHEST_OPEN,1,3f);
    }
}
