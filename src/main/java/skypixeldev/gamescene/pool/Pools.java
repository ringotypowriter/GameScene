package skypixeldev.gamescene.pool;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.Namespace;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Pools {
    private static Pools pools;
    public synchronized static Pools getImplement(){
        if(pools == null){
            File folder = new File(Bootstrap.getInstance().getDataFolder(),"itemPools");
            pools = new Pools(folder);
        }
        return  pools;
    }

    private final Map<String,ItemPool> itemPoolMap;
    @Getter private final ItemPool rootPool;
    @Getter private final File rootFile;
    @Getter private final File poolsFolder;

    public ItemPool getItemPool(String key){
        return itemPoolMap.get(key);
    }

    public void reload(){
        itemPoolMap.values().forEach(ItemPool::reload);
        rootPool.reload();
    }

    public ItemStack getItem(String name, int quantity){
        name = name.toLowerCase();
        if(!name.contains(":")){
            return rootPool.getItem(name, quantity);
        }
        Namespace namespace = new Namespace(name);
        return Optional.ofNullable(getItemPool(namespace.getKey())).map(pool -> pool.getItem(namespace.getValue())).orElse(null);
    }

    public ItemStack getItem(String name){
        return getItem(name,1);
    }

    public ItemStack getItem(Namespace namespace, int quantity){
        if(namespace.getKey().equalsIgnoreCase("root") || namespace.getKey() == null){
            return rootPool.getItem(namespace.getValue().toLowerCase(), quantity);
        }
        return Optional.ofNullable(getItemPool(namespace.getKey().toLowerCase())).map(pool -> pool.getItem(namespace.getValue().toLowerCase())).orElse(null);
    }

    public ItemStack getItem(Namespace namespace){
        return getItem(namespace,1);
    }

    public Pools(File folder){
        this.poolsFolder = folder;
        folder.mkdirs();
        itemPoolMap = new HashMap<>();
        if(folder.isDirectory()) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                Log.lPf("Loading Items in " + file.getName());
                if(!file.getName().contains(".")){
                    continue;
                }
                if(file.getName().equalsIgnoreCase("root.yml")){
                    continue;
                }
                if(file.getName().endsWith(".yml")){
                    ItemPool pool = new ItemPool(file);
                    itemPoolMap.put(pool.getName(), pool);
                    Log.lPf("Loaded Items in " + file.getName());
                }
            }
        }

        // root
        rootFile = new File(folder,"root.yml");
        if(!rootFile.exists()){
            try {
                rootFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        rootPool = new ItemPool(rootFile);
    }
}
