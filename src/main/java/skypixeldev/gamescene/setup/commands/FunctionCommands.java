package skypixeldev.gamescene.setup.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.LootTable;
import skypixeldev.gamescene.scene.manager.SceneManager;

import java.util.Map;

@CommandAlias("func")
public class FunctionCommands extends BaseCommand {
    public final static String PERM_NODE_FUNCTION = "gamescene.function";

    public static void initCommands(Plugin plugin, PaperCommandManager manager){
        manager.registerCommand(new FunctionCommands());
    }

    @Subcommand("execVar")
    @CommandPermission(PERM_NODE_FUNCTION)
    @Syntax("<Variables> <GameScene> [isAsync ? -a]")
    public void execVar(CommandSender usr, String variableKey, String gameScene, @Optional String s){
        GameScene scene = SceneManager.searchSceneByName(gameScene);
        if(scene == null){
            usr.sendMessage("§c没有正在运行的名为 " + gameScene + " 的GameScene");
            return;
        }
        String val = scene.getVariables().getString(variableKey);
        if(val == null){
            usr.sendMessage("§c没有变量: " + variableKey);
        }
        usr.sendMessage("§e原文本: " + val);
        if(java.util.Optional.ofNullable(s).map(arg -> arg.equals("-a")).orElse(false))
            usr.sendMessage("§eWARNING：该方法将会异步运行 (可能不兼容)");

        scene.executeRaw(val);
    }
    @Subcommand("variables")
    @CommandPermission(PERM_NODE_FUNCTION)
    @Syntax("<GameScene>")
    public void variables(CommandSender usr, String gameScene){
        GameScene scene = SceneManager.searchSceneByName(gameScene);
        if(scene == null){
            usr.sendMessage("§c没有正在运行的名为 " + gameScene + " 的GameScene");
            return;
        }
        Map<String,Object> map = scene.getVariables().entryMap();
        usr.sendMessage("§e-------GameScene " + gameScene + " 的变量表-------");
        map.forEach((key, value) -> usr.sendMessage("  §f" + key + "§f: §b" + value));
        usr.sendMessage("§e-------------------------------");

    }

    @Subcommand("getLoot")
    @CommandPermission(PERM_NODE_FUNCTION)
    @Syntax("<GameScene> <Loot Table> <Repear Times>")
    public void getLoot(Player usr, String gameScene, String lootTable, @Default("1") int repeats){
        GameScene scene = SceneManager.searchSceneByName(gameScene);
        if(scene == null){
            usr.sendMessage("§c没有正在运行的名为 " + gameScene + " 的GameScene");
            return;
        }
        LootTable.LootTableObject object = scene.getLootTable().getLoadedLootTable(lootTable);
        if(object == null){
            usr.sendMessage("§c没有名为 " + lootTable + " 的LootTable");
            return;
        }
        usr.getInventory().addItem(object.roll(repeats).toArray(new ItemStack[0]));
        usr.sendMessage("§a你已获得物品");
    }

}
