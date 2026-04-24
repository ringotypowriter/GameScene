package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.pub.property.PlayerProperty;
import skypixeldev.gamescene.scene.pub.property.Properties;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionClearProperties implements IFunction {
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String playerName = Selector.searchNonNull(argument,"p","player","pl");
        if(Bukkit.getPlayer(playerName) == null) return false;

        Arrays.stream(PlayerProperty.values()).forEach(p -> Properties.removeProperty(Bukkit.getPlayer(playerName), whereInvoke, p));

        return true;
    }

    @Override
    public String getFunction() {
        return "clearPlayerProperties";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
