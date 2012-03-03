
package com.argo.bukkit.honeypot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.argo.util.TextFileHandler;

/** Originally this class just kept a List of all HoneyPot blocks and iterated over that list for
 * every block break.  This is probably actually very efficient when there are a small number of
 * blocks to iterate over, but on my server I have over 300 HoneyPot blocks (it's a large HoneyPot
 * statue).  That meant every time a player broke a block anywhere on the server, this mod was iterating
 * over a list of 300 items to check if it was on the HoneyPot list.
 * 
 * So my first inclination was just to put the blocks in a HashMap and instead of doing 300 block comparisons,
 * this mod would just do one hash lookup - certainly more efficient than running 300 block comparisons individually.
 * 
 * However, for the use case where folks are using < 5 HoneyPot blocks, the ArrayList is probably still more
 * efficient than a Hash calculation and lookup. So I kept that, and we choose which to use based on how many
 * Honeypot blocks there are.
 * 
 * One additional speed improvement: On my server, at least, all the HoneyPot blocks are in one spot. So even
 * more efficient than having to do the Hash calculation/lookup for every block break is just to check if
 * the x/y/z coordinates are outside the "Honeypot region" (where all the Honeypot blocks are located). This
 * results in a very fast short-circuit of the majority of block breaks on the server: for example, if the
 * HoneyPot's are all located between X:100 and X:120, then any block breaks outside of those bounds only have
 * to do a few integer checks and then we're done: even faster than a Hash calculation/lookup.
 * 
 * @author Argomirr, morganm
 *
 */
public class Honeyfarm {
	private static final Logger log = Logger.getLogger(Honeyfarm.class.toString());
	
	/* This sets the value at which the HashMap checking kicks in instead of List checks. I haven't
	 * actually tested this for exact tuning, but the assumption is that running a small amount of
	 * List checks (default is 5) is going to be faster than doing a Location.hashCode() and subsequent
	 * hash lookup.
	 */
	private static final int HASH_CUTOFF_BLOCKS = 5;
	
	private static final String potListPath = "plugins/CandyBag/list.ncsv";
	private static String logPath;

	private static List<Location> potsList = new ArrayList<Location>();
	private static Map<Location, Boolean> potsMap = new HashMap<Location, Boolean>();
	private static List<String> potSelectUsers = new ArrayList<String>();
	private static Set<CuboidRegion> regions = new HashSet<CuboidRegion>();
	
	private static int minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
	private static World boundsWorld = null;
	private static boolean useHash = false;
	private static boolean useBoundsChecking = false;
	
	public static void setLogPath(String logPath) {
		Honeyfarm.logPath = logPath;
	}
	
	/** Loads data from the backing file store.
	 * 
	 * @return
	 */
	public static boolean refreshData() {
		TextFileHandler r = new TextFileHandler(potListPath);
		potsMap.clear();
		potsList.clear();
		try {
			List<String> list = r.readLines();
			String[] coord;
			
			int line = 0;
			while(!list.isEmpty()) {
				line++;
				
				String thisLine = list.remove(0);
				
				// deal with region lines
				if( thisLine.startsWith("region:") ) {
					String regionString = thisLine.substring(7);
					CuboidRegion region = CuboidRegion.importFromString(regionString);
					regions.add(region);
				}
				// standard single block line
				else {
					coord = thisLine.split(",");
					if(coord.length == 4)
					{
						World w = Honeypot.getCurrentInstance().getServer().getWorld(coord[0]);
						if( w == null ) {
							log.warning("[CandyBag] Skipping line "+line+": no World defined for world "+coord[0]);
							continue;
						}
	
						potsList.add(new Location(w, new Double(coord[1]), new Double(coord[2]), new Double(coord[3])));
						potsMap.put(new Location(w, new Double(coord[1]), new Double(coord[2]), new Double(coord[3])), Boolean.TRUE);
					}
					else if(coord.length == 3) {
						// for backwards compatibility, if no world specified, just assume "world"
						World w = Honeypot.getCurrentInstance().getServer().getWorld("world");
						if( w == null ) {
							log.warning("[CandyBag] Skipping line "+line+": line using old-style definition, tried to assume for world \"world\", but no \"world\" found");
							continue;
						}
	
						potsList.add(new Location(w, new Double(coord[0]), new Double(coord[1]), new Double(coord[2])));
						potsMap.put(new Location(w, new Double(coord[0]), new Double(coord[1]), new Double(coord[2])), Boolean.TRUE);
					}
					else
						log.warning("[CandyyBag] Skipping line "+line+": incorrect number of data elements");
				}
			}
			
			calculateBounds();
			
			if( potsList.size() > HASH_CUTOFF_BLOCKS )
				useHash = true;

		} catch (FileNotFoundException ex) {
			return false;
		} catch (IOException ex) {
			return false;
		}
		return true;
	}
	
