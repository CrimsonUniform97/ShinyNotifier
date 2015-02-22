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

import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
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
    
    protected final static String SHINY = "SHINY";
    protected final static String UNDISCOVERED = "UNDISCOVERED";
    
    //@Mod.Instance(name = "shinynotifier", dependencies = "after:pixelmon")
    @Mod.Instance("shinynotifier")
	public static ShinyNotifier instance;

	public static File modDirectory;
	
	Configuration config;
	
	PreparedStatement playerStatement;
	PreparedStatement captureStatement;
	PreparedStatement gscheckStatement;
    
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
        
        prepareDatabaseStatements(con);
        setupCommands();
    }
    
    private void prepareDatabaseStatements(Connection con) {
    	String playerStatementText = "MERGE INTO " + DatabaseUtil.playerTable + " KEY(id) "
        		+ "VALUES (?, ?)";
        System.out.println("playerStatementText: " + playerStatementText);
        try {
        	playerStatement = con.prepareStatement(playerStatementText);
        } catch (SQLException e) {
        	System.err.println("Error preparing database statement to insert player data for ShinyNotifier.");
        	e.printStackTrace();
        	throw new RuntimeException(e);
        }
        
        String captureStatementText = "INSERT INTO " + DatabaseUtil.captureTable + "(captureTimestamp, playerUUID, pokemon, type) "
        		+ "VALUES (?, ?, ?, ?)";
        System.out.println("captureStatementText: " + captureStatementText);
        try {
        	captureStatement = con.prepareStatement(captureStatementText);
        } catch (SQLException e) {
        	System.err.println("Error preparing database statement to insert captured pixelmon data for ShinyNotifier.");
        	e.printStackTrace();
        	throw new RuntimeException(e);
        }
        
        String gscheckStatementText = "SELECT c.captureTimestamp, c.pokemon, c.type FROM " + 
        		DatabaseUtil.playerTable + " p LEFT JOIN " + DatabaseUtil.captureTable + " c " +
        		//"ON p.id = c.playerUUID WHERE p.name = ? ORDER BY c.type ASC, c.captureTimestamp DESC";
        		"ON p.id = c.playerUUID WHERE p.name = ? ORDER BY c.captureTimestamp DESC";
        System.out.println("gscheckStatementText: " + gscheckStatementText);
        try {
        	gscheckStatement = con.prepareStatement(gscheckStatementText);
        } catch (SQLException e) {
        	System.err.println("Error preparing database statement for the gscheck command for ShinyNotifier.");
        	e.printStackTrace();
        	throw new RuntimeException(e);
        }
    }
    
    private void setupCommands() {
    	MinecraftServer server = MinecraftServer.getServer();
    	ServerCommandManager commandMgr = (ServerCommandManager) server.getCommandManager();
    	
    	commandMgr.registerCommand(new GSCheckCommand());
    }
   
}