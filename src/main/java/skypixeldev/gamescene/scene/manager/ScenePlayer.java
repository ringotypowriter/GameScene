package skypixeldev.gamescene.scene.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.scene.GameScene;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class ScenePlayer {
//    @Getter private Player player;
    private Cache<String,Object> data;
    private GameScene fromScene;
    private UUID uuid;
    private String nameCache;
    public ScenePlayer(Player player, GameScene scene){
//        this.player = player;
        this.uuid = player.getUniqueId();
        nameCache = player.getName();
        this.fromScene = scene;
        data = Caffeine.newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .expireAfterWrite(1,TimeUnit.HOURS)
                .maximumSize(2^16)
                .removalListener((key, value, cause) -> {
                    Log.l("[" + uuid + "] " + "A Data of " + key + " has been expired.");
                })
                .build();
        // If has recover
        SceneRecovery.recoverPlayer(this);
    }

    public String getName(){
        return nameCache;
    }

    public Player getPlayerIfExist(){
        return Bukkit.getPlayer(uuid);
    }

    public Optional<Player> getPlayer(){
        return Optional.ofNullable(getPlayerIfExist());
    }

    public GameScene getScene() {
        return fromScene;
    }

    public void setData(String key, Object obj){
        if(key.startsWith("_")){
            throw new IllegalArgumentException("key " + key + " is a restricted key which cannot be used for custom data!");
        }
        this.data.put(key, obj);
    }

    public void invalidateDataByFilter(Predicate<String> filter){
        Set<String> keys = new HashSet<>();
        data.asMap().keySet().forEach(key -> {
            if(filter.test(key)) keys.add(key);
        });
        data.invalidateAll(keys);
    }

    public void invalidateData(String key){
        data.invalidate(key);
    }

    protected void setDataRestricted(String key, Object obj){
        this.data.put("_" + key, obj);
    }

    protected Object getDataRestricted(String key, Object def){
        return this.data.get("_" + key, newKey -> def);
    }

    protected Object getDataRestricted(String key){
        return getDataRestricted(key,null);
    }

    public Object getDataOrDefault(String key, Object def){
        return Optional.ofNullable(data.getIfPresent(key)).orElse(def);
    }

    public boolean existData(String key){
        return getData(key) != null || data.asMap().containsKey(key);
    }


    public Object getData(String key){
        return getDataOrDefault(key,null);
    }

    public void resetData(){
        data.invalidateAll();
    }

    public Map<String,Object> cloneData(){
        Map<String,Object> maps = new ConcurrentHashMap<>(data.asMap());
//        maps.remove(null);
        return Collections.unmodifiableMap(maps);
    }

    public void importData(Map<String,Object> map){
        data.invalidateAll();
        data.putAll(map);
    }

    public UUID getUniqueID(){
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScenePlayer that = (ScenePlayer) o;
        return Objects.equals(uuid, that.getUniqueID());
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
