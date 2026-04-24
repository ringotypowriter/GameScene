package skypixeldev.gamescene.export.itemMenus;

import org.bukkit.inventory.ItemStack;

public class ActionMenuItem extends MenuItem {
    private ItemClickHandler handler;

    public ActionMenuItem(String displayName, ItemClickHandler itemClickHandler, ItemStack icon, String... class1) {
        super(displayName, icon, class1);
        this.handler = itemClickHandler;
    }


    @Override
    public void onItemClick(ItemClickEvent event) {
        handler.onItemClick(event);
    }

    public ItemClickHandler getHandler() {
        return this.handler;
    }

    public void setHandler(ItemClickHandler handler) {
        this.handler = handler;
    }
}
