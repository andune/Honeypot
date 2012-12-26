/**
 * 
 */
package com.argo.bukkit.util;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.argo.bukkit.honeypot.Honeypot;

/**
 * @author andune
 *
 */
public class UltraBan implements BanHandler {
    private Server server;
    private Plugin ultrabanPlugin;
    
    @Override
    public String getHandlerName() {
        return "UltraBan";
    }

    @Override
    public void init(Honeypot honeypotPlugin) {
        this.server = honeypotPlugin.getServer();
        ultrabanPlugin = server.getPluginManager().getPlugin("UltraBan");
        if( ultrabanPlugin == null ) // Compatibility for old releases
            ultrabanPlugin = server.getPluginManager().getPlugin("ultraban");
    }
    
    @Override
    public boolean isSupported() {
        return ultrabanPlugin != null;
    }
    
    @Override
    public String getVersion() {
        if( ultrabanPlugin != null )
            return ultrabanPlugin.getDescription().getVersion();
        else
            return null;
    }

    @Override
    public void ban(Player player, String sender, String reason) {
        server.dispatchCommand(server.getConsoleSender(),
                "eban " + player.getName() + " " + reason);
    }

    @Override
    public void kick(Player player, String sender, String reason) {
        server.dispatchCommand(server.getConsoleSender(),
                "ekick " + player.getName() + " " + reason);
    }
}
