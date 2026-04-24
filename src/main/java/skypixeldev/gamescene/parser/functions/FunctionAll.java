package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.FunctionManager;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.parser.wildcard.WildcardPlayer;
import skypixeldev.gamescene.scene.GameScene;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FunctionAll implements IFunction {
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String function = Selector.searchNonNull(argument,"function","func","msg","do","content","task");
        if(function == null){
            return false;
        }
        Parsing parsing;
        try{
            parsing = new Parsing(function);
        }catch (Exception e){
            e.printStackTrace();
            Log.lPf("Cannot parse function '" + function + "' in all functional");
            return true;
        }
        boolean inScene = Parsers.parseBoolean(Selector.searchNonNull(argument,"inScene","scene","restricted"));
        List<Player> selectors = new ArrayList<>();
        GameScene scene = Parsers.parseGameScene(argument.get("gameScene"), whereInvoke);
        if(inScene){
            scene.players().forEach(uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(selectors::add));
        }else{
            selectors.addAll(Bukkit.getOnlinePlayers());
        }
        selectors.forEach(pl -> FunctionManager.execute(parsing, scene, new WildcardPlayer(pl)));
        return true;
    }

    @Override
    public String getFunction() {
        return "all";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
