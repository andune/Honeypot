package com.argo.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.argo.bukkit.honeypot.Honeypot;
import com.argo.bukkit.honeypot.config.Config;

/** NO LONGER IN USE. Kept for reference while all the old ban system
 * support is converted to new interface.
 *
 * @author Argomirr, andune
 *
 */
public class OldBansHandler {

    @SuppressWarnings("unused")
	private Honeypot plugin;
    private Config config;
    private BanMethod bmethod = BanMethod.VANILLA; // default

    public OldBansHandler(Honeypot plugin) {
    	this.plugin = plugin;
    	config = plugin.getHPConfig();
    }

    public BanMethod setupbanHandler(JavaPlugin plugin) {

        // Check for KiwiAdmin
        Plugin testKA = plugin.getServer().getPluginManager().
                getPlugin("KiwiAdmin");
        if (testKA == null) //Compatibility for older bad-releases
        {
            testKA = plugin.getServer().getPluginManager().
                    getPlugin("kiwiadmin");
        }

        if (testKA != null)
            bmethod = BanMethod.KABANS;
        return bmethod;
    }

    public void ban(Player p, String sender, String reason) {
        // get player location (more useful than HP block loc.)
        if (config.getLocFlag() == true) {
            Location loc = p.getLocation();
            int locx = (int) loc.getX();
            int locy = (int) loc.getY();
            int locz = (int) loc.getZ();
            reason = reason
                    + " (" + locx + "," + locy + "," + locz + ")";
        }
        // use right ban method
        switch (bmethod) {
            case VANILLA:
                // fix for black screen after BAN
                p.kickPlayer(config.getPotMsg());
                VanillaBan(p);
                break;
            case EASYBAN:
                // also fix for black screen after BAN
                p.kickPlayer(config.getPotMsg());
                Eban(p, reason);
                break;
            case KABANS:
                KAban(p, reason);
                break;
            case UBAN:
                Uban(p, reason);
                break;
            default:
                break;
        }
    }

    public void kick(Player p, String sender, String reason) {
        // use right kick method
        switch (bmethod) {
            case VANILLA:
                p.kickPlayer(reason);
                break;
            case EASYBAN:
                EBkick(p, reason);
                break;
            case KABANS:
                KAkick(p, reason);
                break;
            case UBAN:
                Ukick(p, reason);
                break;
            default:
                p.kickPlayer(reason);
                break;
        }
    }

    private void EBkick(Player player, String reason) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                "ekick " + player.getName() + " " + reason);
    }

    private void KAkick(Player player, String reason) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                "kick " + player.getName() + " " + reason);
    }

    private void Ukick(Player player, String reason) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                "kick " + player.getName() + " " + reason);
    }

    private void VanillaBan(Player player) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                "ban " + player.getName());
    }

    private void Eban(Player player, String reason) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                "eban " + player.getName() + " " + reason);
    }

    private void KAban(Player player, String reason) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                "ban " + player.getName() + " " + reason);
    }

    private void Uban(Player player, String reason) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                "ipban " + player.getName() + " " + reason);
    }
}
