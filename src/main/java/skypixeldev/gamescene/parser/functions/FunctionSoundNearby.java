package skypixeldev.gamescene.parser.functions;

import org.bukkit.Location;
import org.bukkit.Sound;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.manager.SceneManager;

import java.util.Map;

public class FunctionSoundNearby implements IFunction {
    /**
     * soundNearby(location: $locationKey$, sound: $enumName$ (, volume: $volumeFloat$, pitch: $pitchFloat$, gameScene: $gameScene$) )
     *
     * @param argument
     * @param whereInvoke
     * @param isAsync
     * @return
     */
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String locationName = argument.get("location");
        GameScene scene = whereInvoke;
        if(argument.containsKey("gameScene")){
            scene = SceneManager.searchSceneByName(argument.get("gameScene"));
        }
        if(!scene.getLocations().contains(locationName)){
            return false;
        }
        Location location = scene.getLocations().get(locationName);
        Sound sound = Sound.BLOCK_NOTE_HARP;
        float volume = Float.parseFloat(argument.getOrDefault("volume", "1"));
        float pitch = Float.parseFloat(argument.getOrDefault("pitch", "1"));
        try{
            sound = Sound.valueOf(argument.getOrDefault("sound", sound.name()));
        } catch (IllegalArgumentException | NullPointerException exc){
            scene.broadcastMessage("§c<Internal PF Exception>: Unknown SoundType: " + argument.get("sound"));
        }
        location.getWorld().playSound(location, sound, volume, pitch);
        return true;
    }

    @Override
    public String getFunction() {
        return "soundNearby";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
