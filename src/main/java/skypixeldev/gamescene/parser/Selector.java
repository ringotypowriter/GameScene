package skypixeldev.gamescene.parser;

import java.util.Map;

public final class Selector {
    public static String searchNonNull(Map<String,String> arguments, String... alias){
        assert arguments != null;
        String result = null;
        for(String s : alias){
            result = arguments.get(s);
            if(result != null){
                break;
            }
        }
        return result;
    }

    public static void fill(Map<String,String> arguments, String fillWith, String... alias){
        assert alias != null && arguments != null;
        for(String s : alias){
            arguments.putIfAbsent(s, fillWith);
        }
    }
}
