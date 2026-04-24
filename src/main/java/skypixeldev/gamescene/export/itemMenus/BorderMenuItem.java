package skypixeldev.gamescene.export.itemMenus;

import org.bukkit.inventory.ItemStack;

enum BorderAction {
    NO_ACTION, EXIT
}

public class BorderMenuItem extends MenuItem {
    private BorderAction action;

    public BorderMenuItem(String displayName, ItemStack icon, String[] lore, BorderAction action) {
        super(displayName, icon, lore);
        this.action = action;
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if (action == BorderAction.EXIT) {
            event.getPlayer().closeInventory();
        } else {
            return;
        }
    }

}