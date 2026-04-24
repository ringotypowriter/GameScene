package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.util.Vector;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public class FunctionApplyVelocity implements IFunction {
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        if(isAsync){
            return false;
        }
        double x = Parsers.parseDouble(argument.get("x"), 0);
        double y = Parsers.parseDouble(argument.get("y"), 0);
        double z = Parsers.parseDouble(argument.get("z"),0);
        String player = Selector.searchNonNull(argument,"player","p","pl");
        if(player == null || Bukkit.getPlayer(player) == null){
            return false;
        }
        Bukkit.getPlayer(player).setVelocity(new Vector(x,y,z));
        return true;
    }

    @Override
    public String getFunction() {
        return "applyVelocity";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
