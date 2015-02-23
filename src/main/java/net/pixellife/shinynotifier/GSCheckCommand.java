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

public class GSCheckCommand extends CommandBase {
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	@Override
	public String getCommandName() {
		return "gscheck";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return getCommandUsageText();
	}
	
	private String getCommandUsageText() {
		return "/gscheck <player> [<player> ...]\n"
				+ "Returns a listing of when each player captured shiny and "
				+ "undiscovered egg group pixelmon.";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] arguments) {
		if(commandSender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) commandSender;
			ChatComponentText playerMessage;
			
			if ( PermsHelper.hasPerm(player.getDisplayName(), "ShinyNotifier.gscheck") ) {
				String[] messageLines = getCheckInfo(arguments).split("\n");
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
			System.out.println(getCheckInfo(arguments));
		}
	}
	
	private String getCheckInfo(String[] arguments) {
		if ( arguments.length > 0 ) {
			
			String outputText = "";
			int shinyCount = 0;
			int unknownCount = 0;
			
			for ( String playerName : arguments ) {
				
				// Not the first player? Add some whitespace for readability.
				if ( outputText != "" ) {
					outputText += "\n\n\n";
				}
				outputText += playerName + "\n\n";
				
				PreparedStatement gscheckStatement = ShinyNotifier.instance.gscheckStatement;
				ResultSet gscheckResults;
				
				try {
					gscheckStatement.setString(1, playerName);
				} catch (SQLException e1) {
					System.err.println("Error setting playerName when attempting to call"
							+ " prepared database statement for the /gscheck command.");
					e1.printStackTrace();
					outputText += "Error getting data.";
					continue;
				}
				try {
					gscheckResults = gscheckStatement.executeQuery();
				} catch (SQLException e) {
					System.err.println("Error executing the prepared database statement"
							+ " for the /gscheck command.");
					e.printStackTrace();
					outputText += "Error getting data.";
					continue;
				}
				
				shinyCount = 0;
				unknownCount = 0;
				
				try {
					while ( gscheckResults.next() ) {
						Timestamp captureTimestamp = gscheckResults.getTimestamp(1);
						if ( captureTimestamp == null ) {
							continue;
						}
						String captureTimeText = dateFormat.format(new Date(captureTimestamp.getTime()));
						
						String pokemon = gscheckResults.getString(2);
						String type = gscheckResults.getString(3);
						
						if ( type == ShinyNotifier.SHINY ) {
							outputText += captureTimeText + " Shiny " + pokemon + "\n";
							shinyCount++;
						} else {
							outputText += captureTimeText + " " + pokemon + "\n";
							unknownCount++;
						}
					}
				} catch (SQLException e) {
					System.err.println("Error retrieving results from database for "
							+ "/gscheck command.");
					e.printStackTrace();
					outputText += "Error getting data.";
					continue;
				}
				
				outputText += "\nShinies: " + shinyCount +
						" -- Unknowns: " + unknownCount + "\n";
			}
			
			return outputText;
			
		} else {
			// Not enough arguments. Print usage.
			return "Not enough parameters. /gscheck requires at least one.\n\n" +
			getCommandUsageText();
		}
	}
}
