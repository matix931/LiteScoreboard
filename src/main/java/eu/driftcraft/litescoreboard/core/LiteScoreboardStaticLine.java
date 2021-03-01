/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.driftcraft.litescoreboard.core;

/**
 *
 * @author Mati
 */
public class LiteScoreboardStaticLine {
    
    private final int lineNumber;
    private final String line;

    public LiteScoreboardStaticLine(int lineNumber, String line) {
        this.lineNumber = lineNumber;
        this.line = line;
    }

    public String getLine() {
        return line;
    }

    public int getLineNumber() {
        return lineNumber;
    }
    
}
