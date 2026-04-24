package skypixeldev.gamescene.scene.pub.property;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.manager.SceneManager;

public class PropertyImplementation implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onFall(EntityDamageEvent evt){
        if(evt.getCause() == EntityDamageEvent.DamageCause.FALL){
            if(evt.getEntity() instanceof Player){
                Player player = (Player) evt.getEntity();
                boolean cancelled = false;
                for(GameScene scene : SceneManager.getPlayingScene(player)){
                    if(Properties.hasProperty(player, scene, PlayerProperty.IMMUNE_FALL_DAMAGE)){
                        cancelled = true;
                    }
                }
                evt.setCancelled(cancelled);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMonsterDamage(EntityDamageByEntityEvent evt){
        if(evt.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
            if(evt.getEntity() instanceof Player && evt.getDamager() instanceof LivingEntity && !(evt.getDamager() instanceof Player) && !(evt.getDamager() instanceof Projectile)){
                Player player = (Player) evt.getEntity();
                boolean cancelled = false;
                for(GameScene scene : SceneManager.getPlayingScene(player)){
                    if(Properties.hasProperty(player, scene, PlayerProperty.IMMUNE_MONSTER_DAMAGE)){
                        cancelled = true;
                    }
                }
                evt.setCancelled(cancelled);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent evt){
        if(evt.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
            if(evt.getEntity() instanceof Player && (evt.getDamager() instanceof Player || (evt.getDamager() instanceof Projectile))){
                Player player = (Player) evt.getEntity();
                boolean cancelled = false;
                for(GameScene scene : SceneManager.getPlayingScene(player)){
                    if(Properties.hasProperty(player, scene, PlayerProperty.IMMUNE_PLAYER_DAMAGE)){
                        cancelled = true;
                    }
                }
                evt.setCancelled(cancelled);
            }
        }
    }

    @EventHandler
    public void onProjectileDamage(EntityDamageEvent evt){
        if(evt.getCause() == EntityDamageEvent.DamageCause.PROJECTILE){
            if(evt.getEntity() instanceof Player){
                Player player = (Player) evt.getEntity();
                boolean cancelled = false;
                for(GameScene scene : SceneManager.getPlayingScene(player)){
                    if(Properties.hasProperty(player, scene, PlayerProperty.IMMUNE_PROJECTILE_DAMAGE)){
                        cancelled = true;
                    }
                }
                evt.setCancelled(cancelled);
            }
        }
    }

    @EventHandler
    public void onFireDamage(EntityDamageEvent evt){
        if(evt.getCause() == EntityDamageEvent.DamageCause.FIRE || evt.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK){
            if(evt.getEntity() instanceof Player){
                Player player = (Player) evt.getEntity();
                boolean cancelled = false;
                for(GameScene scene : SceneManager.getPlayingScene(player)){
                    if(Properties.hasProperty(player, scene, PlayerProperty.IMMUNE_FIRE_DAMAGE)){
                        cancelled = true;
                    }
                }
                evt.setCancelled(cancelled);
            }
        }
    }

    @EventHandler
    public void onDrownDamage(EntityDamageEvent evt){
        if(evt.getCause() == EntityDamageEvent.DamageCause.DROWNING){
            if(evt.getEntity() instanceof Player){
                Player player = (Player) evt.getEntity();
                boolean cancelled = false;
                for(GameScene scene : SceneManager.getPlayingScene(player)){
                    if(Properties.hasProperty(player, scene, PlayerProperty.IMMUNE_DROWN_DAMAGE)){
                        cancelled = true;
                    }
                }
                evt.setCancelled(cancelled);
            }
        }
    }

    @EventHandler
    public void onAllDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            boolean cancelled = false;
            for (GameScene scene : SceneManager.getPlayingScene(player)) {
                if (Properties.hasProperty(player, scene, PlayerProperty.IMMUNE_ALL_DAMAGE)) {
                    cancelled = true;
                }
            }
            evt.setCancelled(cancelled);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent evt) {
        Player player = (Player) evt.getEntity();
        boolean cancelled = false;
        for (GameScene scene : SceneManager.getPlayingScene(player)) {
//            Log.l("Checking Scene for " + player + " is in " + scene.getName());
            if (Properties.hasProperty(player, scene, PlayerProperty.IMMUNE_HUNGER)) {
                cancelled = true;
//                Log.lGo("Removed Player Hunger Level Dropped");
            }
        }
        if (cancelled) {
            evt.setFoodLevel(20);
            player.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onPlayerDealDamage(EntityDamageByEntityEvent evt){
        Player whoDeal = null;
        if(evt.getDamager() instanceof Player){
            whoDeal = (Player) evt.getDamager();
        }else if(evt.getDamager() instanceof Projectile){
            if(((Projectile) evt.getDamager()).getShooter() instanceof Player){
                whoDeal = (Player) ((Projectile) evt.getDamager()).getShooter();
            }
        }

        if(whoDeal != null){
            boolean cancelled = false;
            for (GameScene scene : SceneManager.getPlayingScene(whoDeal)) {
                if (Properties.hasProperty(whoDeal, scene, PlayerProperty.NO_ATTACK_DAMAGE)) {
                    cancelled = true;
                }
            }
            evt.setCancelled(cancelled);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent evt){
        Player player = evt.getPlayer();
        boolean cancelled = false;
        for (GameScene scene : SceneManager.getPlayingScene(player)) {
            if (Properties.hasProperty(player, scene, PlayerProperty.NO_BLOCK_PLACE)) {
                cancelled = true;
            }
        }
        evt.setCancelled(cancelled);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent evt){
        Player player = evt.getPlayer();
        boolean cancelled = false;
        for (GameScene scene : SceneManager.getPlayingScene(player)) {
            if (Properties.hasProperty(player, scene, PlayerProperty.NO_BLOCK_BREAK)) {
                cancelled = true;
            }
        }
        evt.setCancelled(cancelled);
    }


}
