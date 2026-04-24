package skypixeldev.gamescene.scene.gameObject.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.scene.GameObjects;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.LootTable;
import skypixeldev.gamescene.scene.gameObject.BaseObject;

import java.util.Map;
import java.util.Optional;

public class GObjectLootChest extends BaseObject {
    public GObjectLootChest(GameScene scene) {
        super(scene);
    }

    @Override
    public void onUpdate(GameScene scene, GameObjects.SingletonObject runtime) {

    }

    @Override
    public void onInitialize(GameScene scene, GameObjects objects, GameObjects.SingletonObject runtime) {
        Map<String,Object> options = runtime.getOptions();
        if(options.containsKey("location") && (options.containsKey("lootTable") || options.containsKey("lootItem"))){
            Location location = scene.getLocations().get((String) options.get("location"));
            if(location == null){
                Log.lGo("missing location of " + runtime.getUniqueID() + ":lootChest");
                return;
            }
            LootTable.LootTableObject table = scene.getLootTable().getLoadedLootTable(Optional.ofNullable((String)options.get("lootTable")).orElse((String) options.get("lootItem")));
            if(table == null){
                Log.lGo("missing loots of " + runtime + ":lootChest");
                return;
            }
            if(location.getBlock().getType() != Material.CHEST && location.getBlock().getType() != Material.TRAPPED_CHEST){
                Log.lGo("missing chest of " + location.toVector().toString());
                return;
            }
            Inventory inventory = ((Chest) location.getBlock().getState()).getBlockInventory();
            if((Boolean) options.getOrDefault("override", Boolean.FALSE)){
                inventory.clear();
            }
            table.rollToInventory(inventory, (Integer) options.getOrDefault("times", 3), (Boolean) options.getOrDefault("shuffle", Boolean.TRUE));
        }
    }

    @Override
    public void onRemove(GameScene scene) {

    }

    @Override
    public String getProvider() {
        return "std";
    }
}
