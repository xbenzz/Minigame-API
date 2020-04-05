package me.minigames.Utilities;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class Timer extends BukkitRunnable {

    private int time;

    public Timer(int time) {
        this.time = time;
    }

    @Override
    public void run() {
        setTime(--time);
        execute();
        if (time <= 0) {
            end();
            cancel();
        }
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }


    public abstract void execute();


    public abstract void end();
}