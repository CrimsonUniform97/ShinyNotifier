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

public class GSReloadCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "gsreload";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return getCommandUsageText();
	}
	
	private String getCommandUsageText() {
		return "/gsreload\n"
				+ "Forcibly reloads configurations for ShinyNotifier.";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] arguments) {
		if(commandSender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) commandSender;
			ChatComponentText playerMessage;
			
			if ( PermsHelper.hasPerm(player.getDisplayName(), "ShinyNotifier.gsreload") ) {
				ShinyNotifier.instance.reloadConfigurations();
				playerMessage = new ChatComponentText("Configurations reloaded!");
				player.addChatMessage(playerMessage);
			} else {
				playerMessage = new ChatComponentText("Sorry, you don't have have permissions for this command.");
				player.addChatMessage(playerMessage);
			}
		} else if(commandSender instanceof TileEntityCommandBlock) {
			// TODO: Wtf is a TileEntityCommandBlocK?
		} else {
			// Otherwise, it's the server console
			ShinyNotifier.instance.reloadConfigurations();
			System.out.println("Configurations reloaded!");
		}
	}
}
