package skypixeldev.gamescene.parser.wildcard;

import lombok.Getter;
import org.bukkit.OfflinePlayer;
import skypixeldev.gamescene.interfaces.IWildcard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WildcardPlayer implements IWildcard {

    @Override
    public Map<String,String> replaceWildcard(Map<String, String> argumentsToReplace) {
        List<String> copyKeySet = new ArrayList<>(argumentsToReplace.keySet());
        copyKeySet.forEach(key -> {
            argumentsToReplace.compute(key,(val,old) -> {
                if(old != null) {
                    return old.replaceAll("\\$" + getWildcard() + "\\$", forReplace.getName());
                }
                return null;
            });
        });
        return argumentsToReplace;
    }

    @Getter private OfflinePlayer forReplace;

    public WildcardPlayer(OfflinePlayer player){
        this.forReplace = player;
    }


    @Override
    public String getWildcard() {
        return "player";
    }
}
