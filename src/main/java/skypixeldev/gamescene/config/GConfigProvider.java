package skypixeldev.gamescene.config;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GConfigProvider {
    @Getter private File parentsFolder;
    private HashMap<String,GConfig> configs;
    @Getter private Plugin owner;

    public GConfigProvider(Plugin owner, String providerName){
        this(owner, providerName, null);
    }

    protected GConfigProvider(GConfigProvider provider) {
        this.parentsFolder = provider.parentsFolder;
        this.configs = new HashMap<>(provider.configs);
        this.owner = getOwner();
    }

    public GConfigProvider makeMirrors(){
        GConfigProvider pov = new GConfigProvider(this);
        pov.configs.entrySet().forEach(en -> en.setValue(new GConfigMirror(en.getValue())));
        return pov;
    }

    public GConfigProvider(Plugin owner, String providerName, File whereFolder){
        this.owner = owner;
        File lastFolder = owner.getDataFolder();
        if(whereFolder != null){
            lastFolder = whereFolder;
        }
        lastFolder.mkdirs();
        parentsFolder = new File(lastFolder, providerName);
        parentsFolder.mkdirs();
        configs = new HashMap<>();
    }

    public GConfig getOrCreateConfig(String name){
        return getOrCreateConfig(name,null);
    }

    public GConfig getOrCreateConfig(String name, OptionBuilder defaults){
        if (configs.containsKey(name)) {
            return configs.get(name);
        }else{
            GConfig config = GConfig.of(parentsFolder,name, defaults);
            configs.put(name,config);
            return config;
        }
    }
}
