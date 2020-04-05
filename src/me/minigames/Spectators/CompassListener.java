package me.minigames.Spectators;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CompassListener implements Listener {
	
	@EventHandler
	public void clickPunishGUI(InventoryClickEvent event) {
		if (!event.getInventory().getName().equals("Spectator Menu ")) {
			return;
		}
	    event.setCancelled(true);
	}
}
