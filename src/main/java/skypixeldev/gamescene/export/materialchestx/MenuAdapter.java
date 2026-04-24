package skypixeldev.gamescene.export.materialchestx;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MenuAdapter implements Listener {
    private ConcurrentHashMap<UUID, MaterialMenu> menus;
    private JavaPlugin owner;
    private MenuCreator creator;
    private boolean updateOpen;

    public MenuAdapter(MenuCreator creator, JavaPlugin owner, boolean life) {
        this.creator = creator;
        this.owner = owner;
        this.menus = new ConcurrentHashMap<>();
        this.updateOpen = false;
        if (life) {
            Bukkit.getPluginManager().registerEvents(this, getOwner());
        }
    }

    public MenuAdapter(MenuCreator creator, JavaPlugin owner) {
        this(creator, owner, true);
    }

    public void updateWhenOpen() {
        updateOpen = true;
    }

    public boolean hasUpdateWhenOpen() {
        return updateOpen;
    }

    public void update(Player pl) {
        menus.remove(pl.getUniqueId());
    }

    public void openMenu(Player pl) {
        try {
            if (!updateOpen) {
                MaterialMenu menu = menus.get(pl.getUniqueId());
                if (menu == null) {
                    menu = creator.create(pl);
                }
                menu.open();
            } else {
                creator.create(pl).open();
            }
        } catch (NullPointerException exc) {
            exc.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(" §c[Menu Adapter Error] Some plugin provided a broken creator,we cannot create a menu for player!");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent evt) {
        if (getMenus().containsKey(evt.getPlayer().getUniqueId())) {
            getMenus().remove(evt.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onKicked(PlayerKickEvent evt) {
        if (getMenus().containsKey(evt.getPlayer().getUniqueId())) {
            getMenus().remove(evt.getPlayer().getUniqueId());
        }
    }

    public ConcurrentHashMap<UUID, MaterialMenu> getMenus() {
        return menus;
    }

    public JavaPlugin getOwner() {
        return owner;
    }
}
