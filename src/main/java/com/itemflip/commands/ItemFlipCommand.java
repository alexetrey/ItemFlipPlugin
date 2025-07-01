package com.itemflip.commands;

import com.itemflip.ItemFlipPlugin;
import com.itemflip.models.FlipGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemFlipCommand implements CommandExecutor, TabCompleter {
    private final ItemFlipPlugin plugin;

    public ItemFlipCommand(ItemFlipPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        if (!player.hasPermission("itemflip.use")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (plugin.getFlipManager().isInGame(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You are already in a game!");
                    return true;
                }
                plugin.getGuiManager().openStakeGUI(player);
            }
            case "join" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /itemflip join <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }
                if (!plugin.getFlipManager().joinGame(player, target.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Could not join the game!");
                }
            }
            case "list" -> {
                Map<UUID, FlipGame> games = plugin.getFlipManager().getPendingGames();
                if (games.isEmpty()) {
                    player.sendMessage(ChatColor.YELLOW + "There are no active games!");
                    return true;
                }
                player.sendMessage(ChatColor.GREEN + "Active games:");
                for (FlipGame game : games.values()) {
                    player.sendMessage(ChatColor.YELLOW + "- " + game.getCreator().getName() + 
                        " is flipping " + game.getStake().getAmount() + "x " + 
                        game.getStake().getType().toString().toLowerCase().replace("_", " "));
                }
            }
            case "cancel" -> {
                if (!plugin.getFlipManager().isInGame(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You don't have an active game!");
                    return true;
                }
                plugin.getFlipManager().cancelGame(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Game cancelled!");
            }
            case "help" -> sendHelpMessage(player);
            default -> {
                player.sendMessage(ChatColor.RED + "Unknown command! Use /itemflip help for commands.");
                return true;
            }
        }
        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GREEN + "=== Item Flip Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/itemflip create " + ChatColor.WHITE + "- Create a new flip game");
        player.sendMessage(ChatColor.YELLOW + "/itemflip join <player> " + ChatColor.WHITE + "- Join a player's game");
        player.sendMessage(ChatColor.YELLOW + "/itemflip list " + ChatColor.WHITE + "- List active games");
        player.sendMessage(ChatColor.YELLOW + "/itemflip cancel " + ChatColor.WHITE + "- Cancel your active game");
        player.sendMessage(ChatColor.YELLOW + "/itemflip help " + ChatColor.WHITE + "- Show this help message");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> commands = List.of("create", "join", "list", "cancel", "help");
            String input = args[0].toLowerCase();
            for (String cmd : commands) {
                if (cmd.startsWith(input)) {
                    completions.add(cmd);
                }
            }
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            String input = args[1].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input)) {
                    completions.add(player.getName());
                }
            }
        }
        
        return completions;
    }
} 