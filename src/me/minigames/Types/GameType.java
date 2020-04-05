package me.minigames.Types;

import java.util.Arrays;
import java.util.List;

public enum GameType {
	
	SOLO_SKYWARS("Solo Skywars", Arrays.asList("Kill all opposing players who are on their own", "sky island. Open chests and kill other players", "to get better gear! Last man standing wins."));
	/* TEAM_SKYWARS("Team Skywars"),
	SURVIVAL_GAMES("Survival Games"),
	CAKEWARS("Cakewars"),
	MURDER("Murder Mystery"),
	BUILDER("Champion Builders"),
	CTF("Capture The Flag"); */
	
	private String name;
	private List<String> desc;
	
	GameType(String name, List<String> desc) {
		this.name = name;
		this.desc = desc;
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getDesc() {
		return desc;
	}

}
