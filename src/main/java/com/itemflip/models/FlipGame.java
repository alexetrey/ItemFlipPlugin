package com.itemflip.models;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FlipGame {
    private final Player creator;
    private Player joiner;
    private final ItemStack stake;
    private final long creationTime;

    public FlipGame(Player creator, ItemStack stake) {
        this.creator = creator;
        this.stake = stake.clone();
        this.creationTime = System.currentTimeMillis();
    }

    public Player getCreator() {
        return creator;
    }

    public Player getJoiner() {
        return joiner;
    }

    public void setJoiner(Player joiner) {
        this.joiner = joiner;
    }

    public ItemStack getStake() {
        return stake.clone();
    }

    public long getCreationTime() {
        return creationTime;
    }
} 