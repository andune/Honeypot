/**
 * 
 */
package com.argo.bukkit.honeypot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

/** Class to manage HoneyPot breaks when the minimum offense count is > 1.  So if they are
 * allowed to break 2+ HoneyPot blocks before we take action, this keeps track of the number
 * of breaks a player has so far as well as what blocks they've broken, so we can roll
 * back the damage.
 * 
 * @author morganm
 *
 */
public class HoneyStack implements Runnable {
	private static final Logger log = Honeypot.log;
	
	// keeps track of the total honeypot points this player has accumulated.  These
	// are NOT the kind of points you want to be winning.  :P
	private HashMap<String, Integer> honeypotPoints = new HashMap<String, Integer>();
	
	private HashMap<String, ArrayList<Location>> playerHoneyBreaks = new HashMap<String, ArrayList<Location>>();
	private HashMap<Location, BlockState> honeyBlocks = new HashMap<Location, BlockState>();
	private HashMap<String, Long> playerLogouts = new HashMap<String, Long>();
	
	public Integer getHoneyPoints(String playerName) {
		return honeypotPoints.get(playerName);
	}
	
	/** Called when a player breaks a HoneyPot, to increment their offense counter as well as
	 * record the broken block so we can roll it back later.
	 * 
	 * @param playerName
	 * @param bs
	 * @param points how many points this block is worth
	 */
	public void breakHoneypot(String playerName, BlockState bs, int points) {
		Location l = bs.getBlock().getLocation();
		if( honeyBlocks.get(l) == null )
			honeyBlocks.put(l, bs);
		
		Integer count = honeypotPoints.get(playerName);
		if( count == null )
			count = 0;
		count += points;
		honeypotPoints.put(playerName, Integer.valueOf(count));
		
		ArrayList<Location> list = playerHoneyBreaks.get(playerName);
		if( list == null ) {
			list = new ArrayList<Location>();
			playerHoneyBreaks.put(playerName, list);
		}
		
		list.add(l);
	}
	
	/** Roll back any HoneyPot blocks that were broken by a given player.
	 * 
	 * @param playerName
	 */
	public void rollBack(String playerName) {
		ArrayList<Location> locations = playerHoneyBreaks.get(playerName);
		if( locations == null )
			return;
		
		for(Location location : locations) {
			// we try/catch so that even if one block fails (really shouldn't ever happen), the other
			// ones can still go through.
			try {
				BlockState bs = honeyBlocks.get(location);
				bs.update(true);
				log.info("[Candybag] successfully rolled back Candybag damage from player "+playerName+" at location "+Honeypot.prettyPrintLocation(location));
			}
			catch(Exception e) {
				log.warning("[Candybag] error rolling back Candybag damage at location "+Honeypot.prettyPrintLocation(location));
				e.printStackTrace();
			}
		}
		
		playerHoneyBreaks.remove(playerName);
		
		// NOTE: we intentionally do NOT reset the "honeypotCount" for this player. This way if an admin
		// is choosing to use "kick" intead of "ban", the player is insta-kicked if they log 	back in and
		// try to break more honeypots, since they're already over the minimum limit.
	}
	
	/** Called to rollback all HoneyPot damage, this is used if the plugin/server is shutdown
	 * to prevent the damage from being permanent.
	 * 
	 */
	public void rollBackAll() {
		Set<String> players = playerHoneyBreaks.keySet();
		if( players == null )
			return;
		
		for(String player : players) {
			rollBack(player);
		}
	}
	
	/** When a player logs out, we record their logout time, which starts a countdown to when we
	 * automatically clean up their Honeypot blocks.  Note we don't care if they log back in later,
	 * once they logout for the first time, it's an automatic trigger to do the Honeypot cleanup.
	 * 
	 * @param playerName
	 */
	public void playerLogout(String playerName) {
		Integer points = getHoneyPoints(playerName);
		
		if( points != null && points > 1 ) {
			if( playerLogouts.get(playerName) == null )
				playerLogouts.put(playerName, new Long(System.currentTimeMillis()/1000));
		}
	}
	
	/** Called by the Bukkit scheduler to run every so often.  Check current logout list and
	 * looks for any logouts that broke Honeypot blocks, if they've been logged out for more
	 * than 5 minutes, reset the Honeypot breaks for that player.
	 * 
	 */
	public void run() {
		if( playerLogouts.isEmpty() )
			return;
		
		for(Iterator<String> i = playerLogouts.keySet().iterator(); i.hasNext();) {
			String playerName = i.next();
			Long logoutTime = playerLogouts.get(playerName);
			if( ((System.currentTimeMillis()/1000) - logoutTime) > 300 ) {	// 5 minutes	
				rollBack(playerName);
				i.remove();
			}
		}
	}
}
