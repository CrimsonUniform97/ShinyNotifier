package net.pixellife.shinynotifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import com.pixelmonmod.pixelmon.api.events.PixelmonCaptureEvent;
import com.pixelmonmod.pixelmon.enums.EnumEggGroup;

import net.minecraft.server.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PixelmonCaptureHandler {
	
	private final String SHINY = "SHINY";
	private final String UNDISCOVERED = "UNDISCOVERED";
	
	@SubscribeEvent
    public void onPokemonCapture(PixelmonCaptureEvent event) throws SQLException {
		UUID playerId = event.player.getUniqueID();
		String playerName = event.player.getDisplayName();
		String pokeName = event.capturedPixelmon.getName();
		String type = null;
		
    	if (event.capturedPixelmon.getIsShiny()) {
    		type = SHINY;
    		System.out.println("Captured Shiny!");
    		
    	} else if (event.capturedPixelmon.group == EnumEggGroup.Undiscovered ) {
    		type = UNDISCOVERED;
    		System.out.println("Captured Undiscovered!");
    		
    	} else {
    		return;
    	}
    	
    	sendCaptureNotification(type, pokeName, playerName);
    	recordCapture(type, pokeName, playerId, playerName);
    }
	
	private void sendCaptureNotification(String type, String pokeName, String playerName) {
		
		String notificationText = "";
		
		if ( type == SHINY ) {
			notificationText = playerName + " just caught a shiny " + pokeName + "!";
		} else if ( type == UNDISCOVERED ) {
			notificationText = playerName + " just caught a " + pokeName + "!";
		}
		
		Player[] playerList = Bukkit.getServer().getOnlinePlayers();
		
		for ( Player onlinePlayer : playerList ) {
			//if ( onlinePlayer.hasPermission("ShinyNotifier.receive") ) {
			if ( PermsHelper.hasPerm(onlinePlayer.getDisplayName(), "ShinyNotifier.receive") ) {
				onlinePlayer.sendMessage(notificationText);
				// TODO: test
			}
		}
	}
	
	private void recordCapture(String type, String pokeName, UUID playerId, String playerName) throws SQLException {
		PreparedStatement insertPlayerStatement = ShinyNotifier.instance.playerStatement;
		insertPlayerStatement.setObject(0, playerId);
		insertPlayerStatement.setString(1, playerName);
		insertPlayerStatement.execute();
		
		Calendar cal = Calendar.getInstance(); 
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		
		PreparedStatement insertCaptureStatement = ShinyNotifier.instance.captureStatement;
		insertCaptureStatement.setTimestamp(0, timestamp);
		insertCaptureStatement.setObject(1, playerId);
		insertCaptureStatement.setString(2, pokeName);
		insertCaptureStatement.setString(3, type);
	}
}
