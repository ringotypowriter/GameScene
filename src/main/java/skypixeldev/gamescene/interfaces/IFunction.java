package skypixeldev.gamescene.interfaces;

import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public interface IFunction {
    boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync);
    String getFunction();
    String getProvider();
}
