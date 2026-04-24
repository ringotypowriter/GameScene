package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;
import java.util.logging.Level;

public class FunctionSetGameMode implements IFunction {

    private static final GameMode[] GAME_MODES_ID = {GameMode.SURVIVAL, GameMode.CREATIVE, GameMode.ADVENTURE, GameMode.SPECTATOR};


    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String playerName = Selector.searchNonNull(argument,"player","p","pl");
        String gameMode = Selector.searchNonNull(argument,"gameMode","GameMode","gMode","gm");
        if(Bukkit.getPlayer(playerName) == null){
            return false;
        }
        GameMode mode = null;
        int intMode = Parsers.parseInteger(gameMode,-1);
        if(intMode >= 0 && intMode < GAME_MODES_ID.length){
            mode = GAME_MODES_ID[intMode];
        }else{
            try {
                mode = GameMode.valueOf(gameMode.toUpperCase());
            }catch (Throwable throwable){
                Bukkit.getLogger().log(Level.WARNING,"cannot found gamemode " + gameMode, throwable);
                return false;
            }
        }
        Bukkit.getPlayer(playerName).setGameMode(mode);
        return true;
    }

    @Override
    public String getFunction() {
        return "setGameMode";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
