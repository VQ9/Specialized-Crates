package me.ztowne13.customcrates.crates.types.animations;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.CrateHead;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class KeyCrate extends CrateHead {

	public KeyCrate(Crate crate)
	{
		super(crate);
	}

	@Override
	public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
	{
		if(canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
		{
			Reward r = getCrates().getCs().getCr().getRandomReward(p);
			r.runCommands(p);

			ArrayList<Reward> rewards = new ArrayList<Reward>();
			rewards.add(r);

			getCrates().tick(l, cs, p, rewards);
			takeKeyFromPlayer(p, !requireKeyInHand);
			return true;
		}
		
		playFailToOpen(p);
		return false;
	}

	@Override
	public void loadValueFromConfig(StatusLogger sl)
	{

	}

	@Override
	public void finishUp(Player p)
	{

	}

}
