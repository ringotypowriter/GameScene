package skypixeldev.gamescene.parser.wildcard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.interfaces.IWildcard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class WildcardPlaceholder implements IWildcard {

    @Getter private WildcardPlayer playerWildcard;

    @Override
    public Map<String,String> replaceWildcard(Map<String, String> map) {
        if(!playerWildcard.getForReplace().isOnline()){
            return map;
        }
        Player player = playerWildcard.getForReplace().getPlayer();
        List<String> copyKeySet = new ArrayList<>(map.keySet());
        copyKeySet.forEach(key -> {
            map.compute(key,(val,old) -> {
                if(old != null) {
                    return PlaceholderAPI.setPlaceholders(player, old);
                }
                return null;
            });
        });
        return map;
    }

    @Override
    public String getWildcard() {
        return "placeholderapi";
    }
}
