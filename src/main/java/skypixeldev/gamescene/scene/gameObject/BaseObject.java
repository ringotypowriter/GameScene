package skypixeldev.gamescene.scene.gameObject;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.interfaces.Removable;
import skypixeldev.gamescene.scene.GameObjects;
import skypixeldev.gamescene.scene.GameScene;

public abstract class BaseObject implements Listener, Removable {
    @Getter private GameScene scene;
    public BaseObject(GameScene scene){
        this.scene = scene;
        Bukkit.getPluginManager().registerEvents(this, Bootstrap.getInstance());
    }
    public abstract void onUpdate(GameScene scene, GameObjects.SingletonObject runtime);
    public abstract void onInitialize(GameScene scene, GameObjects objects, GameObjects.SingletonObject runtime);
    public abstract void onRemove(GameScene scene);
    public abstract String getProvider();

    private int skipped = 0;
    @Getter @Setter
    private int maximumApplyDelay = 0;

    private boolean canUpdate(){
        if(maximumApplyDelay <= 0){
            return true;
        }
        skipped++;
        if(skipped > maximumApplyDelay){
            skipped = 0;
            return true;
        }
        return false;
    }

    public void tryUpdate(GameScene scene, GameObjects.SingletonObject runtime){
        if(canUpdate()){
            onUpdate(scene, runtime);
        }
    }

    @Override
    public void remove() {
        HandlerList.unregisterAll(this);
        onRemove(scene);
    }
}
