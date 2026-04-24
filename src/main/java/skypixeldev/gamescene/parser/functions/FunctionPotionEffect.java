package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;
import java.util.logging.Level;

public class FunctionPotionEffect implements IFunction {
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String playerName = Selector.searchNonNull(argument,"p","player","pl");
        String potName = Selector.searchNonNull(argument,"pot","pe","poteff","effect","potionEffect");
        int level = Parsers.parseInteger(Selector.searchNonNull(argument,"level,amplifier","lvl","amp","l","a"),0);
        int duration = Parsers.parseInteger(Selector.searchNonNull(argument,"duration","dur","tick","d","ticks","t"),20);

        boolean force = Parsers.parseBoolean(Selector.searchNonNull(argument,"force","f"));

        if(Bukkit.getPlayer(playerName) == null) return false;
        if(PotionEffectType.getByName(potName.toUpperCase()) == null){
            Bukkit.getLogger().log(Level.WARNING,"unknown potion effect name: " + potName, new IllegalArgumentException());
            return true;
        }
        Bukkit.getPlayer(playerName).addPotionEffect(new PotionEffect(PotionEffectType.getByName(potName.toUpperCase()), duration, level), force);
        return true;
    }

    @Override
    public String getFunction() {
        return "addPotionEffect";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
