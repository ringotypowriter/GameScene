package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.parser.exception.ParsingFunctionUnsupportedException;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.LootTable;
import skypixeldev.gamescene.scene.itemParser.ItemParsingManager;
import skypixeldev.gamescene.scene.manager.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionGiveItems implements IFunction {
    /**
     * giveItem(player: $player$, safeGive: $need?$ <item: $item$ / (lootTable: $LootTable$, repeat: $repeatTimes$)> )
     *
     * @param argument
     * @param whereInvoke
     * @param isAsync
     * @return
     */
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        if(isAsync) throw new ParsingFunctionUnsupportedException();

        GameScene scene = whereInvoke;
        if(argument.containsKey("gameScene")){
            scene = SceneManager.searchSceneByName(argument.get("gameScene"));
        }

        String playerName = Selector.searchNonNull(argument,"player","p","pl");
        if(Bukkit.getPlayer(playerName) == null){
            return false;
        }
        Player player = Bukkit.getPlayer(playerName);
        List<ItemStack> sets = new ArrayList<>();
        boolean safeGive = Parsers.parseBoolean(argument.getOrDefault("safeGive", "false"));
        if(argument.containsKey("item")){
            sets.add(ItemParsingManager.parseItem(new Parsing(argument.get("item"))));
        }else if(argument.containsKey("lootTable")){
            LootTable.LootTableObject lootTable = scene.getLootTable().getLoadedLootTable(argument.get("lootTable"));
            int repeats = Integer.parseInt(argument.getOrDefault("repeat", "1"));
            sets.addAll(lootTable.roll(repeats));
        }else{
            throw new ParsingFunctionUnsupportedException("don't have any item argument provided");
        }
        if(sets.isEmpty()){
            return true;
        }
        Map<Integer,ItemStack> remain = player.getInventory().addItem(sets.toArray(new ItemStack[0]));
        if(!remain.isEmpty() && safeGive){
            remain.values().forEach(i -> player.getWorld().dropItem(player.getLocation(), i));
        }
        return true;
    }

    @Override
    public String getFunction() {
        return "giveItems";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
