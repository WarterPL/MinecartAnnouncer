package com.warterpl.minecartannoucer.Commands;

import com.warterpl.minecartannoucer.MinecartAnnouncer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandManager {
    public static boolean Run(CommandSender sender, Command command, String label, String[] args)
    {
        String cmd = command.getName();
        if(cmd.equalsIgnoreCase("dev_showRailMessages"))
            return dev_showRailMessages(sender);
        return false;
    }
    private static boolean dev_showRailMessages(CommandSender sender)
    {
        if (sender instanceof Player player && player.isOp()) {

            if (MinecartAnnouncer.msgBlocks.isEmpty()) {
                player.sendMessage("There aren't any assigned messages.");
            } else {
                player.sendMessage("Assigned messages:");
                for (var entry : MinecartAnnouncer.msgBlocks) {
                    player.sendMessage("> " + entry.getLocation());
                }
            }
            return true;
        }
        sender.sendMessage("Only players can execute that command.");
        return false;
    }
}