package skypixeldev.gamescene.parser.wildcard;

import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.export.RangedRandom;
import skypixeldev.gamescene.interfaces.IWildcard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WildcardRandom implements IWildcard{
    private Random random = new Random(System.currentTimeMillis() - 2000);
    @Override
    public Map<String,String> replaceWildcard(Map<String, String> argumentsToReplace) {
        boolean foundWildcard = false;
        for(String s : argumentsToReplace.values()){
            if(s.contains("$random$")){
                foundWildcard = true;
            }
        }

        if(foundWildcard) {
            int randomMin = Integer.parseInt(argumentsToReplace.getOrDefault("randomMin", "-1"));
            int randomMax = Integer.parseInt(argumentsToReplace.getOrDefault("randomMax", "-1"));
            if (randomMax == randomMin) {
                Log.lPf("a function used \\$random\\$ but doesn't define max or min");
                return argumentsToReplace;
            }
            int result = new RangedRandom(randomMin,randomMax).nextInt();

            List<String> copyKeySet = new ArrayList<>(argumentsToReplace.keySet());
            copyKeySet.forEach(key -> {
                argumentsToReplace.compute(key, (val, old) -> {
                    if (old != null) {
                        return old.replaceAll("\\$" + getWildcard() + "\\$", result + "");
                    }
                    return null;
                });
            });
        }
        return argumentsToReplace;
    }

    @Override
    public String getWildcard() {
        return "random";
    }
}
