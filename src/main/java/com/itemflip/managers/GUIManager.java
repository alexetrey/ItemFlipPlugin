package com.itemflip.managers;

import com.itemflip.ItemFlipPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager implements Listener {
    private final ItemFlipPlugin plugin;
    private final Map<UUID, Inventory> activeGUIs;
    private static final String GUI_TITLE = ChatColor.DARK_PURPLE + "Item Flip - Select Your Stake";
    private static final int STAKE_SLOT = 13;
    private static final int CONFIRM_SLOT = 22;

    public GUIManager(ItemFlipPlugin plugin) {
        this.plugin = plugin;
        this.activeGUIs = new HashMap<>();
    }

    public void openStakeGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);
        
        ItemStack confirmButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.RED + "Place an item to stake!");
        confirmMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Place your item in the middle slot",
            ChatColor.GRAY + "to create a flip game!"
        ));
        confirmButton.setItemMeta(confirmMeta);
        
        for (int i = 0; i < gui.getSize(); i++) {
            if (i != STAKE_SLOT) {
                ItemStack placeholder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta meta = placeholder.getItemMeta();
                meta.setDisplayName(" ");
                placeholder.setItemMeta(meta);
                gui.setItem(i, placeholder);
            }
        }
        
        gui.setItem(CONFIRM_SLOT, confirmButton);
        
        activeGUIs.put(player.getUniqueId(), gui);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        event.setCancelled(true);

        if (event.getRawSlot() == STAKE_SLOT) {
            event.setCancelled(false);
            
            Bukkit.getScheduler().runTask(plugin, () -> updateConfirmButton(event.getInventory()));
        }
        else if (event.getRawSlot() == CONFIRM_SLOT) {
            ItemStack stake = event.getInventory().getItem(STAKE_SLOT);
            if (stake != null && !stake.getType().equals(Material.AIR)) {
                plugin.getFlipManager().createGame(player, stake);
                player.closeInventory();
            }
        }
    }

    private void updateConfirmButton(Inventory inventory) {
        ItemStack stake = inventory.getItem(STAKE_SLOT);
        ItemStack confirmButton = new ItemStack(
            stake != null && !stake.getType().equals(Material.AIR) 
                ? Material.GREEN_STAINED_GLASS_PANE 
                : Material.RED_STAINED_GLASS_PANE
        );
        
        ItemMeta meta = confirmButton.getItemMeta();
        if (stake != null && !stake.getType().equals(Material.AIR)) {
            meta.setDisplayName(ChatColor.GREEN + "Click to Create Game!");
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Click to start a flip game",
                ChatColor.GRAY + "with your staked item!"
            ));
        } else {
            meta.setDisplayName(ChatColor.RED + "Place an item to stake!");
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Place your item in the middle slot",
                ChatColor.GRAY + "to create a flip game!"
            ));
        }
        confirmButton.setItemMeta(meta);
        inventory.setItem(CONFIRM_SLOT, confirmButton);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        ItemStack stake = event.getInventory().getItem(STAKE_SLOT);
        if (stake != null && !stake.getType().equals(Material.AIR)) {
            player.getInventory().addItem(stake);
        }
        
        activeGUIs.remove(player.getUniqueId());
    }
} 