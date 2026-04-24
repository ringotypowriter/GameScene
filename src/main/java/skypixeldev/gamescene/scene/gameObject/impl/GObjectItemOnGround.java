package skypixeldev.gamescene.scene.gameObject.impl;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.export.ItemBuilder;
import skypixeldev.gamescene.parser.FunctionManager;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.parser.wildcard.WildcardPlayer;
import skypixeldev.gamescene.scene.GameObjects;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.events.EventEmitter;
import skypixeldev.gamescene.scene.gameObject.BaseObject;
import skypixeldev.gamescene.scene.itemParser.ItemParsingManager;
import skypixeldev.gamescene.scene.pub.TemporaryEntities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Level;

public class GObjectItemOnGround extends BaseObject {

    public static class IOGPubImpl implements Listener {
        private static Random rand = new SecureRandom();
        
        private GameScene implOwner;
        private EventEmitter events;
        public IOGPubImpl(GameScene scene){
            scene.getEvents().on("player_pickup_loot", (obj) -> {
                Player pl = (Player) obj[0];
                ItemStack stack = (ItemStack) obj[1];
                if (stack != null) {
                    if (!stack.getType().equals(Material.AIR)) {
                        pl.playSound(pl.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                    }
                }
                return false;
            });
            implOwner = scene;
            events = scene.getEvents();
        }

        private static boolean isPickupStand(Entity stand){
            if(stand.hasMetadata("LootPickupStand")){
                return true;
            }
            return false;
        }
        
        private static String getSceneOwner(Entity stand){
            if(!stand.hasMetadata("SceneOwner")){
                return null;
            }
            if(stand.getMetadata("SceneOwner").isEmpty()){
                return null;
            }
            try{
                return stand.getMetadata("SceneOwner").get(0).asString();
            }catch (Throwable throwable){
                return null;
            }
        }

        public GameScene getScene() {
            return implOwner;
        }

        public boolean isCurrentScene(Entity stand){
            return this.implOwner.getName().equals(getSceneOwner(stand));
        }


        public static Entity spawnItemOnGround(Location loc, ItemStack stack, boolean canPickup, GameScene owner) {
            if (stack == null) {
                return null;
            }
            if (stack.getType().equals(Material.AIR)) {
                return null;
            }
            ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class);
            stand.setMetadata("LootPickupStand", new FixedMetadataValue(Bootstrap.getInstance(), true));
            if(!canPickup){
                stand.setMetadata("NoPickup", new FixedMetadataValue(Bootstrap.getInstance(), true));
            }
            stand.setMetadata("SceneOwner", new FixedMetadataValue(Bootstrap.getInstance(), owner.getName()));
            stand.setHeadPose(new EulerAngle(0, Math.toRadians(rand.nextInt(360)), 0));
            if (stack.getType().name().endsWith("HELMET") || stack.getType().isBlock()) {
                stand.setHelmet(stack);
                stand.teleport(loc.clone().add(0, -1.5, 0));
            } else if (stack.getType().equals(Material.BONE)) {
                stand.setHelmet(stack);
                stand.setHeadPose(new EulerAngle(Math.toRadians(90), Math.toRadians(rand.nextInt(360)), 0));
                stand.teleport(loc.clone().add(0, -1.2, 0));
            } else {
                stand.setSmall(true);
                stand.setHelmet(stack);
                stand.teleport(loc.clone().add(0, -0.6, 0));
                stand.setHeadPose(new EulerAngle(Math.toRadians(90), Math.toRadians(rand.nextInt(360)), 0));
            }

            stand.setVisible(false);
            stand.setGravity(false);
            stand.setBasePlate(false);
            stand.setInvulnerable(true);
            stand.setArms(false);

            stand.setCustomName("§f" + Unsafe.getItemDisplayName(stack) + "§fx" + stack.getAmount() + " §7-> §e右键捡起");
            if(!Bootstrap.getGlobalSettings().getBoolean("Auto-Hide-And-Show-CustomTag-Of-ArmorStand")) {
               stand.setCustomNameVisible(true);
            }
            return stand;
        }

