package me.angelique.angelMinigame;

import me.angelique.angelMinigame.arena.ArenaManager;
import me.angelique.angelMinigame.commands.ArenaCommand;
import me.angelique.angelMinigame.commands.GameCommand;
import me.angelique.angelMinigame.game.ArenaSnapshot;
import me.angelique.angelMinigame.game.GameManager;
import me.angelique.angelMinigame.game.script.ActionRegistry;
import me.angelique.angelMinigame.game.script.actions.*;
import me.angelique.angelMinigame.gui.ArenaListGui;
import me.angelique.angelMinigame.integration.AngelCoreBridge;
import me.angelique.angelMinigame.listener.ArenaSetupListener;
import me.angelique.angelMinigame.listener.GameEventListener;
import me.angelique.angelMinigame.listener.GameProtectionListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class AngelMinigame extends JavaPlugin {

    private static AngelMinigame instance;
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private ActionRegistry actionRegistry;
    private AngelCoreBridge coreBridge;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        arenaManager = new ArenaManager(this);
        arenaManager.load();

        actionRegistry = new ActionRegistry();
        registerActions();

        gameManager = new GameManager(this, arenaManager, actionRegistry);
        ArenaSnapshot.init(this);

        coreBridge = new AngelCoreBridge(this);

        registerCommands();
        registerListeners();

        getLogger().info("AngelMinigame enabled. " + arenaManager.getArenas().size() + " arenas loaded.");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) gameManager.shutdown();
        if (arenaManager != null) arenaManager.save();
        getLogger().info("AngelMinigame disabled.");
    }

    private void registerActions() {
        actionRegistry.register("fill", new FillAction());
        actionRegistry.register("replace", new ReplaceAction());
        actionRegistry.register("remove_blocks", new RemoveAction());
        actionRegistry.register("set_block", new SetBlockAction());
        actionRegistry.register("block_decay", new BlockDecayAction());
        actionRegistry.register("title", new TitleAction());
        actionRegistry.register("actionbar", new ActionBarAction());
        actionRegistry.register("message", new MessageAction());
        actionRegistry.register("broadcast", new BroadcastAction());
        actionRegistry.register("sound", new SoundAction());
        actionRegistry.register("stop_sound", new StopSoundAction());
        actionRegistry.register("effect", new EffectAction());
        actionRegistry.register("clear_effects", new ClearEffectsAction());
        actionRegistry.register("heal", new HealAction());
        actionRegistry.register("damage", new DamageAction());
        actionRegistry.register("kill", new KillAction());
        actionRegistry.register("launch", new LaunchAction());
        actionRegistry.register("teleport", new TeleportAction());
        actionRegistry.register("give_item", new GiveItemAction());
        actionRegistry.register("clear_inv", new ClearInventoryAction());
        actionRegistry.register("gamemode", new GamemodeAction());
        actionRegistry.register("flight", new FlightAction());
        actionRegistry.register("flight_off", new FlightOffAction());
        actionRegistry.register("freeze", new FreezeAction());
        actionRegistry.register("unfreeze", new UnfreezeAction());
        actionRegistry.register("glow", new GlowAction());
        actionRegistry.register("glow_off", new GlowOffAction());
        actionRegistry.register("invisible", new InvisibleAction());
        actionRegistry.register("invisible_off", new InvisibleOffAction());
        actionRegistry.register("health", new HealthAction());
        actionRegistry.register("hunger", new HungerAction());
        actionRegistry.register("xp", new XpAction());
        actionRegistry.register("set_time", new SetTimeAction());
        actionRegistry.register("set_weather", new SetWeatherAction());
        actionRegistry.register("border_set", new BorderSetAction());
        actionRegistry.register("border_shrink", new BorderShrinkAction());
        actionRegistry.register("spawn_entity", new SpawnEntityAction());
        actionRegistry.register("entity_clear", new EntityClearAction());
        actionRegistry.register("lightning", new LightningAction());
        actionRegistry.register("explosion", new ExplosionAction());
        actionRegistry.register("particle", new ParticleAction());
        actionRegistry.register("firework", new FireworkAction());
        actionRegistry.register("cage", new CageAction());
        actionRegistry.register("uncage", new UncageAction());
        actionRegistry.register("command", new CommandAction());
    }

    private void registerCommands() {
        ArenaCommand arenaCmd = new ArenaCommand(this);
        getCommand("arena").setExecutor(arenaCmd);
        getCommand("arena").setTabCompleter(arenaCmd);

        GameCommand gameCmd = new GameCommand(this);
        getCommand("game").setExecutor(gameCmd);
        getCommand("game").setTabCompleter(gameCmd);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ArenaSetupListener(this), this);
        getServer().getPluginManager().registerEvents(new GameProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new GameEventListener(this), this);
        getServer().getPluginManager().registerEvents(new ArenaListGui(this), this);
    }

    public static AngelMinigame getInstance() { return instance; }
    public ArenaManager getArenaManager() { return arenaManager; }
    public GameManager getGameManager() { return gameManager; }
    public ActionRegistry getActionRegistry() { return actionRegistry; }
    public AngelCoreBridge getCoreBridge() { return coreBridge; }

    public static String clr(String s) {
        return s == null ? "" : org.bukkit.ChatColor.translateAlternateColorCodes('&', s);
    }
}
