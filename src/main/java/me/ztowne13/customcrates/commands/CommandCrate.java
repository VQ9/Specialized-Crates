package me.ztowne13.customcrates.commands;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.sub.*;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * All commands for the plugin
 */
public class CommandCrate extends Commands implements CommandExecutor {
    private final SpecializedCrates instance;
    private final VirtualCrates vcSubCommand;
    private final Claim claimSubCommand;
    private final ArrayList<SubCommand> subCommands;

    public CommandCrate(SpecializedCrates instance) {
        super("scrates");
        this.instance = instance;

        vcSubCommand = new VirtualCrates();
        claimSubCommand = new Claim();

        subCommands = new ArrayList<>(Arrays.asList(
                new Config(),
                new DelAllCrateType(),
                new DeleteCrate(),
                new GiveCrate(),
                new GiveKey(),
                new Help(),
                new Info(),
                new LastConfigMenu(),
                new ListCrates(),
                new ListHistory(),
                new Reload(),
                new Errors(),
                new Edit(),
                new Debug(),
                new ForceOpen(),
                new ToggleParticles(),
                new SpawnCrate(),
                claimSubCommand,
                vcSubCommand
        ));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        setCmdSender(sender);

        if (args.length > 0 && canExecute(true, true, "customcrates.admin", "specializedcrates.admin")) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.isCommand(args[0])) {
                    if (subCommand.checkProperUsage(sender, args)) {
                        return subCommand.run(instance, this, args);
                    }
                    return false;
                }
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("luckychest")) {
            if (canExecute(false, true, "customcrates.luckychestcommand", "specializedcrates.luckychestcommand")) {
                PlayerDataManager pdm = PlayerManager.get(instance, (Player) sender).getPlayerDataManager();
                pdm.setActivatedLuckyChests(!pdm.isActivatedLuckyChests());
                Messages.TOGGLE_LUCKYCRATE.msgSpecified(instance, (Player) sender, new String[]{"%state%"},
                        new String[]{pdm.isActivatedLuckyChests() + ""});
            } else {
                msg(Messages.NO_PERMISSIONS.getFromConf(instance)
                        .replace("%permission%", "specializedcrates.luckychestcommand"));
            }
            return true;
        } else if (args.length > 0 && args[0].equalsIgnoreCase("claim")) {
            if (canExecute(false, true, "customcrates.claim", "specializedcrates.claim")) {
                claimSubCommand.run(instance, this, args);
            } else {
                msg(Messages.NO_PERMISSIONS.getFromConf(instance)
                        .replace("%permission%", "specializedcrates.claim"));
            }
            return true;
        } else if (!canExecute(false, true, "customcrates.admin", "specializedcrates.admin")) {
            if (vcSubCommand.run(instance, this, args)) {
                return true;
            } else if (args.length == 0) {
                msg(Messages.NO_PERMISSIONS.getFromConf(instance)
                        .replace("%permission%", "specializedcrates.crates (for users) or specializedcrates.admin (for admins)"));
                return true;
            }

            msg(Messages.NO_PERMISSIONS.getFromConf(instance)
                    .replace("%permission%", "specializedcrates.admin"));
        } else {
            msgPage(1);
        }


        return false;
    }

    @Override
    public void msgPage(int page) {
        msg("");
        msg("");
        msg("");
        msg("&6&lSpecialized &7&lCrates");
        msg("");
        msg("&6&l> &econfig");
        msg("    &7Configure the plugin in game.");
        msg("&6&l> &egivekey [crate] [player] [amnt] {-v, for virtual keys}");
        msg("    &7Get the get for the specified crate.");
        msg("&6&l> &egivecrate [crate] [player] [amnt] {-v, for virtual crates}");
        msg("    &7Get the item for a specific crate.");
        msg("&6&l> &edeletecrate");
        msg("    &7Delete a crate from the world.");
        msg("&6&l> &ereload");
        msg("    &7Reload all data from the config.yml.");
        msg("&6&l> &elistHistory [player] [amnt of entries]");
        msg("    &7List the crate history for an online player.");
        msg("&6&l> &elistCrates");
        msg("    &7List all the valid crates.");
        msg("&6&l> &edelAllCrateType [crate]");
        msg("    &7Delete all existing crates of the type.");
        msg("&6&l> &einfo");
        msg("    &7Get all info for the plugin.");
        msg("&6&l> &eluckychest");
        msg("    &7Toggle whether or not you want luckychests to appear.");
        msg("&6&l> &eerrors");
        msg("    &7See all crate errors in game!");
        msg("&6&l> &eedit [crate]");
        msg("    &7Open up the in game config directly for a crate.");
        msg("&6&l> &e!");
        msg("    &7Open the last in-game config menu you were in.");
        msg("&6&l> &evirtualcrates");
        msg("    &7The menu displayed when a non-admins types /crates");
        msg("&6&l> &eforceopen [crate] {[player], all}");
        msg("    &7Force a player to open a crate.");
        msg("&6&l> &etoggleparticles");
        msg("    &7Temporarily disable all particle effects.");
        msg("&6&l> &espawncrate [crate] [world] [x] [y] [z]");
        msg("    &7Spawn a crate at a given location.");
        msg("");

    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
}
