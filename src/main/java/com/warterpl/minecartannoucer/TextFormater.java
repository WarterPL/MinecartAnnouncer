package com.warterpl.minecartannoucer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
public class TextFormater {
    public static String formatText(String text) {

        text = text.replace("$0", "§0"); // Black
        text = text.replace("$1", "§1"); // Blue
        text = text.replace("$2", "§2"); // Green
        text = text.replace("$3", "§3"); // Light blue
        text = text.replace("$4", "§4"); // Red
        text = text.replace("$5", "§5"); // Purple
        text = text.replace("$6", "§6"); // Orange
        text = text.replace("$7", "§7"); // Gray
        text = text.replace("$8", "§8"); // Dark gray
        text = text.replace("$9", "§9"); // Other blue
        text = text.replace("$A", "§a"); // Light green
        text = text.replace("$B", "§b"); // Aqua
        text = text.replace("$C", "§c"); // Light Red
        text = text.replace("$D", "§d"); // Pink
        text = text.replace("$E", "§e"); // Yellow
        text = text.replace("$F", "§f"); // White
        text = text.replace("$L", "§l"); // Bold
        text = text.replace("$N", "§n"); // Underline
        text = text.replace("$M", "§m"); // Crossed
        text = text.replace("$O", "§o"); // Italic
        text = text.replace("$K", "§k"); // Scrambled
        text = text.replace("$R", "§r"); // Reset

        return text;
    }
    public static String unformatText(String text)
    {
        text = text.replace("§0", "$0");
        text = text.replace("§1", "$1");
        text = text.replace("§2", "$2");
        text = text.replace("§3", "$3");
        text = text.replace("§4", "$4");
        text = text.replace("§5", "$5");
        text = text.replace("§6", "$6");
        text = text.replace("§7", "$7");
        text = text.replace("§8", "$8");
        text = text.replace("§9", "$9");
        text = text.replace("§a", "$A");
        text = text.replace("§b", "$B");
        text = text.replace("§c", "$C");
        text = text.replace("§d", "$D");
        text = text.replace("§e", "$E");
        text = text.replace("§f", "$F");
        text = text.replace("§l", "$L");
        text = text.replace("§n", "$N");
        text = text.replace("§m", "$M");
        text = text.replace("§o", "$O");
        text = text.replace("§k", "$K");
        text = text.replace("§r", "$R");

        return text;
    }

    //Spigot dependent
    public static void sendActionBar(String rawText, Player player) {
        String formatted = TextFormater.formatText(rawText);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formatted));
    }
}
