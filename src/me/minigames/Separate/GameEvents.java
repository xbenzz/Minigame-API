package me.minigames.Separate;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import me.minigames.Framework;
import me.minigames.Events.GameStartEvent;
import me.minigames.Game.MiniGame;
import me.minigames.Spectators.CompassGUI;
import me.minigames.Types.GameState;
import me.minigames.Utilities.Utils;

public class GameEvents implements Listener {
	
	private MiniGame game;
	
	public GameEvents(MiniGame game) {
		this.game = game;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        
        Location loc = Utils.getLocation(Framework.getInstance().getConfig().getString("worlds." + game.getMap().getId() + ".lobby"));
        player.teleport(loc);
        
		DisplayBoard.setWaitingBoard(player, game);
        
        if (game.getState() == GameState.LOBBY) {
        	game.addUser(player.getUniqueId());
            e.setJoinMessage(Utils.color("&e" + player.getName() + " &ahas joined the game! &2" + game.getUsers().size() + "/" + game.getMaxPlayers()));
            if (game.getUsers().size() >= game.getMinPlayers()) {
                game.begin();
                
                for (Player p : Bukkit.getOnlinePlayers()) {
                	Utils.setBar(p, Utils.color("&f&lGame Starting in &e&l" + game.getTimer().getTime()));
                }
            }
        } else if (game.getState() == GameState.STARTING) {
        	if (game.getUsers().size() == game.getMaxPlayers()) {
            	game.addSpectator(player.getUniqueId());
        	} else {
        		game.addUser(player.getUniqueId());
        		e.setJoinMessage(Utils.color("&e" + player.getName() + " &ahas joined the game! &2" + game.getUsers().size() + "/" + game.getMaxPlayers()));
        	}
        } else if (game.getState() == GameState.INGAME) {
        	game.addSpectator(player.getUniqueId());
        	for (UUID id : game.getSpectators()) {
        		Player spec = Bukkit.getPlayer(id);
        		player.hidePlayer(spec);
        	}
        	e.setJoinMessage(null);
        } else if (game.getState() == GameState.RESTARTING) {
        	player.kickPlayer("Server Restarting!");
        }
	}
	

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (game.getUsers().contains(player.getUniqueId())) {
        	game.removeUser(player.getUniqueId());
        } else if (game.getSpectators().contains(player.getUniqueId())) {
        	game.removeSpec(player.getUniqueId());
        }
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        event.setQuitMessage(null);
   }
    
    @EventHandler
    public void onStart(GameStartEvent event) {
        for (Player p : Bukkit.getOnlinePlayers()) {
        	DisplayBoard.setInGameBoard(p, game);
        	Utils.removeBar(p);
        }
   }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
        	return;
        }
        
        if (game.getState() != GameState.INGAME) {
            event.setCancelled(true);
        } else if (game.getSpectators().contains(event.getDamager().getUniqueId())) {
            event.setCancelled(true);
        } else if (((Damageable) event.getEntity()).getHealth() <= 1.5D || event.getDamage() >= ((Damageable) event.getEntity()).getHealth()) {
            event.setCancelled(true);
            game.die((Player) event.getEntity());
            if (event.getDamager() instanceof Player) {
            	Player killer = (Player) event.getDamager();
            	Player deather = (Player) event.getEntity();
                Bukkit.broadcastMessage(Utils.color("&2" + deather.getName() + " &7was slaughtered to death by &e" + killer.getName()));
            } else if (event.getDamager() instanceof Arrow) {
            	Arrow a = (Arrow)event.getDamager();
            	if (a.getShooter()instanceof Player) {
            		Player killer = (Player)a.getShooter();
                	Player deather = (Player) event.getEntity();
                    Bukkit.broadcastMessage(Utils.color("&2" + deather.getName() + " &7was shot to death by &e" + killer.getName()));
            	}
            } else if (event.getDamager() instanceof Snowball) {
            	Snowball a = (Snowball)event.getDamager();
            	if (a.getShooter()instanceof Player) {
            		Player killer = (Player)a.getShooter();
                	Player deather = (Player) event.getEntity();
                    Bukkit.broadcastMessage(Utils.color("&2" + deather.getName() + " &7got knocked off by &e" + killer.getName()));
            	}
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
        	return;
        }
        if (event instanceof EntityDamageByEntityEvent) {
            return;
        }
        if (game.getState() != GameState.INGAME) {
            event.setCancelled(true);
        } else if (game.getSpectators().contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        } else if (((Damageable) event.getEntity()).getHealth() <= 1.5D || event.getDamage() >= ((Damageable) event.getEntity()).getHealth()) {
            event.setCancelled(true);
            game.die((Player) event.getEntity());
        	Player deather = (Player) event.getEntity();
            Bukkit.broadcastMessage(Utils.color("&2" + deather.getName() + " &7has died!"));
        }
    }
    

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	Player p = event.getPlayer();
    	CompassGUI g = new CompassGUI();
    	if (game.getSpectators().contains(p.getUniqueId())) {
    	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    	        if (event.getClickedBlock().getType() == Material.CHEST)  {
        			event.setCancelled(true);
    	        }
    	    }
    		if (event.getItem().getType() == Material.COMPASS) {
    			g.getCompass(p, 18, game);
    			event.setCancelled(true);
    		}
    	}
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (game.getState() != GameState.INGAME) {
            event.setCancelled(true);
        } else if (game.getSpectators().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        } else {
        	event.setCancelled(false);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (game.getState() != GameState.INGAME) {
            event.setCancelled(true);
        } else if (game.getSpectators().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        } else {
        	event.setCancelled(false);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (game.getState() != GameState.INGAME) {
            event.setCancelled(true);
        } else if (game.getSpectators().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        } else {
        	event.setCancelled(false);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
        	return;
        }
        if (game.getState() != GameState.INGAME) {
            event.setCancelled(true);
        } else if (game.getSpectators().contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        } else {
        	event.setCancelled(false);
        }
    }

}
