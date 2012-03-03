package com.argo.bukkit.honeypot.listener;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.argo.bukkit.honeypot.HoneyStack;
import com.argo.bukkit.honeypot.Honeyfarm;
import com.argo.bukkit.honeypot.Honeypot;
import com.argo.bukkit.honeypot.config.Config;
import com.argo.bukkit.util.BansHandler;

public class HoneypotBlockListener implements Listener {

    private Honeypot plugin;
    private HoneyStack honeyStack;
    private Config config;

    public HoneypotBlockListener(Honeypot instance) {
        plugin = instance;
        config = plugin.getHPConfig();
        honeyStack = plugin.getHoneyStack();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (Honeyfarm.isPot(block.getLocation())) {
            Player player = event.getPlayer();
            if( !plugin.hasPermission(player, "honeypot.break") ) {
            	boolean usingHoneyPoints = false;
            	int blockPoints = 0;
            	int maxPoints = 0;
            	
            	// if offensePoints are defined, we use a pointMap to determine how many points this
            	// broken block is worth.  This allows some blocks to be worth more, so perhaps you
            	// can instaban if they steal honeypot diamonds, but maybe it takes 3 offenses if
            	// they're griefing your wooden structure or flower field or whatever.
            	if (config.getOffensePoints() > 0) {
            		usingHoneyPoints = true;
            		maxPoints = config.getOffensePoints();
            		blockPoints = 1;		// default point value is 1 if nothing is defined in the map
            		
            		// now lookup this block type in the map
            		Integer blockId = block.getTypeId();
            		Map<Integer, Integer> typeMap = config.getBlockPointMap();
            		if( typeMap != null ) {
            			Integer points = typeMap.get(blockId);
            			final String materialName = Material.getMaterial(blockId).toString();
            			plugin.log("points for blockId "+blockId+" ("+materialName+") = "+points);

            			if( points != null )
            				blockPoints = points.intValue();
            		}
            	}
            	// otherwise we use offenseCount if defined.
            	else if (config.getOffenseCount() > 1) {
            		usingHoneyPoints = true;
            		maxPoints = config.getOffenseCount();
            		blockPoints = 1;			// each break is worth 1 point
            	}
            	
            	if( usingHoneyPoints ) {
                    String playerName = player.getName();
                    honeyStack.breakHoneypot(playerName, block.getState(), blockPoints);

                    int points = honeyStack.getHoneyPoints(player.getName());

                    // log to both Honeypot logfile and system logs
                    String logMessage = "player " + playerName + 
                            " broke HoneyPot block at "
                            + Honeypot.prettyPrintLocation(block.getLocation()) 
                            + ", break/place count/points: " + points
                            + " ---- "+ BansHandler.methodName;

                    plugin.log(logMessage);
                    if (config.getLogFlag()) {
                        Honeyfarm.log(logMessage);
                    }

                    if (points < maxPoints) {
                        return;		// do no further processing if they haven't reached the limit yet
                    } else {
                        honeyStack.rollBack(playerName);
                    }
                }
            	
            	// if make it here, there is no offensePoint or offenseCount defined, or this
            	// player has exceeded the limits and it's time for action.

                event.setCancelled(true);

                if (config.getKickFlag()) {
                    plugin.getBansHandler().kick(player, config.getPotSender(), 
                            config.getPotMsg());
                } else if (config.getBanFlag()) {
                	plugin.getBansHandler().ban(player, config.getPotSender(),
                            config.getPotReason());
                }

    			final String materialName = Material.getMaterial(block.getTypeId()).toString();
                String logMessage = "Player " + player.getName() + 
                        " was caught breaking a block in a candy bag (material="+materialName
                        +") at location "
                        + Honeypot.prettyPrintLocation(block.getLocation()) +
                        ".";

                plugin.log(logMessage);
                if (config.getLogFlag()) {
                    Honeyfarm.log(logMessage);
                }

                if (config.getShoutFlag()) {
                    plugin.getServer().broadcastMessage(ChatColor.DARK_RED + 
                            "[CandyBag]" + ChatColor.GRAY + " Player " + 
                            ChatColor.DARK_RED + player.getName() + 
                            ChatColor.GRAY + " was caught breaking a "
                            + "block in candy bag.");
                }
            } else {
                player.sendMessage(ChatColor.GREEN + "Honeypot removed.");
                Honeyfarm.removePot(event.getBlock().getLocation());
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (Honeyfarm.isPot(block.getLocation())) {
            Player player = event.getPlayer();
            if( !plugin.hasPermission(player, "honeypot.break") ) {
            	boolean usingHoneyPoints = false;
            	int blockPoints = 0;
            	int maxPoints = 0;
            	
            	// if offensePoints are defined, we use a pointMap to determine how many points this
            	// broken block is worth.  This allows some blocks to be worth more, so perhaps you
            	// can instaban if they steal honeypot diamonds, but maybe it takes 3 offenses if
            	// they're griefing your wooden structure or flower field or whatever.
            	if (config.getOffensePoints() > 0) {
            		usingHoneyPoints = true;
            		maxPoints = config.getOffensePoints();
            		blockPoints = 1;		// default point value is 1 if nothing is defined in the map
            		
            		// now lookup this block type in the map
            		Integer blockId = block.getTypeId();
            		Map<Integer, Integer> typeMap = config.getBlockPointMap();
            		if( typeMap != null ) {
            			Integer points = typeMap.get(blockId);
            			final String materialName = Material.getMaterial(blockId).toString();
            			plugin.log("points for blockId "+blockId+" ("+materialName+") = "+points);

            			if( points != null )
            				blockPoints = points.intValue();
            		}
            	}
            	// otherwise we use offenseCount if defined.
            	else if (config.getOffenseCount() > 1) {
            		usingHoneyPoints = true;
            		maxPoints = config.getOffenseCount();
            		blockPoints = 1;			// each break is worth 1 point
            	}
            	
            	if( usingHoneyPoints ) {
                    String playerName = player.getName();
                    honeyStack.breakHoneypot(playerName, block.getState(), blockPoints);

                    int points = honeyStack.getHoneyPoints(player.getName());

                    // log to both Honeypot logfile and system logs
                    String logMessage = "player " + playerName + 
                            " placed block at "
                            + Honeypot.prettyPrintLocation(block.getLocation()) 
                            + ", break/place count/points: " + points
                            + " ----- " + BansHandler.methodName;

                    plugin.log(logMessage);
                    if (config.getLogFlag()) {
                        Honeyfarm.log(logMessage);
                    }

                    if (points < maxPoints) {
                        return;		// do no further processing if they haven't reached the limit yet
                    } else {
                        honeyStack.rollBack(playerName);
                    }
                }
            	
            	// if make it here, there is no offensePoint or offenseCount defined, or this
            	// player has exceeded the limits and it's time for action.

                event.setCancelled(true);

                if (config.getKickFlag()) {
                    plugin.getBansHandler().kick(player, config.getPotSender(), 
                            config.getBagMsg());
                } else if (config.getBanFlag()) {
                	plugin.getBansHandler().ban(player, config.getPotSender(),
                            config.getBagReason());
                }

    			final String materialName = Material.getMaterial(block.getTypeId()).toString();
                String logMessage = "Player " + player.getName() + 
                        " was caught placing a block in a candy bag (material="+materialName
                        +") at location "
                        + Honeypot.prettyPrintLocation(block.getLocation()) +
                        ".";

                plugin.log(logMessage);
                if (config.getLogFlag()) {
                    Honeyfarm.log(logMessage);
                }

                if (config.getShoutFlag()) {
                    plugin.getServer().broadcastMessage(ChatColor.DARK_RED + 
                            "[CandyBag]" + ChatColor.GRAY + " Player " + 
                            ChatColor.DARK_RED + player.getName() + 
                            ChatColor.GRAY + " was caught placing a "
                            + "block in a candy bag.");
                }
            }
        }
    }
}
