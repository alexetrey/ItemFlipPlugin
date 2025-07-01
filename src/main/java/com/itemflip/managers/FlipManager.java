package com.itemflip.managers;

import com.itemflip.ItemFlipPlugin;
import com.itemflip.models.FlipGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class FlipManager implements Listener {
    private final ItemFlipPlugin plugin;
    private final Map<UUID, FlipGame> activeGames;
    private final Map<UUID, FlipGame> pendingGames;

    public FlipManager(ItemFlipPlugin plugin) {
        this.plugin = plugin;
        this.activeGames = new HashMap<>();
        this.pendingGames = new HashMap<>();
    }

    public boolean createGame(Player creator, ItemStack stake) {
        if (isInGame(creator.getUniqueId())) {
            creator.sendMessage(ChatColor.RED + "You are already in a game!");
            return false;
        }

        FlipGame game = new FlipGame(creator, stake);
        pendingGames.put(creator.getUniqueId(), game);
        
        creator.getInventory().removeItem(stake);
        
        creator.sendMessage(ChatColor.GREEN + "Game created! Players can join using /itemflip join " + creator.getName());
        return true;
    }

    public boolean joinGame(Player joiner, UUID creatorUUID) {
        if (isInGame(joiner.getUniqueId())) {
            joiner.sendMessage(ChatColor.RED + "You are already in a game!");
            return false;
        }

        FlipGame game = pendingGames.get(creatorUUID);
        if (game == null) {
            joiner.sendMessage(ChatColor.RED + "Game not found!");
            return false;
        }

        if (!joiner.getInventory().containsAtLeast(game.getStake(), game.getStake().getAmount())) {
            joiner.sendMessage(ChatColor.RED + "You don't have the required items to join this game!");
            return false;
        }

        joiner.getInventory().removeItem(game.getStake());
        
        game.setJoiner(joiner);
        pendingGames.remove(creatorUUID);
        activeGames.put(creatorUUID, game);
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> runFlip(game), 60L);
        
        return true;
    }

    private void runFlip(FlipGame game) {
        boolean creatorWins = ThreadLocalRandom.current().nextBoolean();
        Player winner = creatorWins ? game.getCreator() : game.getJoiner();
        Player loser = creatorWins ? game.getJoiner() : game.getCreator();

        ItemStack winnings = game.getStake().clone();
        winnings.setAmount(winnings.getAmount() * 2);
        winner.getInventory().addItem(winnings);

        String winMessage = ChatColor.GREEN + "🎉 " + winner.getName() + " won the item flip against " + loser.getName() + "!";
        winner.sendMessage(winMessage);
        loser.sendMessage(ChatColor.RED + "Better luck next time! " + winner.getName() + " won the flip!");

        activeGames.remove(game.getCreator().getUniqueId());
    }

    public void cancelGame(UUID playerUUID) {
        FlipGame game = pendingGames.remove(playerUUID);
        if (game != null) {
            Player creator = game.getCreator();
            creator.getInventory().addItem(game.getStake());
            creator.sendMessage(ChatColor.YELLOW + "Your game has been cancelled.");
        }
    }

    public void cancelAllGames() {
        for (FlipGame game : pendingGames.values()) {
            Player creator = game.getCreator();
            creator.getInventory().addItem(game.getStake());
            creator.sendMessage(ChatColor.YELLOW + "Your game has been cancelled due to server reload/stop.");
        }
        pendingGames.clear();
        activeGames.clear();
    }

    public boolean isInGame(UUID playerUUID) {
        return pendingGames.containsKey(playerUUID) || 
               activeGames.containsKey(playerUUID) ||
               activeGames.values().stream().anyMatch(game -> 
                   game.getJoiner() != null && game.getJoiner().getUniqueId().equals(playerUUID));
    }

    public Map<UUID, FlipGame> getPendingGames() {
        return Collections.unmodifiableMap(pendingGames);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        cancelGame(playerUUID);
    }
} 