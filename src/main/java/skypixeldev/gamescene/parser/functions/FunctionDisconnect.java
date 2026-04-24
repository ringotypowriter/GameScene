package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;
import java.util.Optional;

public class FunctionDisconnect implements IFunction {
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String playerName = Selector.searchNonNull(argument,"player","p","pl");
        if(Bukkit.getPlayer(playerName) == null){
            return false;
        }
        String reason = Optional.ofNullable(Selector.searchNonNull(argument,"reason","r","content","text","t","c","message","msg","m")).orElse("服务器内部转发, 你被强制断线");
        Bukkit.getPlayer(playerName).kickPlayer(reason);
        return true;
    }

    @Override
    public String getFunction() {
        return "disconnect";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
