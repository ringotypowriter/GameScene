package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public class FunctionRemoveEffect implements IFunction {
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String playerName = Selector.searchNonNull(argument,"p","player","pl");
        String potName = Selector.searchNonNull(argument,"pot","pe","poteff","effect","potionEffect");
        if(Bukkit.getPlayer(playerName) == null) return false;
        Player player = Bukkit.getPlayer(playerName);
        if(potName == null || PotionEffectType.getByName(potName.toUpperCase()) == null){
            player.getActivePotionEffects().forEach(eff -> player.removePotionEffect(eff.getType()));
        }else{
            player.removePotionEffect(PotionEffectType.getByName(potName.toUpperCase()));
        }
        return true;
    }

    @Override
    public String getFunction() {
        return "removePotionEffect";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
