package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.crates.PlacedCrate;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockRemoveListener implements Listener
{
	CustomCrates cc;
	
	public BlockRemoveListener(CustomCrates cc)
	{
		this.cc = cc;
	}
	
	@EventHandler
	public void onBlockChange(BlockExplodeEvent e)
	{

		if((Boolean) SettingsValues.EXPLODE_DYNAMIC.getValue(cc))
		{
			for(Block b: new ArrayList<Block>(e.blockList()))
			{
				if(PlacedCrate.crateExistsAt(cc, b.getLocation()))
				{
					e.blockList().remove(b);
				}
			}
		}
	}
	
	@EventHandler
	public void onPistonPush(BlockPistonExtendEvent e)
	{
		e.setCancelled(shouldCancel(e.getBlocks(), e.getDirection()));
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e)
	{
		if((Boolean) SettingsValues.EXPLODE_DYNAMIC.getValue(cc))
		{
			for(Block b: new ArrayList<Block>(e.blockList()))
			{
				if(PlacedCrate.crateExistsAt(cc, b.getLocation()))
				{
					e.blockList().remove(b);
				}
			}
		}
	}
	
	public boolean shouldCancel(List<Block> blocks, BlockFace bf)
	{
		boolean shouldCancel = false;
		if((Boolean) SettingsValues.EXPLODE_DYNAMIC.getValue(cc))
		{
			for(Block b: blocks)
			{
				if(PlacedCrate.crateExistsAt(cc, b.getLocation()) || PlacedCrate.crateExistsAt(cc, b.getRelative(bf).getLocation()))
				{
					shouldCancel = true;
					break;
				}
			}
		}
		return shouldCancel;
	}
}
