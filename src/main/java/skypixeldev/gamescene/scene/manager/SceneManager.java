package skypixeldev.gamescene.scene.manager;

import org.bukkit.entity.Player;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SceneManager {
    private static final Map<String, GameScene> runningScenes;
    static{
        runningScenes = new ConcurrentHashMap<>();
    }

    public static void unloadScene(String key){
        runningScenes.remove(key);
        Log.l("The Scene " + key + " unloaded from environmental map.");
    }

    public static GameScene searchSceneByName(String sceneName){
        if(sceneName == null){
            return null;
        }
        return runningScenes.get(sceneName);
    }

    public static Set<GameScene> getPlayingScene(Player player){
        if(player == null){
            return Collections.emptySet();
        }
        Set<GameScene> scenes = new HashSet<>();
        for(GameScene scene : runningScenes.values()){
           if(scene.isInside(player)){
               scenes.add(scene);
           }
        }
        return scenes;
    }

    public static void appendRunningScene(GameScene scene){
        runningScenes.put(scene.getName(), scene);
    }

    public static GameScene getAnyPlayingScene(Player player){
        return getPlayingScene(player).stream().findAny().orElse(null);
    }

    public static GameScene loadScene(String sceneName){
        return loadScene(sceneName,false);
    }
    public static GameScene loadScene(String sceneName, boolean run){
        if (runningScenes.containsKey(sceneName)) {
            GameScene exist = runningScenes.get(sceneName);
            if(!exist.isRunning() && run){
                exist.run();
            }
            return exist;
        }else {
            GameScene scene = new GameScene(sceneName, run);
            runningScenes.put(scene.getName(), scene);
            Log.l("The Scene " + scene.getName() + " is loaded now!");
            if (run) {
                Log.l("Auto-Run detected, The Game Scene will start running automatically.");
                Log.l("Server now will be in the performance of the running scene.");
            }
            return scene;
        }
    }
}
