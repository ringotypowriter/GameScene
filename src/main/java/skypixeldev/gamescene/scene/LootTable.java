package skypixeldev.gamescene.scene;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.config.GConfig;
import skypixeldev.gamescene.export.RangedRandom;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.scene.itemParser.ItemParsingManager;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class LootTable {

    public static class LootTableObject{
        @Getter private String tableKey;
        private RangedRandom random;
        @Getter private LootTable owner;
        @Getter private List<LootDiceObject> dices;
        @Getter @Setter
        private boolean isSingleDice;

        private LootTableObject(String tableKey, int randomMin, int randomMax, List<LootDiceObject> dices, LootTable owner){
            this.tableKey = tableKey;
            this.random = new RangedRandom(randomMin, randomMax);
            random.setRandomSeed(System.currentTimeMillis()-5000);
            this.owner = owner;
            this.dices = new ArrayList<>();
            this.dices.addAll(dices);
        }

        public List<ItemStack> roll(){
            return roll(1);
        }

        public List<ItemStack> roll(int repeat){
            List<ItemStack> sets = new ArrayList<>(repeat+3);
            for(int i = 0;i < repeat;i++){
                int priorNumber = random.nextInt();
                for(LootDiceObject dice : dices){
                    if(dice.getPriorityOperator().apply(dice.getDicePriority(), priorNumber)){
                        sets.addAll(dice.roll());
                    }
                }
            }
            return sets;
        }

        public void rollToInventory(Inventory inv, int repeat, boolean shuffle){
            List<ItemStack> rolled = roll(repeat);
            if(rolled.isEmpty()){
                return;
            }
            List<ItemStack> list = new ArrayList<>(rolled);
            Random generalRandom = new Random(System.currentTimeMillis() + 1500);
            for(int i = 0; i < list.size();i++){
                if(shuffle){
                    inv.setItem(generalRandom.nextInt(inv.getSize()), list.get(i));
                }else{
                    inv.addItem(list.get(i));
                }
            }
        }


    }

    public static class LootDiceObject{
        @Getter private String diceKey;
        @Getter private int dicePriority;
        @Getter private BiFunction<Integer,Integer,Boolean> priorityOperator;
        private List<Parsing[]> items;
        @Getter private Random random;
        @Getter private LootTable owner;

        public List<ItemStack> roll(){
            return Arrays.stream(items.get(random.nextInt(items.size()))).map(ItemParsingManager::parseItem).collect(Collectors.toList());
        }

        public LootDiceObject(String diceKey, int dicePriority, BiFunction<Integer,Integer,Boolean> operator, List<String> items, LootTable owner){
            this.diceKey = diceKey;
            this.dicePriority = dicePriority;
            this.priorityOperator = operator;
            this.items = new ArrayList<>();
            for(String s : items){
                if(!s.contains("&&")) {
                    this.items.add(new Parsing[]{new Parsing(s)});
                }else{
//                    String[] strings = s.split("\\s*&&\\s*");
                    String[] strings = s.split("\\s*&&(?=([^\"]*\"[^\"]*\")*[^\"]*$)\\s*");
                    this.items.add(Arrays.stream(strings).map(parsing -> {
                        try{
                            return new Parsing(parsing);
                        }catch (Exception exc){
                            return null;
                        }
                    }).filter(Objects::nonNull).toArray(Parsing[]::new));
                }
            }
            this.owner = owner;
            this.random = new Random(System.currentTimeMillis() + 10000);
        }

    }

    private static HashMap<String, BiFunction<Integer,Integer,Boolean>> operators;

    private void putOperator(BiFunction<Integer,Integer,Boolean> lazyFunction, String... symbol){
        for(String s : symbol){
            operators.put(s,lazyFunction);
        }
    }

    private void initOperators(){
        operators = new HashMap<>();
        putOperator(Integer::equals,"=","==","equals","equal");
        putOperator((i1,i2) -> i2 < i1, "<","less","lesser");
        putOperator((i1,i2) -> i2 > i1,">","great","greater");
        putOperator((i1,i2) -> i2 <= i1, "≤","less-equal","lesser-equal");
        putOperator((i1,i2) -> i2 >= i1,"≥","great-equal","greater-equal");
        putOperator((i1,i2) -> !i2.equals(i1), "≠","unequal","unequals");
    }

    @Getter
    private GameScene scene;
    @Getter private GConfig config;
    @Getter private Map<String,LootTableObject> loadedTables;

    public LootTable(GameScene scene){
        initOperators();
        this.scene = scene;
        this.config = scene.getProvider().getOrCreateConfig("LootTable");
        loadedTables = new HashMap<>();
        ConfigurationSection root = config.getOrCreateConfigurationSection("LootTables");
        for(String key : root.getKeys(false)){
            ConfigurationSection section = root.getConfigurationSection(key);
            if(section != null){
                int randomMin = section.getInt("Range.Min",0);
                int randomMax = section.getInt("Range.Max",100);
                ConfigurationSection dices = section.getConfigurationSection("Dices");
                ConfigurationSection singleDice = section.getConfigurationSection("SingleDice");
                if(dices == null && singleDice == null){
                    throw new IllegalStateException("table " + key + " has a invalid dices");
                }
                List<LootDiceObject> diceObjects = new ArrayList<>();
                if(dices != null) {
                    for (String diceKey : dices.getKeys(false)) {
                        ConfigurationSection diceSec = dices.getConfigurationSection(diceKey);
                        int priority;
                        BiFunction<Integer, Integer, Boolean> operator = operators.get("<");
                        if (diceSec.contains("priority")) {
                            priority = diceSec.getInt("priority", 0);
                            operator = operators.get(diceSec.getString("priorityOperator", "="));
                        } else {
                            priority = diceSec.getInt("percent");
                        }
                        List<String> items = diceSec.getStringList("items");
                        diceObjects.add(new LootDiceObject(diceKey, priority, operator, items, this));
                    }
                }else{
                    ConfigurationSection diceSec = singleDice;
                    int priority = -1;
                    BiFunction<Integer, Integer, Boolean> operator = operators.get("<");
                    if (diceSec.contains("priority")) {
                        diceSec.getInt("priority", 0);
                        operator = operators.get(diceSec.getString("priorityOperator", "="));
                    } else {
                        priority = diceSec.getInt("percent");
                        if(diceSec.getBoolean("reversePercent", false)){
                            operator = operators.get(">");
                        }
                    }
                    List<String> items = diceSec.getStringList("items");
                    LootDiceObject diceObject = new LootDiceObject("SingleDice", priority, operator, items, this);
                    diceObjects.add(diceObject);

                }

                LootTableObject object = new LootTableObject(key,randomMin, randomMax,diceObjects,this);
                if(singleDice != null){
                    object.setSingleDice(true);
                }
                loadedTables.put(object.getTableKey(), object);
                Log.lLt("Loaded LootTables " + object.getTableKey() + " for Game Scene " + scene.getName());
            }
        }
        this.config.saveToFile();
    }

    public LootTableObject getLoadedLootTable(String key){
        return this.loadedTables.get(key);
    }

}
