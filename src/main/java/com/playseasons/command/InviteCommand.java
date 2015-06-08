package com.playseasons.command;

import com.censoredsoftware.library.command.type.BaseCommand;
import com.censoredsoftware.library.command.type.CommandResult;
import com.demigodsrpg.chitchat.Chitchat;
import com.playseasons.PlaySeasons;
import com.playseasons.util.RegionUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class InviteCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (command.getName().equals("invite")) {
            // Needs at least 1 argument
            if (args.length < 1) {
                return CommandResult.INVALID_SYNTAX;
            }

            // Get the invitee
            Player invitee = Bukkit.getPlayer(args[0]);
            if (invitee == null) {
                sender.sendMessage(ChatColor.RED + "Player either offline or does not exist, please try again later.");
                return CommandResult.QUIET_ERROR;
            }

            // Register from console
            if (sender instanceof ConsoleCommandSender) {
                PlaySeasons.getPlayerRegistry().inviteConsole(invitee);
            }

            // Stop untrusted from inviting
            else if (!PlaySeasons.getPlayerRegistry().isTrusted((Player) sender)) {
                sender.sendMessage(ChatColor.RED + "Sorry, you aren't (yet) a trusted player.");
                return CommandResult.QUIET_ERROR;
            }

            // Register from player
            else {
                PlaySeasons.getPlayerRegistry().invite(invitee, (Player) sender);
            }

            invitee.teleport(RegionUtil.spawnLocation());
            Chitchat.sendTitle(invitee, 10, 80, 10, ChatColor.YELLOW + "Celebrate!", ChatColor.GREEN + "You were invited! Have fun!");

            // If this is reached, the invite worked
            sender.sendMessage(ChatColor.RED + invitee.getName() + " has been invited.");

            return CommandResult.SUCCESS;
        }

        return CommandResult.ERROR;
    }
}