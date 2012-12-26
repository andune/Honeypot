/**
 * 
 */
package com.argo.bukkit.util;

import org.bukkit.entity.Player;

import com.argo.bukkit.honeypot.Honeypot;

/**
 * @author andune
 *
 */
public interface BanHandler {
    public void init(Honeypot plugin);
    
    /**
     * Return true if this particular BanHandler is supported on the
     * server.
     * 
     * @return
     */
    public boolean isSupported();
    
    /**
     * Return the name of this handler.
     * 
     * @return
     */
    public String getHandlerName();
    
    /**
     * Get the version of this handler, if any.
     * 
     * @return the version string, or null
     */
    public String getVersion();
    
    public void ban(Player player, String sender, String reason);
    public void kick(Player player, String sender, String reason);
}
