package net.pixellife.shinynotifier;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.PixelmonCaptureEvent;
import com.pixelmonmod.pixelmon.comm.PacketRegistry;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.customStarters.CustomStarters;
import com.pixelmonmod.pixelmon.database.DatabaseStorage;
import com.pixelmonmod.pixelmon.spawning.PixelmonBiomeDictionary;

@Mod(modid = ShinyNotifier.MODID, version = ShinyNotifier.VERSION, name = "Shiny Notifier", acceptableRemoteVersions="*")
public class ShinyNotifier// extends JavaPlugin
{	
    public static final String MODID = "shinynotifier";
    public static final String VERSION = "0.1";
    
    //@Mod.Instance(name = "shinynotifier", dependencies = "after:pixelmon")
    @Mod.Instance("shinynotifier")
	public static ShinyNotifier instance;

	public static File modDirectory;
	
	Configuration config;
	
	PreparedStatement playerStatement;
	PreparedStatement captureStatement;
    
	@EventHandler
    public void preInit(FMLPreInitializationEvent event) {
		this.config = new Configuration(event.getSuggestedConfigurationFile());
		this.config.load();
		modDirectory = new File(event.getModConfigurationDirectory().getParent());
		System.out.println("modDirectory: " + modDirectory.getPath());
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	System.out.println("this.config: " + this.config.toString());
        Pixelmon.EVENT_BUS.register(new PixelmonCaptureHandler());
        Connection con = DatabaseUtil.getConnection();
        System.out.println("con: " + con.toString());
        
        String playerStatementText = "MERGE INTO " + DatabaseUtil.playerTable + " KEY(id) "
        		+ "VALUES (?, ?)";
        System.out.println("playerStatementText: " + playerStatementText);
        try {
        	playerStatement = con.prepareStatement(playerStatementText);
        } catch (SQLException e) {
        	throw new RuntimeException("Error preparing database statement to insert player data for ShinyNotifier.");
        }
        
        String captureStatementText = "INSERT INTO " + DatabaseUtil.captureTable + "(captureTimestamp, playerUUID, pokemon, type) "
        		+ "VALUES (?, ?, ?, ?)";
        System.out.println("captureStatementText: " + captureStatementText);
        try {
        	captureStatement = con.prepareStatement(captureStatementText);
        } catch (SQLException e) {
        	throw new RuntimeException("Error preparing database statement to insert captured pixelmon data for ShinyNotifier.");
        }
    }
   
}