/**
 * 
 */
package com.argo.bukkit.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.argo.bukkit.honeypot.Honeypot;
import com.argo.bukkit.honeypot.config.Config;
import com.mcbans.firestar.mcbans.MCBans;
import com.mcbans.firestar.mcbans.api.MCBansAPI;

/**
 * @author andune
 *
 */
public class MCBans4 implements BanHandler {
    private Config config;
    private Plugin mcbansPlugin;
    private MCBansAPI mcbAPI;
    
    @Override
    public String getHandlerName() {
        return "MCBans";
    }
    
    @Override
    public void init(Honeypot plugin) {
        this.config = plugin.getHPConfig();
        
        mcbansPlugin = plugin.getServer().getPluginManager().getPlugin("MCBans");
        if( mcbansPlugin != null )
            mcbAPI = ((MCBans) mcbansPlugin).getAPI(plugin);
    }
    
    @Override
    public boolean isSupported() {
        return mcbAPI != null;
    }
    
    @Override
    public String getVersion() {
        if( mcbansPlugin != null )
            return mcbansPlugin.getDescription().getVersion();
        else
            return null;
    }

    @Override
    public void ban(Player player, String sender, String reason) {
        if (config.isGlobalBan()){
            mcbAPI.globalBan(player.getName(), player.getUniqueId().toString(), sender, "", reason);
        }else{
            mcbAPI.localBan(player.getName(), player.getUniqueId().toString(), sender, "", reason);
        }

        player.kickPlayer(config.getPotMsg());
    }

    @Override
    public void kick(Player player, String sender, String reason) {
        mcbAPI.kick(player.getName(), player.getUniqueId().toString(), sender, "", reason);
        player.kickPlayer(config.getPotMsg());
    }

}
