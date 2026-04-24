package skypixeldev.gamescene.scene.pub;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import skypixeldev.gamescene.Bootstrap;

import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

public class TemporaryEntities {
    private static UUID genKey;

    private static class Impl implements Listener{

        private static Vector<UUID> toRemove = new Vector<>();

        @EventHandler
        public void onServerDisable(PluginDisableEvent evt){
            if(evt.getPlugin().getName().equalsIgnoreCase("GameScene")){
                for(World world : Bukkit.getWorlds()){
                    for(Entity entity : world.getEntities()){
                        if(isCurrentStateEntity(entity)){
                            entity.remove();
                        }
                    }
                }
            }
        }

        @EventHandler
        public void onChunkLoad(final ChunkLoadEvent evt){
            Bukkit.getScheduler().scheduleSyncDelayedTask(Bootstrap.getInstance(),() -> {
                if(evt.getChunk() != null){
                    if(evt.getChunk().isLoaded()){
                        for(Entity entity : evt.getChunk().getEntities()){
                            if(!isCurrentStateEntity(entity)){
                                entity.remove();
                            }
                            if(toRemove.contains(entity.getUniqueId())){
                                toRemove.remove(entity.getUniqueId());
                                entity.remove();
                            }
                        }
                    }
                }

            });
        }
    }

    public static void init(){
        genKey = UUID.randomUUID();
        Bukkit.getPluginManager().registerEvents(new Impl(), Bootstrap.getInstance());
    }

    public static void markTemporary(Entity entity){
        entity.setMetadata("TemporaryKey", new FixedMetadataValue(Bootstrap.getInstance(), genKey.toString()));
    }

    public static void markRemoved(UUID entityUUID){
        if(Bukkit.getEntity(entityUUID) != null){
            Bukkit.getEntity(entityUUID).remove();
            return;
        }
        Impl.toRemove.add(entityUUID);
    }

    public static UUID getGenKey() {
        return genKey;
    }

    public static boolean isCurrentStateEntity(Entity entity){
        if(entity.hasMetadata("TemporaryKey")){
            if(!entity.getMetadata("TemporaryKey").isEmpty()){
                return UUID.fromString(entity.getMetadata("TemporaryKey").get(0).asString()).equals(genKey);
            }
        }
        return true;
    }

}
