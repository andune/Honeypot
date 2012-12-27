/**
 * 
 */
package com.argo.bukkit.util;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.argo.bukkit.honeypot.Honeypot;
import com.argo.bukkit.honeypot.config.Config;

/**
 * @author andune
 *
 */
public abstract class CommandBanHandler implements BanHandler {
    protected Logger log;
    protected String handlerName;
    protected Server server;
    protected Config config;
    protected Plugin banPlugin;
    protected String banCmd;
    protected String kickCmd;
    
    /**
     * 
     * @param handlerName the plugin name, such as "MCBans". This must match the
     * name that the plugin uses and respond to for pluginManager.getPlugin().
     * This is also the name that will be used in the banSystem config property.
     * @param banCmd the command to run to ban a player. %player% and %reason%
     * will be substituted. If null, default command "ban" will be used.
     * @param kickCMd the command to run to kick the player. If null, default
     * Bukkit kick will be used
     */
    protected CommandBanHandler(String handlerName, String banCmd, String kickCmd) {
        this.handlerName = handlerName;
        this.banCmd = banCmd;
        this.kickCmd = kickCmd;
        
        if( this.banCmd == null )
            this.banCmd = "ban %player% %reason%";
    }

    @Override
    public String getHandlerName() {
        return handlerName;
    }

    @Override
    public void init(Honeypot plugin) {
        this.server = plugin.getServer();
        this.config = plugin.getHPConfig();
        this.log = plugin.getLogger();
        
        banPlugin = server.getPluginManager().getPlugin(handlerName);
    }

    @Override
    public boolean isSupported() {
        return banPlugin != null;
    }

    @Override
    public String getVersion() {
        if( banPlugin != null )
            return banPlugin.getDescription().getVersion();
        else
            return null;
    }

    @Override
    public void ban(Player player, String sender, String reason) {
        String cmd = banCmd.replaceAll("%player%", player.getName());
        cmd = cmd.replaceAll("%reason%", reason);
        log.info("running ban command: "+cmd);
        server.dispatchCommand(server.getConsoleSender(), cmd);
        if( player.isOnline() )
            player.kickPlayer(config.getPotMsg());
    }

    @Override
    public void kick(Player player, String sender, String reason) {
        if( kickCmd != null ) {
            String cmd = kickCmd.replaceAll("%player%", player.getName());
            cmd = cmd.replaceAll("%reason%", reason);
            log.info("running kick command: "+cmd);
            server.dispatchCommand(server.getConsoleSender(), cmd);
        }
        // otherwise just use default Bukkit kick
        else
            player.kickPlayer(reason);
    }

}
