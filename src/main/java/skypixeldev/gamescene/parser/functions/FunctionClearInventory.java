package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.scene.GameScene;

import java.util.Map;

public class FunctionClearInventory implements IFunction {
    private ItemStack air = new ItemStack(Material.AIR);
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        String playerName = Selector.searchNonNull(argument,"player","p","pl");
        if(Bukkit.getPlayer(playerName) == null){
            return false;
        }
        Player player = Bukkit.getPlayer(playerName);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{air,air,air,air});
        return true;
    }

    @Override
    public String getFunction() {
        return "clearInv";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
