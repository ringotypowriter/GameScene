package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class FunctionHidePlayer implements IFunction {
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String playerName = Selector.searchNonNull(argument,"player","p","pl");
        if(Bukkit.getPlayer(playerName) == null){
            return false;
        }
        Player player = Bukkit.getPlayer(playerName);
        Bukkit.getOnlinePlayers().forEach(o -> player.hidePlayer(Bootstrap.getInstance(), o));
        if(!Hider.hidings.contains(player.getUniqueId())) {
            Hider.hidings.add(player.getUniqueId());
        }
        return true;
    }

    @Override
    public String getFunction() {
        return "hideOthers";
    }

    @Override
    public String getProvider() {
        return "std";
    }

    public final static class Hider implements Listener{
        protected static CopyOnWriteArrayList<UUID> hidings = new CopyOnWriteArrayList<>();

        @EventHandler
        public void onHiding(PlayerJoinEvent evt){
            hidings.removeIf(uuid -> {
                if(Bukkit.getPlayer(uuid) == null){
                    return true;
                }
                Player player = Bukkit.getPlayer(uuid);
                player.hidePlayer(Bootstrap.getInstance(), evt.getPlayer());
                return false;
            });
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent evt){
            Bukkit.getOnlinePlayers().forEach(o -> evt.getPlayer().showPlayer(Bootstrap.getInstance(), o));
            hidings.remove(evt.getPlayer().getUniqueId());
        }
    }

    public FunctionHidePlayer(){
        Bukkit.getPluginManager().registerEvents(new Hider(), Bootstrap.getInstance());
    }
}
