package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.manager.SceneManager;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FunctionTeleport implements IFunction {
    /**
     *  teleport(player: $player$, location: $locationKey$ (, gameScene: $gameScene$) )
     *  gameScene 可选, 若其不存在, 则默认选用 调用该方法的Game Scene对象
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
        String locationName = Selector.searchNonNull(argument,"l","loc","location","locate","locations");
        GameScene scene = whereInvoke;
        if(argument.containsKey("gameScene")){
            scene = SceneManager.searchSceneByName(argument.get("gameScene"));
        }
        return Optional.ofNullable(scene.getLocations().get(locationName)).map(player::teleport).orElse(false);
    }

    @Override
    public String getFunction() {
        return "teleport";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
