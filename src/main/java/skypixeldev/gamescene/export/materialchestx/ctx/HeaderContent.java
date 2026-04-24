package skypixeldev.gamescene.export.materialchestx.ctx;



import skypixeldev.gamescene.export.itemMenus.ActionMenuItem;
import skypixeldev.gamescene.export.itemMenus.MenuItem;
import skypixeldev.gamescene.export.materialchestx.animation.Animation;
import skypixeldev.gamescene.export.materialchestx.layout.Layout;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeaderContent {
    private String displayname;
    private ItemStack icon;
    private List<String> lore;
    private Animation animation;
    private Layout lastLayout;
    private MenuItem[] contents;

    public HeaderContent(MenuItem[] contents, String displayname, ItemStack icon, String... lore) {
        this.contents = contents;
        this.lore = new ArrayList<>();
        if (lore == null) {
            if (icon.hasItemMeta()) {
                if (icon.getItemMeta().hasLore()) {
                    this.lore.addAll(icon.getItemMeta().getLore());
                }
            }
        } else {
            this.lore.addAll(Arrays.asList(lore));
        }
        this.displayname = displayname;
        this.icon = icon.clone();
    }

    public HeaderContent(MenuItem[] contents, String displayname, ItemStack icon, Animation animation, String... lore) {
        this(contents, displayname, icon, lore);
        setAnimation(animation);
    }

    public String getDisplayname() {
        return displayname;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public void setLastLayout(Layout layout) {
        this.lastLayout = layout;
    }

    public void clearLastLayout() {
        this.lastLayout = null;
    }

    public MenuItem getHeader(final int current, final Layout layout) {
        ItemStack disIcon = icon.clone();
        if (layout.getCurrentContent() == current) {
            ItemMeta meta = disIcon.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            disIcon.setItemMeta(meta);
        }
        this.lastLayout = layout;
        return new ActionMenuItem(displayname, (evt) -> {
            layout.setCurrentContent(current);
            evt.getPlayer().playSound(evt.getPlayer().getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 3F);
            playAnimation();
        }, disIcon, lore.toArray(new String[]{}));
    }

    public void playAnimation() {
        if (animation != null) {
            if (lastLayout != null) {
                if (!lastLayout.hasPlayingAnimation()) {
                    lastLayout.playAnimation(animation);
                }
            }
        }
    }

    public MenuItem[] getContents() {
        return contents;
    }
}
