package me.minigames.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.minigames.Game.MiniGame;

public class GameStartEvent extends Event {

    private static HandlerList handlerList = new HandlerList();
    private MiniGame game;
    
    public GameStartEvent(MiniGame g) {
        this.game = g;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public MiniGame getGame() {
        return game;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}