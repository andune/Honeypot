/**
 * 
 */
package com.argo.bukkit.util;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.argo.bukkit.honeypot.Honeypot;
import com.argo.bukkit.honeypot.config.Config;

/**
 * @author andune
 *
 */
public class EasyBan implements BanHandler {
    private Server server;
    private Config config;
    private Plugin easybanPlugin;
    
    @Override
    public String getHandlerName() {
        return "EasyBan";
    }

    @Override
    public void init(Honeypot plugin) {
        this.server = plugin.getServer();
        this.config = plugin.getHPConfig();
        
        easybanPlugin = server.getPluginManager().getPlugin("EasyBan");
        if( easybanPlugin == null ) // Compatibility for oldEasyBan release
            easybanPlugin = server.getPluginManager().getPlugin("easyban");
    }
    
    @Override
    public boolean isSupported() {
        return easybanPlugin != null;
    }
    
    @Override
    public String getVersion() {
        if( easybanPlugin != null )
            return easybanPlugin.getDescription().getVersion();
        else
            return null;
    }

    @Override
    public void ban(Player player, String sender, String reason) {
        player.kickPlayer(config.getPotMsg());
        server.dispatchCommand(server.getConsoleSender(),
                "eban " + player.getName() + " " + reason);
    }

    @Override
    public void kick(Player player, String sender, String reason) {
        server.dispatchCommand(server.getConsoleSender(),
                "ekick " + player.getName() + " " + reason);
    }
}
