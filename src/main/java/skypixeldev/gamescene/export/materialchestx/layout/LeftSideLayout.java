package skypixeldev.gamescene.export.materialchestx.layout;



import skypixeldev.gamescene.export.itemMenus.ActionMenuItem;
import skypixeldev.gamescene.export.itemMenus.ItemMenu;
import skypixeldev.gamescene.export.itemMenus.MenuItem;
import skypixeldev.gamescene.export.materialchestx.ctx.HeaderContent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LeftSideLayout extends Layout {
    private int cursor;
    private int range = 100;
    private int[] sidebar = {1, 10, 19, 28, 37, 46};
    private int[] headers = {9, 18, 27, 36};
    private int[] contents = {
            2, 3, 4, 5, 6, 7, 8
            , 11, 12, 13, 14, 15, 16, 17
            , 20, 21, 22, 23, 24, 25, 26
            , 29, 30, 31, 32, 33, 34, 35
            , 38, 39, 40, 41, 42, 43, 44
    };
    private int[] footer = {48, 49, 50, 51, 52};

    private boolean isSupportMoreLine = false;

    public LeftSideLayout() {
    }

    public LeftSideLayout(ItemMenu.Size size) {
        if (size.name().equalsIgnoreCase("MORE_LINE")) {
            //More Line Setups
            contents = new int[]{
                    2, 3, 4, 5, 6, 7, 8
                    , 11, 12, 13, 14, 15, 16, 17
                    , 20, 21, 22, 23, 24, 25, 26
                    , 29, 30, 31, 32, 33, 34, 35
                    , 38, 39, 40, 41, 42, 43, 44
                    , 47, 48, 49, 50, 51, 52, 53
            };
            isSupportMoreLine = true;
            footer = new int[]{48 + 9, 49 + 9, 50 + 9, 51 + 9, 52 + 9};
        }
    }

    //Range - 0 ~ 99 (100)
    public void newRange(int range) {
        this.range = range;
        if (cursor < 0) {
            cursor = range - 1;
        }
        if (cursor >= range) {
            cursor = 0;
        }
    }

    public void scrollNext() {
        cursor++;
        if (cursor < 0) {
            cursor = range - 1;
        }
        if (cursor >= range) {
            cursor = 0;
        }
    }

    public int getCursor() {
        return cursor;
    }

    public void scrollPrevious() {
        cursor--;
        if (cursor < 0) {
            cursor = range - 1;
        }
        if (cursor >= range) {
            cursor = 0;
        }
    }

    @Override
    public ItemMenu onDrawHeaders(ItemMenu menu, HeaderContent[] ctx) {
        newRange(ctx.length);
        for (int slot : sidebar) {
            menu.setItem(slot, ItemMenu.EMPTY_SLOT_ITEM);
        }
        int discur = cursor;
        for (int slot : headers) {
            if (discur < 0) {
                discur = ctx.length - 1;
            }
            if (discur >= ctx.length) {
                if (ctx.length <= 4) {
                    break;
                }
                discur = 0;
            }
            HeaderContent header = ctx[discur];
            menu.setItem(slot, header.getHeader(discur, this));
            discur++;
        }
        if (ctx.length > 4) {
            menu.setItem(0, new ActionMenuItem("§e上翻一页", (evt) -> {
                if (!evt.getClickType().isShiftClick()) {
                    scrollPrevious();
                    evt.getPlayer().playSound(evt.getPlayer().getLocation(), Sound.BLOCK_SNOW_STEP, 1f, 3f);
                } else {
                    for (int i = 0; i < 3; i++) {
                        scrollPrevious();
                    }
                    evt.getPlayer().playSound(evt.getPlayer().getLocation(), Sound.BLOCK_SNOW_STEP, 1f, 3f);
                }
            }, new ItemStack(Material.SPECTRAL_ARROW), "§f上翻一页菜单", "", " §e普通点击 §7-上翻§e1§7页", " §eShift+点击 §7-上翻§e3§7页"));

            menu.setItem(45, new ActionMenuItem("§e下翻一页", (evt) -> {
                if (!evt.getClickType().isShiftClick()) {
                    scrollNext();
                    evt.getPlayer().playSound(evt.getPlayer().getLocation(), Sound.BLOCK_SNOW_STEP, 1f, 3f);
                } else {
                    for (int i = 0; i < 3; i++) {
                        scrollNext();
                    }
                    evt.getPlayer().playSound(evt.getPlayer().getLocation(), Sound.BLOCK_SNOW_STEP, 1f, 3f);
                }
            }, new ItemStack(Material.SPECTRAL_ARROW), "§f下翻一页菜单", "", " §e普通点击 §7-下翻§e1§7页", " §eShift+点击 §7-下翻§e3§7页"));
        }
        return menu;
    }

    @Override
    public ItemMenu onDrawContent(ItemMenu menu, HeaderContent[] ctx) {
        for (int slot : footer) {
            menu.setItem(slot, ItemMenu.EMPTY_SLOT_ITEM);
        }
        if (this.getCurrentContent() < 0) {
            this.setCurrentContent(0);
        }
        if (this.getCurrentContent() >= ctx.length) {
            this.setCurrentContent(ctx.length - 1);
        }
        MenuItem[] items = ctx[this.getCurrentContent()].getContents();
        List<Integer> available = new ArrayList<>();
        for (int c : contents) {
            available.add(c);
        }
        int discur = contents.length * this.getCurrentContentPage();
        int skip = 0;
        for (int slot : contents) {
            if (discur >= items.length) {
                break;
            }
            int toSlot = slot - skip;
            if (this.hasPlayingAnimation()) {
                toSlot = this.getPlayingAnimation().getFrameSlot(toSlot, available);
            }
            if (toSlot < 0) {
                skip++;
                continue;
            }
            if (items[discur] != null) {
                menu.setItem(toSlot, items[discur]);
            }
            discur++;
        }
        if(((int) Math.floor(items.length / contents.length)) != 0) {
            menu.setItem(47 + (!isSupportMoreLine ? 0 : 9), new ActionMenuItem("§e上一页", (evt) -> {
                if (this.getCurrentContentPage() == 0) {
                    evt.getPlayer().sendMessage(" §f[§cE§f] 没有上一页了!");
                    evt.getPlayer().playSound(evt.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                } else {
                    this.setCurrentContentPage(this.getCurrentContentPage() - 1);
                    if (this.getCurrentContentPage() - 1 >= 0) {
                        ctx[this.getCurrentContentPage() - 1].playAnimation();
                    }
                }
            }, new ItemStack(Material.TIPPED_ARROW), "", "§7点击查看上一页内容"));
            menu.setItem(53 + (!isSupportMoreLine ? 0 : 9), new ActionMenuItem("§e下一页", (evt) -> {
                if (this.getCurrentContentPage() == ((int) Math.floor(items.length / contents.length))) {
                    evt.getPlayer().sendMessage(" §f[§cE§f] 没有下一页了!");
                    evt.getPlayer().playSound(evt.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                } else {
                    this.setCurrentContentPage(this.getCurrentContentPage() + 1);
                    if (this.getCurrentContentPage() + 1 < ctx.length) {
                        ctx[this.getCurrentContentPage() + 1].playAnimation();
                    }
                }
            }, new ItemStack(Material.TIPPED_ARROW), "", "§7点击查看下一页内容"));
        }
        return menu;
    }
}
