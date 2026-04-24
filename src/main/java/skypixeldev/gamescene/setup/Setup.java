package skypixeldev.gamescene.setup;

import lombok.Getter;
import org.bukkit.Location;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.manager.SceneManager;

public class Setup {
    @Getter private GameScene scene;
    public Setup(String gameScene){
        this.scene = SceneManager.loadScene(gameScene, false);
    }

    public void appendLocation(String key, Location location){
        scene.getLocations().save(key, location);
        Log.l("The Location '" + key + "' in " + location.toString() + " for '" + scene.getName() +  "' has been written");
    }

    public void appendCuboidArea(String key, Location first, Location last){
        scene.getAreas().saveCuboid(key, first, last);
        Log.l("The Area '" + key + "' of cuboid has been written.");
    }

    public void appendRadiusArea(String key, Location center, double radius){
        scene.getAreas().saveRadius(key, center, radius);
        Log.l("The Area '" + key + "' of " + radius + "xRadius has been written");
    }

    public void runScene(){
        scene.run();
    }

    public void stopSetup(){
        scene.remove();
    }
}
