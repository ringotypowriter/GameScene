package skypixeldev.gamescene.export.itemMenus;


import skypixeldev.gamescene.Bootstrap;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * A Menu controlled by ItemStacks in an Inventory.
 */
public class ItemMenu {
    /**
     * The {@link cn.bitpixel.general.itemMenus.StaticMenuItem} that appears in empty
     * slots if {@link cn.bitpixel.general.itemMenus.MenuItem#fillEmptySlots()} is
     * called.
     */
    @SuppressWarnings("deprecation")
    public static final MenuItem EMPTY_SLOT_ITEM = new StaticMenuItem(" ",
            new ItemStack(Material.STAINED_GLASS_PANE, 1,
                    DyeColor.GRAY.getWoolData()));
    // private JavaPlugin plugin;
    private String name;
    private Size size;
    private MenuItem[] items;
    private ItemMenu parent;
    private HashMap<Integer, TreeMap<MenuItem, OverridableFliter>> overridableMenuItems = new HashMap<>();

    /**
     * Creates an {@link cn.bitpixel.general.itemMenus.MenuItem}.
     *
     * @param name   The name of the inventory.
     * @param size   The {@link com.gmail.nuclearcat1337.itemMenu.Size} of the
     *               inventory.
     * @param parent The ItemMenu's parent.
     */
    public ItemMenu(String name, Size size, ItemMenu parent) {
        // this.plugin = plugin;
        this.name = name;
        this.size = size;
        this.items = new MenuItem[size.getSize()];
        this.parent = parent;
    }

    /**
     * Creates an {@link cn.bitpixel.general.itemMenus.MenuItem} with no parent.
     *
     * @param name   The name of the inventory.
     * @param size   The {@link cn.bitpixel.general.itemMenus.MenuItem.Size} of the
     *               inventory.
     * @param plugin The Plugin instance.
     */
    public ItemMenu(String name, Size size) {
        this(name, size, null);
    }

    /**
     * Gets the name of the {@link cn.bitpixel.general.itemMenus.MenuItem}.
     *
     * @return The {@link cn.bitpixel.general.itemMenus.MenuItem}'s name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the {@link cn.bitpixel.general.itemMenus.MenuItem.Size} of the
     * {@link cn.bitpixel.general.itemMenus.MenuItem}.
     *
     * @return The {@link cn.bitpixel.general.itemMenus.MenuItem}'s
     * {@link cn.bitpixel.general.itemMenus.MenuItem.Size}.
     */
    public Size getSize() {
        return size;
    }

    /**
     * Checks if the {@link cn.bitpixel.general.itemMenus.MenuItem} has a parent.
     *
     * @return True if the {@link cn.bitpixel.general.itemMenus.MenuItem} has a
     * parent, else false.
     */
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Gets the parent of the {@link cn.bitpixel.general.itemMenus.MenuItem}.
     *
     * @return The {@link cn.bitpixel.general.itemMenus.MenuItem}'s parent.
     */
    public ItemMenu getParent() {
        return parent;
    }

    /**
     * Sets the parent of the {@link cn.bitpixel.general.itemMenus.MenuItem}.
     *
     * @param parent The {@link cn.bitpixel.general.itemMenus.MenuItem}'s parent.
     */
    public void setParent(ItemMenu parent) {
        this.parent = parent;
    }

    /**
     * Sets the {@link cn.bitpixel.general.itemMenus.MenuItem} of a slot.
     *
     * @param position The slot position.
     * @param menuItem The {@link cn.bitpixel.general.itemMenus.MenuItem}.
     * @return The {@link cn.bitpixel.general.itemMenus.MenuItem}.
     */
    public ItemMenu setItem(int position, MenuItem menuItem) {
        items[position] = menuItem;
        return this;
    }

    public MenuItem[] getItems() {
        return items;
    }

    public ItemMenu clearItem(int position) {
        items[position] = null;
        return this;
    }

    public ItemMenu clearAllItems() {
        Arrays.fill(items, null);
        return this;
    }

