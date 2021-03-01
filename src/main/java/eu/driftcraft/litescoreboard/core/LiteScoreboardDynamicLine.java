/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.driftcraft.litescoreboard.core;

import eu.driftcraft.litescoreboard.LiteScoreboard;
import eu.driftcraft.litescoreboard.placeholders.LiteScoreboardPlaceholder;
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author Mati
 */
public class LiteScoreboardDynamicLine {
    
    private final LiteScoreboard plugin;
    
    private final int lineNumber;
    private final String line;
    private final List<LiteScoreboardPlaceholder> usedPlaceholders;

    public LiteScoreboardDynamicLine(LiteScoreboard plugin, int lineNumber, String line, List<LiteScoreboardPlaceholder> usedPlaceholders) {
        this.plugin = plugin;
        this.lineNumber = lineNumber;
        this.line = line;
        this.usedPlaceholders = usedPlaceholders;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public List<LiteScoreboardPlaceholder> getUsedPlaceholders() {
        return usedPlaceholders;
    }
    
    public String evaluate(Player player) {
        String evaluatedLine = line;
        for (LiteScoreboardPlaceholder ph : usedPlaceholders) {
            String value = ph.getGetter().getValue(plugin, player);
            evaluatedLine = evaluatedLine.replace(ph.getPattern(), value);
        }
        return evaluatedLine;
    }
    
}
