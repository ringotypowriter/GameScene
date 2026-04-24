package skypixeldev.gamescene.scene.pub.property;

import lombok.NonNull;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.manager.SceneManager;
import skypixeldev.gamescene.scene.manager.ScenePlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Properties {
    public static void addProperty(ScenePlayer player,@NonNull PlayerProperty property){
        if(player == null){
            return;
        }
        player.setData(generateKey(property), true);
    }

    private static String generateKey(PlayerProperty property){
        return "Properties-" + property.name().toLowerCase();
    }

    public static void removeProperty(ScenePlayer player, PlayerProperty property){
        if(player == null){
            return;
        }
        player.invalidateData(generateKey(property));
    }

    public static Set<PlayerProperty> getAvailableProperty(ScenePlayer player){
        if(player == null){
            return Collections.emptySet();
        }
        Set<PlayerProperty> properties = new HashSet<>();
        for(PlayerProperty prop : PlayerProperty.values()){
            if(player.existData(generateKey(prop))){
                properties.add(prop);
            }
        }
        return properties;
    }

    public static void addProperty(Player player, GameScene scene, PlayerProperty property){
        addProperty(scene.getScenePlayerIfPresent(player.getUniqueId()), property);
    }

    public static void removeProperty(Player player, GameScene scene, PlayerProperty property){
        removeProperty(scene.getScenePlayerIfPresent(player.getUniqueId()), property);
    }

    public static Set<PlayerProperty> getAvailableProperty(Player player, GameScene scene){
        return getAvailableProperty(scene.getScenePlayerIfPresent(player.getUniqueId()));
    }

    public static boolean hasProperty(ScenePlayer player, PlayerProperty property){
        if(player == null){
            throw new IllegalArgumentException("player cannot be null");
        }
        return player.existData(generateKey(property));
    }

    public static boolean hasProperty(Player player, GameScene scene, PlayerProperty property){
        return hasProperty(scene.getScenePlayerIfPresent(player.getUniqueId()), property);
    }
}
