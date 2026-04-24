package skypixeldev.gamescene.journals.conversation.hovering;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import skypixeldev.gamescene.Bootstrap;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HoveringManager {
    private final static int DEFAULT_MAXIMUM_SIZE = 12;
    private static ConcurrentHashMap<UUID,Hovering> hoverings = new ConcurrentHashMap<>();
    protected static void addHovering(Hovering hovering){
        if(hoverings.containsKey(hovering.getViewer().getUniqueId())){
            hoverings.get(hovering.getViewer().getUniqueId()).destroy();
        }
        hoverings.put(hovering.getViewer().getUniqueId(), hovering);
    }
    protected static void removeHoveringRecord(UUID viewerID){
        hoverings.remove(viewerID);
    }

    public static Hovering getHoveringOrCreate(Player player){
        return Optional.ofNullable(hoverings.get(player.getUniqueId())).orElseGet(() -> new Hovering(player, DEFAULT_MAXIMUM_SIZE));
    }

    static {
        Bukkit.getPluginManager().registerEvents(new Listeners(), Bootstrap.getInstance());
    }

    private static class Listeners implements Listener{
        @EventHandler
        public void onQuit(PlayerQuitEvent evt){
            Optional.ofNullable(hoverings.get(evt.getPlayer().getUniqueId())).ifPresent(Hovering::destroy);
        }
    }
}
