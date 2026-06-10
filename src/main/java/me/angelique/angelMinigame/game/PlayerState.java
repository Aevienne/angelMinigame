package me.angelique.angelMinigame.game;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.UUID;

public class PlayerState {

    private final UUID uuid;
    private final Location originalLocation;
    private final ItemStack[] savedInventory;
    private final ItemStack[] savedArmor;
    private final double savedHealth;
    private final int savedFood;
    private final float savedSaturation;
    private final Collection<PotionEffect> savedEffects;
    private final GameMode savedGameMode;
    private final int savedXpLevel;
    private final float savedXpProgress;

    public PlayerState(UUID uuid, Location originalLocation, ItemStack[] savedInventory,
                       ItemStack[] savedArmor, double savedHealth, int savedFood,
                       float savedSaturation, Collection<PotionEffect> savedEffects,
                       GameMode savedGameMode, int savedXpLevel, float savedXpProgress) {
        this.uuid = uuid;
        this.originalLocation = originalLocation;
        this.savedInventory = savedInventory;
        this.savedArmor = savedArmor;
        this.savedHealth = savedHealth;
        this.savedFood = savedFood;
        this.savedSaturation = savedSaturation;
        this.savedEffects = savedEffects;
        this.savedGameMode = savedGameMode;
        this.savedXpLevel = savedXpLevel;
        this.savedXpProgress = savedXpProgress;
    }

    public UUID getUuid() { return uuid; }
    public Location getOriginalLocation() { return originalLocation; }
    public ItemStack[] getSavedInventory() { return savedInventory; }
    public ItemStack[] getSavedArmor() { return savedArmor; }
    public double getSavedHealth() { return savedHealth; }
    public int getSavedFood() { return savedFood; }
    public float getSavedSaturation() { return savedSaturation; }
    public Collection<PotionEffect> getSavedEffects() { return savedEffects; }
    public GameMode getSavedGameMode() { return savedGameMode; }
    public int getSavedXpLevel() { return savedXpLevel; }
    public float getSavedXpProgress() { return savedXpProgress; }
}
