package skypixeldev.gamescene.export.dungeonplus;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.serverct.ersha.dungeon.common.api.event.DungeonEvent;
import org.serverct.ersha.dungeon.common.api.event.DungeonPlayerEvent;
import org.serverct.ersha.dungeon.common.api.event.dungeon.DungeonEndEvent;
import org.serverct.ersha.dungeon.common.api.event.dungeon.DungeonStartEvent;
import org.serverct.ersha.dungeon.common.api.event.impl.PluginDungeonEvent;
import org.serverct.ersha.dungeon.common.team.type.PlayerStateType;
import org.serverct.ersha.dungeon.internal.dungeon.Dungeon;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.config.OptionBuilder;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.manager.SceneManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DPCompat implements Listener {

    public static Map<String,String> listenerMap = new HashMap<>();
    public static Map<UUID,String> reconnectCache = new HashMap<>();
    public static void initCompat(){
        Bukkit.getPluginManager().registerEvents(new DPCompat(), Bootstrap.getInstance());
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bootstrap.getInstance(), () -> {
           for(String name : Bootstrap.getAllSuspiciousScene()){
               try{
                   GameScene sample = SceneManager.loadScene(name);
                   ConfigurationSection section = sample.getSettings().getOrCreateConfigurationSection("DungeonPlus", OptionBuilder.of()
                           .key("Enable").value(false).key("FollowingDungeon").value("YourDungeonName"));
                   if(section.getBoolean("Enable")){
                       listenerMap.put(section.getString("FollowingDungeon"), sample.getName());
                   }
                   sample.getSettings().saveToFile();
               }catch (Throwable ignored){}
           }
        });
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent evt){
        Optional.ofNullable(reconnectCache.get(evt.getPlayer().getUniqueId())).map(SceneManager::searchSceneByName).ifPresent(sc -> sc.joinPlayer(evt.getPlayer()));
    }

    @EventHandler
    public void onRunAndEnd(DungeonEvent evt){
        if(evt.getEvent() instanceof DungeonStartEvent.After) {
            String dungeonName = evt.getDungeon().getDungeonName();
            World world = evt.getDungeon().getDungeonWorld();
            Log.l("Dungeon " + dungeonName + " is creating world.");
            if (world.getName().endsWith("edit")) {
                return;
            }
            if (listenerMap.containsKey(dungeonName)) {
                String name = listenerMap.get(dungeonName);
                try {
                    Log.l("Dungeon " + dungeonName + " called, creating a clone scene for it. (" + name + ")");
                    GameScene sample = SceneManager.loadScene(name);
                    GameScene clone = new GameScene(sample, world);
                    evt.getDungeon().getDungeonMeta().put("GameSceneName", clone.getName());
                    evt.getDungeon().getTeam().getPlayers(PlayerStateType.ONLINE).forEach(pl -> {
                        clone.joinPlayer(pl);
                        reconnectCache.put(pl.getUniqueId(), clone.getName());
                    });
                } catch (Exception e) {
                    Log.lEr(e, "When loading " + name + " for " + dungeonName);
                }
            }
        }else if(evt.getEvent() instanceof DungeonEndEvent.Before){
            Optional.ofNullable(evt.getDungeon().getDungeonMeta().getData().get("GameSceneName")).map(o -> (String)o).map(SceneManager::searchSceneByName).map(s -> {
                evt.getDungeon().getTeam().getPlayers().forEach(reconnectCache::remove);
                return s;
            }).ifPresent(GameScene::remove);
        }
    }
//    if (world.getName().startsWith("dungeon_")) {
//        String[] ar = world.getName().split("_");
//        String dungeonName = ar[1];
//        Log.l("Dungeon " + dungeonName + " is creating world.");
//        if (world.getName().endsWith("edit")) {
//            return;
//        }
//        if (listenerMap.containsKey(dungeonName)) {
//            String name = listenerMap.get(dungeonName);
//            try {
//                Log.l("Dungeon " + dungeonName + " called, creating a clone scene for it. (" + name + ")");
//                GameScene sample = SceneManager.loadScene(name);
//                new GameScene(sample, evt.getWorld());
//            } catch (Exception e) {
//                Log.lEr(e, "When loading " + name + " for " + dungeonName);
//            }
//        }
//    }
}
