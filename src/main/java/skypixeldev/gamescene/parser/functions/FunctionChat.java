package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.parser.exception.ParsingFunctionUnsupportedException;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public class FunctionChat implements IFunction {
    /**
     * playerChat(player:$player$, chat: $chatContent$)
     * @param argument
     * @param whereInvoke
     * @param isAsync
     * @return
     */
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        if(isAsync) throw new ParsingFunctionUnsupportedException();
        if(!argument.containsKey("chat")) return false;

        String playerName = Selector.searchNonNull(argument,"player","p","pl");
        if(Bukkit.getPlayer(playerName) == null){
            return false;
        }
        Player player = Bukkit.getPlayer(playerName);
        player.chat(argument.get("chat"));
        return true;
    }

    @Override
    public String getFunction() {
        return "playerChat";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
