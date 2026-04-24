package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public class FunctionMessage implements IFunction {

    /**
     * sendMessage(player: $player$, msg: $msg$ (, isAction: true/false )
     * @param argument
     * @return
     */
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String playerName = Selector.searchNonNull(argument,"player","p","pl");
        String msg = argument.get("msg");
        if(Bukkit.getPlayer(playerName) == null){
            Log.lPf("cannot found player " + playerName);
            return false;
        }
        boolean isAction = false;
        if(argument.containsKey("isAction")){
            isAction = Parsers.parseBoolean(argument.get("isAction"));
        }
        if(isAction){
            Bukkit.getPlayer(playerName).sendActionBar(msg);
        }else {
            Bukkit.getPlayer(playerName).sendMessage(msg);
        }
        return true;
    }

    @Override
    public String getFunction() {
        return "sendMessage";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
