package skypixeldev.gamescene.export.materialchestx;



import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.export.itemMenus.ItemMenu;
import skypixeldev.gamescene.export.materialchestx.ctx.HeaderContent;
import skypixeldev.gamescene.export.materialchestx.layout.Layout;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MaterialMenu {
    private ItemMenu menu;
    private Layout layout;
    private BukkitRunnable refresher;
    private String title;
    private Player player;
    private ArrayList<HeaderContent> ctx;

    public MaterialMenu(Layout layout, String name, Player player) {
        this.layout = layout;
        ItemMenu.Size line;
//		try{
//			line = ItemMenu.Size.valueOf("MORE_LINE");
//		}catch (Exception exc){
//			line = ItemMenu.Size.SIX_LINE;
//		}
        //Cannot work

        line = ItemMenu.Size.SIX_LINE;
        menu = new ItemMenu(name, line);
        this.title = name;
        this.player = player;
        this.ctx = new ArrayList<>();
    }

    public boolean isSupportMoreLine() {
        return menu.getSize().name().equalsIgnoreCase("MORE_LINE");
    }

    public void addHeaderContent(HeaderContent content) {
        ctx.add(content);
        content.setLastLayout(layout);
    }

    public void removeHeaderContent(HeaderContent content) {
        ctx.remove(content);
        content.clearLastLayout();
    }

    public void removeHeaderContent(int content) {
        ctx.remove(content);
    }

    public List<HeaderContent> getContents() {
        return Collections.unmodifiableList(ctx);
    }

    public boolean isValid() {
        if (player == null) {
            return false;
        }
        if (!player.isOnline()) {
            return false;
        }
        return true;
    }

    public void destoryRefresher() {
        if (refresher != null) {
            refresher.cancel();
            refresher = null;
            System.out.println("[MaterialMenu] Cancelled the =refresher of " + player.getName() + " !");
        }
    }

    public void open() {
        if (menu == null) {
            menu = new ItemMenu(title, ItemMenu.Size.SIX_LINE);
        }
        menu.clearAllItems();
        createAndRunRefresherIfActive(true);
    }

    public void createAndRunRefresherIfActive(boolean open) {
        if (menu == null) {
            menu = new ItemMenu(title, ItemMenu.Size.SIX_LINE);
        }
        if (!isValid()) {
            return;
        }
        if (!menu.isCurrentMenu(player)) {
            if (!open) {
                return;
            } else {
                player.closeInventory();
                menu.open(player);
            }
        }
        if (refresher == null) {
            refresher = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!isValid()) {
                        destoryRefresher();
                        return;
                    }
                    if (!menu.isCurrentMenu(player)) {
                        destoryRefresher();
                        return;
                    }
                    menu.clearAllItems();
                    HeaderContent[] arrayCtx = ctx.toArray(new HeaderContent[]{});
                    layout.onDrawHeaders(menu, arrayCtx);
                    layout.onDrawContent(menu, arrayCtx);
                    //Done Of Draw
                    menu.update(player);
                    player.updateInventory();
                    //Update the Inventory
                }
            };
            refresher.runTaskTimerAsynchronously(Bootstrap.getInstance(), 0L, 1L);
        }
    }
}
