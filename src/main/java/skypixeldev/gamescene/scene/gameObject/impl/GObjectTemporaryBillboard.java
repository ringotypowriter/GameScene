package skypixeldev.gamescene.scene.gameObject.impl;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.scene.GameObjects;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.gameObject.BaseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GObjectTemporaryBillboard extends BaseObject {

    private List<String> contents;
    private double radius;
    private Hologram hologram;
    private Location location;

    private boolean hasHologramSupport(){
        return Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
    }

    public GObjectTemporaryBillboard(GameScene scene) {
        super(scene);
        if(!hasHologramSupport()) {
            throw new UnsupportedOperationException("The server doesn't have HolographicDisplays");
        }
    }

    @Override
    public void onUpdate(GameScene scene, GameObjects.SingletonObject runtime) {
        if(hologram == null){
            hologram = HologramsAPI.createHologram(Bootstrap.getInstance(), location);
            hologram.getVisibilityManager().setVisibleByDefault(false);
            contents.forEach(hologram::appendTextLine);
        }
        List<Player> willToShow = new ArrayList<>(location.getWorld().getNearbyPlayers(location, radius));
        VisibilityManager vm = hologram.getVisibilityManager();
        vm.resetVisibilityAll();
        willToShow.forEach(vm::showTo);
    }

    @SuppressWarnings("unchecked cast")
    @Override
    public void onInitialize(GameScene scene, GameObjects objects, GameObjects.SingletonObject runtime) {
        Map<String,Object> options = runtime.getOptions();
        contents = (List<String>) options.get("contents");
        radius = (double) options.getOrDefault("Radius", 12);
        String locationName = (String) options.get("location");
        if (!scene.getLocations().contains(locationName)) {
            throw new IllegalArgumentException("cannot found location '" + locationName + "'");
        }
        location = scene.getLocations().get(locationName);
        hologram = HologramsAPI.createHologram(Bootstrap.getInstance(), location);
        hologram.getVisibilityManager().setVisibleByDefault(false);
        contents.forEach(hologram::appendTextLine);
    }

    @Override
    public void onRemove(GameScene scene) {
        hologram.delete();
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
