package skypixeldev.gamescene.scene.gameObject.impl;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.scene.GameObjects;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.gameObject.BaseObject;

import java.util.List;
import java.util.Map;

public class GObjectBillboard extends BaseObject {

    private List<String> contents;
    private Hologram hologram;
    private Location location;

    private boolean hasHologramSupport(){
        return Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
    }

    public GObjectBillboard(GameScene scene) {
        super(scene);
        if(!hasHologramSupport()) {
            throw new UnsupportedOperationException("The server doesn't have HolographicDisplays");
        }
    }

    @Override
    public void onUpdate(GameScene scene, GameObjects.SingletonObject runtime) {
        if(hologram == null){
            hologram = HologramsAPI.createHologram(Bootstrap.getInstance(), location);
            contents.forEach(hologram::appendTextLine);
        }
    }

    @SuppressWarnings("unchecked cast")
    @Override
    public void onInitialize(GameScene scene, GameObjects objects, GameObjects.SingletonObject runtime) {
        Map<String,Object> options = runtime.getOptions();
        contents = (List<String>) options.get("contents");
        String locationName = (String) options.get("location");
        if (!scene.getLocations().contains(locationName)) {
            throw new IllegalArgumentException("cannot found location '" + locationName + "'");
        }
        location = scene.getLocations().get(locationName);
        hologram = HologramsAPI.createHologram(Bootstrap.getInstance(), location);
        hologram.getVisibilityManager().setVisibleByDefault(true);
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
