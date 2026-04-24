package skypixeldev.gamescene.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import skypixeldev.gamescene.scene.GameScene;

@RequiredArgsConstructor
public class SceneRunEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    @Getter @NonNull private GameScene scene;


    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
