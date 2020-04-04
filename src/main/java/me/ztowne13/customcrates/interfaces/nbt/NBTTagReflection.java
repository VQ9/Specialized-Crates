package me.ztowne13.customcrates.interfaces.nbt;

import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
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
            return Class.forName("org.bukkit.craftbukkit." + VersionUtils.getVersionRaw() + ".inventory.CraftItemStack");
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
            return Class.forName("net.minecraft.server." + VersionUtils.getVersionRaw() + ".NBTTagCompound").newInstance();
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

        String[] args = tag.split(" ");
        String key = null, value = null;

        try
        {
            key = args[0];
            value = args[1].replaceAll(",Properties:\\{textures:\\[0:\\{Value:", ",Properties:{textures:[{Value:");
        }
        catch (Exception exc)
        {
            ChatUtils.log("Tag " + tag +
                    " is not formatted 'TagType Tag' (without the quotes). Try using the in-game config, it does NBT tags automatically!");
        }

        try
        {
            if (value.startsWith("{") && value.endsWith("}"))
            {
                Class clazz = VersionUtils.getNmsClass("MojangsonParser");
                Object newComp = clazz.getMethod("parse", String.class).invoke(clazz, value);

                tagCompound.getClass().getMethod("set", String.class, VersionUtils.getNmsClass("NBTBase"))
                        .invoke(tagCompound, key, newComp);
            }
            else if ((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\"")))
            {
                value = ChatUtils.stripQuotes(value);

                if (value.equalsIgnoreCase("1b"))
                {
                    tagCompound.getClass().getMethod("setInt", String.class, int.class)
                            .invoke(tagCompound, key, 1);
                }
                else
                {
                    tagCompound.getClass().getMethod("setString", String.class, String.class)
                            .invoke(tagCompound, key, value);
                }
            }
            else if (Utils.isInt(value))
            {
                tagCompound.getClass().getMethod("setInt", String.class, int.class)
                        .invoke(tagCompound, key, Integer.parseInt(value));
            }
            else if (Utils.isDouble(value))
            {
                tagCompound.getClass().getMethod("setDouble", String.class, double.class)
                        .invoke(tagCompound, key, Double.valueOf(value));
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
            exc.printStackTrace();
        }

        try
        {
            stack.getClass().getMethod("setTag", tagCompound.getClass()).invoke(stack, tagCompound);
            ItemStack toReturn = (ItemStack) getCraftItemStack().getMethod("asBukkitCopy", stack.getClass())
                    .invoke(getCraftItemStack(), stack);

            return toReturn;
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
            "HideFlags",
            "Potion"
    };

    private static String[] booleanTags = new String[]{
            "Unbreakable"
    };

    public static List<String> getFrom(ItemStack item)
    {
        List<String> list = new ArrayList<>();

        Object stack = getNMSItemStack(item);
        Object tagCompound = getNBTTagCompound(stack);

        try
        {
            Set<String> keys = (Set<String>) tagCompound.getClass().getMethod(
                    VersionUtils.Version.v1_13.isServerVersionOrLater() ? "getKeys" : "c").invoke(tagCompound);

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
                    Object nbtBase;
                    if (Arrays.asList(booleanTags).contains(key))
                    {
                        nbtBase = tagCompound.getClass().getMethod("getBoolean", String.class).invoke(tagCompound, key);
                        if ((boolean) nbtBase)
                            list.add(key + " " + 1);
                        else
                            list.add(key + " " + 0);
                    }
                    else
                    {
                        nbtBase = tagCompound.getClass().getMethod("get", String.class).invoke(tagCompound, key);
                        list.add(key + " " + nbtBase);
                    }

                }
            }
        }
        catch (Exception exc)
        {
            //exc.printStackTrace();
        }
        return list;
    }
}
