package skypixeldev.gamescene.pool;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.inventory.ItemStack;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.scene.itemParser.ItemParsingManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ItemPool {
    private FileConfiguration configuration;
    private final Map<String, Parsing> contents;
    @Getter private final String name;
    @Getter private final File file;
    public ItemPool(File file){
        this.file = file;
        name = file.getName().split("\\.")[0].toLowerCase();
        configuration = YamlConfiguration.loadConfiguration(file);
        contents = new HashMap<>();
        for(String key : configuration.getKeys(false)){
            contents.put(key.toLowerCase(), new Parsing(configuration.getString(key)));
            Log.lPf("Loaded Items " + name + ":" + key.toLowerCase());
        }
    }

    public void reload(){
        configuration = YamlConfiguration.loadConfiguration(file);
        contents.clear();
        for(String key : configuration.getKeys(false)){
            contents.put(key.toLowerCase(), new Parsing(configuration.getString(key)));
            Log.lPf("Loaded Items " + name + ":" + key.toLowerCase());
        }
    }

    public ItemStack getItem(String key){
        return getItem(key,1);
    }

    public ItemStack getItem(String key, int quantity){
        if(quantity < 1){
            return ItemParsingManager.ERROR_ITEM;
        }
        if(!contents.containsKey(key)){
            return ItemParsingManager.ERROR_ITEM;
        }
        Parsing val = contents.get(key);
        ItemStack gen = ItemParsingManager.parseItem(val);
        if(val.getOriginalExpression().contains("\\$random\\$")){
            gen.setAmount(quantity);
        }else{
            gen.setAmount(gen.getAmount() * quantity);
        }
        return gen;
    }
}
