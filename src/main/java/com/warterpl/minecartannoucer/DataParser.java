package com.warterpl.minecartannoucer;

import com.warterpl.helper.Pair;
import org.bukkit.boss.BarColor;

public class DataParser {
    final static char definitionCharacter = '#';
    public final static String TitlePageDef = definitionCharacter + "DEF:TITLE" + definitionCharacter;
    public final static String SubtitleDef = definitionCharacter + "DEF:SUBTITLE" + definitionCharacter;
    public final static String BossbarDef = definitionCharacter + "DEF:BOSSBAR" + definitionCharacter;
    public final static String BossbarColorDef = definitionCharacter + "DEF:BB_COLOR-";
    public final static String BossbarDurationDef = definitionCharacter + "DEF:BB_DUR-";
    public final static String BossbarRgxTimeLeftDef = "#GET:BB_TIME#";

    public static Pair<String, String> ParseTitlePage(String data)
    {
        int subtitlePos = data.indexOf(SubtitleDef);
        String title, subtitle;
        if(subtitlePos != -1) {
            title = data.substring(TitlePageDef.length(), subtitlePos).trim();
            subtitle = data.substring(subtitlePos + SubtitleDef.length()).trim();
        }
        else {
            title = data.substring(TitlePageDef.length()).trim();
            subtitle = "";
        }

        return new Pair<>(title, subtitle);
    }
    public static BossbarSettings ParseBossbarPage(String data)
    {
        String bossbarInfo = data.substring(BossbarDef.length()).trim();
        BarColor barColor;
        int duration;
        Pair<String, BarColor> extractedColor = ExtractBossbarColor(bossbarInfo);
        bossbarInfo = extractedColor.first;
        barColor = extractedColor.second;
        Pair<String, Integer> extractedDuration = ExtractIntegerParam(bossbarInfo, BossbarDurationDef);
        bossbarInfo = extractedDuration.first;
        duration = extractedDuration.second;

        return new BossbarSettings(bossbarInfo, barColor, duration);
    }
    static Pair<String, Integer> ExtractIntegerParam(String bossbarInfo, String argument)
    {
        int intParam = 10;
        int durationPos = bossbarInfo.indexOf(argument);
        if(durationPos == -1)
            return new Pair<>(bossbarInfo.trim(), intParam);

        int endDurationPos = bossbarInfo.indexOf(definitionCharacter, durationPos+1);
        if(endDurationPos == -1)
        {
            bossbarInfo = bossbarInfo.substring(0, durationPos)
                    + bossbarInfo.substring(durationPos + argument.length());
            return new Pair<>(bossbarInfo.trim(), intParam);
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

        return new Pair<>(bossbarInfo.trim(), intParam);
    }
    static Pair<String, BarColor> ExtractBossbarColor(String bossbarInfo)
    {
        BarColor barColor;
        int colorPos = bossbarInfo.indexOf(BossbarColorDef);
        if(colorPos == -1)
            return new Pair<>(bossbarInfo.trim(), BarColor.WHITE);

        int endColorPos = bossbarInfo.indexOf(definitionCharacter, colorPos+1);
        if(endColorPos == -1)
        {
            barColor = BarColor.WHITE;
            bossbarInfo = bossbarInfo.substring(0, colorPos)
                    + bossbarInfo.substring(colorPos + BossbarColorDef.length());
            return new Pair<>(bossbarInfo.trim(), barColor);
        }

        switch (bossbarInfo.substring(colorPos + BossbarColorDef.length(), endColorPos))
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

        return new Pair<String, BarColor>(bossbarInfo.trim(), barColor);
    }
}
