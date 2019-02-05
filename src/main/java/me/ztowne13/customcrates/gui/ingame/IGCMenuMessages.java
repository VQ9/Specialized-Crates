package me.ztowne13.customcrates.gui.ingame;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by ztowne13 on 3/15/16.
 */
public class IGCMenuMessages extends IGCMenu
{
	static int msgLoreLength = 40;
	public IGCMenuMessages(CustomCrates cc, Player p, IGCMenu lastMenu)
	{
		super(cc, p, lastMenu, "&7&l> &6&lMessages.YML");
	}

	@Override
	public void open()
	{
		p.closeInventory();
		putInMenu();

		InventoryBuilder ib = createDefault(InventoryUtils.getRowsFor(2, Messages.values().length-1));
		ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
		ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());
		ib.setItem(ib.getInv().getSize()-9, IGCDefaultItems.EXIT_BUTTON.getIb());

		ArrayList<Messages> msgs = new ArrayList<>();

		for(Messages msg: Messages.values())
		{
			if(msg.getMsg().equalsIgnoreCase(""))
			{
				msgs.add(msg);
			}
		}

		int i = 2;
		for(Messages msg : msgs)
		{
			if (i % 9 == 0)
			{
				i += 2;
			}

			String properMsg = msg.getPropperMsg(cc);
			ib.setItem(i, new ItemBuilder(Material.BOOK, 1, 0).setName("&a" + msg.toString().toLowerCase()).addLore(properMsg.substring(0, properMsg.length() > msgLoreLength ? msgLoreLength : properMsg.length()) + (properMsg.length() > msgLoreLength ? "..." : "")));
			i++;
		}

		ib.open();
	}

	@Override
	public void manageClick(int slot)
	{
		if(slot == 0)
		{
			cc.getMessageFile().save();
			ChatUtils.msgSuccess(p, "Messages.YML saved!");
		}
		else if(slot == 9)
		{
			reload();
		}
		else if(slot == ib.getInv().getSize()-9)
		{
			up();
		}
		else if(!(ib.getInv().getItem(slot) == null))
		{
			Messages msg = Messages.valueOf(ChatUtils.removeColor(ib.getInv().getItem(slot).getItemMeta().getDisplayName()).toUpperCase());
			new InputMenu(cc, p, msg.name(), msg.getPropperMsg(cc), String.class, this);
		}
	}

	@Override
	public boolean handleInput(String value, String input)
	{
		Messages msg = Messages.valueOf(value.toUpperCase());
		msg.writeValue(cc, input);
		ChatUtils.msgSuccess(p, "Set " + value + " to '" + input + "'");
		return true;
	}
}
