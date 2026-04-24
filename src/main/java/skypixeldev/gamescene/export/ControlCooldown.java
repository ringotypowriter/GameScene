package skypixeldev.gamescene.export;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import skypixeldev.gamescene.Bootstrap;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ControlCooldown {
    private final static Map<UUID,Long> cooldownGlobal = new ConcurrentHashMap<>();

    static{
        new BukkitRunnable(){
            @Override
            public void run() {
                cooldownGlobal.entrySet().removeIf(uuidLongEntry -> System.currentTimeMillis() >= uuidLongEntry.getValue());
            }
        }.runTaskTimerAsynchronously(Bootstrap.getInstance(),  (20 * 60 * 10),(20 * 60 * 10) );
    }

    public static void addCooldown(Player player, long endTime){
        cooldownGlobal.put(player.getUniqueId(), endTime);
    }

    public static boolean isCooldown(Player player){
        if(!cooldownGlobal.containsKey(player.getUniqueId())){
            return false;
        }
        long endTime = cooldownGlobal.getOrDefault(player.getUniqueId(),System.currentTimeMillis()-1000);
        return System.currentTimeMillis() < endTime;
    }
}
