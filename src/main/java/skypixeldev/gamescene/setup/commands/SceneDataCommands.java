package skypixeldev.gamescene.setup.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.manager.SceneManager;
import skypixeldev.gamescene.scene.manager.ScenePlayer;

@CommandAlias("sceneData")
public class SceneDataCommands extends BaseCommand {
    @Subcommand("lookup")
    @CommandPermission("gamescene.admin.scene.lookup")
    public void lookup(CommandSender user, @Optional String someone){
        Player toLookup = null;
        if(someone != null){
            toLookup = Bukkit.getPlayer(someone);
        }else{
            if(user instanceof Player){
                toLookup = (Player) user;
            }
        }
        if(toLookup != null){
            for(GameScene scene : SceneManager.getPlayingScene(toLookup)) {
                user.sendMessage("|§6玩家 " + toLookup.getName() + " 位于 " + scene.getName() + " 的数据|");
                ScenePlayer player = scene.getScenePlayer(toLookup).get();
                player.cloneData().forEach((key,data) -> user.sendMessage("§a" + key + "§f: " + data));
                user.sendMessage("|§6玩家 " + toLookup.getName() + " 位于 " + scene.getName() + " 的数据|");
                user.sendMessage("");
                user.sendMessage("");
            }
            user.sendMessage("§a查询完毕..");
        }else{
            user.sendMessage("§e未指定查询玩家.");
        }
    }

    @Subcommand("joinPlayer")
    @CommandPermission("gamescene.manipulate.joinPlayer")
    public void joinPlayer(CommandSender user, String someone, String sceneName){
        Player toLookup = null;
        if(someone != null){
            toLookup = Bukkit.getPlayer(someone);
        }

        if(toLookup == null){
            user.sendMessage("§e未查询到玩家.");
            return;
        }
        GameScene scene = SceneManager.searchSceneByName(sceneName);
        if(scene == null){
            user.sendMessage("§eScene §d" + sceneName + " §e不存在.");
            return;
        }
        if(scene.isInside(toLookup)){
            user.sendMessage("§e该玩家已存在于Scene中");
            return;
        }
        scene.joinPlayer(toLookup);
        user.sendMessage("§e已将玩家 §d" + toLookup.getName() + " §e加入Scene §d" + scene.getName() + " §e中");
    }

    @Subcommand("quitPlayer")
    @CommandPermission("gamescene.manipulate.quitPlayer")
    public void quitPlayer(CommandSender user, String someone, String sceneName){
        Player toLookup = null;
        if(someone != null){
            toLookup = Bukkit.getPlayer(someone);
        }

        if(toLookup == null){
            user.sendMessage("§6未查询到玩家.");
            return;
        }
        GameScene scene = SceneManager.searchSceneByName(sceneName);
        if(scene == null){
            user.sendMessage("§6Scene §d" + sceneName + " §6不存在.");
            return;
        }
        if(!scene.isInside(toLookup)){
            user.sendMessage("§6该玩家不存在于Scene中");
            return;
        }
        scene.leavePlayer(toLookup);
        user.sendMessage("§6已将玩家 §d" + toLookup.getName() + " §6退出Scene §d" + scene.getName());
    }

    @Subcommand("resetPlayerData")
    @CommandPermission("gamescene.admin.scene.resetData")
    public void resetPlayerData(CommandSender user, String someone, @Optional String sceneName){
        Player toLookup = null;
        if(someone != null){
            toLookup = Bukkit.getPlayer(someone);
        }else{
            if(user instanceof Player){
                toLookup = (Player) user;
            }
        }

        if(toLookup == null){
            user.sendMessage("§e未指定查询玩家.");
            return;
        }

        GameScene scene = SceneManager.searchSceneByName(sceneName);
        if(scene == null){
            for(GameScene each : SceneManager.getPlayingScene(toLookup)) {
                each.getScenePlayer(toLookup).ifPresent(ScenePlayer::resetData);
                user.sendMessage("§e已清空玩家 §6" + toLookup.getName() + " §e在Scene §d" + each.getName() + " §e下的所有数据!");
            }
        }else{
            scene.getScenePlayer(toLookup).ifPresent(ScenePlayer::resetData);
            user.sendMessage("§e已清空玩家 §6" + toLookup.getName() + " §e在Scene §d" + scene.getName() + " §e下的所有数据!");
        }
    }
}
