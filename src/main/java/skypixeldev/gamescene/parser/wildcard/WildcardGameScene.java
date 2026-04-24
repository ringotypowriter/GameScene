package skypixeldev.gamescene.parser.wildcard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import skypixeldev.gamescene.interfaces.IWildcard;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

@AllArgsConstructor
public class WildcardGameScene implements IWildcard {
    @Getter private GameScene scene;
    @Override
    public Map<String,String> replaceWildcard(Map<String, String> argumentsToReplace) {
        return this.replaceValues(argumentsToReplace, "\\$" + getWildcard() + "\\$", scene.getName());
    }

    @Override
    public String getWildcard() {
        return "game_scene";
    }
}
