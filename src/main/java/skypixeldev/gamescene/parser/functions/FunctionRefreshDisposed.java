package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public class FunctionRefreshDisposed implements IFunction {
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String playerName = Selector.searchNonNull(argument,"player","p","pl");
        String objectName = Selector.searchNonNull(argument,"object","gObject","gameObject","go");
        if(Bukkit.getPlayer(playerName) == null){
            return false;
        }
        if(objectName == null){
            return false;
        }
        Player player = Bukkit.getPlayer(playerName);
        whereInvoke.getScenePlayer(player.getUniqueId()).ifPresent(sp -> sp.invalidateData("Disposed-" + objectName));
        return true;
    }

    @Override
    public String getFunction() {
        return "refreshDisposed";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
