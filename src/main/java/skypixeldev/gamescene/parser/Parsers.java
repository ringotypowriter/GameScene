package skypixeldev.gamescene.parser;

import lombok.NonNull;
import lombok.val;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.manager.SceneManager;

import java.util.*;

public class Parsers {
    public static List<Parsing> parseFunctions(Collection<String> functions){
        ArrayList<Parsing> list = new ArrayList<>();
        functions.forEach(str -> list.add(new Parsing(str)));
        return list;
    }
    public static List<Parsing> parseFunctions(String... functions){
        if(functions == null){
            throw new IllegalArgumentException("functions cannot be null");
        }
        return parseFunctions(Arrays.asList(functions));
    }

    public static List<String> parseList(String originalArgument){
        if(originalArgument == null){
            return Collections.emptyList();
        }
        if(!originalArgument.contains(";")){
            return Collections.singletonList(originalArgument);
        }
        return new ArrayList<>(Arrays.asList(originalArgument.split(";")));
    }

    public static double parseDouble(String val,@NonNull double def){
        if(val == null){
            return def;
        }

        try{
            return Double.parseDouble(val);
        }catch (NumberFormatException exc){
            Log.lPf("Cannot parse " + val + " as a double");
            return def;
        }
    }

    public static int parseInteger(String val,@NonNull int def){
        if(val == null){
            return def;
        }

        try{
            return Integer.parseInt(val);
        }catch (NumberFormatException exc){
            return def;
        }
    }

    public static GameScene parseGameScene(String val,GameScene whoInvoke){
        return Optional.ofNullable(SceneManager.searchSceneByName(val)).orElse(whoInvoke);
    }

    public static boolean parseBoolean(String val){
        if(val == null){
            return false;
        }
        String var = val.toLowerCase();
        if(var.equals("yes") ||
                var.equals("true") ||
                var.equals("right") ||
                var.equals("correct") ||
                var.equals("allowed") ||
                var.equals("allow") ||
                var.equals("permitted") ||
                var.equals("on")){
            return true;
        }
        return false;
    }

    public static byte parseByte(String val,@NonNull byte def){
        if(val == null){
            return def;
        }
        try{
            return Byte.parseByte(val);
        }catch (NumberFormatException exc){
            Log.lPf("Cannot parse " + val + " as a byte");
            return def;
        }
    }
}
