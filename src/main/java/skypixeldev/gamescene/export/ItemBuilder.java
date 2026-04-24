package skypixeldev.gamescene.export;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

public class ItemBuilder {
    @Getter private MaterialData data;
    @Getter private String displayName;
    @Getter private List<String> lores;
    @Getter private List<ItemFlag> itemFlag;
    @Getter private Map<Enchantment,Integer> enchants;
    @Getter private int amount;
    @Getter private short shortDamage;
    @Getter private boolean unbreakable;

    public ItemBuilder(Material material){
        this(new MaterialData(material));
    }

    public ItemBuilder(Material material, byte dataID){
        this(new MaterialData(material, dataID));
    }

    public ItemBuilder(MaterialData data){
        this.data = data;
        lores = new ArrayList<>();
        itemFlag = new ArrayList<>();
        enchants = new HashMap<>();
        amount = 1;
        shortDamage = 0;
        unbreakable = false;
    }
    public ItemStack build(){
        ItemStack stack = data.toItemStack(amount);
        stack.setDurability(shortDamage);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lores);
        enchants.forEach((enc,lvl) -> meta.addEnchant(enc,lvl,true));
        meta.addItemFlags(itemFlag.toArray(new ItemFlag[0]));
        meta.setUnbreakable(unbreakable);
        stack.setItemMeta(meta);

        return stack;
    }

    public ItemBuilder spotlight(){
        return itemFlag(ItemFlag.HIDE_ENCHANTS).enchant(Enchantment.ARROW_DAMAGE,1);
    }

    public ItemBuilder unbreakable() {
        this.unbreakable = true;
        return this;
    }

    public ItemBuilder newData(MaterialData data) {
        this.data = data;
        return this;
    }

    public ItemBuilder durability(short damage){
        this.shortDamage = damage;
        return this;
    }

    public ItemBuilder lore(String... lores) {
        this.lores.addAll(Arrays.asList(lores));
        return this;
    }

    public ItemBuilder enchant(Enchantment enc, int lvl) {
        this.enchants.put(enc,lvl);
        return this;
    }

    public ItemBuilder enchant(Map<Enchantment,Integer> encs) {
        encs.forEach((enc,lvl) -> this.enchants.put(enc,lvl));
        return this;
    }

    public ItemBuilder itemFlag(ItemFlag... itemFlag) {
        this.itemFlag.addAll(Arrays.asList(itemFlag));
        return this;
    }

    public ItemBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }
}
