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

public class GSTopCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "gstop";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return getCommandUsageText();
	}
	
	private String getCommandUsageText() {
		return "/gstop <daysBackToLook>\n"
				+ "Returns a listing of the players that have caught the most "
				+ "shiny and undiscovered pokemon within the given timeframe.";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] arguments) {
		if(commandSender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) commandSender;
			ChatComponentText playerMessage;
			
			if ( PermsHelper.hasPerm(player.getDisplayName(), "ShinyNotifier.gstop") ) {
				String[] messageLines = getTopCatchers(arguments).split("\n");
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
			System.out.println(getTopCatchers(arguments));
		}
	}
	
	private String getTopCatchers(String[] arguments) {
		int daysBackToLook;
		if ( arguments.length == 1 ) {
			
			try {
				daysBackToLook = Integer.parseInt(arguments[0]);
			} catch (NumberFormatException e) {
				return "Incorrect number of parameters. /gstop requires "
						+ "exactly one number.\n\n" +
						getCommandUsageText();
			}
			
			String outputText = "GSTop\nDays back to look: " + arguments[0] + " days"
					+ "\n\nUnknowns:\n";
			
			PreparedStatement gstopUndiscoveredStatement = ShinyNotifier.instance.gstopUndiscoveredStatement;
			ResultSet gstopResults;
			
			try {
				gstopUndiscoveredStatement.setInt(1, daysBackToLook);
			} catch (SQLException e1) {
				System.err.println("Error setting daysBackToLook when attempting to call"
						+ " prepared database statement for the /gstop (undiscovered) command.");
				e1.printStackTrace();
				//outputText += "Error getting data.";
				return "Error getting data.";
			}
			
			try {
				gstopResults = gstopUndiscoveredStatement.executeQuery();
			} catch (SQLException e) {
				System.err.println("Error executing the prepared database statement"
						+ " for the /gstop (undiscovered) command.");
				e.printStackTrace();
				//outputText += "Error getting data.";
				return "Error getting data.";
			}
			
			try {
				while ( gstopResults.next() ) {
					String playerName = gstopResults.getString(1);
					int numUndiscovered = gstopResults.getInt(2);
					
					outputText += "  " + playerName + ": " + numUndiscovered + "\n";
				}
			} catch (SQLException e) {
				System.err.println("Error retrieving results from database for "
						+ "/gstop (undiscovered) command.");
				e.printStackTrace();
				//outputText += "Error getting data.";
				return "Error getting data.";
			}

			outputText += "\nShinies:\n";
			
			PreparedStatement gstopShiniesStatement = ShinyNotifier.instance.gstopShiniesStatement;
			try {
				gstopResults.close();
			} catch (SQLException e) {
				System.err.println("Error closing result set for"
						+ " prepared database statement for the /gstop (undiscovered) command.");
				e.printStackTrace();
				//outputText += "Error getting data.";
				return "Error getting data.";
			}
			
			try {
				gstopShiniesStatement.setInt(1, daysBackToLook);
			} catch (SQLException e1) {
				System.err.println("Error setting daysBackToLook when attempting to call"
						+ " prepared database statement for the /gstop (shinies) command.");
				e1.printStackTrace();
				//outputText += "Error getting data.";
				return "Error getting data.";
			}
			
			try {
				gstopResults = gstopShiniesStatement.executeQuery();
			} catch (SQLException e) {
				System.err.println("Error executing the prepared database statement"
						+ " for the /gstop (shinies) command.");
				e.printStackTrace();
				//outputText += "Error getting data.";
				return "Error getting data.";
			}
			
			try {
				while ( gstopResults.next() ) {
					String playerName = gstopResults.getString(1);
					int numShinies = gstopResults.getInt(2);
					
					outputText += "  " + playerName + ": " + numShinies + "\n";
				}
			} catch (SQLException e) {
				System.err.println("Error retrieving results from database for "
						+ "/gstop (shinies) command.");
				e.printStackTrace();
				//outputText += "Error getting data.";
				return "Error getting data.";
			}
			
			return outputText;
			
		} else {
			// Not enough arguments. Print usage.
			return "Incorrect number of parameters. /gstop requires exactly one whole number.\n\n" +
				getCommandUsageText();
		}
	}
}
