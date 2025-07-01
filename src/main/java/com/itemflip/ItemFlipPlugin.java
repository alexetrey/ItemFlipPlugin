package com.itemflip;

import com.itemflip.commands.ItemFlipCommand;
import com.itemflip.managers.FlipManager;
import com.itemflip.managers.GUIManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemFlipPlugin extends JavaPlugin {
    private static ItemFlipPlugin instance;
    private FlipManager flipManager;
    private GUIManager guiManager;

    @Override
    public void onEnable() {
        instance = this;
        
        this.flipManager = new FlipManager(this);
        this.guiManager = new GUIManager(this);
        
        getCommand("itemflip").setExecutor(new ItemFlipCommand(this));
        
        getServer().getPluginManager().registerEvents(guiManager, this);
        getServer().getPluginManager().registerEvents(flipManager, this);
        
        saveDefaultConfig();
        
        getLogger().info("ItemFlipPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        if (flipManager != null) {
            flipManager.cancelAllGames();
        }
        
        getLogger().info("ItemFlipPlugin has been disabled!");
    }

    public static ItemFlipPlugin getInstance() {
        return instance;
    }

    public FlipManager getFlipManager() {
        return flipManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }
} 