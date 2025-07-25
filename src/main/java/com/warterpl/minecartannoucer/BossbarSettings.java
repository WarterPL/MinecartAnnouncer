package com.warterpl.minecartannoucer;

import org.bukkit.boss.BarColor;

public class BossbarSettings {
    public final String name;
    public final BarColor color;
    public final int duration;

    public BossbarSettings(String name, BarColor color, int duration) {
        this.name = name;
        this.color = color;
        this.duration = duration;
    }
}