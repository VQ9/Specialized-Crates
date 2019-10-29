package me.ztowne13.customcrates.utils.nbt_utils;

import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.NMSUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ztowne13 on 6/11/16.
 */
public class NBTTagReflection
{
    public static Class getCraftItemStack()
    {
        try
        {
            return Class.forName("org.bukkit.craftbukkit." + NMSUtils.getVersionRaw() + ".inventory.CraftItemStack");
        }
        catch (Exception exc)
        {
            ChatUtils.log("Failed to load CraftItemStack. Please check plugin is up to date.");
        }
        return null;
    }

    public static Object getNMSItemStack(ItemStack stack)
    {
        try
        {
            return getCraftItemStack().getMethod("asNMSCopy", ItemStack.class).invoke(getCraftItemStack(), stack);
        }
        catch (Exception exc)
        {
            ChatUtils.log("Failed to load NMS ItemStack. Please check plugin is up to date.");
        }
        return null;
    }

    public static Object getNewNBTTagCompound()
    {
        try
        {
            return Class.forName("net.minecraft.server." + NMSUtils.getVersionRaw() + ".NBTTagCompound").newInstance();
        }
        catch (Exception exc)
        {
            ChatUtils.log("Failed to create new NBT Tag Compound. Please check plugin is up to date.");
        }
        return null;
    }

    public static Object getNBTTagCompound(Object nmsStack)
    {
        try
        {
            return nmsStack.getClass().getMethod("getTag").invoke(nmsStack);
        }
        catch (Exception exc)
        {
            ChatUtils.log("Failed to get existing NBT Tag Compound. Please check plugin is up to date.");
        }
        return null;
    }

    public static ItemStack applyTo(ItemStack item, String tag)
    {

        Object stack = getNMSItemStack(item);
        Object tagCompound = getNBTTagCompound(stack);
        if (tagCompound == null)
        {
            tagCompound = getNewNBTTagCompound();
        }

        if (DynamicMaterial.BAT_SPAWN_EGG.isSameMaterial(item))
        {
            Object idNTC = getNewNBTTagCompound();
            try
            {
                idNTC.getClass().getMethod("setString", String.class, String.class)
                        .invoke(idNTC, "id", EntityType.valueOf(tag.toUpperCase()).getName());
                tagCompound.getClass().getMethod("set", String.class,
                        Class.forName("net.minecraft.server." + NMSUtils.getVersionRaw() + ".NBTBase"))
                        .invoke(tagCompound, "EntityTag", idNTC);
            }
            catch (Exception exc)
            {
                ChatUtils.log("Failed to get apply monster egg NBT Tag Compound. Please check plugin is up to date.");
            }
        }
        else
        {
            String[] args = tag.split(" ");
            String key = args[0];
            String value = args[1];

            try
            {
                if(Utils.isInt(value))
                {
                    tagCompound.getClass().getMethod("setInt", String.class, int.class)
                            .invoke(tagCompound, key, Integer.parseInt(value));
                }
                else if(Utils.isDouble(value))
                {
                    tagCompound.getClass().getMethod("setDouble", String.class, double.class)
                            .invoke(tagCompound, key, Double.valueOf(value));
                }
                else if(Utils.isBoolean(value))
                {
                    tagCompound.getClass().getMethod("set", String.class, boolean.class)
                            .invoke(tagCompound, key, Boolean.valueOf(value));
                }
                else
                {
                    tagCompound.getClass().getMethod("setString", String.class, String.class)
                            .invoke(tagCompound, key, value);
                }
            }
            catch (Exception exc)
            {
                ChatUtils.log("Failed to get apply '" + key + " " + value + "' tag. Please check plugin is up to date.");
            }
        }

        try
        {
            stack.getClass().getMethod("setTag", tagCompound.getClass()).invoke(stack, tagCompound);
            return (ItemStack) getCraftItemStack().getMethod("asBukkitCopy", stack.getClass())
                    .invoke(getCraftItemStack(), stack);
        }
        catch (Exception exc)
        {
            ChatUtils.log("Failed to get apply final Tag. Please check plugin is up to date.");
        }
        return null;
    }

    private static String[] excludedTags = new String[]{
            "display",
            "Enchantments",
            "SkullOwner",
            "HideFlags",
            "Potion"
    };

    public static List<String> getFrom(ItemStack item)
    {
        List<String> list = new ArrayList<>();

        Object stack = getNMSItemStack(item);
        Object tagCompound = getNBTTagCompound(stack);

        if (DynamicMaterial.BAT_SPAWN_EGG.isSameMaterial(item))
        {
            try
            {
                Object nbtTagCompound =
                        tagCompound.getClass().getMethod("getCompound", String.class).invoke(tagCompound, "EntityTag");
                list.add(nbtTagCompound.getClass().getMethod("getString", String.class).invoke(nbtTagCompound, "id")
                        .toString());
            }
            catch (Exception exc)
            {
                ChatUtils.log("Failed to load NBT Tag Compound BASE from stack. Please check plugin is up to date.");
            }
        }
        else
        {
            try
            {
                //Bukkit.broadcastMessage("VALS: " + tagCompound.getClass().getMethod(NMSUtils.Version.v1_12.isServerVersionOrLater() ? "getKeys" : "c"));
                Set<String> keys = (Set<String>) tagCompound.getClass().getMethod(NMSUtils.Version.v1_12.isServerVersionOrLater() ? "getKeys" : "c").invoke(tagCompound);

                for (String key : keys)
                {
                    boolean toSkip = false;
                    for (String excludedTag : excludedTags)
                    {
                        if (key.equalsIgnoreCase(excludedTag))
                        {
                            toSkip = true;
                            break;
                        }
                    }

                    if (!toSkip)
                    {

                        Object nbtBase = tagCompound.getClass().getMethod("get", String.class).invoke(tagCompound, key);

                        list.add(key + " " + nbtBase);
                    }
                }
            }
            catch(Exception exc)
            {
                exc.printStackTrace();
                ChatUtils.log("Failed to load NBT Tag Compound BASE from stack. Please check plugin is up to date.");
            }
        }
        return list;
    }
}
