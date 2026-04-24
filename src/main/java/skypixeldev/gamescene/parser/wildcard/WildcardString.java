package skypixeldev.gamescene.parser.wildcard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import skypixeldev.gamescene.interfaces.IWildcard;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

@AllArgsConstructor
public class WildcardString implements IWildcard {
    @Getter private String wildcardIdentifier;
    @Getter private String replacement;
    @Override
    public Map<String,String> replaceWildcard(Map<String, String> argumentsToReplace) {
        return this.replaceValues(argumentsToReplace, "\\$" + getWildcard() + "\\$", getReplacement());
    }

    @Override
    public String getWildcard() {
        return getWildcardIdentifier();
    }
}
