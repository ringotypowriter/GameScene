package skypixeldev.gamescene.parser.wildcard;

import skypixeldev.gamescene.interfaces.IWildcard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WildcardFormatCode implements IWildcard {
    @Override
    public Map<String,String> replaceWildcard(Map<String, String> argumentsToReplace) {
        List<String> copyKeySet = new ArrayList<>(argumentsToReplace.keySet());
        copyKeySet.forEach(key -> {
            argumentsToReplace.compute(key,(val,old) -> {
                if(old != null) {
                    return old.replaceAll("&", "§").replaceAll("\\$and\\$","&");
                }
                return null;
            });
        });
        return argumentsToReplace;
    }

    @Override
    public String getWildcard() {
        return "formatCode";
    }
}
