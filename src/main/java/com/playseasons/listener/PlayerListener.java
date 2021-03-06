package com.playseasons.listener;

import com.demigodsrpg.chitchat.Chitchat;
import com.playseasons.impl.PlaySeasons;
import com.playseasons.model.PlayerModel;
import com.playseasons.util.RegionUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

public class PlayerListener implements Listener {

    final PlaySeasons plugin;

    public PlayerListener(PlaySeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Chitchat.sendTitle(player, 10, 60, 10, ChatColor.YELLOW + "You're at " + ChatColor.GREEN + ChatColor.BOLD +
                "Seasons" + ChatColor.YELLOW + "!", ChatColor.DARK_GRAY + "Current: " + ChatColor.GRAY +
                "Season 1 - Genesis");
        if (plugin.getPlayerRegistry().isVisitor(player)) {
            Optional<PlayerModel> maybeThem = plugin.getPlayerRegistry().fromName(player.getName());
            if (maybeThem.isPresent()) {
                plugin.getPlayerRegistry().remove(maybeThem.get().getKey());
                plugin.getPlayerRegistry().invite(player, maybeThem.get().getInvitedFrom());
                player.teleport(RegionUtil.spawnLocation());
                Chitchat.sendTitle(player, 10, 80, 10, ChatColor.YELLOW + "Celebrate!", ChatColor.GREEN +
                        "You were invited! Have fun!");
                return;
            }
            if (player.hasPermission("seasons.admin")) {
                plugin.getPlayerRegistry().inviteSelf(player);
                player.kickPlayer(ChatColor.GREEN + "Sorry, you weren't invited yet. Please rejoin.");
                return;
            }
            if (!RegionUtil.visitingContains(player.getLocation())) {
                try {
                    player.teleport(RegionUtil.visitingLocation());
                } catch (NullPointerException oops) {
                    oops.printStackTrace();
                }
            }
            player.sendMessage(ChatColor.YELLOW + "Currently you are just a " + ChatColor.GRAY + ChatColor.ITALIC +
                    "visitor" + ChatColor.YELLOW + ", ask for an invite!");
        } else {
            if (RegionUtil.spawnContains(player.getLocation()) || RegionUtil.visitingContains(player.getLocation())) {
                try {
                    player.teleport(RegionUtil.spawnLocation());
                } catch (NullPointerException oops) {
                    oops.printStackTrace();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.getPlayerRegistry().isVisitorOrExpelled(player)) {
            if (!RegionUtil.visitingContains(event.getTo())) {
                Chitchat.sendTitle(player, 10, 60, 10, ChatColor.GREEN + "Sorry!", ChatColor.RED +
                        "Only invited members are allowed there.");
                try {
                    player.teleport(RegionUtil.visitingLocation());
                } catch (NullPointerException oops) {
                    oops.printStackTrace();
                }
            }
        }
    }
}