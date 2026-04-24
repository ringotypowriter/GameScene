package skypixeldev.gamescene.scene.gameObject.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import skypixeldev.gamescene.STD;
import skypixeldev.gamescene.parser.FunctionManager;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.parser.wildcard.WildcardPlayer;
import skypixeldev.gamescene.scene.GameObjects;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.gameObject.BaseObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GObjectPlayerSpawn extends BaseObject {
    private Location spawnLoc;
    private List<Parsing> functions;

    public GObjectPlayerSpawn(GameScene scene) {
        super(scene);
    }

    @Override
    public void onUpdate(GameScene scene, GameObjects.SingletonObject runtime) {

    }

    @Override
    public void onInitialize(GameScene scene, GameObjects objects, GameObjects.SingletonObject runtime) {
        spawnLoc = scene.getLocations().get((String) runtime.getOptions().get("location"));
        if(spawnLoc == null){
            throw new IllegalArgumentException();
        }

        ArrayList<String> toParsing = new ArrayList<>();
        for(Object o : (List<?>) runtime.getOptions().getOrDefault("functions", Collections.emptyList())){
            if(o instanceof String){
                toParsing.add((String) o);
            }
        }

        functions = Parsers.parseFunctions(toParsing);

        scene.getEvents().on(STD.EVENT_PLAYER_JOIN, (obj) -> {
            Player pl = (Player) obj[0];
            pl.teleport(spawnLoc);

            FunctionManager.execute(functions, scene, new WildcardPlayer(pl));

            return false;
        });


    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent evt){
        if(spawnLoc != null){
            evt.setRespawnLocation(spawnLoc);
        }
    }

    @Override
    public void onRemove(GameScene scene) {

    }

    @Override
    public String getProvider() {
        return "STD";
    }
}
