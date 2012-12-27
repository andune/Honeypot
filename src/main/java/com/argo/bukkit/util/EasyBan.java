/**
 * 
 */
package com.argo.bukkit.util;


/**
 * @author andune
 *
 */
public class EasyBan extends CommandBanHandler implements BanHandler {
    public EasyBan() {
        super("EasyBan",                    // plugin name
                "eban %player% %reason%",   // ban command
                "ekick %player% %reason%"); // kick command
    }
}
