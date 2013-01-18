/**
 * 
 */
package com.argo.bukkit.util;

/**
 * KiwiAdmin support, as originally coded, just calls the normal ban
 * and kick commands. So technically KiwiAdmin is 100% compatible with
 * Vanilla (same commands) and there's no need to support it directly.
 * But since we had it before, and with the refactor it's so little code
 * required to keep it, here it is.
 * 
 * @author andune
 *
 */
public class KiwiAdmin extends CommandBanHandler implements BanHandler {
    public KiwiAdmin() {
        super("KiwiAdmin",           // plugin name
                (String) null,       // ban command
                (String) null);      // kick command
    }

}
