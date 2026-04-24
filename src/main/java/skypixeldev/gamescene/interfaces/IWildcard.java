package skypixeldev.gamescene.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IWildcard {
    Map<String,String> replaceWildcard(Map<String,String> argumentsToReplace);
    default Map<String,String> replaceValues(Map<String,String> map, String regex, String replacement){
        List<String> copyKeySet = new ArrayList<>(map.keySet());
        copyKeySet.forEach(key -> {
            map.compute(key,(val,old) -> {
                if(old != null) {
                    return old.replaceAll(regex, replacement);
                }
                return null;
            });
        });
        return map;
    }
    String getWildcard();
}
