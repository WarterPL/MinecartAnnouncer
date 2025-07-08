package com.warterpl.minecartannoucer.rails;

import com.warterpl.helper.Pair;
import com.warterpl.minecartannoucer.MinecartAnnouncer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.boss.*;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDisplayer
{
    class BossbarSettings
    {
        public final String name;
        public final BarColor color;
        public final int duration;
        public BossbarSettings(String name, BarColor color, int duration)
        {
            this.name = name;
            this.color = color;
            this.duration = duration;
        }
    }
    private final Map<Player, Block> previousPlayerRail = new HashMap<>();
    final char definitionCharacter = '#';
    final String titlePageDefinition = definitionCharacter + "DEF:TITLE" + definitionCharacter;
    final String subtitleDefinition = definitionCharacter + "DEF:SUBTITLE" + definitionCharacter;
    final String bossbarDefinition = definitionCharacter + "DEF:BOSSBAR" + definitionCharacter;
    final String bossbarColorDefinition = definitionCharacter + "DEF:BB_COLOR-";
    final String bossbarDurationDefinition = definitionCharacter + "DEF:BB_DUR-";
    final String bossbarRgxTimeLeftDefinition = "#GET:BB_TIME#";
    public void SendMessage(Minecart minecart)
    {
        Player player = (Player) minecart.getPassengers().get(0);

        if (player != null) {
            Block minecartBlock = minecart.getLocation().getBlock().getRelative(0, 0, 0);

            boolean directional = (minecartBlock.getRelative(-1, -1, 0).getType() == Material.BONE_BLOCK ||
                    minecartBlock.getRelative(1, -1, 0).getType() == Material.BONE_BLOCK ||
                    minecartBlock.getRelative(0, -1, -1).getType() == Material.BONE_BLOCK ||
                    minecartBlock.getRelative(0, -1, 1).getType() == Material.BONE_BLOCK
            );

            Block previousRail = previousPlayerRail.get(player);

            if (MinecartAnnouncer.railMessages.containsKey(minecartBlock)) {
                HandlePage(directional, previousRail, minecartBlock, player);
            }
            previousPlayerRail.put(player, minecartBlock);
        }
    }
    void HandlePage(boolean directional, Block previousRail, Block minecartBlock, Player player)
    {
        if(previousRail == null)
            return;

        boolean repeating = previousRail.equals(minecartBlock);
        boolean directionalCondition = previousRail.getRelative(0, -1, 0).getType() == Material.BONE_BLOCK;


        if((directional && !directionalCondition) || repeating) return;
        StringBuilder message = new StringBuilder();
        List<Pair<String, String>> titleCards = new ArrayList<>();
        List<BossbarSettings> bossbarCards = new ArrayList<>();

        List<String> messages = MinecartAnnouncer.railMessages.get(minecartBlock);

        boolean isFirstUndefinedPage = true;

        for(String s : messages)
        {
            if(!s.equals(messages.getFirst()) &&
                !s.startsWith(titlePageDefinition) &&
                !s.startsWith(bossbarDefinition) &&
                !isFirstUndefinedPage)
                    message.append("\n§r");

            if(s.startsWith(titlePageDefinition)){
                int subtitlePos = s.indexOf(subtitleDefinition);
                if(subtitlePos != -1)
                    titleCards.add(new Pair<>(
                        s.substring(titlePageDefinition.length(), subtitlePos),
                        s.substring(subtitlePos + subtitleDefinition.length())
                    ));
                else
                    titleCards.add(new Pair<>(s.substring(12), ""));
            }
            else if(s.startsWith(bossbarDefinition))
            {
                String bossbarInfo = s.substring(bossbarDefinition.length());
                BarColor barColor;
                int duration;
                Pair<String, BarColor> extractedColor = ExtractBossbarColor(bossbarInfo);
                bossbarInfo = extractedColor.first;
                barColor = extractedColor.second;
                Pair<String, Integer> extractedDuration = ExtractIntegerParam(bossbarInfo, bossbarDurationDefinition);
                bossbarInfo = extractedDuration.first;
                duration = extractedDuration.second;
                bossbarCards.add(new BossbarSettings(bossbarInfo, barColor, duration));
            }
            else if(!s.isEmpty())
            {
                message.append(s);
                isFirstUndefinedPage = false;
            }
        }

        player.sendMessage(message.toString());
        HandleTitlePages(titleCards, player);
        HandleBossbars(bossbarCards, player);
    }
    Pair<String, Integer> ExtractIntegerParam(String bossbarInfo, String argument)
    {
        int intParam = 10;
        int durationPos = bossbarInfo.indexOf(argument);
        if(durationPos == -1)
            return new Pair<>(bossbarInfo, intParam);

        int endDurationPos = bossbarInfo.indexOf(definitionCharacter, durationPos+1);
        if(endDurationPos == -1)
        {
            bossbarInfo = bossbarInfo.substring(0, durationPos)
                    + bossbarInfo.substring(durationPos + argument.length());
            return new Pair<>(bossbarInfo, intParam);
        }

        String durationString = bossbarInfo.substring(durationPos + argument.length(), endDurationPos);
        try
        {
            intParam = Integer.parseInt(durationString);
            intParam = Math.abs(intParam);
        }
        catch (Exception e)
        {  }

        bossbarInfo = bossbarInfo.substring(0, durationPos)
                + bossbarInfo.substring(endDurationPos+1);

        return new Pair<>(bossbarInfo, intParam);
    }
    Pair<String, BarColor> ExtractBossbarColor(String bossbarInfo)
    {
        BarColor barColor;
        int colorPos = bossbarInfo.indexOf(bossbarColorDefinition);
        if(colorPos == -1)
            return new Pair<>(bossbarInfo, BarColor.WHITE);

        int endColorPos = bossbarInfo.indexOf(definitionCharacter, colorPos+1);
        if(endColorPos == -1)
        {
            barColor = BarColor.WHITE;
            bossbarInfo = bossbarInfo.substring(0, colorPos)
                    + bossbarInfo.substring(colorPos + bossbarColorDefinition.length());
            return new Pair<>(bossbarInfo, barColor);
        }

        switch (bossbarInfo.substring(colorPos + bossbarColorDefinition.length(), endColorPos))
        {
            case "RED":
                barColor = BarColor.RED;
                break;
            case "BLUE":
                barColor = BarColor.BLUE;
                break;
            case "PINK":
                barColor = BarColor.PINK;
                break;
            case "GREEN":
                barColor = BarColor.GREEN;
                break;
            case "YELLOW":
                barColor = BarColor.YELLOW;
                break;
            case "PURPLE":
                barColor = BarColor.PURPLE;
                break;
            default:
                barColor = BarColor.WHITE;
                break;
            }
        bossbarInfo = bossbarInfo.substring(0, colorPos)
                + bossbarInfo.substring(endColorPos+1);

        return new Pair<String, BarColor>(bossbarInfo, barColor);
    }
    void HandleTitlePages(List<Pair<String, String>> titleCards, Player player)
    {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {

                if (index < titleCards.size()) {
                    Pair<String, String> card = titleCards.get(index);
                    String title = card.first;
                    String subtitle = card.second;

                    player.sendTitle(title, subtitle, 10, 40, 10);
                    index++;
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(MinecartAnnouncer.plugin, 0L, 60L);
    }

    void HandleBossbars(List<BossbarSettings> bossbars, Player player) {
        if (bossbars.isEmpty()) return;

        displayNextBossbar(bossbars, player, 0);
    }

    private void displayNextBossbar(List<BossbarSettings> bossbars, Player player, int index) {
        if (index >= bossbars.size()) return;

        BossbarSettings settings = bossbars.get(index);
        String text = settings.name.replaceAll(bossbarRgxTimeLeftDefinition, Integer.toString(settings.duration));
        BossBar bossBar = Bukkit.createBossBar(text, settings.color, BarStyle.SOLID);
        bossBar.addPlayer(player);

        new BukkitRunnable() {
            int timeLeft = settings.duration;

            @Override
            public void run() {
                if (timeLeft < 0) {
                    bossBar.removePlayer(player);
                    cancel();
                    displayNextBossbar(bossbars, player, index + 1);
                } else {
                    bossBar.setTitle(settings.name.replaceAll(bossbarRgxTimeLeftDefinition, Integer.toString(timeLeft)));
                    double progress = (double) timeLeft / settings.duration;
                    bossBar.setProgress(progress);
                    timeLeft--;
                }
            }
        }.runTaskTimer(MinecartAnnouncer.plugin, 0L, 20L);
    }
}
