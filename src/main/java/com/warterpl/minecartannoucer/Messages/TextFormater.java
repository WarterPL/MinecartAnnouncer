package com.warterpl.minecartannoucer.Messages;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
public class TextFormater {

    private static final char[] Formats = "0123456789ABCDEFLNMOKR".toCharArray();
    public static String formatText(String text) {
        for(char c : Formats)
        {
            text = text.replace("$"+c, "§"+Character.toLowerCase(c));
        }
        return text;
    }
    public static String unformatText(String text)
    {
        for (char c : Formats)
        {
            text = text.replace("§"+Character.toLowerCase(c), "$"+c);
        }
        return text;
    }

    //Spigot dependent
    public static void sendActionBar(String rawText, Player player) {
        String formatted = TextFormater.formatText(rawText);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formatted));
    }
}
