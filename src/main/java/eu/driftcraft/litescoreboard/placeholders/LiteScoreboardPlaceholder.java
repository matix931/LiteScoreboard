/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.driftcraft.litescoreboard.placeholders;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.exceptions.McMMOPlayerNotFoundException;
import com.google.common.base.Strings;
import java.text.DecimalFormat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;

/**
 *
 * @author Mati
 */
public enum LiteScoreboardPlaceholder {
    
    MCMMO_POWER((plugin, player) -> {
        String value;
        try {
            value = String.valueOf(ExperienceAPI.getPowerLevel(player));
        } catch(McMMOPlayerNotFoundException ex) {
            value = "Loading...";
        }
        return value;
    }),
    X((plugin, player) -> String.valueOf(player.getLocation().getBlockX())),
    Y((plugin, player) -> String.valueOf(player.getLocation().getBlockY())),
    Z((plugin, player) -> String.valueOf(player.getLocation().getBlockZ())),
    PLAYTIME((plugin, player) -> {
        int totalHours = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60 / 60;
        int hours = totalHours % 24;
        int days = (totalHours - hours) / 24;
        StringBuilder sb = new StringBuilder();
        if(days > 0) {
            sb.append(days).append("d ");
        }
        sb.append(hours).append("h");
        return sb.toString();
    }),
    GAMETIME((plugin, player) -> {
        World world = Bukkit.getServer().getWorld("world");
        if(world == null) {
            return "NO WORLD";
        }
        long time = (world.getTime() + 6000) % 24000;
        int hourPart = (int) (time % 1000);
        int hours = (int) ((time - hourPart) / 1000);
        int minutes = hourPart * 60 / 1000;
       
       StringBuilder sb = new StringBuilder();
       sb.append(hours)
               .append(":")
               .append(Strings.padStart(String.valueOf(minutes), 2, '0'));
       return sb.toString();
    }),
    MONEY((plugin, player) -> {
        double balance = plugin.getEconomy().getBalance(player);
        return numberFormat(balance);
    }),
    PLAYERKILLS((plugin, player) -> {
        return String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS));
    }),
    MOBKILLS((plugin, player) -> {
        return String.valueOf(player.getStatistic(Statistic.MOB_KILLS));
    }),
    DEATHS((plugin, player) -> {
        return String.valueOf(player.getStatistic(Statistic.DEATHS));
    }),
    ;
    
    private static final DecimalFormat df = new DecimalFormat("#.##");
    private static final String[] numberSuffixes = {"", "K", "M", "B", "T", "Q", "Qi", "S"};
    
    private final LiteScoreboardPlaceholderGetter getter;
    private final String pattern;
    

    private LiteScoreboardPlaceholder(LiteScoreboardPlaceholderGetter getter) {
        this.pattern = "%" + name().toLowerCase() + "%";
        this.getter = getter;
    }

    public String getPattern() {
        return pattern;
    }

    public LiteScoreboardPlaceholderGetter getGetter() {
        return getter;
    }
    
    private static String numberFormat(double value) {
        int idx = 0;
        while (value / 1000 >= 1 && idx != numberSuffixes.length - 1) {
            value /= 1000;
            idx++;
        }
        return String.format("%s%s", df.format(value), numberSuffixes[idx]);
    }
}
