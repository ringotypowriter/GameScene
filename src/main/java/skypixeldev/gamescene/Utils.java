package skypixeldev.gamescene;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class Utils {
    private static SimpleDateFormat formater = new SimpleDateFormat();
    public static String locationToString(Location location){
        return  location.getWorld().getName() + ","
                + location.getX() + ","
                + location.getY() + ","
                + location.getZ() + ","
                + location.getYaw() + ","
                + location.getPitch();
    }

    public static String getCurrentFormatTime(){
        return formater.format(new Date(System.currentTimeMillis()));
    }

    private static SecureRandom random = new SecureRandom();

    public static void shufflePlace(Inventory inventory, Collection<ItemStack> items){
        items.forEach(item -> inventory.setItem(random.nextInt(inventory.getSize()), item));
    }

    public static Location stringToLocation(String str){
        String[] args = str.split(",");
        return new Location(Bukkit.getWorld(args[0]),
                Double.parseDouble(args[1]),
                Double.parseDouble(args[2]),
                Double.parseDouble(args[3]),
                Float.parseFloat(args[4]),
                Float.parseFloat(args[5])
        );
    }
}
