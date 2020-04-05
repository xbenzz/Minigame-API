package me.minigames.Game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.google.common.base.Joiner;

import me.minigames.Framework;
import me.minigames.Events.GameStartEvent;
import me.minigames.Map.Area;
import me.minigames.Map.Map;
import me.minigames.Types.GameState;
import me.minigames.Types.GameType;
import me.minigames.Utilities.Timer;
import me.minigames.Utilities.Utils;

public class MiniGame {
	
	private GameType type;
	private int minPlayers;
	private int maxPlayers;
	
	private GameState status;
	private Map map;
	private Area area;
	Timer timer;
	
	private boolean team;
	
	private List<UUID> users = new ArrayList<UUID>();
	private List<UUID> spectators = new ArrayList<UUID>();
	
	public MiniGame(GameType type, int minPlayers, int maxPlayers) {
		this.type = type;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.status = GameState.LOBBY;
		
		setMap(getRandomMap());
        
		createWorld();
		
        Location loc = Utils.getLocation(Framework.getInstance().getConfig().getString("worlds." + getMap().getId() + ".lobby"));
        Bukkit.getWorld(getMap().getName()).loadChunk(loc.getChunk());
        
		Location loc1 = Utils.getLocation(Framework.getInstance().getConfig().getString("worlds." + getMap().getId() + ".corner1"));
		Location loc2 = Utils.getLocation(Framework.getInstance().getConfig().getString("worlds." + getMap().getId() + ".corner2"));
		setArea(new Area(loc1, loc2));
		
		getArea().fillChests();
		
		if (type == GameType.SOLO_SKYWARS) {
			team = false;
		} else {
			team = true;
		}
		
	}
	
	public void begin() {
		if (users.size() < minPlayers) {
			return;
		}
		setState(GameState.STARTING);
        setTimer(new Timer(60) {
            @Override
            public void execute() {
                if (users.size() < minPlayers) {
                    Bukkit.broadcastMessage(Utils.color("&cNot enough players to start."));
            		setState(GameState.LOBBY);
                    cancel();
                    return;
                }
                switch (getTime()) {
            		case 50:
            			Bukkit.broadcastMessage(Utils.color("&aGame starting in &6" + getTime() + " &aseconds."));
            			break;
                	case 30:
                        Bukkit.broadcastMessage(Utils.color("&aGame starting in &6" + getTime() + " &aseconds."));
                        break;
                    case 3:
                        Bukkit.broadcastMessage(Utils.color("&aGame starting in &c" + getTime() + " &aseconds.."));
                        break;
                    case 2:
                        Bukkit.broadcastMessage(Utils.color("&aGame starting in &c" + getTime() + " &aseconds.."));
                        break;
                    case 1:
                        Bukkit.broadcastMessage(Utils.color("&aGame starting in &c1 &asecond!"));
                        break;
                }
            }
        
            @Override
            public void end() {
            	setState(GameState.INGAME);
            	
            	if (isTeam()) {
            		
            	} else {
            		Iterator<String> location = Framework.getInstance().getConfig().getStringList("worlds." + map.getId() + ".spawns").iterator();
            		for (UUID uuid : users) {
            			Player player = Bukkit.getPlayer(uuid);
            			if (location.hasNext()) {
            				Location l = Utils.getLocation(location.next());
            				player.teleport(l);
            				Utils.sendTitle(Utils.color("&a&lBEGIN!"), Utils.color("&fLet the games begin"), player);
            			}
            		}
        		
            		String center = Framework.getInstance().getConfig().getString("worlds." + map.getId() + ".center");
            		for (UUID uuid : spectators) {
            			Player player = Bukkit.getPlayer(uuid);
            			player.teleport(Utils.getLocation(center));
            			Utils.sendTitle(Utils.color("&7&lSPECTATOR!"), Utils.color("&fYou are a spectator"), player);
            		}
        		
            		Bukkit.broadcastMessage(Utils.color(" "));
            		Bukkit.broadcastMessage(Utils.color(" "));
            		Bukkit.broadcastMessage(Utils.color("&c&l&m=--=--=--=--=--=--=--=--=--=--=--=--==--="));
            		Bukkit.broadcastMessage(Utils.color("  &e&lGame - &a&l" + type.getName()));
            		Bukkit.broadcastMessage(Utils.color(" "));
            		for (String s : type.getDesc()) {
            			Bukkit.broadcastMessage(Utils.color("  &f" + s));
            		}
            		Bukkit.broadcastMessage(Utils.color("  &e&lMap - &a&l" + map.getName()));
            		Bukkit.broadcastMessage(Utils.color("  &e&lBuilt By - &a&l" + Joiner.on(Utils.color("&f, &a&l")).join(map.getAuthors())));
            		Bukkit.broadcastMessage(Utils.color("&c&l&m=--=--=--=--=--=--=--=--=--=--=--=--==--="));
            		Bukkit.broadcastMessage(Utils.color(" "));
            		Bukkit.broadcastMessage(Utils.color("&c&lNOTE: Teaming is bannable in Solo Skywars!"));

            	}
            	MiniGame.this.counter(15);
            }
        });
        getTimer().runTaskTimer(Framework.getInstance(), 20L, 20L); 
	}
	
