package skypixeldev.gamescene.scene.itemParser;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.export.ItemBuilder;
import skypixeldev.gamescene.interfaces.Prioritized;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.parser.wildcard.WildcardFormatCode;
import skypixeldev.gamescene.parser.wildcard.WildcardRandom;
import skypixeldev.gamescene.unstable.NBTManager;

import java.util.*;
import java.util.stream.Collectors;

public class ItemParsingManager {
    private static Set<ItemProvider> externalProviders;

    private static WildcardRandom random;

    static{
        random = new WildcardRandom();
        externalProviders = new TreeSet<>(Prioritized.COMPARATOR);
    }

    public final static ItemStack ERROR_ITEM = new ItemBuilder(Material.INK_SACK)
            .durability((short) 8)
            .displayName("§e<§c解析错误§e>")
            .lore("§f底层代码产生了一个无法解析的物品")
            .lore("§f使得你得到了它")
            .lore("§f请报告管理员并告知它应是什么物品")
            .lore("§f以帮助我们快速修复它").enchant(Enchantment.ARROW_DAMAGE, 1)
            .itemFlag(ItemFlag.HIDE_ENCHANTS)
            .unbreakable()
            .build();

    public static ItemStack parseItem(Parsing parsing){
        return parseInternal(parsing);
    }

    private static ItemStack parseInternal(Parsing parsing){
        Material material = Material.getMaterial(parsing.getFunctionName().toUpperCase());
        Map<String,String> options = new HashMap<>(parsing.getArguments());
        options = random.replaceWildcard(options);
        options = new WildcardFormatCode().replaceWildcard(options);
        if(material != null){
            int amount = Parsers.parseInteger(options.get("amount"), 1);
            byte data = Parsers.parseByte(options.get("data") , (byte) 0);
            ItemBuilder builder = new ItemBuilder(material,data).amount(amount);
            if(options.containsKey("displayName")){
                builder = builder.displayName(options.get("displayName"));
            }
            if(options.containsKey("lores")){
                for(String s : Parsers.parseList(options.get("lores"))){
                    builder = builder.lore(s);
                }
            }
            if(options.containsKey("itemFlags")){
                for(String s : Parsers.parseList(options.get("itemFlags"))){
                    try{
                        ItemFlag flag = ItemFlag.valueOf(s);
                        builder = builder.itemFlag(flag);
                    }catch (IllegalArgumentException exc){
                        Log.lPf(s + " is not an itemFlag");
                    }
                }
            }

            if(options.containsKey("unbreakable")){
                if(Parsers.parseBoolean(options.get("unbreakable"))){
                    builder = builder.unbreakable();
                }
            }

            if((options.containsKey("enchanted") && Parsers.parseBoolean(options.get("enchanted")))) {
                Map<Enchantment, Integer> map = new HashMap<>();
                for(Enchantment enchantment : Enchantment.values()){
                    if(options.containsKey(enchantment.getName().toUpperCase())){
                        map.put(enchantment, Parsers.parseInteger(options.get(enchantment.getName().toUpperCase()), 0));
                    }
                }
                map.entrySet().removeIf(entry -> entry.getValue() < 1);

                builder = builder.enchant(map);
            }

            ItemStack stack = builder.build();

            // NBT
            if(NBTManager.isStable()) {
                for (String s : options.keySet().stream().map(String::toLowerCase).collect(Collectors.toList())) {
                    s = s.replaceAll(">>", ";");
                    if (s.startsWith("nbt;")) {
                        String[] array = s.split(";");
                        if (array.length == 3) {
                            String dataType = array[1];
                            String key = array[2];
                            if (dataType.equalsIgnoreCase("double") || dataType.equalsIgnoreCase("d")) {
                                stack = NBTManager.setDoubleTag(stack, key, Parsers.parseDouble(options.get(s), 0));
                            } else if (dataType.equalsIgnoreCase("string")
                                    || dataType.equalsIgnoreCase("str")
                                    || dataType.equalsIgnoreCase("s")) {
                                stack = NBTManager.setStringTag(stack, key, options.get(s));
                            } else if (dataType.equalsIgnoreCase("list")
                                    || dataType.equalsIgnoreCase("stringList")
                                    || dataType.equalsIgnoreCase("strList")
                                    || dataType.equalsIgnoreCase("l")
                                    || dataType.equalsIgnoreCase("stringArray")
                                    || dataType.equalsIgnoreCase("array")
                                    || dataType.equalsIgnoreCase("strArray")) {
                                stack = NBTManager.setStringListTag(stack, key, Parsers.parseList(options.get(s)));
                            } else {
                                Log.lPf("unknown nbt type: " + dataType + " (only can be string, double and list)");
                            }
                        }
                    }
                }
            }

            return stack;
        }else{
            for(ItemProvider provider : externalProviders){
                ItemStack res = provider.getItem(parsing.getFunctionName(), options);
                if(res !=  null){
                    return res;
                }
            }
        }
        Log.lPf("unknown item: " + parsing.getOriginalExpression());
        return ERROR_ITEM;
    }

    public static void registerItemProvider(ItemProvider provider){
        externalProviders.add(provider);
        Log.lPf("Imported Item Provider from " + provider.getProviderName());
    }
}
