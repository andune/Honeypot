
package com.argo.bukkit.honeypot;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;

public class CmdHoneypot implements CommandExecutor {
	private Honeypot plugin;

    public CmdHoneypot(Honeypot instance) {
    	plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String cmdName, String[] args) {
        // must have 'create' permission to access any commands
        if( !plugin.hasPermission(sender, "honeypot.create") ) {
            sender.sendMessage(ChatColor.RED + "You are not allowed to use this command.");
            return true;
        }
        
        if(args.length == 0) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(Honeyfarm.getPotSelect(player)) {
                    player.sendMessage(ChatColor.GREEN + "Honeypot creation finished.");
                    Honeyfarm.setPotSelect(player, false);
                } else {
                    player.sendMessage(ChatColor.GREEN + "Right click a block with a " + plugin.getHPConfig().getToolId() + " to create a honeypot. When finished, use /hp again.");
                    Honeyfarm.setPotSelect(player, true);
                }
            } else {
                sender.sendMessage("Sorry, this command can only be used by players.");
            }
        } else if(args.length == 1) {
            if( args[0].equalsIgnoreCase("region") ) {
                if( sender instanceof Player ) {
                    Player player = (Player) sender;
                    Plugin plug = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
                    if( plug != null ) {
                        WorldEditPlugin worldEdit = (WorldEditPlugin) plug;
                        try {
                            Region region = worldEdit.getSession(player).getSelection(worldEdit.getSession(player).getSelectionWorld());
                            CuboidRegion cuboidRegion = new CuboidRegion(region, player.getWorld());
                            Honeyfarm.createPot(cuboidRegion);
                            Honeyfarm.saveData();
                            sender.sendMessage(ChatColor.DARK_RED+"WorldEdit region recorded as a Honeypot");
                        }
                        catch(IncompleteRegionException ire) {
                            sender.sendMessage(ChatColor.DARK_RED+"WorldEdit region incomplete");
                        }
                    }
                } else {
                    sender.sendMessage("Sorry, this command can only be used by players.");
                }
            }
            else if(args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("s")) {
                sender.sendMessage(ChatColor.GREEN + "Saving honeypot data...");
                if(!Honeyfarm.saveData()) {
                    sender.sendMessage(ChatColor.DARK_RED + "Failed to save data.");
                }
            }
            // added to allow live-reloading of data file  -morganm 5/20/11
            else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
                sender.sendMessage(ChatColor.GREEN + "Reloading honeypot data from saved file...");

                if(!Honeyfarm.refreshData()) {
                    sender.sendMessage(ChatColor.DARK_RED + "Failed to load data.");
                }
            }
        }
        return true;
    }

}
