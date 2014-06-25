/**
 * 
 */
package com.argo.bukkit.util;


/**
 * MaxBans: http://dev.bukkit.org/server-mods/maxbans/
 * Github:  http://github.com/netherfoam/MaxBans/
 * I'm just going to assume it's actually this simple.
 * @author netherfoam
 */
public class MaxBans extends CommandBanHandler implements BanHandler {
    public EasyBan() {
        super("MaxBans",                    // plugin name
                "ban %player% %reason%",   // ban command
                "kick %player% %reason%"); // kick command
    }
}
