package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.util.Vector;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public class FunctionApplyVelocityDelayed implements IFunction {
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        if(isAsync){
            return false;
        }
        final double x = Parsers.parseDouble(argument.get("x"), 0);
        final double y = Parsers.parseDouble(argument.get("y"), 0);
        final double z = Parsers.parseDouble(argument.get("z"),0);
        final int ticks = Parsers.parseInteger("ticks",0);
        final String player = Selector.searchNonNull(argument,"player","p","pl");
        if(player == null || Bukkit.getPlayer(player) == null){
            return false;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bootstrap.getInstance(), () -> Bukkit.getPlayer(player).setVelocity(new Vector(x,y,z)), ticks);
        return true;
    }

    @Override
    public String getFunction() {
        return "applyVelocityDelayed";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
