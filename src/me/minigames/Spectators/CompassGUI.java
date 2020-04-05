package me.minigames.Spectators;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.minigames.Framework;
import me.minigames.Game.MiniGame;
import me.minigames.Utilities.Utils;

public class CompassGUI {
	
	public void getCompass(Player p, int amount, MiniGame game) {
		final Inventory inv = Bukkit.createInventory(null, amount, "Spectator Menu");
		
		Bukkit.getScheduler().runTaskTimer(Framework.getInstance(), () -> {
			int slot = 0;
			for (UUID uuid : game.getUsers()) {
				Player g = Bukkit.getPlayer(uuid);
			
				ItemStack title = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
				SkullMeta titleMeta = (SkullMeta)title.getItemMeta();
				titleMeta.setOwner(g.getName());
				titleMeta.setDisplayName(Utils.color("&a&l" + g.getName()));
				titleMeta.setLore(Arrays.asList(Utils.color("&eClick to Teleport!")));
				title.setItemMeta(titleMeta);
		    
				inv.setItem(slot, title);
				slot++; 
			}
		}, 0L, 5L);
		p.openInventory(inv);
	}

}
