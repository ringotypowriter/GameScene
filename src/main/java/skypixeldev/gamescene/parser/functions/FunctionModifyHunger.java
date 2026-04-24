package skypixeldev.gamescene.parser.functions;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.parser.exception.ParsingFunctionUnsupportedException;
import skypixeldev.gamescene.scene.GameScene;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class FunctionModifyHunger implements IFunction {
    private static HashMap<String, BiFunction<Integer,Integer,Integer>> operators;


    public FunctionModifyHunger(){
        operators = new HashMap<>();

        putOperator(Integer::sum,"+","add","increase","plus");
        putOperator((health, val) -> health - val,"-","remove","reduce","decrease");
        putOperator((health, val) -> health * val,"*","multiply","x");
        putOperator((health, val) -> health / val,"/","divide","÷");

    }

    private void putOperator(BiFunction<Integer,Integer,Integer> lazyFunction, String... symbol){
        for(String s : symbol){
            operators.put(s,lazyFunction);
        }
    }

    private BiFunction<Integer,Integer,Integer> getOperator(String operator){
        return operators.getOrDefault(operator, (health,val) -> health);
    }

    /**
     * modifyHealth(player: $player$, operator: $operator$, val: $value$
     * operator:
     *  ~ + add heal = health + val
     *  ~ - remove damage = health - val
     *  ~ * multiply x = health * val
     *  ~ / divide ÷ = health / val
     */
    @Override
    public boolean execute(Map<String, String> argument, GameScene whereInvoke, boolean isAsync) {
        if(isAsync){
            throw new ParsingFunctionUnsupportedException("modifyHunger cannot be in async");
        }
        String playerName = Selector.searchNonNull(argument,"player","p","pl");
        if(Bukkit.getPlayer(playerName) == null){
            return false;
        }
        Player player = Bukkit.getPlayer(playerName);
        String operator = Selector.searchNonNull(argument,"operator","operation","opt","o");
        int val = Parsers.parseInteger(Selector.searchNonNull(argument,"value","val","v"),0);
        player.setFoodLevel(Math.min(20,
                getOperator(operator).apply(player.getFoodLevel(), val)));
        return true;
    }

    @Override
    public String getFunction() {
        return "modifyHunger";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
