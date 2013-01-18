/**
 * 
 */
package com.argo.bukkit.honeypot;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;

/**
 * @author andune
 *
 */
public class WorldEditIntegration {
	private final Plugin plugin;

	public WorldEditIntegration(Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Create a new HoneyPot region using the WorldGuard region currently selected
	 * by the player.
	 * 
	 * @param player
	 */
	public void createNewHoneypotRegion(final Player player) {
        Plugin plug = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        if( plug != null ) {
            WorldEditPlugin worldEdit = (WorldEditPlugin) plug;
            try {
                Region region = worldEdit.getSession(player).getSelection(worldEdit.getSession(player).getSelectionWorld());
                CuboidRegion cuboidRegion = new CuboidRegion(region, player.getWorld());
                Honeyfarm.createPot(cuboidRegion);
                Honeyfarm.saveData();
                player.sendMessage(ChatColor.DARK_RED+"WorldEdit region recorded as a Honeypot");
            }
            catch(IncompleteRegionException ire) {
                player.sendMessage(ChatColor.DARK_RED+"WorldEdit region incomplete");
            }
        }
		
	}
}
