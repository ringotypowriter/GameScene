package skypixeldev.gamescene.pool.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import skypixeldev.gamescene.STD;
import skypixeldev.gamescene.pool.Pools;


@CommandAlias("sitems")
public class SItemCommand extends BaseCommand {
    @Subcommand("give")
    @CommandAlias("sgive")
    @CommandPermission("gamescene.admin.items.give")
    @CommandCompletion("@players")
    @Syntax("<player> <itemName> [amount] &e-给予玩家player itemName物品amount个, 默认为1")
    public void onGive(CommandSender player, String playerName, String itemName, @Default("1") int amount){
        if(amount < 1){
            player.sendMessage("§6Items §8// §e数量" + amount + " 不合法, 请检查格式.");
            return;
        }
        ItemStack stack = Pools.getImplement().getItem(itemName, amount);
        if(stack == null){
            player.sendMessage("§6Items §8// §e物品" + itemName + " 不存在, 请检查格式.");
            return;
        }
        Player tar = Bukkit.getPlayer(playerName);
        if(tar == null){
            player.sendMessage("§6Items §8// §e玩家" + playerName + " 不存在, 请检查格式.");
            return;
        }
        tar.getInventory().addItem(stack).values().forEach(item -> tar.getLocation().getWorld().dropItem(tar.getLocation(), item));
        tar.playSound(tar.getLocation(), Sound.ENTITY_ITEM_PICKUP,1,1);
    }

    @Subcommand("reload")
    @CommandAlias("sireload")
    @CommandPermission("gamescene.admin.items.reload")
    public void onReload(CommandSender sender){
        sender.sendMessage("§6Items §8// §e正在重新加载物品池...");
        long start = System.currentTimeMillis();
        STD.getPools().reload();
        long used = System.currentTimeMillis()-start;
        sender.sendMessage("§6Items §8// §e重载完成, 用时 " + used + "ms");
    }
}
