/**
 * 
 */
package com.argo.bukkit.util;


/**
 * Not entirely sure the ban command here seems right, but, it's what has
 * been in MCBans for a long time now. If someone uses UltraBan and knows
 * it should be different, please fix it and send me a gitpull.
 * 
 * @author andune
 *
 */
public class UltraBan extends CommandBanHandler implements BanHandler {
    public UltraBan() {
        super("UltraBan",                       // plugin name
                "ipban %player% %reason%",      // ban command
                "kick %player% %reason%");      // kick command
    }
}
