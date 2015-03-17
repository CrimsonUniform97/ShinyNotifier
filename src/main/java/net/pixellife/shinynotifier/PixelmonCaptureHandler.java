package net.pixellife.shinynotifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.pixelmonmod.pixelmon.api.events.PixelmonCaptureEvent;
import com.pixelmonmod.pixelmon.enums.EnumEggGroup;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import scala.actors.threadpool.Arrays;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PixelmonCaptureHandler {
	
	@SubscribeEvent
    public void onPokemonCapture(PixelmonCaptureEvent event) throws SQLException {
		UUID playerId = event.player.getUniqueID();
		String playerName = event.player.getDisplayName();
		String pokeName = event.capturedPixelmon.getName();
		String type = null;
		
    	if (event.capturedPixelmon.getIsShiny()) {
    		type = ShinyNotifier.SHINY;
    		//System.out.println("Captured Shiny!");
    		
    	} else if ( ShinyNotifier.instance.watchedPixelmon.contains(pokeName) ) {
    		type = ShinyNotifier.WATCHED;
    		//System.out.println("Captured Undiscovered!");
    		
    	} else {
    		return;
    	}
    	
    	sendCaptureNotification(type, pokeName, playerName);
    	recordCapture(type, pokeName, playerId, playerName);
    }
	
	private void sendCaptureNotification(String type, String pokeName, String playerName) {
		
		ChatComponentText notificationText;
		
		if ( type == ShinyNotifier.SHINY ) {
			notificationText = new ChatComponentText(playerName + " just caught a shiny " + pokeName + "!");
			System.out.println(playerName + " just caught a shiny " + pokeName + "!");
		} else if ( type == ShinyNotifier.WATCHED ) {
			notificationText = new ChatComponentText(playerName + " just caught a " + pokeName + "!");
			System.out.println(playerName + " just caught a " + pokeName + "!");
		} else {
			return;
		}
		
		List<EntityPlayerMP> playerList = (List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		
		for ( EntityPlayerMP onlinePlayer : playerList ) {
			if ( PermsHelper.hasPerm(onlinePlayer.getDisplayName(), "ShinyNotifier.receive") ) {
				onlinePlayer.addChatMessage(notificationText);
			}
		}
	}
	
	private void recordCapture(String type, String pokeName, UUID playerId, String playerName) throws SQLException {
		PreparedStatement insertPlayerStatement = ShinyNotifier.instance.playerStatement;
		insertPlayerStatement.setObject(1, playerId);
		insertPlayerStatement.setString(2, playerName);
		insertPlayerStatement.execute();
		
		Long timeInMS = System.currentTimeMillis(); 
		Timestamp timestamp = new Timestamp(timeInMS);
		
		PreparedStatement insertCaptureStatement = ShinyNotifier.instance.captureStatement;
		insertCaptureStatement.setTimestamp(1, timestamp);
		insertCaptureStatement.setObject(2, playerId);
		insertCaptureStatement.setString(3, pokeName);
		insertCaptureStatement.setString(4, type);
		insertCaptureStatement.execute();
	}
}
