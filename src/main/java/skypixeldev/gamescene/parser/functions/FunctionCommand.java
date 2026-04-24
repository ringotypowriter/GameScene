package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.exception.ParsingFunctionUnsupportedException;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public class FunctionCommand implements IFunction {
    /**
     *  consoleCommand(command: $commandContent$)
     *
     * @param argument
     * @param whereInvoke
     * @param isAsync
     * @return
     */
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        if(isAsync) throw new ParsingFunctionUnsupportedException();
        if(!argument.containsKey("command")) return false;

        String command = argument.get("command");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        return true;
    }

    @Override
    public String getFunction() {
        return "consoleCommand";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
