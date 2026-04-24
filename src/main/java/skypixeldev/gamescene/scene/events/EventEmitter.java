package skypixeldev.gamescene.scene.events;

import lombok.Getter;
import skypixeldev.gamescene.scene.GameScene;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventEmitter {
    private final ConcurrentHashMap<String, List<EventFunction>> maps = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<EventFunction>> once = new ConcurrentHashMap<>();
    private final Vector<GlobalFunction> globalFunction = new Vector<>();

    private static EventEmitter pubEmitter;

    public static EventEmitter getPub(){
        if(pubEmitter == null){
            pubEmitter = new EventEmitter(null);
        }
        return pubEmitter;
    }

    @Getter private final GameScene scene;

    public EventEmitter(GameScene scene){
        this.scene = scene;
    }

    public void on(String evt, EventFunction func) {
        List<EventFunction> funcs = new ArrayList<>();
        if (maps.containsKey(evt)) {
            funcs = maps.get(evt);
        }
        funcs.add(func);
        maps.put(evt, funcs);
    }

    public void once(String evt, EventFunction func) {
        List<EventFunction> funcs = new ArrayList<>();
        if (once.containsKey(evt)) {
            funcs = once.get(evt);
        }
        funcs.add(func);
        once.put(evt, funcs);
    }

    public EventResult emit(String evt, Object... objects) {
        if ("error".equalsIgnoreCase(evt)) {
            throw new RuntimeException("error events: " + Arrays.toString(objects) + "");
        }
        boolean cancelled = false;
        Object[] copyArray = Arrays.copyOf(objects, objects.length);
        if (maps.containsKey(evt)) {
            for (EventFunction func : maps.get(evt)) {
                cancelled = func.function(copyArray);
            }
        }
        if (once.containsKey(evt)) {
            for (EventFunction func : once.get(evt)) {
                cancelled =func.function(copyArray);
            }
            once.get(evt).clear();
        }

        // Global Events
        for (GlobalFunction function : globalFunction) {
            cancelled = function.function(evt, copyArray);
        }

        return new EventResult(cancelled, copyArray);
    }
}
