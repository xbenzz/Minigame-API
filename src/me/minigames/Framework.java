package me.minigames;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.minigames.Game.MiniGame;
import me.minigames.Separate.GameEvents;
import me.minigames.Types.GameType;

public class Framework extends JavaPlugin {
	
	public static Framework instance;
	
	public void onEnable() {
		instance = this;
	    loadConfig();
	    
	    registerCommands();
	    registerEvents();
	}
	
	public void onDisable() {
	}
	
	private void registerCommands() {
	    // getCommand("punish").setExecutor(new PunishCommand());
	}
	   
	private void registerEvents() {
		MiniGame game = new MiniGame(GameType.SOLO_SKYWARS, 2, 16);
	    PluginManager manager = Bukkit.getPluginManager();
	    manager.registerEvents(new GameEvents(game), this);
	}
	
	public static Framework getInstance() {
		return instance;
	}
	
	private void loadConfig() {
		FileConfiguration cfg = getConfig();
		cfg.options().copyDefaults(true);
		saveDefaultConfig();
	}

}
