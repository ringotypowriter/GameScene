package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public class FunctionLog implements IFunction {
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        Bukkit.getConsoleSender().sendMessage(argument.get("msg"));
        return true;
    }

    @Override
    public String getFunction() {
        return "log";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
