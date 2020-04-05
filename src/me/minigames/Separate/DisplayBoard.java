package me.minigames.Separate;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.minigames.Framework;
import me.minigames.Game.MiniGame;
import me.minigames.Utilities.Utils;
import net.md_5.bungee.api.ChatColor;

public class DisplayBoard {
	
	public static void setWaitingBoard(Player player, MiniGame game) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("noflicker", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§b§lSolo Skywars");
        
        Score blan2 = obj.getScore("     ");
        blan2.setScore(9);	
        
        Score i = obj.getScore("§6§lInfo:");
        i.setScore(8);	
        
        Team name = board.registerNewTeam("Name");
        name.addEntry(ChatColor.AQUA.toString());
        name.setPrefix("  §eName:§f ");
        name.setSuffix("§f" + player.getName());
        obj.getScore(ChatColor.AQUA.toString()).setScore(7);
        
        Team lvl = board.registerNewTeam("Level");
        lvl.addEntry(ChatColor.BLACK.toString());
        lvl.setPrefix("  §eLevel:§f ");
        lvl.setSuffix("§f" + String.valueOf(1));
        obj.getScore(ChatColor.BLACK.toString()).setScore(6);
        
        Team bal = board.registerNewTeam("Credits");
        bal.addEntry(ChatColor.BLUE.toString());
        bal.setPrefix("  §eCredits:§f ");
        bal.setSuffix("§f" + String.valueOf(100));
        obj.getScore(ChatColor.BLUE.toString()).setScore(5);
        
        Score blan = obj.getScore("   ");
        blan.setScore(4);	
        
        Score stats = obj.getScore("§d§lStats:");
        stats.setScore(3);	  
        
        Team map = board.registerNewTeam("Map");
        map.addEntry(ChatColor.DARK_AQUA.toString());
        map.setPrefix("  §eMap:§f ");
        map.setSuffix("§f" + game.getMap().getName());
        obj.getScore(ChatColor.DARK_AQUA.toString()).setScore(2);
        
        Team players = board.registerNewTeam("PlayersLobby");
        players.addEntry(ChatColor.DARK_BLUE.toString());
        players.setPrefix("  §ePlayers:§f ");
        players.setSuffix("§f" + game.getUsers().size() + "/" + game.getMaxPlayers());
        obj.getScore(ChatColor.DARK_BLUE.toString()).setScore(1);
        
        player.setScoreboard(board);
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(Framework.getInstance(), () -> {
        	board.getTeam("PlayersLobby").setSuffix(ChatColor.WHITE.toString() + game.getUsers().size() + "/" + game.getMaxPlayers());
        }, 20L, 20L);
	}
	
	public static void setInGameBoard(Player player, MiniGame game) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("noflicker", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§b§lSolo Skywars");
        
        Score blan2 = obj.getScore("     ");
        blan2.setScore(9);	
        
        Score i = obj.getScore("§6§lInfo:");
        i.setScore(8);	
        
        Team kills = board.registerNewTeam("Kills");
        kills.addEntry(ChatColor.AQUA.toString());
        kills.setPrefix("  §eKills:§f ");
        kills.setSuffix("§f" + String.valueOf(1));
        obj.getScore(ChatColor.AQUA.toString()).setScore(7);
        
        Team deaths = board.registerNewTeam("Deaths");
        deaths.addEntry(ChatColor.BLACK.toString());
        deaths.setPrefix("  §eDeaths:§f ");
        deaths.setSuffix("§f" + String.valueOf(1));
        obj.getScore(ChatColor.BLACK.toString()).setScore(6);
        
        Team time = board.registerNewTeam("Time");
        time.addEntry(ChatColor.BLUE.toString());
        time.setPrefix("  §eTime:§f ");
        time.setSuffix("§f" + Utils.getLength(game.getTimer().getTime()));
        obj.getScore(ChatColor.BLUE.toString()).setScore(5);
        
        Score blan = obj.getScore("   ");
        blan.setScore(4);	
        
        Score stats = obj.getScore("§d§lStats:");
        stats.setScore(3);	  
        
        Team map = board.registerNewTeam("Map");
        map.addEntry(ChatColor.DARK_AQUA.toString());
        map.setPrefix("  §eMap:§f ");
        map.setSuffix("§f" + game.getMap().getName());
        obj.getScore(ChatColor.DARK_AQUA.toString()).setScore(2);
        
        Team players = board.registerNewTeam("PlayersGame");
        players.addEntry(ChatColor.DARK_BLUE.toString());
        players.setPrefix("  §ePlayers:§f ");
        players.setSuffix("§f" + game.getUsers().size());
        obj.getScore(ChatColor.DARK_BLUE.toString()).setScore(1);
        
        player.setScoreboard(board);
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(Framework.getInstance(), () -> {
        	board.getTeam("Kills").setSuffix(ChatColor.WHITE.toString() + "1");
        	board.getTeam("Deaths").setSuffix(ChatColor.WHITE.toString() + "1");
        	String t = "";
        	if (game.getTimer() != null) {
        		t = Utils.getLength(game.getTimer().getTime());
        	} else {
        		t = "N/A";
        	}
        	board.getTeam("Time").setSuffix(ChatColor.WHITE.toString() + "§f" + t);
        	board.getTeam("PlayersGame").setSuffix(ChatColor.WHITE.toString() + game.getUsers().size());
        }, 20L, 20L);
	}

}
