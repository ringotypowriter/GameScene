package skypixeldev.gamescene.scene;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import skypixeldev.gamescene.Utils;
import skypixeldev.gamescene.config.GConfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class Locations {
    @Getter private GameScene scene;
    @Getter private GConfig config;

    private Supplier<World> worldReplacer;
    public Locations(GameScene scene){
        this.scene = scene;
        this.config = scene.getProvider().getOrCreateConfig("Locations");
        this.config.saveToFile();
    }

    // Format: worldName,x,y,z,yaw,pitch
    //             0     1 2 3  4    5

    public Set<Location> search(String regex){
        if(regex == null){
            return Collections.emptySet();
        }
        Set<Location> result = new HashSet<>();
        for(String key : config.getConfiguration().getKeys(false)){
            if(key.matches(regex)){
                result.add(get(key));
            }
        }
        return result;
    }

    public void setWorldReplacer(Supplier<World> replacer){
        this.worldReplacer = replacer;
    }

    public Set<Location> searchByStart(String chars){
        if(chars == null){
            return Collections.emptySet();
        }
        Set<Location> result = new HashSet<>();
        for(String key : config.getConfiguration().getKeys(false)){
            if(key.startsWith(chars)){
                result.add(get(key));
            }
        }
        return result;
    }

    public boolean contains(String key){
        return config.getConfiguration().contains(key);
    }

    public Location get(String key){
        if(key == null){
            return null;
        }
        if(!config.getConfiguration().contains(key)){
            return null;
        }
        if(!config.getConfiguration().isString(key)){
            return null;
        }
        if(worldReplacer != null){
            Location r = Utils.stringToLocation(config.getString(key));
            r.setWorld(worldReplacer.get());
            return r;
        }
        return Utils.stringToLocation(config.getString(key));
    }

    public void save(String key, Location location){
        config.set(key, Utils.locationToString(location));
    }
}
