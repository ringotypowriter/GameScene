package skypixeldev.gamescene.export.itemMenus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An Item inside an {@link cn.bitpixel.general.itemMenus.MenuItem}.
 */
public class MenuItem {
    private String displayName;
    private ItemStack icon;
    private List<String> lore;

    public MenuItem(String displayName, ItemStack icon, String... lore) {
        this.displayName = displayName;
        this.icon = icon;
        this.lore = new ArrayList<String>();
        if (lore == null) {
            if (icon.hasItemMeta()) {
                if (icon.getItemMeta().hasLore()) {
                    this.lore.addAll(icon.getItemMeta().getLore());
                }
            }
        }
        this.lore.addAll(Arrays.asList(lore));
        //Arrays.asList(lore);
    }

    /**
     * Sets the display name and lore of an ItemStack.
     *
     * @param itemStack   The ItemStack.
     * @param displayName The display name.
     * @param lore        The lore.
     * @return The ItemStack.
     */
    public static ItemStack setNameAndLore(ItemStack itemStack,
                                           String displayName, List<String> lore) {
        if (itemStack.getType() == Material.AIR) {
            return itemStack;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        }
        if (meta == null) {
            return itemStack;
        }
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Gets the display name of the MenuItem.
     *
     * @return The display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    /**
     * Gets the icon of the MenuItem.
     *
     * @return The icon.
     */
    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack newIcon) {
        this.icon = newIcon;
    }

    /**
     * Gets the lore of the MenuItem.
     *
     * @return The lore.
     */
    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    /**
     * Gets the ItemStack to be shown to the player.
     *
     * @param player The player.
     * @return The final icon.
     */
    public ItemStack getFinalIcon(Player player) {
        return setNameAndLore(getIcon().clone(), getDisplayName(), getLore());
    }

    /**
     * Called when the MenuItem is clicked.
     *
     * @param event The {@link cn.bitpixel.general.itemMenus.ItemClickEvent}.
     */
    public void onItemClick(ItemClickEvent event) {
        // Do nothing by default
    }


}