    public void counter(int minutes) {
    	GameStartEvent e = new GameStartEvent(this);
    	Bukkit.getPluginManager().callEvent(e);
    	
        setTimer(new Timer(60 * minutes) {

            @Override
            public void execute()  {
            	if (users.size() == 1) {
            		end();
            		cancel();
            	}
            }

            @Override
            public void end() {
                MiniGame.this.finishGame();
            }
        });
        getTimer().runTaskTimer(Framework.getInstance(), 20L, 20L);
    }
    
    public void finishGame() {
        setState(GameState.RESTARTING);

        	timer.cancel();
        	timer = null;

        if (users.size() == 1) {
            Player won = null;
            for (UUID player : users) {
                won = Bukkit.getPlayer(player);
            }
            
            for (Player p : Bukkit.getOnlinePlayers()) {
    			Utils.sendTitle(Utils.color("&c&lLOSER"), Utils.color("&e" + won.getName() + " &fhas won!"), p);
            }
            
        	Bukkit.broadcastMessage(Utils.color(" "));
        	Bukkit.broadcastMessage(Utils.color(" "));
            Bukkit.broadcastMessage(Utils.color("&c&l&m=--=--=--=--=--=--=--=--=--=--=--=--==--="));
            if (won != null) {
            	Bukkit.broadcastMessage(Utils.color("&a&l" + won.getName() + " &6&lhas won the game!"));
        		Utils.sendTitle(Utils.color("&a&lWINNER"), Utils.color("&eYou won the game!"), won);
                Bukkit.broadcastMessage(" ");
            }
            for (Player user : Bukkit.getOnlinePlayers()) {
                user.sendMessage(Utils.color("&330 Credits (Participation)"));
                user.sendMessage(Utils.color("&30 Guild Points (Participation)"));
            }
            Bukkit.broadcastMessage(Utils.color("&c&l&m=--=--=--=--=--=--=--=--=--=--=--=--==--="));      
            Bukkit.broadcastMessage(Utils.color(" "));
            
        } else {
        	Player won = Bukkit.getPlayer(users.stream().findFirst().get());
        	
            for (Player p : Bukkit.getOnlinePlayers()) {
    			Utils.sendTitle(Utils.color("&c&lLOSER"), Utils.color("&e" + won.getName() + " &fhas won!"), p);
            }
            
        	Bukkit.broadcastMessage(Utils.color(" "));
        	Bukkit.broadcastMessage(Utils.color(" "));
            Bukkit.broadcastMessage(Utils.color("&c&l&m=--=--=--=--=--=--=--=--=--=--=--=--==--="));
            Bukkit.broadcastMessage(Utils.color("&e&lTimes Up! - Most Kills Won"));
            if (won != null) {
            	Bukkit.broadcastMessage(Utils.color("&a&l" + won.getName() + " &6&lhas won the game!"));
        		Utils.sendTitle(Utils.color("&a&lWINNER"), Utils.color("&eYou won the game!"), won);
                Bukkit.broadcastMessage(" ");
            }
            for (Player user : Bukkit.getOnlinePlayers()) {
                user.sendMessage(Utils.color("&330 Credits (Participation)"));
                user.sendMessage(Utils.color("&30 Guild Points (Participation)"));
            }
            Bukkit.broadcastMessage(Utils.color("&c&l&m=--=--=--=--=--=--=--=--=--=--=--=--==--="));
        	Bukkit.broadcastMessage(Utils.color(" "));
        }
        
        spectators.clear();
        users.clear();
        
        Bukkit.getScheduler().runTaskLater(Framework.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.getInventory().clear();
                player.getInventory().setHelmet(new ItemStack(Material.AIR));
                player.getInventory().setChestplate(new ItemStack(Material.AIR));
                player.getInventory().setLeggings(new ItemStack(Material.AIR));
                player.getInventory().setBoots(new ItemStack(Material.AIR));
                player.setExp(0F);
                player.setLevel(0);
                player.setFlying(false);
                player.setAllowFlight(false);
                player.kickPlayer("Restarting to Lobby"); //TODO: Change to teleport to new game
                
                Bukkit.getScheduler().runTaskLater(Framework.getInstance(), () -> {
                	removeWorld();
                	Bukkit.getServer().shutdown(); //TODO: Change to restart script
                }, 20L * 1);
            }
        }, 20L * 10);      
    }
	
	
	
    public void die(Player player) {
    	users.remove(player.getUniqueId());
	    addSpectator(player.getUniqueId());
	    for (PotionEffect effect : player.getActivePotionEffects()) {
	    	player.removePotionEffect(effect.getType());
	    }
		Utils.sendTitle(Utils.color("&c&lDEATH!"), Utils.color("&fYou are now a spectator"), player);
	}
    
    
    public void addSpectator(UUID id) {
    	spectators.add(id);
        Player player = Bukkit.getPlayer(id);
        player.setGameMode(GameMode.ADVENTURE);
        player.setExp(0);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        player.setAllowFlight(true);
        player.setFlying(true);
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.hidePlayer(player);
        }
        Utils.specItems(player);
	    player.teleport(Utils.getLocation(Framework.getInstance().getConfig().getString("worlds." + getMap().getId() + ".center")));
    }
    
    public void removeWorld() {
        Bukkit.unloadWorld(map.getName(), false);
        try {
            FileUtils.deleteDirectory(new File(map.getName() + "/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Removed " + map.getName() + " with id " + map.getId());
    }

    public void createWorld() { 
        try {
            Utils.unzip("plugins/Framework/" + map.getName() + ".zip", Bukkit.getWorlds().get(0).getWorldFolder().getParentFile().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        WorldCreator worldCreator = new WorldCreator(map.getName());
        worldCreator.generator("MultiWorld:Empty");
        Bukkit.createWorld(worldCreator);

        System.out.println("Created " + map.getName() + " with id " + map.getId());
        System.out.println("World UUID is " + Bukkit.getWorld(map.getName()).getUID());
    }
    
    public Map getRandomMap() {
        Random r = new Random();
        int id = r.nextInt(Framework.getInstance().getConfig().getInt("worlds-amount")) + 1;
        System.out.println("Using map with id " + id);
        String name = Framework.getInstance().getConfig().getString("worlds." + (id) + ".name");
        List<String> author = Framework.getInstance().getConfig().getStringList("worlds." + (id) + ".authors");
        String desc = Framework.getInstance().getConfig().getString("worlds." + (id) + ".desc");
        Map map = new Map(name, desc, author, id);
        return map;
    }
	
    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public List<UUID> getUsers() {
        return users;
    }

    public List<UUID> getSpectators() {
        return spectators;
    }

    public GameState getState() {
        return status;
    }

    public void setState(GameState state) {
        this.status = state;
    }
    
    public Area getArea() {
    	return area;
    }
    
    public void setArea(Area a) {
    	this.area = a;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public GameType getType() {
        return type;
    }
    
    public void addUser(UUID id) {
        users.add(id);
    }
    
    public void removeUser(UUID id) {
        users.remove(id);
    }
    
    public void removeSpec(UUID id) {
    	spectators.remove(id);
    }
    
    public boolean isTeam() {
    	return team;
    }

}
