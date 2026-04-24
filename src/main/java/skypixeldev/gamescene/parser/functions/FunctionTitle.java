package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public class FunctionTitle implements IFunction {
    /**
     * sendTitle(player: $player$, title: $msg$, subtitle: $msg$ (, fadeIn: $tick$, stay: $tick$, fadeOut: $tick$ )
     * Defaults:
     *  stay - 5 * 20
     *  fadeIn - 0
     *  fadeOut - 0
     * @param argument
     * @return
     */
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean iAsync) {
        String playerName = Selector.searchNonNull(argument,"player","p","pl");
        String title = argument.get("title");
        String subtitle = argument.get("subtitle");
        if(Bukkit.getPlayer(playerName) == null){
            return false;
        }
        int fadeIn = Integer.parseInt(argument.getOrDefault("fadeIn","0"));
        int stay = Integer.parseInt(argument.getOrDefault("stay", "100"));
        int fadeOut = Integer.parseInt(argument.getOrDefault("fadeOut", "0"));
        Bukkit.getPlayer(playerName).sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        return true;
    }

    @Override
    public String getFunction() {
        return "sendTitle";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
