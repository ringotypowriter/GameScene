package skypixeldev.gamescene;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public class Log {
    public static void l(String msg){
        Bukkit.getConsoleSender().sendMessage("§7[§dGAME SCENE§7] §f" + msg);
    }

    public static void lEr(Throwable throwable,String reason){
        Bukkit.getLogger().log(Level.INFO, throwable, () -> reason);
    }

    public static void lPf(String msg){
        Bukkit.getConsoleSender().sendMessage("§7[§6Parsing Functions§7] §f" + msg);
    }
    public static void lGo(String msg){
        Bukkit.getConsoleSender().sendMessage("§7[§6Game Object§7] §f" + msg);
    }
    public static void lSc(String msg){
        Bukkit.getConsoleSender().sendMessage("§7[§6Schedulers§7] §f" + msg);
    }
    public static void lLt(String msg){
        Bukkit.getConsoleSender().sendMessage("§7[§6LootTables§7] §f" + msg);
    }
    public static void lJr(String msg) {
        Bukkit.getConsoleSender().sendMessage("§7[§dJournals§7] §f" + msg);
    }
}
