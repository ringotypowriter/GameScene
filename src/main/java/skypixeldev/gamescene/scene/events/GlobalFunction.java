package skypixeldev.gamescene.scene.events;

public interface GlobalFunction extends EventFunction{
    @Override
    default boolean function(Object... obj){
        return function("none", obj);
    }

    boolean function(String eventName, Object... obj);
}
