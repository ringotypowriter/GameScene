package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public class FunctionSoundPlayer implements IFunction {
    /**
     * soundPlayer(player: $player$, sound: $enumName$ (, volume: $volumeFloat$, pitch: $pitchFloat$) )
     *
     * @param argument
     * @param whereInvoke
     * @param isAsync
     * @return
     */
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String playerName = Selector.searchNonNull(argument,"player","p","pl");
        if(Bukkit.getPlayer(playerName) == null){
            return false;
        }
        Player player = Bukkit.getPlayer(playerName);
        Sound sound = Sound.BLOCK_NOTE_HARP;
        float volume = Float.parseFloat(argument.getOrDefault("volume", "1"));
        float pitch = Float.parseFloat(argument.getOrDefault("pitch", "1"));
        try{
            sound = Sound.valueOf(argument.getOrDefault("sound", argument.getOrDefault("sound","BLOCK_WOODEN_BUTTON_CLICK_OFF").toUpperCase().replaceAll("\"","")));
        } catch (IllegalArgumentException | NullPointerException exc){
            player.sendMessage("§c<Internal PF Exception>: Unknown SoundType: " + argument.get("sound"));
        }
        player.playSound(player.getLocation(), sound, volume, pitch);
        return true;
    }

    @Override
    public String getFunction() {
        return "soundPlayer";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