        @EventHandler
        public void onArmorInteract(PlayerArmorStandManipulateEvent evt) {
            if(!isCurrentScene(evt.getRightClicked())){
                return;
            }
            if (evt.getPlayerItem() != null) {
                if (!evt.getPlayerItem().getType().equals(Material.AIR)) {
                    evt.setCancelled(true);
                    return;
                }
            }
            if (evt.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
                return;
            }
            evt.setCancelled(true);
            if (evt.getRightClicked().hasMetadata("Picked")) {
                evt.getRightClicked().remove();
                return;
            }
            ArmorStand stand = evt.getRightClicked();
            handlePickup(stand, evt.getPlayer());

        }

        private void handlePickup(ArmorStand stand, Player player) {
            if(player.hasMetadata("Disposable")) {
                stand.setMetadata("Picked", new FixedMetadataValue(Bootstrap.getInstance(), true));
            }
            if(stand.hasMetadata("functions")){
                try{
                    List<Parsing> parsings = (List<Parsing>) stand.getMetadata("functions").get(0).value();
                    FunctionManager.execute(parsings, getScene(), new WildcardPlayer(player));
                }catch (Throwable throwable){
                    Bukkit.getLogger().log(Level.WARNING,"read metadata error", throwable);
                    player.sendMessage("Some errors occurred. (" + throwable.getClass().getName() + ")");
                }
                stand.setCustomNameVisible(true);
                return;
            }
            if (stand.getHelmet() != null) {
                if (!events.emit("player_pickup_loot", player, stand.getHelmet()).isCancelled()) {
                    player.getInventory().addItem(stand.getHelmet()).values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
                    stand.setHelmet(new ItemStack(Material.AIR));
                    getScene().getScenePlayerIfPresent(player.getUniqueId()).setData("LastPickupDelay", System.currentTimeMillis());
                }
            }
            if (stand.getChestplate() != null) {
                if (!events.emit("player_pickup_loot", player, stand.getChestplate()).isCancelled()) {
                    player.getInventory().addItem(stand.getChestplate()).values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
                    stand.setChestplate(new ItemStack(Material.AIR));
                    getScene().getScenePlayerIfPresent(player.getUniqueId()).setData("LastPickupDelay", System.currentTimeMillis());
                }
            }
            if (stand.getLeggings() != null) {
                if (!events.emit("player_pickup_loot", player, stand.getLeggings()).isCancelled()) {
                    player.getInventory().addItem(stand.getLeggings()).values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
                    stand.setLeggings(new ItemStack(Material.AIR));
                    getScene().getScenePlayerIfPresent(player.getUniqueId()).setData("LastPickupDelay", System.currentTimeMillis());
                }
            }
            if (stand.getBoots() != null) {
                if (!events.emit("player_pickup_loot", player, stand.getBoots()).isCancelled()) {
                    player.getInventory().addItem(stand.getBoots()).values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
                    stand.setBoots(new ItemStack(Material.AIR));
                    getScene().getScenePlayerIfPresent(player.getUniqueId()).setData("LastPickupDelay", System.currentTimeMillis());
                }
            }
            if (stand.getItemInHand() != null) {
                if (!events.emit("player_pickup_loot", player, stand.getItemInHand()).isCancelled()) {
                    player.getInventory().addItem(stand.getItemInHand()).values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
                    stand.setItemInHand(new ItemStack(Material.AIR));
                    getScene().getScenePlayerIfPresent(player.getUniqueId()).setData("LastPickupDelay", System.currentTimeMillis());
                }
            }
            stand.remove();
        }

        @EventHandler
        public void onArmorInteract(PlayerInteractAtEntityEvent evt) {
            if(!isCurrentScene(evt.getRightClicked())){
                return;
            }
            if (evt.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
                return;
            }
            if (!(evt.getRightClicked() instanceof ArmorStand)) {
                return;
            }
            if (evt.getPlayer().getInventory().getItemInMainHand() == null) {
                return;
            }
            if (evt.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                return;
            }
            evt.setCancelled(true);
            if (evt.getRightClicked().hasMetadata("Picked")) {
                evt.getRightClicked().remove();
                return;
            }

            ArmorStand stand = (ArmorStand) evt.getRightClicked();

            handlePickup(stand, evt.getPlayer());

        }
    }

