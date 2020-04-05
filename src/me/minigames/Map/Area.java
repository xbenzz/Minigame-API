package me.minigames.Map;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Area {
	
    private Location[] locs;
    private World world;
    private List<Location> containedPoints;

    public Area(Location pA, Location pB) {
        if (pA.getWorld() != pB.getWorld())
            throw new IllegalArgumentException("Area's locations do not describe the same world!");

        locs = new Location[]{pA, pB};
        world = pA.getWorld();

        containedPoints = new ArrayList<>();
        int hX = Math.max(locs[0].getBlockX(), locs[1].getBlockX());
        int lX = Math.min(locs[0].getBlockX(), locs[1].getBlockX());
        int hY = Math.max(locs[0].getBlockY(), locs[1].getBlockY());
        int lY = Math.min(locs[0].getBlockY(), locs[1].getBlockY());
        int hZ = Math.max(locs[0].getBlockZ(), locs[1].getBlockZ());
        int lZ = Math.min(locs[0].getBlockZ(), locs[1].getBlockZ());
        for (int x = lX; x <= hX; x++) {
            for (int y = lY; y <= hY; y++) {
                for (int z = lZ; z <= hZ; z++) {
                    containedPoints.add(new Location(world, x, y, z));
                }
            }
        }
    }
    
    public void fillChests() {
    	for (Chunk c : world.getLoadedChunks()) {
            for (BlockState b : c.getTileEntities()) {
                if (b instanceof Chest) {
                    Chest chest = (Chest) b;
                    Inventory inventory = chest.getBlockInventory();
                    Material[] swords = {Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.STONE_SWORD};
                    Material[] armor = {Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.IRON_HELMET, Material.DIAMOND_CHESTPLATE};
                   
                    for (int i = 0; i < 2; i++) {
                        Random rand = new Random();

                        int max = 9;
                        for (int amountOfItems = 0; amountOfItems < max; amountOfItems++) {
                            inventory.addItem(new ItemStack(swords[rand.nextInt(swords.length)]));
                            inventory.addItem(new ItemStack(armor[rand.nextInt(armor.length)]));
                        }
                    }
                }
            }
    	}
    }

    /**
     * Get the corners of the Area
     *
     * @return {First_point, second_point}
     */
    public Location[] getBounds() {
        return locs;
    }

    public World getWorld() {
        return world;
    }

    /**
     * Get a list of all coordinates inside this Area
     *
     * @return List of block Locations
     */
    public List<Location> getContainedPoints() {
        return containedPoints;
    }
    
    /**
     * Checks if a given point is inside the Area
     *
     * @param loc Point to check
     * @return Whether this point is contained by the Area
     */
    public boolean isPointContained(Location loc) {
        return containedPoints.contains(loc);
    }

    public Location getCenter() {
        return new Location(world, (locs[0].getX() + locs[1].getX()) / 2, (locs[0].getY() + locs[1].getY()) / 2, (locs[0].getZ() + locs[1].getZ()) / 2);
    }
}