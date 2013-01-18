/**
 * 
 */
package com.argo.bukkit.honeypot.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.argo.bukkit.honeypot.Honeypot;

/**
 * @author morganm
 *
 */
public class YMLFile implements Config {
	private static final Logger log = Honeypot.log;
	
	private JavaPlugin plugin;
	private FileConfiguration bukkitConfig;
	private Map<Integer, Integer> pointKeyMap;
	
	@Override
	public void load(final JavaPlugin plugin) {
		this.plugin = plugin;
		
		// this forces Bukkit to load the config
		bukkitConfig = plugin.getConfig();
	}
	
	@Override
	public void save() throws Exception {
		plugin.saveConfig();
	}
	
    @Override
    public String getBanSystem() {
        return bukkitConfig.getString("banSystem", "Any");
    }
    
    @Override
    public String[] getCustomBanCommands() {
    	// Look for multi-string version first, if none, fall back to
    	// single-string version or default.
    	List<String> cmds = bukkitConfig.getStringList("customBanCommand");
    	
    	if( cmds == null || cmds.isEmpty() ) {
    		return new String[] { bukkitConfig.getString("customBanCommand", "ban %player% %reason%") };
    	}
    	else {
    		return cmds.toArray(new String[] {});
    	}
    }
    
    @Override
    public String[] getCustomKickCommands() {
    	// Look for multi-string version first, if none, fall back to
    	// single-string version or default.
    	List<String> cmds = bukkitConfig.getStringList("customKickCommand");

    	if( cmds == null || cmds.isEmpty() ) {
    		return new String[] { bukkitConfig.getString("customKickCommand", "kick %player% %reason%") };
    	}
    	else {
    		return cmds.toArray(new String[] {});
    	}
    }

	@Override
	public int getOffenseCount() {
		return bukkitConfig.getInt("offenseCount", 1);
	}

	@Override
	public String getPotMsg() {
		return bukkitConfig.getString("honeypotKickMessage", defaultHoneypotMsg);
	}

	@Override
	public String getPotReason() {
		return bukkitConfig.getString("honeypotBanReason", defaultHoneypotBanReason);
	}

	@Override
	public String getPotSender() {
		return bukkitConfig.getString("honeypotKickBanSender", defaultKickBanSender);
	}

	@Override
	public int getToolId() {
		return bukkitConfig.getInt("toolID", defaultToolID);
	}

	@Override
	public boolean getKickFlag() {
		return bukkitConfig.getBoolean("kickFlag", true);
	}

	@Override
	public boolean getBanFlag() {
		return bukkitConfig.getBoolean("banFlag", false);
	}

	@Override
	public boolean getLocFlag() {
		return bukkitConfig.getBoolean("banLogLocation", true);
	}

	@Override
	public boolean getLogFlag() {
		return bukkitConfig.getBoolean("banLog", true);
	}

	@Override
	public boolean getShoutFlag() {
		return bukkitConfig.getBoolean("shoutFlag", true);
	}

	@Override
	public String getLogPath() {
		return bukkitConfig.getString("logPath", defaultLogPath);
	}

	@Override
	public int getOffensePoints() {
		return bukkitConfig.getInt("offensePoints", 0);
	}
	
	@Override
	public boolean isGlobalBan() {
		return bukkitConfig.getBoolean("globalBan", false);
	}

	@Override
	public Map<Integer, Integer> getBlockPointMap() {
		// pointKeyMap is cached on this methods first call. So if it's null, build the cache
		// now, otherwise we just return the cached map.
		if( pointKeyMap == null ) {
			pointKeyMap = new HashMap<Integer, Integer>();
			
			ConfigurationSection section = bukkitConfig.getConfigurationSection("offensePointMap");
			if( section == null )
				return pointKeyMap;
			
			Set<String> configPointKeys = section.getKeys(false);
			if( configPointKeys == null || configPointKeys.isEmpty() )
				return pointKeyMap;

			for(Iterator<String> i = configPointKeys.iterator(); i.hasNext();) {
				Object o = i.next();
				
				int keyInt = -1;
				
				// shouldn't be possible since Configuration.getKeys() is supposed to return List<String>,
				// but it turns out it's can/does return Integers in the list.  Oops.
				if( o instanceof Integer ) {
					// even though we can read the integer here, the call below to "getInt()" will fail since
					// Bukkit requires the key value be a string, so print a nice error message to the
					// admin to tell them to fix their config file.
					log.warning("Found raw integer key value offensePointMap."+o+", you must wrap the integer in quotes in order for it to work properly, like so: \""+o+"\"");
					continue;
				}
				else if( o instanceof String ) {
					try {
						keyInt = Integer.valueOf((String) o);
					}
					catch(NumberFormatException e) {
						log.warning("[Honeypot] invalid key value "+o+" in offensePointMap: value must be an integer");
					}
				}

				if( keyInt < 0 ) {
					continue;
				}

				int value = bukkitConfig.getInt("offensePointMap."+o, -1);

				if( value > -1 )
					pointKeyMap.put(Integer.valueOf(keyInt), Integer.valueOf(value));
			}
		}
		
		return pointKeyMap;
	}

	/** Implementation-specific method called on first load to invoke all of the config params
	 * (which forces the defaults to be loaded) and then save them out to disk.
	 * 
	 */
	/* No longer used as a result of new-style config making this obsolete.
	public void firstLoad() {
		options().copyDefaults(true);
		
		getBanFlag();
		getBlockPointMap();
		getKickFlag();
		getLocFlag();
		getLogFlag();
		getLogPath();
		getOffenseCount();
		getOffensePoints();
		getPotMsg();
		getPotReason();
		getPotSender();
		getShoutFlag();
		getToolId();
		
		try {
			save();
		} catch(Exception e) { e.printStackTrace(); }
	}
	*/
}