	/** For efficiency, we do a 1-time calculation of the maximum x/y/z boundaries of all Honeypot blocks.
	 * Any block breaks outside these bounds will be ignored.
	 */
	private static void calculateBounds() {
		// start by assuming boundsChecking is true, change it to false if we find a condition otherwise
		useBoundsChecking = true;

		for(Location l : potsMap.keySet()) {
			int x, y, z;
			x = l.getBlockX();
			y = l.getBlockY();
			z = l.getBlockZ();
			
			// Check to see if the world of this block differs from the last, if so, we disable
			// bounds checking and stop checking the rest of the blocks.
			World w = l.getWorld();
			if( boundsWorld != null && !w.equals(boundsWorld) ) {
				useBoundsChecking = false;
				break;
			}
			boundsWorld = w;
			
			if( x < minX || minX == 0 )
				minX = x;
			else if( x > maxX || maxX == 0)
				maxX = x;
			
			if( y < minY || minY == 0)
				minY = y;
			else if( y > maxY || maxY == 0)
				maxY = y;

			if( z < minZ || minZ == 0)
				minZ = z;
			else if( z > maxZ || maxZ == 0)
				maxZ = z;
		}
		
		/* debug info
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb);
		f.format("minX = %d, maxX = %d, minY = %d, maxY = %d, minZ = %d, maxZ = %d",
				minX, maxX, minY, maxY, minZ, maxZ);
		log.info(sb.toString());
		*/
		
		// we only use boundsChecking if the honeypot blocks are all located in same general area
		if( (maxX - minX < 300) && (maxZ - minZ < 300) && (maxY - minY < 30) )
			useBoundsChecking = false;
	}
	
	public static boolean saveData() {
		TextFileHandler r = new TextFileHandler(potListPath);

		List<String> tmp = new ArrayList<String>();

		for(CuboidRegion region : regions) {
			tmp.add("region:"+region.exportAsString());
		}
		
		for(Location loc : potsMap.keySet()) {
			tmp.add(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ());
		}
		try {
			r.writeLines(tmp);
		} catch (IOException ex) {
			return false;
		}
		
		return true;
	}

	public static void log(String line) {
		TextFileHandler r = new TextFileHandler(logPath);
		try {
			r.appendLine("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] " + line);
		} catch (IOException ex) {}
	}

	public static boolean isPot(Location loc) {
		int x, y, z;
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		
		// check regions first, if any
		if( regions.size() > 0 ) {
			for(CuboidRegion region : regions) {
				if( region.contains(loc) ) {
					return true;
				}
			}
		}
		
		// do efficient bounds checking. if the location is outside of our calculated honeypot bounds, it's
		// obviously not a honeypot block, no need to do a Hash lookup for the block.
		if( useBoundsChecking ) {
			if( !loc.getWorld().equals(boundsWorld) )
				return false;
			
			if( x < minX && x > maxX && y < minY && y > maxY && z < minZ && z > maxZ )
				return false;
		}
		
		if( useHash ) {
//			log.debug("doing isPot hash lookup");
			Boolean b = potsMap.get(loc);
			if( Boolean.TRUE.equals(b) )
				return true;
			else
				return false;
		}
		else {
			if( potsList.contains(loc) )
				return true;
			else
				return false;
		}
	}

	public static void createPot(CuboidRegion region) {
		regions.add(region);
	}
	
	public static void createPot(Location loc) {
		if(!isPot(loc)) {
			potsList.add(loc);
			potsMap.put(loc, Boolean.TRUE);
		}
	}

	public static void removePot(Location loc) {
		potsList.remove(loc);
		potsMap.remove(loc);
	}

	public static void setPotSelect(Player player, boolean state) {
		if(state) {
			potSelectUsers.add(player.getName());
		} else {
			potSelectUsers.remove(player.getName());
		}
	}

	public static boolean getPotSelect(Player player) {
		if(potSelectUsers.contains(player.getName()))
			return true;
		else
			return false;
	}
}
