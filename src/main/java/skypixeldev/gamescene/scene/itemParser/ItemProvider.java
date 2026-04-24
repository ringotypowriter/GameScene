package skypixeldev.gamescene.scene.itemParser;

import org.bukkit.inventory.ItemStack;
import skypixeldev.gamescene.interfaces.Prioritized;

import java.util.Map;

public interface ItemProvider extends Prioritized {
    String getProviderName();
    ItemStack getItem(String keyName, Map<String,String> options);
}
