package skypixeldev.gamescene.unstable;

import com.google.common.collect.Lists;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NBTManager {

    public static boolean isStable(){
        try{
            Class.forName("org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack");
            return true;
        }catch (Exception ignored){
            return false;
        }
    }

    public static ItemStack setStringTag(ItemStack stack, String key, String value) {
        ItemStack result = stack.clone();
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(result);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
        compound.set(key, new NBTTagString(value));
        nmsStack.setTag(compound);
        result = CraftItemStack.asBukkitCopy(nmsStack);
        return result;
    }

    public static ItemStack setDoubleTag(ItemStack stack, String key, double value) {
        ItemStack result = stack.clone();
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(result);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
        compound.set(key, new NBTTagDouble(value));
        nmsStack.setTag(compound);
        result = CraftItemStack.asBukkitCopy(nmsStack);
        return result;
    }

    public static ItemStack setIntegerTag(ItemStack stack, String key, int value){
        ItemStack result = stack.clone();
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(result);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
        compound.set(key, new NBTTagInt(value));
        nmsStack.setTag(compound);
        result = CraftItemStack.asBukkitCopy(nmsStack);
        return result;
    }

    public static boolean hasTag(ItemStack stack, String key) {
        if (stack == null) {
            return false;
        }
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        if (nmsStack == null) {
            return false;
        }
        if (!nmsStack.hasTag()) {
            return false;
        }
        return nmsStack.getTag().hasKey(key);
    }

    public static String getStringTag(ItemStack stack, String key) {
        if (stack == null) {
            return "";
        }
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        if (nmsStack == null) {
            return "";
        }
        if (!nmsStack.hasTag()) {
            return "";
        }
        return Optional.of(nmsStack.getTag().getString(key)).orElse("");
    }

    public static double getDoubleTag(ItemStack stack, String key) {
        if (stack == null) {
            return 0;
        }
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        if (nmsStack == null) {
            return 0;
        }
        if (!nmsStack.hasTag()) {
            return 0;
        }
        return nmsStack.getTag().getDouble(key);
    }

    public static int getIntegerTag(ItemStack stack, String key) {
        if (stack == null) {
            return 0;
        }
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        if (nmsStack == null) {
            return 0;
        }
        if (!nmsStack.hasTag()) {
            return 0;
        }
        return nmsStack.getTag().getInt(key);
    }

    public static ItemStack removeTag(ItemStack stack, String key){
        ItemStack result = stack.clone();
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(result);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
        if(compound.hasKey(key)) {
            compound.remove(key);
        }
        nmsStack.setTag(compound);
        result = CraftItemStack.asBukkitCopy(nmsStack);
        return result;
    }

    public static ItemStack setStringListTag(ItemStack stack, String key, List<String> value) {
        ItemStack result = stack.clone();
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asNMSCopy(result);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
        NBTTagList lists = new NBTTagList();
        for (String entry : value) {
            lists.add(new NBTTagString(entry));
        }
        compound.set(key, lists);
        nmsStack.setTag(compound);
        result = CraftItemStack.asBukkitCopy(nmsStack);
        return result;
    }

    public static List<String> getStingListTag(ItemStack stack, String key) {
        if (stack == null) {
            return Lists.newArrayList();
        }
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        if (nmsStack == null) {
            return Lists.newArrayList();
        }
        if (!nmsStack.hasTag()) {
            return Lists.newArrayList();
        }
        ArrayList<String> result = new ArrayList<>();
        NBTTagList list = nmsStack.getTag().getList(key, 8);
        for (int i = 0; i < list.size(); i++) {
            result.add(list.getString(i));
        }
        return result;
    }
}
