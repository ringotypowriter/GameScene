package skypixeldev.gamescene.setup;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.export.Pair;
import skypixeldev.gamescene.scene.GameScene;

@CommandAlias("gsetup")
public class SetupCommands extends BaseCommand {
    @Subcommand("load")
    @CommandPermission(SetupManager.PERM_NODE_SETUP)
    @Syntax("<GameScene>")
    public void load( CommandSender user, String name){
        if(SetupManager.loadNewSetup(name)){
            Log.l(user.getName() + " Loaded a Game Scene: " + name);
            SetupManager.pollAllSetup(pl -> {
                pl.sendTitle("§6" + name,"§f已载入!请开始进行游戏内设置.", 0,20 * 5 ,0);
                pl.playSound(pl.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 2);
            });
        }else{
            user.sendMessage("§e无法载入该GameScene, 或许当前服务器已经有了一个正在进行设置的GameScene!");
            user.sendMessage("§e如果需要卸载当前GameScene, 请输入/gsetup unload");
        }
    }

    @Subcommand("unload")
    @CommandPermission(SetupManager.PERM_NODE_SETUP)
    public void unload(CommandSender user){
        if(!SetupManager.hasRunningSetup()){
            user.sendMessage("§e当前没有正在进行设置的GameScene!");
            return;
        }
        String name = SetupManager.getRunningSetup().getScene().getName();
        Log.l(user.getName() + " Unloaded a Game Scene: " + name);
        SetupManager.unloadCurrentSetup();
        SetupManager.pollAllSetup(pl -> {
            pl.sendTitle("§6" + name,"§f已卸载! 相关设置已保存.", 0,20 * 5 ,0);
            pl.playSound(pl.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 2);
        });
    }

    @Subcommand("tool")
    @CommandPermission(SetupManager.PERM_NODE_SETUP)
    public void tool(Player user){
        if(!SetupManager.hasRunningSetup()){
            user.sendMessage("§e当前没有正在进行设置的GameScene!");
            return;
        }
        user.sendMessage("§a正在发送中...");
        user.getInventory().addItem(SetupManager.SetupBlockSelector.SINGLE_SELECTOR);
        user.getInventory().addItem(SetupManager.SetupBlockSelector.MULTI_SELECTOR);
        user.sendMessage("§a发送完成");
        user.playSound(user.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
    }
    @Subcommand("saveSingleBlock")
    @CommandPermission(SetupManager.PERM_NODE_SETUP)
    @Syntax("<Key>")
    public void saveSingleBlock(Player user, String key){
        if(SetupManager.hasRunningSetup()){
            Location loc = SetupManager.getSelector().getPlayerSingletonLocation(user);
            if(loc == null){
                user.sendMessage("§c你尚未通过设置工具设置一个单方块坐标!");
                return;
            }
            Log.l(user.getName() + " tend to write a block location: " + key);
            SetupManager.getRunningSetup().appendLocation(key, loc);
            SetupManager.pollAllSetup(pl -> {
                pl.sendTitle("§a" + key,"§f该方块坐标已保存!", 0,20 * 5 ,0);
                pl.playSound(pl.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 2);
            });
        }else{
            user.sendMessage("§e当前没有正在进行设置的GameScene!");
        }
    }
    @Subcommand("saveCuboid")
    @CommandPermission(SetupManager.PERM_NODE_SETUP)
    @Syntax("<Key>")
    public void saveCuboidArea(Player user, String key){
        if(SetupManager.hasRunningSetup()){
            Pair<Location,Location> loc = SetupManager.getSelector().getPlayerPairLocation(user);
            if(loc == null || loc.getFirst() == null || loc.getSecond() == null){
                user.sendMessage("§c你尚未通过设置工具设置一个完整的多方块坐标!");
                return;
            }
            Log.l(user.getName() + " tend to write a cuboid area: " + key);
            SetupManager.getRunningSetup().appendCuboidArea(key, loc.getFirst(), loc.getSecond());
            SetupManager.pollAllSetup(pl -> {
                pl.sendTitle("§d" + key + " (两点方体)","§f该矩形区域已保存!", 0,20 * 5 ,0);
                pl.playSound(pl.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 2);
            });
        }else{
            user.sendMessage("§e当前没有正在进行设置的GameScene!");
        }
    }
    @Subcommand("saveRadiusArea")
    @CommandPermission(SetupManager.PERM_NODE_SETUP)
    @Syntax("<Key> <Radius>")
    public void saveRadiusArea(Player user, String key, double radius){
        if(SetupManager.hasRunningSetup()){
            Location loc = SetupManager.getSelector().getPlayerSingletonLocation(user);
            if(loc == null){
                user.sendMessage("§c你尚未通过设置工具设置一个单方块坐标!");
                return;
            }
            Log.l(user.getName() + " tend to write a radius area: " + key + " (x" + radius + ")");
            SetupManager.getRunningSetup().appendRadiusArea(key, loc,radius);
            SetupManager.pollAllSetup(pl -> {
                pl.sendTitle("§d" + key + " (x" + radius + ")","§f该圆形区域已保存!", 0,20 * 5 ,0);
                pl.playSound(pl.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 2);
            });
        }else{
            user.sendMessage("§e当前没有正在进行设置的GameScene!");
        }
    }
    @Subcommand("saveCurrent")
    @CommandPermission(SetupManager.PERM_NODE_SETUP)
    @Syntax("<Key>")
    public void saveCurrent(Player user, String key){
        if(SetupManager.hasRunningSetup()){
            Location loc = user.getLocation();
            Log.l(user.getName() + " tend to write a entity location: " + key);
            SetupManager.getRunningSetup().appendLocation(key, loc);
            SetupManager.pollAllSetup(pl -> {
                pl.sendTitle("§a" + key,"§f该实体坐标已保存!", 0,20 * 5 ,0);
                pl.playSound(pl.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 2);
            });
        }else{
            user.sendMessage("§e当前没有正在进行设置的GameScene!");
        }
    }

    @Subcommand("run")
    @CommandPermission(SetupManager.PERM_NODE_SETUP)
    public void run(CommandSender user){
        if(SetupManager.hasRunningSetup()){
            Setup setup = SetupManager.getRunningSetup();
            GameScene scene = setup.getScene();
            if(scene.isRunning()){
                user.sendMessage("§e当前GameScene已经正在运行!");
                return;
            }
            setup.runScene();
            SetupManager.pollAllSetup(pl -> {
                pl.sendMessage("§a" + scene.getName() + " §f正在测试运行中!");
                pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
            });
        }else{
            user.sendMessage("§e当前没有正在进行设置的GameScene!");
        }
    }
}
