package skypixeldev.gamescene.scene;

import lombok.Getter;
import lombok.val;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.Utils;
import skypixeldev.gamescene.config.GConfig;
import skypixeldev.gamescene.config.OptionBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Variables {
    @Getter private GameScene scene;
    @Getter private GConfig config;
    private ConcurrentHashMap<String,Object> variables;
    public Variables(GameScene scene){
        this.scene = scene;
        this.config = scene.getProvider().getOrCreateConfig("Variables");
        if(config.getConfiguration().getConfigurationSection("VariableMaps") == null){
            config.getConfiguration().createSection("VariableMaps", OptionBuilder.of()
                    .key("GeneratedAt").value(Utils.getCurrentFormatTime())
                    .key("GeneratedBy").value("SkyPixel - GameScene v" + Bootstrap.getInstance().getDescription().getVersion())
                    .key("SceneCreator").value("Unknown")
                    .build());
        }

        variables = new ConcurrentHashMap<>();
        OptionBuilder.of(config.getConfiguration().getConfigurationSection("VariableMaps")).build()
                .forEach(variables::put);

        this.config.saveToFile();
    }

    public void reset(){
        variables.clear();
        OptionBuilder.of(config.getConfiguration().getConfigurationSection("VariableMaps")).build()
                .forEach(variables::put);
    }

    public boolean existKey(String key){
        return variables.containsKey(key);
    }

    public void setString(String key,String val){
        this.variables.put(key,val);
    }

    public void setInteger(String key, Integer val){
        this.variables.put(key, val);
    }

    public void setDouble(String key, Double val){
        this.variables.put(key, val);
    }

    public void setBoolean(String key, Boolean val){
        this.variables.put(key, val);
    }


    public String getString(String key){
        return (String) this.variables.get(key);
    }

    public boolean getBoolean(String key){
        return (Boolean) this.variables.get(key);
    }

    public int getInt(String key){
        return (Integer) this.variables.get(key);
    }

    public double getDouble(String key){
        return (Double) this.variables.get(key);
    }

    public List<?> getList(String key){
        return (List<?>) this.variables.get(key);
    }


    public void increaseInteger(String key, int val){
        if(variables.get(key) instanceof Integer){
            variables.replace(key, ((Integer)variables.getOrDefault(key, 0)) + val);
        }
    }

    public void increaseDouble(String key, double val){
        if(variables.get(key) instanceof Double){
            variables.replace(key, ((Double)variables.getOrDefault(key, 0d)) + val);
        }
    }

    public void reverseBoolean(String key){
        if(variables.get(key) instanceof Boolean){
            variables.replace(key, !(Boolean) variables.get(key));
        }
    }

    public void appendStringList(String key, String entry){
        if(variables.containsKey(key) && variables.get(key) instanceof List<?>){
           ((List<String>) variables.get(key)).add(entry);
        }
    }

    public void removeStringList(String key, String entry){
        if(variables.containsKey(key) && variables.get(key) instanceof List<?>){
            ((List<String>) variables.get(key)).remove(entry);
        }
    }

    public Map<String,Object> entryMap(){
        return Collections.unmodifiableMap(variables);
    }


}
