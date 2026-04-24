package skypixeldev.gamescene.export.itemMenus;

import org.bukkit.entity.Player;

public interface OverridableFliter {
    public boolean canDisplayFor(Player pl);
}
