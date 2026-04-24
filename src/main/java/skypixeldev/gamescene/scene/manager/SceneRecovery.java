package skypixeldev.gamescene.scene.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import skypixeldev.gamescene.Log;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SceneRecovery {
    private static Cache<UUID, Map<String,Object>> dataRecovery;
    static{
        dataRecovery = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener((key, value, cause) -> {
                    if(key == null || value == null){
                        return;
                    }
                    if(cause == RemovalCause.EXPIRED){
                        Log.l("[" + key.toString() + "] has been removed whose owner cannot reconnect.");
                    }
                })
                .build();
    }
    public static void putRecovery(ScenePlayer player){
        putRecovery(player.getUniqueID(), player.cloneData());
    }

    public static void putRecovery(UUID uuid, Map<String,Object> cloneData){
        dataRecovery.put(uuid, cloneData);
    }


    public static void recoverPlayer(ScenePlayer player){
        Optional.ofNullable(dataRecovery.getIfPresent(player.getUniqueID())).ifPresent(player::importData);
    }


}
