package skypixeldev.gamescene.scene.gameObject;

import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.scene.GameScene;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public class GObjectManager {
    private static ConcurrentHashMap<String, Class<? extends BaseObject>> objects = new ConcurrentHashMap<>();

    public static void register(String key, Class<? extends BaseObject> clazz){
        objects.put(key, clazz);
        Log.lGo("Imported GameObject " + key + " from " + clazz.getName());
    }

    public static int getLoadedGObjectSize(){
        return objects.size();
    }

    /*
    自动按照严格的类名格式注册 GameObject实现类
    GObject_____
     */
    public static void registerStrictly(Class<? extends BaseObject> clazz){
        String key = clazz.getSimpleName();
        if(key.startsWith("GObject")){
            key.replaceFirst("GObject", "");
        }
        register(key,clazz);
    }

    public static Class<? extends BaseObject> getImplementClass(String key){
        return objects.get(key);
    }

    public static BaseObject getImplement(String objectType, GameScene createBy){
        if(!objects.containsKey(objectType)){
            throw new IllegalArgumentException("Cannot found gameObject by searching " + objectType);
        }
        try {
            return objects.get(objectType).getDeclaredConstructor(GameScene.class).newInstance(createBy);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to construct gameObject implement");
        }
    }
}
