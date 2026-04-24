package skypixeldev.gamescene.scene.gameObject.impl;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import skypixeldev.gamescene.parser.FunctionManager;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.parser.wildcard.WildcardPlayer;
import skypixeldev.gamescene.scene.GameObjects;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.gameObject.BaseObject;
import skypixeldev.gamescene.scene.pub.UnlockGateHandler;
import skypixeldev.gamescene.scene.pub.UnlockMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GObjectUnlockGate extends BaseObject {
    public GObjectUnlockGate(GameScene scene) {
        super(scene);
    }

    @Override
    public void onUpdate(GameScene scene, GameObjects.SingletonObject runtime) {

    }

    @Getter private Location targetLocation;
    @Getter private List<Parsing> whenSuccess;
    @Getter private List<Parsing> whenFailed;
    @Getter private boolean defaultBehaviour;
    @Getter private int unlockingDuration = 60;
    @Getter private int difficulty = 1;

    @Override
    public void onInitialize(GameScene scene, GameObjects objects, GameObjects.SingletonObject runtime) {
        Location spawnLoc = scene.getLocations().get((String) runtime.getOptions().get("location"));
        if(spawnLoc == null){
            throw new IllegalArgumentException();
        }

        targetLocation = spawnLoc;
        defaultBehaviour = (Boolean) runtime.getOptions().getOrDefault("defaultBehaviour",Boolean.TRUE);
        difficulty = (Integer) runtime.getOptions().getOrDefault("difficulty",1);
        unlockingDuration = (Integer) runtime.getOptions().getOrDefault("unlockingDuration",60);

        ArrayList<String> toParsing = new ArrayList<>();
        if(runtime.getOptions().containsKey("whenSuccess")) {
            for (Object o : (List<?>) runtime.getOptions().getOrDefault("whenSuccess", Collections.emptyList())) {
                if (o instanceof String) {
                    toParsing.add((String) o);
                }
            }

            whenSuccess = Parsers.parseFunctions(toParsing);
        }

        if(runtime.getOptions().containsKey("whenFailed")) {
            toParsing.clear();
            for (Object o : (List<?>) runtime.getOptions().getOrDefault("whenFailed", Collections.emptyList())) {
                if (o instanceof String) {
                    toParsing.add((String) o);
                }
            }

            whenFailed = Parsers.parseFunctions(toParsing);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent evt){
        if(evt.hasBlock() && evt.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(targetLocation.toBlockLocation().equals(evt.getClickedBlock().getLocation())){
                evt.setCancelled(true);
                final Block doorPart1 = evt.getClickedBlock();
                UnlockMenu.openUnlocking(evt.getPlayer(), difficulty, pl -> {
                    if(defaultBehaviour){
                        UnlockGateHandler.markDoorAvailable(pl, doorPart1, unlockingDuration);
                    }
                    if(!whenSuccess.isEmpty()){
                        FunctionManager.execute(whenSuccess, getScene(), new WildcardPlayer(pl));
                    }
                }, pl -> {
                    if(!whenFailed.isEmpty()){
                        FunctionManager.execute(whenFailed, getScene(), new WildcardPlayer(pl));
                    }
                });
            }
        }
    }

    @Override
    public void onRemove(GameScene scene) {

    }

    @Override
    public String getProvider() {
        return null;
    }
}
