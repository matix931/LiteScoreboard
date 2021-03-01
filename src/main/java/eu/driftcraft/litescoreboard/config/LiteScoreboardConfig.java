/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.driftcraft.litescoreboard.config;

/**
 *
 * @author Mati
 */
public class LiteScoreboardConfig {
    
    private int updateInterval = 20;
    private String title = "--- LiteScoreboard ---";
    private String[] lines;

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setLines(String[] lines) {
        this.lines = lines;
    }

    public String[] getLines() {
        return lines;
    }
    
}
