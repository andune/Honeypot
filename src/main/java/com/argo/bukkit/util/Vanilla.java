/**
 * 
 */
package com.argo.bukkit.util;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.argo.bukkit.honeypot.Honeypot;
import com.argo.bukkit.honeypot.config.Config;

/**
 * @author andune
 *
 */
public class Vanilla implements BanHandler {
    private Server server;
    private Config config;
    
    @Override
    public String getHandlerName() {
        return "Vanilla";
    }

    @Override
    public void init(Honeypot plugin) {
        this.server = plugin.getServer();
        this.config = plugin.getHPConfig();
    }

    @Override
    public boolean isSupported() {
        return true;    // vanilla is always supported
    }
    
    @Override
    public String getVersion() {
        return "Vanilla Ban (Bukkit version "+server.getBukkitVersion()+")";
    }

    @Override
    public void ban(Player player, String sender, String reason) {
        player.kickPlayer(config.getPotMsg());
        server.dispatchCommand(server.getConsoleSender(),
                "ban " + player.getName());
    }

    @Override
    public void kick(Player player, String sender, String reason) {
        player.kickPlayer(reason);
    }

}
