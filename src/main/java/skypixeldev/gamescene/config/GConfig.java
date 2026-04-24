package skypixeldev.gamescene.config;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GConfig {
    @Getter protected File configFile;
    @Getter protected FileConfiguration configuration;

    public static GConfig of(Plugin owner, String name, OptionBuilder defaults){
        return new GConfig(owner.getDataFolder(), name, defaults);
    }

    public static GConfig of(File folder, String name, OptionBuilder defaults){
        return new GConfig(folder, name, defaults);
    }

    public static GConfig of(Plugin owner, String name){
        return of(owner, name, null);
    }


    protected GConfig(){

    }


    protected GConfig(File folder, String configName, OptionBuilder defaults){
        folder.mkdirs();
        this.configFile = new File(folder,configName + ".yml");
        if(!configFile.exists()){
            try {
                if(!configFile.createNewFile()){
                    throw new IllegalArgumentException("无法创建配置文件 " + configName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        configuration = YamlConfiguration.loadConfiguration(configFile);

        if(defaults != null) {
            defaults.build().forEach((key, val) -> {
                if (!configuration.contains(key)) {
                    configuration.set(key, val);
                }
            });
            saveToFile();
        }
    }

    public void setInPipeline(Consumer<ConfigurationSection> pipeline){
        pipeline.accept(configuration);
        saveToFile();
    }

    /**
     * 保存文件的Set
     * @param key
     * @param val
     */
    public void set(String key, Object val){
        configuration.set(key,val);
        saveToFile();
    }

    /**
     * 不保存文件的Set
     * @param key
     * @param val
     */
    private void rawSet(String key, Object val){
        configuration.set(key,val);
    }

    public void set(Map<String,Object> map){
        map.forEach(this::set);
    }

    public ConfigurationSection getOrCreateConfigurationSection(String sectionName){
        return getOrCreateConfigurationSection(sectionName,null);
    }

    public ConfigurationSection getOrCreateConfigurationSection(String sectionName, OptionBuilder defaults){
        if(configuration.getConfigurationSection(sectionName) == null){
            if(defaults != null){
                configuration.createSection(sectionName, defaults.build());
            }else {
                configuration.createSection(sectionName);
            }
        }
        return configuration.getConfigurationSection(sectionName);
    }

    public void setOptions(String key, OptionBuilder builder){
        builder.build().forEach((optionKey,val) -> rawSet(key + "." + optionKey, val));
        saveToFile();
    }

    public Object get(String key){
        return configuration.get(key);
    }
    public String getString(String key){
        return configuration.getString(key);
    }
    public int getInt(String key){
        return configuration.getInt(key);
    }
    public double getDouble(String key){
        return configuration.getDouble(key);
    }
    public boolean getBoolean(String key){
        if(!configuration.contains(key) || !configuration.isBoolean(key)){
            return false;
        }
        return configuration.getBoolean(key);
    }
    public List<String> getStringList(String key){
        return configuration.getStringList(key);
    }
    public List<Integer> getIntegerList(String key){
        return configuration.getIntegerList(key);
    }
    public List<Double> getDoubleList(String key){
        return configuration.getDoubleList(key);
    }
    public List<?> getAnyList(String key){
        return configuration.getList(key);
    }

    public Map<String,Object> getSections(String sectionKey){
        ConfigurationSection section = configuration.getConfigurationSection(sectionKey);
        if(section == null){
            throw new IllegalArgumentException("section " + sectionKey + " doesn't exist");
        }
        OptionBuilder builder = OptionBuilder.of();
        for(String key : section.getKeys(false)){
            builder = builder.key(key).value(section.get(key));
        }
        return builder.build();
    }

    public void saveToFile(){
        try {
            configuration.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
