package com.argo.bukkit.util;

import com.argo.bukkit.honeypot.Settings;
import com.firestar.mcbans.mcbans;
import com.firestar.mcbans.mcbans_handler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public class BansHandler {
    private static mcbans_handler mcb;
    private static BansMethod bmethod = BansMethod.VANILLA;

    public static BansMethod setupbanHandler(JavaPlugin plugin) {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("mcbans");
	if(test == null) //Compatibility for older MCBans releases
	    test = plugin.getServer().getPluginManager().getPlugin("MCBans");

        Plugin test2 = plugin.getServer().getPluginManager().getPlugin("EasyBan");
	if(test2 == null) //Compatibility for older EasyBan releases
	    test2 = plugin.getServer().getPluginManager().getPlugin("easyban");
        
        
        if (test != null) {
            mcb = ((mcbans) test).mcb_handler;
	    bmethod = BansMethod.MCBANS;
        } else if (test2 != null) {
	    bmethod = BansMethod.EASYBAN;
        } else {
	    bmethod = BansMethod.VANILLA;
	}
	return bmethod;
    }

    public static void ban(Player p, String sender, String reason) {
        if (Settings.getLocFlag() == true) {
                    Location loc = p.getLocation();
                    int locx = (int) loc.getX();
                    int locy = (int) loc.getY();
                    int locz = (int) loc.getZ();
                    reason = reason +
                            " (" + locx + "," + locy + "," + locz + ")";
                }
	switch(bmethod) {
	    case VANILLA:
		p.kickPlayer(Settings.getPotMsg());
                VanillaBan(p);
		break;
	    case MCBANS:
		MCBan(p, sender, reason, "");
		break;
            case EASYBAN:                                 
                p.kickPlayer(Settings.getPotMsg());
                Eban(p, reason);
		break;
	    case SIMPLEBAN:
		break;
	    default: //NONE
		break;
	}
    }

    public static void kick(Player p, String sender, String reason) {
	switch(bmethod) {
	    case VANILLA:
		p.kickPlayer(reason);
		break;
	    case MCBANS:
		MCBanKick(p, sender, reason);
		break;
            case EASYBAN:
                p.kickPlayer(reason);
		break;
	    case SIMPLEBAN:
                p.kickPlayer(reason);
		break;
	    default: //NONE
                p.kickPlayer(reason);		
		break;
	}
    }

    private static void MCBan(Player player, String sender, String reason, String type) {
	player.kickPlayer(reason); //kick for good measure
	mcb.ban(player.getName(), sender, reason, type);
    }

    private static void MCBanKick(Player player, String sender, String reason) {
	mcb.kick(player.getName(), sender, reason);
    }
    
    private static void VanillaBan(Player player) {
       Bukkit.getServer().dispatchCommand(new ConsoleCommandSender(Bukkit.getServer()),
               "ban "+player.getName());  

    }
    
    private static void Eban(Player player, String reason) {
       Bukkit.getServer().dispatchCommand(new ConsoleCommandSender(Bukkit.getServer()),
               "eban "+player.getName()+" "+reason);  

    }
    
    
}
