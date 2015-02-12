package net.pixellife.shinynotifier;

import net.minecraft.entity.player.EntityPlayer;

import java.lang.reflect.Method;

/**
 * Created by clienthax on 20/10/2014.
 */
public class PermsHelper {
	public static PermsHelper instance = new PermsHelper();
	private Class<?> bukkit;
	private Method getPlayer;
	private Method hasPermission;

	public PermsHelper()
	{
		try {
			this.bukkit = Class.forName("org.bukkit.Bukkit");
			this.getPlayer = this.bukkit.getMethod("getPlayer", new Class[] { String.class });
			this.hasPermission = Class.forName("org.bukkit.entity.Player").getMethod("hasPermission", new Class[] {String.class });
			System.out.println("Bridge Enabled");
		} catch(Exception e){ e.printStackTrace(); };
	}

	public static boolean hasPerm(EntityPlayer player, String permission) {
		return hasPerm(player.getDisplayName(), permission);
	}
	
	public static boolean hasPerm(String playerName, String permission) {
		if (instance.bukkit != null) {
			boolean permPermit = instance.bukkitPermission(playerName, permission);
//			System.out.println("player: "+player.getDisplayName()+" perm: "+permission+" result: "+permPermit);
			return permPermit;
		}
		return false;
	}

	private boolean bukkitPermission(String username, String permission) {
		try {
			Object player = this.getPlayer.invoke(null, new Object[] {username});
			return ((Boolean)this.hasPermission.invoke(player, new Object[] {permission})).booleanValue();

		}catch(Exception e){e.printStackTrace(); return false;}
	}
}