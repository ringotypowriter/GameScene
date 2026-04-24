package skypixeldev.gamescene.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class OptionBuilder {
    private String lastKey;
    private HashMap<String,Object> valueMap;
    private OptionBuilder(){
        valueMap = new HashMap<>();
    }

    public static OptionBuilder of(){
        return new OptionBuilder();
    }

    public static OptionBuilder of(ConfigurationSection section){
        OptionBuilder builder = of();
        if(section == null){
            return builder;
        }
        for(String key : section.getKeys(false)){
            builder = builder.pair(key, section.get(key));
        }
        return builder;
    }

    public OptionBuilder key(String key){
        this.lastKey = key;
        return this;
    }

    public OptionBuilder append(OptionBuilder builder){
        builder.build().forEach(this::pair);
        return this;
    }

    public OptionBuilder append(Map<String,Object> map){
        map.forEach(this::pair);
        return this;
    }

    public OptionBuilder pair(String key, Object val){
        return this.key(key).value(val);
    }

    public OptionBuilder value(Object value){
        if(this.lastKey == null){
            throw new IllegalStateException("Last key is null");
        }
        valueMap.put(lastKey, value);
        lastKey = null;
        return this;
    }

    public Map<String,Object> build(){
        return valueMap;
    }
}
