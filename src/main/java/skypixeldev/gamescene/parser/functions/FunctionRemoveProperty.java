package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.pub.property.PlayerProperty;
import skypixeldev.gamescene.scene.pub.property.Properties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionRemoveProperty implements IFunction {
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String playerName = Selector.searchNonNull(argument,"p","player","pl");
        String properties = Selector.searchNonNull(argument,"prop","property","properties");
        if(Bukkit.getPlayer(playerName) == null || properties == null) return false;

        List<PlayerProperty> property = Parsers.parseList(properties).stream().flatMap(s -> {
            try{
                return Stream.of(PlayerProperty.valueOf(s.toUpperCase()));
            }catch (Throwable throwable){
                Log.lPf(s + " is not a legal property");
                return Stream.empty();
            }
        }).collect(Collectors.toList());

        property.forEach(p -> Properties.removeProperty(Bukkit.getPlayer(playerName), whereInvoke, p));

        return true;
    }

    @Override
    public String getFunction() {
        return "removePlayerProperty";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
