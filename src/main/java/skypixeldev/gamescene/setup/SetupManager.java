package skypixeldev.gamescene.setup;


import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.Utils;
import skypixeldev.gamescene.export.ItemBuilder;
import skypixeldev.gamescene.export.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SetupManager {
    public static class SetupBlockSelector implements Listener{
        public final static ItemStack SINGLE_SELECTOR = new ItemBuilder(Material.EMERALD)
                .displayName("§f<§e单方块坐标选择器§f>")
                .lore("")
                .lore(" §7- §a左击/右击 §f方块 设置坐标")
                .lore(" §6§lGame Scene Setup")
                .lore("")
                .build();
        public final static ItemStack MULTI_SELECTOR = new ItemBuilder(Material.DIAMOND)
                .displayName("§f<§e多方块坐标选择器§f>")
                .lore("")
                .lore(" §7- §a左击 §f方块 设置坐标A")
                .lore(" §7- §a右击 §f方块 设置坐标B")
                .lore(" §6§lGame Scene Setup")
                .lore("")
                .build();

        private final ConcurrentHashMap<Location,Long> restorer = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<UUID, Location> setupLocationSingle = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<UUID, Location> setupLocationAlpha = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<UUID, Location> setupLocationBeta = new ConcurrentHashMap<>();

        public SetupBlockSelector(){
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(!restorer.isEmpty()){
                        Set<Location> toRemove = new HashSet<>();
                        restorer.forEach((loc, endTime) -> {
                            if(System.currentTimeMillis() >= endTime){
                                toRemove.add(loc);
                            }
                        });
                        toRemove.forEach(loc -> {
                            loc.getBlock().getState().update();
                            restorer.remove(loc);
                        });
                    }
                }
            }.runTaskTimer(Bootstrap.getInstance(), 20, 20);
        }

        public Location getPlayerSingletonLocation(Player player){
            return setupLocationSingle.get(player.getUniqueId());
        }

        public Pair<Location,Location> getPlayerPairLocation(Player player){
            return new Pair<>(setupLocationAlpha.get(player.getUniqueId()), setupLocationBeta.get(player.getUniqueId()));
        }

        public void removeSingletonLocation(Player player){
            setupLocationSingle.remove(player.getUniqueId());
        }

        public void removePairLocation(Player player){
            setupLocationAlpha.remove(player.getUniqueId());
            setupLocationBeta.remove(player.getUniqueId());
        }

        @EventHandler
        public void onInteract(PlayerInteractEvent evt){
            if(evt.hasBlock() && evt.hasItem()){
                boolean isMulti = true;
                if(evt.getItem().isSimilar(SINGLE_SELECTOR)){
                    isMulti = false;
                }else if(!evt.getItem().isSimilar(MULTI_SELECTOR)){
                    return;
                }
                evt.setCancelled(true);
                boolean right = (evt.getAction().name().startsWith("RIGHT_"));
                Player player = evt.getPlayer();
                Location blockLocation = evt.getClickedBlock().getLocation();
                if(!isMulti){
                    setupLocationSingle.put(player.getUniqueId(), blockLocation);
                    player.sendBlockChange(blockLocation, Material.EMERALD_BLOCK, (byte)0);
                    restorer.put(blockLocation, System.currentTimeMillis() + 8000);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2f);
                    player.sendTitle("§a√ 设置完成 - 单坐标", "§7§o" + Utils.locationToString(blockLocation), 0,20 * 3,0);
                }else{
                    if(!right){
                        setupLocationAlpha.put(player.getUniqueId(), blockLocation);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2f);
                        player.sendTitle("§a√ 设置完成 - 坐标A", "§7§o" + Utils.locationToString(blockLocation), 0,20 * 3,0);
                    }else{
                        setupLocationBeta.put(player.getUniqueId(), blockLocation);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2f);
                        player.sendTitle("§a√ 设置完成 - 坐标B", "§7§o" + Utils.locationToString(blockLocation), 0,20 * 3,0);
                    }
                    Pair<Location,Location> pair = getPlayerPairLocation(player);
                    if (pair.getFirst() != null) {
                        player.sendBlockChange(pair.getFirst(), Material.DIAMOND_BLOCK, (byte)0);
                        restorer.put(pair.getFirst(), System.currentTimeMillis() + 8000);
                    }

                    if(pair.getSecond() != null){
                        player.sendBlockChange(pair.getSecond(), Material.DIAMOND_BLOCK, (byte)0);
                        restorer.put(pair.getSecond(), System.currentTimeMillis() + 8000);
                    }

                }
            }
        }
    }
    public final static String PERM_NODE_SETUP = "gamescene.setup";
    private static SetupBlockSelector SELECTOR;

    @Getter private static Setup runningSetup = null;
    public static void onLoadCommand(JavaPlugin instance, PaperCommandManager cmdManager){
        SELECTOR = new SetupBlockSelector();
        Bukkit.getPluginManager().registerEvents(SELECTOR,instance);

//        BasicBukkitCommandGraph graph = new BasicBukkitCommandGraph();
//        graph.getRootDispatcherNode().registerNode("gsetup").registerCommands(new SetupCommands());
//
//        BukkitIntake intake = new BukkitIntake(instance, graph);
//        intake.register();
        cmdManager.registerCommand(new SetupCommands());
    }

    public static SetupBlockSelector getSelector(){
        return SELECTOR;
    }

    public static void pollAllSetup(Consumer<Player> queue){
        Bukkit.getOnlinePlayers().forEach(pl -> {
            if(pl.hasPermission(PERM_NODE_SETUP)){
                queue.accept(pl);
            }
        });
    }

    public static boolean hasRunningSetup(){
        return runningSetup != null;
    }

    public static void unloadCurrentSetup(){
        if(runningSetup == null){
            return;
        }
        runningSetup.stopSetup();
        runningSetup = null;
    }


    public static boolean loadNewSetup(String name){
        if(runningSetup != null){
            return false;
        }
        try {
            runningSetup = new Setup(name);
        }catch (Exception exc){
            exc.printStackTrace();
            return false;
        }
        return true;
    }
}
