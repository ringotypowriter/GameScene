package skypixeldev.gamescene.parser.functions;

import org.bukkit.Location;
import org.bukkit.Sound;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.manager.SceneManager;

import java.util.List;
import java.util.Map;

public class FunctionBroadcastNearby implements IFunction {
    /**
     * broadcastNearby(location: $locationKey$, msg: $message$, radius: $radius$ (, gameScene: $gameScene$, isAction: $isAction$) )
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
        List<String> msg = Parsers.parseList(argument.get("msg"));
        double radius = Double.parseDouble(argument.getOrDefault("radius", "3"));
        boolean isAction = Parsers.parseBoolean(argument.getOrDefault("isAction", "false"));
        if(!isAction)
            location.getWorld().getNearbyPlayers(location, radius).forEach(pl -> msg.forEach(pl::sendMessage));
        else
            location.getWorld().getNearbyPlayers(location, radius).forEach(pl -> msg.forEach(pl::sendActionBar));
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
