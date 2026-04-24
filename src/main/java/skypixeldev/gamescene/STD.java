package skypixeldev.gamescene;

import skypixeldev.gamescene.parser.FunctionManager;
import skypixeldev.gamescene.parser.functions.*;
import skypixeldev.gamescene.pool.Pools;
import skypixeldev.gamescene.scene.gameObject.GObjectManager;
import skypixeldev.gamescene.scene.gameObject.impl.*;

public class STD {
    public final static String EVENT_PLAYER_JOIN = "player_join_scene"; // Since 1.0
    public final static String EVENT_PLAYER_LEFT = "player_left_scene"; // Since 1.0
    public final static String EVENT_GLOBAL_PLAYER_QUIT = "player_quit_global"; // Since 1.0
    public final static String EVENT_GLOBAL_PLAYER_JOIN = "player_join_global"; // Since 1.0

    public static Pools getPools(){
        return Pools.getImplement();
    }

    public static void importFunctions(){
        Log.lPf("Importing STD Library...");

        /*
        Functions
         */
        FunctionManager.register(new FunctionLog()); // Log Since 1.0
        FunctionManager.register(new FunctionBroadcast()); // Broadcast Since 1.0
        FunctionManager.register(new FunctionMessage()); // Message Since 1.0
        FunctionManager.register(new FunctionTitle()); // Title Since 1.0
        FunctionManager.register(new FunctionModifyHealth()); // ModifyHealth Since 1.0
        FunctionManager.register(new FunctionTeleport()); // Teleport Since 1.0
        FunctionManager.register(new FunctionCommand()); // ConsoleCommand Since 1.0
        FunctionManager.register(new FunctionChat()); // PlayerChat Since 1.0
        FunctionManager.register(new FunctionSoundNearby()); // SoundNearby Since 1.0
        FunctionManager.register(new FunctionSoundPlayer()); // SoundPlayer Since 1.0
        FunctionManager.register(new FunctionBroadcastNearby());  // BroadcastNearby Since 1.0
        FunctionManager.register(new FunctionGiveItems()); // GiveItems Since 1.0
        FunctionManager.register(new FunctionResetScheduler()); // ResetScheduler Since 1.0
        FunctionManager.register(new FunctionBroadcastScene()); // BroadcastInScene Since 1.0

        FunctionManager.register(new FunctionActivateScheduler()); // ActivateScheduler Since 1.1
        FunctionManager.register(new FunctionApplyVelocity()); // ApplyVelocity Since 1.1
        FunctionManager.register(new FunctionApplyVelocityDelayed()); // ApplyVelocityDelayed Since 1.1
        FunctionManager.register(new FunctionAll()); // All Since 1.1
        FunctionManager.register(new FunctionHidePlayer()); // hideOthers 1.1
        FunctionManager.register(new FunctionShowPlayer()); // showOthers 1.1
        FunctionManager.register(new FunctionRefreshDisposed()); // refreshDisposed 1.1 - For Aura
        FunctionManager.register(new FunctionRefreshAllDisposed()); // refreshAllDisposed 1.1 - For Aura
        FunctionManager.register(new FunctionClearInventory()); // clearInv 1.1
        FunctionManager.register(new FunctionPotionEffect()); // addPotionEffect 1.1
        FunctionManager.register(new FunctionRemoveEffect()); // removePotionEffect 1.1
        FunctionManager.register(new FunctionModifyHunger()); // modifyHunger 1.1
        FunctionManager.register(new FunctionDisconnect()); // disconnect 1.1
        FunctionManager.register(new FunctionAddProperty()); // addProperty 1.1
        FunctionManager.register(new FunctionRemoveProperty()); // removeProperty 1.1
        FunctionManager.register(new FunctionClearProperties()); // clearProperties 1.1
        FunctionManager.register(new FunctionSetGameMode()); // setGameMode 1.1

        /*
        GameObjects
         */
        GObjectManager.register("PlayerSpawn", GObjectPlayerSpawn.class);
        GObjectManager.register("Billboard", GObjectBillboard.class);
        GObjectManager.register("TemporaryBillboard", GObjectTemporaryBillboard.class);
        GObjectManager.register("Aura", GObjectAura.class);
        GObjectManager.register("LootChest", GObjectLootChest.class);
        GObjectManager.register("ItemOnGround", GObjectItemOnGround.class);
        GObjectManager.register("UnlockGate", GObjectUnlockGate.class);

        /*
        Events
         */
    }
}
