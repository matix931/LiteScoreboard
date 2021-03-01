/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.driftcraft.litescoreboard;

import eu.driftcraft.litescoreboard.core.LiteScoreboardManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.gmail.nossr50.api.ExperienceAPI;
import eu.driftcraft.litescoreboard.config.LiteScoreboardConfig;
import eu.driftcraft.litescoreboard.listeners.PlayerListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Mati
 */
public class LiteScoreboard extends JavaPlugin {
    
    private final PluginManager pm;
    private final Server server;
    private final Logger log;
    
    private final String pluginName;
    private final String messagePrefix;
    private final String logPrefix;
    
    private final String pluginFolderPath;
    
    private final LiteScoreboardManager manager;
    
    private LiteScoreboardConfig config;
    
    private Economy economy;
    
    public LiteScoreboard() {
        this.server = getServer();
        this.pm = server.getPluginManager();
        this.log = server.getLogger();
        this.pluginName = getDescription().getName();
        this.messagePrefix = "§3[§c" + pluginName + "§3]§f";
        this.logPrefix = "\033[0;36m[\033[0;31m" + pluginName + "\033[0;36m]\033[0m";
        this.pluginFolderPath = "plugins/"+pluginName;
        
        this.manager = new LiteScoreboardManager(this);
    }
    
    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            showConsoleError("Disabled due to no Vault [Economy] dependency found!");
            pm.disablePlugin(this);
            return;
        }
        
        if (!setupMCMMO()) {
            showConsoleError("Disabled due to no mcMMO dependency found!");
            pm.disablePlugin(this);
            return;
        }
        
        try {
            loadConfig();
        } catch (IOException ex) {
            showConsoleError("Disabled due to configuration file loading error");
            log.log(Level.SEVERE, null, ex);
            pm.disablePlugin(this);
            return;
        }
        
        pm.registerEvents(new PlayerListener(this), this);
        
        manager.start();
    }

    @Override
    public void onDisable() {
        manager.stop();
        Bukkit.getServer().getScheduler().cancelTasks(this);
    }
    
    private boolean setupEconomy() {
        if (pm.getPlugin("Vault") == null) {
            showConsoleError("No Vault plugin");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            showConsoleError("No Economy provider");
            return false;
        }
        economy = rsp.getProvider();
        if(economy==null) {
            showConsoleError("Null Economy provider");
        }
        return economy != null;
    }
    
    private boolean setupMCMMO() {
        if (pm.getPlugin("mcMMO") == null) {
            showConsoleError("No mcMMO plugin");
            return false;
        }
        try {
            ExperienceAPI.class.getClass();
        } catch(Exception ex) {
            return false;
        }
        return true;
    }
    
    public PluginManager getPluginManager() {
        return pm;
    }
    
    public void showConsoleException(Exception ex) {
        log.log(Level.SEVERE, "Error occured", ex);
    }
    
    public void showConsoleError(String error, Object... args) {
        log.log(Level.SEVERE, String.format("%s %s", logPrefix, error), args);
    }
    
    public void showConsoleInfo(String info, Object... args) {
        log.log(Level.INFO, String.format("%s %s", logPrefix, info), args);
    }
    
    public void sendChatMessage(Player player, String msg) {
        player.sendMessage(String.format("%s %s", messagePrefix, msg));
    }
    
    public void sendMessages(CommandSender cs, List<String> msgs) {
        sendMessages(cs, msgs.toArray(new String[0]));
    }
    
    public void sendMessages(CommandSender cs, String... msgs) {
        for(int i=0; i<msgs.length; i++) {
            msgs[i] = String.format("%s %s", messagePrefix, msgs[i]);
        }
        cs.sendMessage(msgs);
    }
    
    public void sendMessage(CommandSender cs, String msg) {
        cs.sendMessage(String.format("%s %s", messagePrefix, msg));
    }
    
    public void sendMessage(Player player, BaseComponent... msgs) {
        TextComponent tcPrefix = new TextComponent(messagePrefix+" ");
        BaseComponent[] msgs2 = new BaseComponent[msgs.length + 1];
        msgs2[0] = tcPrefix;
        for(int i=0; i<msgs.length; i++) {
            msgs2[i+1] = msgs[i];
        }
        player.spigot().sendMessage(msgs2);
    }
    
    private void loadConfig() throws IOException {
        File pluginFolder = new File(pluginFolderPath);
        if(!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        File configFile = new File(pluginFolderPath + "/config.json");
        
        ObjectMapper om = new ObjectMapper();
        SimpleModule sm = new SimpleModule();
        if(!configFile.exists()) {
            showConsoleInfo("No configuration file found, creating default one");
            InputStream is = getClass().getResourceAsStream("/config.json");
            FileUtils.copyInputStreamToFile(is, configFile);
        }
        showConsoleInfo("Loading configuration file");
        config = om.readValue(configFile, LiteScoreboardConfig.class);
        showConsoleInfo("Configuration file loaded");
    }

    public LiteScoreboardConfig getMyConfig() {
        return config;
    }

    public LiteScoreboardManager getManager() {
        return manager;
    }

    public Economy getEconomy() {
        return economy;
    }
    
}
