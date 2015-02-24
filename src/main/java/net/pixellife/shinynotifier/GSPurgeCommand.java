package net.pixellife.shinynotifier;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatComponentText;

public class GSPurgeCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "gspurge";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return getCommandUsageText();
	}
	
	private String getCommandUsageText() {
		return "/gspurge <player> [<player> ...]\n"
				+ "Deletes capture records for the given player(s).";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] arguments) {
		if(commandSender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) commandSender;
			ChatComponentText playerMessage;
			
			if ( PermsHelper.hasPerm(player.getDisplayName(), "ShinyNotifier.gspurge") ) {
				String[] messageLines = purgeData(arguments).split("\n");
				for ( String messageText : messageLines ) { 
					playerMessage = new ChatComponentText(messageText);
					player.addChatMessage(playerMessage);
				}
			} else {
				playerMessage = new ChatComponentText("Sorry, you don't have have permissions for this command.");
				player.addChatMessage(playerMessage);
			}
		} else if(commandSender instanceof TileEntityCommandBlock) {
			// TODO: Wtf is a TileEntityCommandBlocK?
		} else {
			// Otherwise, it's the server console
			System.out.println(purgeData(arguments));
		}
	}
	
	private String purgeData(String[] arguments) {
		if ( arguments.length > 0 ) {
			
			String outputText = "";
			
			for ( String playerName : arguments ) {
				
				PreparedStatement gspurgeStatement = ShinyNotifier.instance.gspurgeStatement;
				ResultSet gscheckResults;
				
				try {
					gspurgeStatement.setString(1, playerName);
				} catch (SQLException e1) {
					System.err.println("Error setting playerName when attempting to call"
							+ " prepared database statement for the /gscheck command.");
					e1.printStackTrace();
					outputText += "Error getting data.";
					continue;
				}
				
				try {
					gspurgeStatement.execute();
				} catch (SQLException e1) {
					System.err.println("Error executing the prepared database statement"
							+ " for the /gspurge command.");
					e1.printStackTrace();
					outputText += "Error getting data.";
					continue;
				}
				
				outputText += "Purging data for " + playerName + " was successful.\n";
			}
			
			return outputText;
			
		} else {
			// Not enough arguments. Print usage.
			return "Not enough parameters. /gspurge requires at least one.\n\n" +
			getCommandUsageText();
		}
	}
}
