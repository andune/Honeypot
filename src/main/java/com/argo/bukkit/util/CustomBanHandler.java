/**
 * 
 */
package com.argo.bukkit.util;

import com.argo.bukkit.honeypot.Honeypot;

/**
 * @author andune
 *
 */
public class CustomBanHandler extends CommandBanHandler implements BanHandler {
    public CustomBanHandler() {
        super("Custom", null, null);
    }
    
    @Override
    public void init(Honeypot plugin) {
        super.init(plugin);
        super.banCmd = config.getCustomBanCommand();
        super.kickCmd = config.getCustomKickCommand();
    }
    
    /**
     * This handler is only enabled if explicitly set as such by the admin.
     */
    @Override
    public boolean isSupported() {
        return getHandlerName().equalsIgnoreCase(config.getBanSystem().toString());
    }
}
