package me.minigames.Utilities;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.WorldServer;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class Utils {	  
	
	public static String color(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static void removeBar(Player p) {
		WorldServer world = ((CraftWorld) p.getLocation().getWorld()).getHandle();
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new EntityEnderDragon(world).getId());
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
	
	public static void setBar(Player p, String text) {
		Location loc = p.getLocation();
		WorldServer world = ((CraftWorld) p.getLocation().getWorld()).getHandle();

		EntityEnderDragon dragon = new EntityEnderDragon(world);
		        dragon.setLocation(loc.getX() - 30, loc.getY() - 100, loc.getZ(), 0, 0);
		        dragon.setCustomName(text);
		        dragon.setInvisible(true);

		    Packet<?> packet = new PacketPlayOutSpawnEntityLiving(dragon); 
		    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
	
    public static void sendTitle(String title, String subtitle, Player p) {
        PacketPlayOutTitle playOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(title));
        PacketPlayOutTitle playOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatComponentText(subtitle));
        sendPacket(playOutTitle, p);
        sendPacket(playOutSubTitle, p);
    }

    public static void sendPacket(Packet<?> packet, Player p) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }
	
	public static void specItems(Player p) {
		ItemStack compass = new ItemStack(Material.COMPASS, 1);
		ItemMeta compMeta = compass.getItemMeta();
		compMeta.setDisplayName(color("&6&lTeleport"));
		compass.setItemMeta(compMeta);
		
		ItemStack play = new ItemStack(Material.EMPTY_MAP, 1);
		ItemMeta playMeta = play.getItemMeta();
		playMeta.setDisplayName(color("&a&lNew Game"));
		play.setItemMeta(playMeta);

		ItemStack quit = new ItemStack(Material.WATCH, 1);
		ItemMeta quitMeta = quit.getItemMeta();
		quitMeta.setDisplayName(color("&c&lBack to Hub"));
		quit.setItemMeta(quitMeta);
		
		p.getInventory().setItem(2, compass);
		p.getInventory().setItem(4, play);
		p.getInventory().setItem(6, quit);
		
	}
	
    public static void spawnFireworks(Location loc, double radius, int count, Color color, int power) {
        FireworkEffect effect = FireworkEffect.builder().with(FireworkEffect.Type.BURST).flicker(true).withColor(color).withFade(Color.BLACK).build();
        for (int i = 0; i < count; i++) {
        	double angle = (2 * Math.PI / count) * i;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Firework firework = loc.getWorld().spawn(loc.clone().add(x, 0, z), Firework.class);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.addEffect(effect);
            fireworkMeta.setPower(power);
            firework.setFireworkMeta(fireworkMeta);
        }
        
    }
    
	public static String getLength(int time) {
		String mins;
		String seconds;
		
		long m = time / 60;
		mins = String.valueOf(m) + " Minutes";
		time = time % 60;
		
		long s = time;
		seconds = String.valueOf(s) + " Seconds";
		
		String total = "";
		if (m != 0) {
			total = mins;
		} else {
			total = seconds;
		}
		return total;
	}
	
    public static Location getLocation(String location) {
        String[] parts = location.split("_");
        Location loc = new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
        return loc;
    }
	
    private static FileSystem createZipFileSystem(String file, boolean create) throws IOException {
        final Path path = Paths.get(file);
        final URI uri = URI.create("jar:file:" + path.toUri().getPath());

        final Map<String, String> env = new HashMap<>();
        if (create) {
            env.put("create", "true");
        }
        return FileSystems.newFileSystem(uri, env);
    }
	
	 public static void unzip(String file, String dest) throws IOException {
	      final Path destDir = Paths.get(dest);
	        if (Files.notExists(destDir)) {
	            System.out.println(destDir + " does not exist. Creating...");
	            Files.createDirectories(destDir);
	        }

	        try (FileSystem zipFileSystem = createZipFileSystem(file, false)) {
	            final Path root = zipFileSystem.getPath("/");

	            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
	                @Override
	                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	                    final Path destFile = Paths.get(destDir.toString(), file.toString());
	                    Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
	                    return FileVisitResult.CONTINUE;
	                }

	                @Override
	                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	                    final Path dirToCreate = Paths.get(destDir.toString(), dir.toString());
	                    if (Files.notExists(dirToCreate)) {
	                        Files.createDirectory(dirToCreate);
	                    }
	                    return FileVisitResult.CONTINUE;
	                }
	            });
	        }
	    }

}
