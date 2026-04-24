package skypixeldev.gamescene.parser.functions;

import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.manager.SceneManager;

import java.util.Map;

public class FunctionActivateScheduler implements IFunction {
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        if(!argument.containsKey("scheduler")){
            return false;
        }
        GameScene scene = whereInvoke;
        if(argument.containsKey("gameScene")){
            scene = SceneManager.searchSceneByName(argument.get("gameScene"));
        }
        scene.getSchedulers().activate(argument.get("scheduler"));
        return true;
    }

    @Override
    public String getFunction() {
        return "activateScheduler";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
