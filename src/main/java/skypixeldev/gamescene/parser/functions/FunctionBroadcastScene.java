package skypixeldev.gamescene.parser.functions;

import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.manager.SceneManager;

import java.util.Map;

public class FunctionBroadcastScene implements IFunction {

    /**
     *  broadcastInScene(msg: $msg$, isAction: $actionBoolean$, <gameScene: $gameScene$> )
     * @param argument
     * @param whereInvoke
     * @param isAsync
     * @return
     */
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        GameScene scene = whereInvoke;
        if(argument.containsKey("gameScene")){
            scene = SceneManager.searchSceneByName(argument.get("gameScene"));
        }
        String msg = argument.get("msg");
        boolean isAction = Parsers.parseBoolean(argument.getOrDefault("isAction", "false"));
        if(!isAction) {
            scene.broadcastMessage(msg);
        } else {
            scene.doPipeline(pl -> {
                pl.sendActionBar(msg);
            });
        }
        return true;
    }

    @Override
    public String getFunction() {
        return "broadcastInScene";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
