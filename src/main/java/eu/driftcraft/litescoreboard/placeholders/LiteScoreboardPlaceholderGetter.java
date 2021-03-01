/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.driftcraft.litescoreboard.placeholders;

import eu.driftcraft.litescoreboard.LiteScoreboard;
import org.bukkit.entity.Player;

/**
 *
 * @author Mati
 */
public interface LiteScoreboardPlaceholderGetter {
    
    public String getValue(LiteScoreboard plugin, Player player);
    
}
