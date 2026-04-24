package skypixeldev.gamescene.export.itemMenus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A {@link cn.bitpixel.general.itemMenus.StaticMenuItem} that opens the
 * {@link cn.bitpixel.general.itemMenus.ItemMenu}'s parent menu if it exists.
 */
public class BackMenuItem extends StaticMenuItem {

    public BackMenuItem() {
        super(ChatColor.RED + "����", new ItemStack(Material.FENCE_GATE));
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        event.setWillGoBack(true);
    }
}