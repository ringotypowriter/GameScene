package skypixeldev.gamescene.scene.gameObject.impl;

import lombok.Getter;
import org.bukkit.Location;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.parser.FunctionManager;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.parser.wildcard.WildcardGameScene;
import skypixeldev.gamescene.parser.wildcard.WildcardPlayer;
import skypixeldev.gamescene.scene.GameObjects;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.gameObject.BaseObject;
import skypixeldev.gamescene.scene.manager.ScenePlayer;

import java.util.*;

public class GObjectAura extends BaseObject {

    private List<Parsing> playerFunction;
    private List<Parsing> globalFunction;
    private double radius;
    private Location location;



    public GObjectAura(GameScene scene) {
        super(scene);
    }

    @Override
    public void onUpdate(GameScene scene, GameObjects.SingletonObject runtime) {
        if(location == null){
            return;
        }



        if(!playerFunction.isEmpty()){
            location.getWorld().getNearbyPlayers(location, radius).forEach(pl -> {
                if(isDisposable()){
                    ScenePlayer player = scene.getScenePlayerIfPresent(pl.getUniqueId());
                    if(player != null){
                        if( (Boolean) player.getDataOrDefault("Disposed-" + runtime.getName(), Boolean.FALSE)){
                            return;
                        }else{
                            player.setData("Disposed-" + runtime.getName(), true);
                        }
                    }
                }
                WildcardPlayer player = new WildcardPlayer(pl);
                WildcardGameScene gameScene = new WildcardGameScene(scene);
                FunctionManager.execute(playerFunction, scene, player, gameScene);
            });
        }

        if(!globalFunction.isEmpty()){
            FunctionManager.execute(globalFunction, scene, new WildcardGameScene(scene));
        }
    }

    @Override
    public void onInitialize(GameScene scene, GameObjects objects, GameObjects.SingletonObject runtime) {


        Map<String,Object> options = runtime.getOptions();
        playerFunction = Collections.synchronizedList(new ArrayList<>());
        globalFunction = Collections.synchronizedList(new ArrayList<>());
        if(options.get("radius") instanceof Integer) {
            radius = ((Integer) options.getOrDefault("radius", 5)) * 1d;
        }else{
            radius = (double) options.getOrDefault("radius", 5);
        }

        String locationName = (String) options.get("location");
        if (!scene.getLocations().contains(locationName)) {
            throw new IllegalArgumentException("cannot found location '" + locationName + "'");
        }
        location = scene.getLocations().get(locationName);

        if(options.containsKey("playerFunctions")){
            for(Object o : (List<?>)options.get("playerFunctions")){
                if(o instanceof String){
                    playerFunction.add(new Parsing((String) o));
                    Log.lGo("Loaded Player Function " + o + " for " + runtime.getName());
                }
            }
        }

        if(options.containsKey("globalFunctions")){
            for(Object o : (List<?>)options.get("globalFunctions")){
                if(o instanceof String){
                    globalFunction.add(new Parsing((String) o));
                }
            }
        }

        if(options.containsKey("disposable")){
            disposable = (Boolean) options.getOrDefault("disposable", Boolean.FALSE);
        }
    }

    @Getter private boolean disposable = false;

    @Override
    public void onRemove(GameScene scene) {

    }

    @Override
    public String getProvider() {
        return "std";
    }
}
