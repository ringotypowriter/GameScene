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

public class FunctionModifyHealth implements IFunction {
    private static HashMap<String, BiFunction<Double,Double,Double>> operators;


    public FunctionModifyHealth(){
        operators = new HashMap<>();
        putOperator(Double::sum,"+","add","heal");
        putOperator((health, val) -> health - val,"-","remove","damage");
        putOperator((health, val) -> health * val,"*","multiply","x");
        putOperator((health, val) -> health / val,"/","divide","÷");

    }

    private void putOperator(BiFunction<Double,Double,Double> lazyFunction, String... symbol){
        for(String s : symbol){
            operators.put(s,lazyFunction);
        }
    }

    private BiFunction<Double,Double,Double> getOperator(String operator){
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
            throw new ParsingFunctionUnsupportedException("modifyHealth cannot be in async");
        }
        String playerName = Selector.searchNonNull(argument,"player","p","pl");
        if(Bukkit.getPlayer(playerName) == null){
            return false;
        }
        Player player = Bukkit.getPlayer(playerName);
        String operator = Selector.searchNonNull(argument,"operator","operation","opt","o");
        double val = Parsers.parseDouble(Selector.searchNonNull(argument,"value","val","v"),0);
        player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(),
                getOperator(operator).apply(player.getHealth(), val)));
        return true;
    }

    @Override
    public String getFunction() {
        return "modifyHealth";
    }

    @Override
    public String getProvider() {
        return "std";
    }
}