    public static class Unsafe{
        public static String getItemDisplayName(ItemStack stack){
            try{
                Class<?> clazz = Class.forName("com.meowj.langutils.lang.LanguageHelper");
                Method method = clazz.getDeclaredMethod("getItemDisplayName", ItemStack.class, String.class);
                return (String) method.invoke(null, stack, "zh_cn");
            }catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException exc){
                return stack.getI18NDisplayName() == null ? stack.getI18NDisplayName() : upperCase(stack.getType().name());
            }
        }

        private static String upperCase(String ori){
            String[] array = ori.replaceAll("_"," ").toLowerCase().split(" ");
            StringBuffer buffer = new StringBuffer();
            for(int i = 0;i < array.length;i++){
                String a = array[i];
                if(a.length() < 1){
                    continue;
                }
                a = a.substring(0,1).toUpperCase() + a.substring(1);
                buffer.append(a).append(" ");
            }
            return buffer.toString();
        }
    }


    public GObjectItemOnGround(GameScene scene) {
        super(scene);
        boolean hasInitialized = false;
        if(scene.getVariables().existKey("IOGInitialized")){
            hasInitialized = scene.getVariables().getBoolean("IOGInitialized");
        }

        if(!hasInitialized){
            Bukkit.getPluginManager().registerEvents(new IOGPubImpl(scene), Bootstrap.getInstance());
            scene.getVariables().setBoolean("IOGInitialized",true);
        }
    }

    @Override
    public void onUpdate(GameScene scene, GameObjects.SingletonObject runtime) {

    }

    private UUID settleEntity;


    @Override
    public void onInitialize(GameScene scene, GameObjects objects, GameObjects.SingletonObject runtime) {
        Location spawnLoc = scene.getLocations().get((String) runtime.getOptions().get("location"));
        if(spawnLoc == null){
            throw new IllegalArgumentException();
        }
        String itemFunc = (String) runtime.getOptions().get("item");
        ItemStack stack;
        try{
            stack = ItemParsingManager.parseItem(new Parsing(itemFunc));
        }catch (Throwable throwable){
            Bukkit.getLogger().log(Level.WARNING, "failed to parse '" + itemFunc + "'", throwable);
            stack = UNDEFINE;
        }
        if(stack == null) {
            stack = UNDEFINE;
        }

        Entity entity = IOGPubImpl.spawnItemOnGround(spawnLoc, stack, Parsers.parseBoolean((String) runtime.getOptions().getOrDefault("CanPickup","true")), scene);
        if(entity != null){
            settleEntity = entity.getUniqueId();
            TemporaryEntities.markTemporary(entity);
            ArrayList<String> toParsing = new ArrayList<>();
            if(runtime.getOptions().containsKey("functions")) {
                for (Object o : (List<?>) runtime.getOptions().getOrDefault("functions", Collections.emptyList())) {
                    if (o instanceof String) {
                        toParsing.add((String) o);
                    }
                }

                List<Parsing> parsings = Parsers.parseFunctions(toParsing);
                entity.setMetadata("functions", new FixedMetadataValue(Bootstrap.getInstance(), parsings));
            }
            if(runtime.getOptions().containsKey("disposable")){
                boolean disposable = (Boolean) runtime.getOptions().getOrDefault("disposable", Boolean.FALSE);
                if(disposable){
                    entity.setMetadata("Disposable", new FixedMetadataValue(Bootstrap.getInstance(), true));
                }
            }
        }


    }

    private final ItemStack UNDEFINE = new ItemBuilder(Material.DIRT).displayName("§d未定义").build();

    @Override
    public void onRemove(GameScene scene) {
        if(settleEntity != null)
            TemporaryEntities.markRemoved(settleEntity);
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
