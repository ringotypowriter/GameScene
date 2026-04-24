package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public class FunctionBroadcast implements IFunction {

    /**
     * broadcast(msg: $msg$)
     * @param argument
     * @return
     */
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        Bukkit.broadcastMessage(argument.get("msg"));
        return true;
    }

    @Override
    public String getFunction() {
        return "broadcast";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
