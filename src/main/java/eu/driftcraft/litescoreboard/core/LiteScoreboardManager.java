/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.driftcraft.litescoreboard.core;

import eu.driftcraft.litescoreboard.LiteScoreboard;
import eu.driftcraft.litescoreboard.placeholders.LiteScoreboardPlaceholder;
import fr.mrmicky.fastboard.FastBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Mati
 */
public class LiteScoreboardManager implements Runnable {
    
    private final LiteScoreboard plugin;
    private final Map<UUID, FastBoard> boards = new ConcurrentHashMap<>();
    
    private BukkitTask task;
    private List<LiteScoreboardDynamicLine> dynamicLines = null;
    private List<LiteScoreboardStaticLine> staticLines = null;

    public LiteScoreboardManager(LiteScoreboard plugin) {
        this.plugin = plugin;
    }
    
    public void start() {
        buildCache();
        stop();
        int interval = plugin.getMyConfig().getUpdateInterval();
        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, interval, interval);
    }
    
    public void stop() {
        if(task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
    }
    
    public void register(Player player) {
        FastBoard board = createBoard(player);
        addBoard(player.getUniqueId(), board);
        updateBoard(board, true);
    }
    
    public void unregister(Player player) {
        FastBoard board = removeBoard(player.getUniqueId());
        if(board != null) {
            board.delete();
        }
    }
    
    private FastBoard createBoard(Player player) {
        FastBoard fb = new FastBoard(player);
        fb.updateTitle(plugin.getMyConfig().getTitle());
        return fb;
    }
    
    private void addBoard(UUID uuid, FastBoard board) {
        boards.put(uuid, board);
    }
    
    private FastBoard removeBoard(UUID uuid) {
        return boards.remove(uuid);
    }
    
    private void updateBoard(FastBoard board, boolean force) {
        String[] lines = plugin.getMyConfig().getLines();
        if(lines != null && lines.length > 0) {
            if(force) {
                for (LiteScoreboardStaticLine staticLine : staticLines) {
                    board.updateLine(staticLine.getLineNumber(), staticLine.getLine());
                }
            }
            for (LiteScoreboardDynamicLine dynamicLine : dynamicLines) {
                board.updateLine(dynamicLine.getLineNumber(), dynamicLine.evaluate(board.getPlayer()));
            }
        } else {
            board.updateLines("DEFINE", "LINES", "IN CONFIGURATION", "FILE");    
        }
    }

    @Override
    public void run() {
        boards.values().forEach((fb) -> {
            updateBoard(fb, false);
        });
    }

    private void buildCache() {
        dynamicLines = new ArrayList<>();
        staticLines = new ArrayList<>();
        final String[] lines = plugin.getMyConfig().getLines();
        if(lines != null) {
            LiteScoreboardPlaceholder[] phList = LiteScoreboardPlaceholder.values();
            for(int i=0; i<lines.length; i++) {
                final String line = lines[i];
                List<LiteScoreboardPlaceholder> phMatchesList = new ArrayList<>();
                if(line.contains("%")) {
                    for (LiteScoreboardPlaceholder ph : phList) {
                        if(line.contains(ph.getPattern())) {
                            phMatchesList.add(ph);
                        }
                    }
                }
                if(!phMatchesList.isEmpty()) {
                    dynamicLines.add(new LiteScoreboardDynamicLine(plugin, i, line, phMatchesList));
                } else {
                    staticLines.add(new LiteScoreboardStaticLine(i, line));
                }
            }
        }
    }
    
}
