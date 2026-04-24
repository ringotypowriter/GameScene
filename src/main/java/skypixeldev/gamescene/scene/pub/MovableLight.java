package skypixeldev.gamescene.scene.pub;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;
import skypixeldev.gamescene.Bootstrap;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MovableLight implements Listener {
    private ConcurrentHashMap<UUID, Location> lastLit = new ConcurrentHashMap<>();
    public MovableLight(Plugin plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);



        new BukkitRunnable(){

            public boolean isLightingItem(ItemStack handItem){
                if(handItem == null){
                    return false;
                }
                return handItem.getType().equals(Material.TORCH) ||
                        handItem.getType().equals(Material.REDSTONE_TORCH_ON) ||
                        handItem.getType().equals(Material.REDSTONE_TORCH_OFF) ||
                        handItem.getType().equals(Material.MAGMA_CREAM);
            }

            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(isLightingItem(player.getInventory().getItemInOffHand()) || isLightingItem(player.getInventory().getItemInMainHand())){
                        replaceOne(player.getUniqueId(), player.getLocation(),15);
                        continue;
                    }else{
                        removeOneLast(player.getUniqueId(), 15);
                    }
                    if(!lastLit.containsKey(player.getUniqueId())) {
                        if (player.getInventory().getHelmet() != null) {
                            if (player.getInventory().getHelmet().getType().equals(Material.GOLD_HELMET)) {
                                replaceOne(player.getUniqueId(), player.getLocation(), 3);
                            }else{
                                removeOneLast(player.getUniqueId(), 3);
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 10,10);
    }

    public static void init(Plugin plugin){
        new MovableLight(plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent evt){
        removeOneLast(evt.getPlayer().getUniqueId(), 15);
    }

    private void removeOneLast(UUID uuid, int strength){
        if(lastLit.containsKey(uuid)){
            Location oldLocation = lastLit.remove(uuid);
            LightAPI.deleteLight(oldLocation, LightType.BLOCK, true);
            updateChunk(oldLocation, strength);
        }
    }

    private static void updateChunk(Location location, int strength){
        for(ChunkInfo info : LightAPI.collectChunks(location, LightType.BLOCK, strength)){
            LightAPI.updateChunk(info, LightType.BLOCK);
        }
    }

    public static void createInstantLight(final Location location){
        LightAPI.createLight(location, LightType.BLOCK, 15, true);
        updateChunk(location,15);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bootstrap.getInstance(), () -> {
            LightAPI.deleteLight(location, LightType.BLOCK, true);
            updateChunk(location,15);
        }, 10);
    }

    private void replaceOne(UUID uuid, Location newLocation, int strength){
        removeOneLast(uuid, strength);
        LightAPI.createLight(newLocation, LightType.BLOCK, strength, true);
        updateChunk(newLocation, strength);
        lastLit.put(uuid, newLocation);
    }
}