    /**
     * Fills all empty slots in the {@link cn.bitpixel.general.itemMenus.MenuItem}
     * with a certain {@link cn.bitpixel.general.itemMenus.MenuItem}.
     *
     * @param menuItem The {@link cn.bitpixel.general.itemMenus.MenuItem}.
     * @return The {@link cn.bitpixel.general.itemMenus.MenuItem}.
     */
    public ItemMenu fillEmptySlots(MenuItem menuItem) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = menuItem;
            }
        }
        return this;
    }

    /**
     * Fills all empty slots in the {@link cn.bitpixel.general.itemMenus.MenuItem}
     * with the default empty slot item.
     *
     * @return The {@link cn.bitpixel.general.itemMenus.MenuItem}.
     */
    public ItemMenu fillEmptySlots() {
        return fillEmptySlots(EMPTY_SLOT_ITEM);
    }

    /**
     * Opens the {@link cn.bitpixel.general.itemMenus.MenuItem} for a player.
     *
     * @param player The player.
     */
    public void open(Player player) {
        if (!ItemMenuListener.getInstance().isRegistered(
                Bootstrap.getInstance())) {
            ItemMenuListener.getInstance().register(
                    Bootstrap.getInstance());
        }
        Inventory inventory = Bukkit.createInventory(new ItemMenuHolder(this,
                        Bukkit.createInventory(player, size.getSize())),
                size.getSize(), name);
        apply(inventory, player);
        applyOverridable(inventory, player);
        player.openInventory(inventory);
    }

    public boolean isCurrentMenu(Player player) {
        if (player.getOpenInventory() != null) {
            Inventory inventory = player.getOpenInventory().getTopInventory();
            if (inventory.getHolder() instanceof ItemMenuHolder
                    && ((ItemMenuHolder) inventory.getHolder()).getMenu()
                    .equals(this)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the {@link cn.bitpixel.general.itemMenus.MenuItem} for a player.
     *
     * @param player The player to update the
     *               {@link cn.bitpixel.general.itemMenus.MenuItem} for.
     */
    public void update(Player player) {
        if (player.getOpenInventory() != null) {
            Inventory inventory = player.getOpenInventory().getTopInventory();
            if (inventory.getHolder() instanceof ItemMenuHolder
                    && ((ItemMenuHolder) inventory.getHolder()).getMenu()
                    .equals(this)) {
                apply(inventory, player);
                applyOverridable(inventory, player);
                player.updateInventory();
            }
        }
    }

    /**
     * Applies the {@link cn.bitpixel.general.itemMenus.MenuItem} for a player to an
     * Inventory.
     *
     * @param inventory The Inventory.
     * @param player    The Player.
     */
    private void apply(Inventory inventory, Player player) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                inventory.setItem(i, items[i].getFinalIcon(player));
            } else {
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
        }
    }

    public void setItemOverridable(int slot, MenuItem item, OverridableFliter fliter) {
        TreeMap<MenuItem, OverridableFliter> items = overridableMenuItems.get(slot);
        if (items == null) {
            items = new TreeMap<MenuItem, OverridableFliter>();
        }
        items.put(item, fliter);
        overridableMenuItems.put(slot, items);
    }

    public void applyOverridable(Inventory inv, Player who) {
        for (Entry<Integer, TreeMap<MenuItem, OverridableFliter>> entry : overridableMenuItems.entrySet()) {
            for (Entry<MenuItem, OverridableFliter> items : entry.getValue().entrySet()) {
                if (items.getValue().canDisplayFor(who)) {
                    inv.setItem(entry.getKey().intValue(), items.getKey().getFinalIcon(who));
                }
            }
        }
    }


    /**
     * Handles InventoryClickEvents for the
     * {@link cn.bitpixel.general.itemMenus.MenuItem}.
     */
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() != null) {
            if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                int slot = event.getRawSlot();
                if (slot >= 0 && slot < size.getSize() && items[slot] != null) {
                    Player player = (Player) event.getWhoClicked();
                    ItemClickEvent itemClickEvent = new ItemClickEvent(player, event.getCurrentItem(), event.getClick());
                    items[slot].onItemClick(itemClickEvent);

                    if (this.overridableMenuItems.containsKey(new Integer(slot))) {
                        for (Entry<MenuItem, OverridableFliter> items : this.overridableMenuItems.get(new Integer(slot)).entrySet()) {
                            if (items.getValue().canDisplayFor(player)) {
                                items.getKey().onItemClick(itemClickEvent);
                            }
                        }
                    }


                    if (itemClickEvent.willUpdate()) {
                        update(player);
                    } else {
                        player.updateInventory();
                        if (itemClickEvent.willClose()
                                || itemClickEvent.willGoBack()) {
                            final String playerName = player.getName();
                            Bukkit.getScheduler().scheduleSyncDelayedTask(
                                    Bootstrap.getInstance(), () -> {
                                        Player p = Bukkit
                                                .getPlayerExact(playerName);
                                        if (p != null) {
                                            p.closeInventory();
                                        }
                                    }, 1);
                        }
                        if (itemClickEvent.willGoBack() && hasParent()) {
                            final String playerName = player.getName();
                            Bukkit.getScheduler().scheduleSyncDelayedTask(
                                    Bootstrap.getInstance(), new Runnable() {
                                        public void run() {
                                            Player p = Bukkit
                                                    .getPlayerExact(playerName);
                                            if (p != null) {
                                                parent.open(p);
                                            }
                                        }
                                    }, 3);
                        }
                    }
                }
            }
        }
    }

    /**
     * Destroys the {@link cn.bitpixel.general.itemMenus.MenuItem}.
     */
    public void destroy() {
        name = null;
        size = null;
        items = null;
        parent = null;
    }

    /**
     * Possible sizes of an {@link cn.bitpixel.general.itemMenus.MenuItem}.
     */
    public enum Size {
        ONE_LINE(9), TWO_LINE(18), THREE_LINE(27), FOUR_LINE(36), FIVE_LINE(45), SIX_LINE(54), MORE_LINE(72);

        private final int size;

        private Size(int size) {
            this.size = size;
        }

        /**
         * Gets the required {@link cn.bitpixel.general.itemMenus.MenuItem.Size} for
         * an amount of slots.
         *
         * @param slots The amount of slots.
         * @return The required {@link cn.bitpixel.general.itemMenus.MenuItem.Size}.
         */
        public static Size fit(int slots) {
            if (slots < 10) {
                return ONE_LINE;
            } else if (slots < 19) {
                return TWO_LINE;
            } else if (slots < 28) {
                return THREE_LINE;
            } else if (slots < 37) {
                return FOUR_LINE;
            } else if (slots < 46) {
                return FIVE_LINE;
            } else {
                return SIX_LINE;
            }
        }

        /**
         * Gets the {@link cn.bitpixel.general.itemMenus.MenuItem.Size}'s amount of
         * slots.
         *
         * @return The amount of slots.
         */
        public int getSize() {
            return size;
        }
    }
}