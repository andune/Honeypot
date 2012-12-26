/**
 * 
 */
package com.argo.bukkit.honeypot.config;

import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import com.argo.bukkit.util.BanMethod;

/** Interface for configuration implementations to use.  Created to allow a seamless code transition
 * between the old properties file and native Bukkit "config.yml" configurations.
 * 
 * @author morganm
 *
 */
public interface Config {
    public static final String defaultHoneypotMsg = "[Honeypot] You have been caught destroying a honeypot block.";
    public static final String defaultHoneypotBanReason = "Destroyed honeypot block.";  //ban reason (all ban systems)
    public static final String defaultKickBanSender = "[Honeypot]"; //who will kick / ban when hp get destroyed? Only MCBANS, in other cases it will be Console !
    public static final String defaultLogPath = "plugins/Honeypot/honeypot.log";
    public static final int defaultToolID = 271;
    
	public void load(JavaPlugin plugin) throws Exception;
	public void save() throws Exception;
	
	/**
	 * Return the Ban system requested by the admin.
	 * 
	 * @return
	 */
	public BanMethod getBanSystem();
	
    public int getOffenseCount();
    
    /** The number of points required for a Honeypot ban (used only if != 0).  By default,
     * one block == 1 point.  Using the PointMap, this allows you to assign higher points
     * to more expensive blocks.
     * 
     * @return
     */
    public int getOffensePoints();
    
    /** If getOffensePoints() returns non-zero, this Map is used to determine block weighting
     * for offense points.  I personally use this, for example, to make diamond/gold/iron blocks
     * a single-shot ban, whereas if they are destroying flowers in the flower field at my
     * honeypot site, those are only 1-point each and they have to destroy a few of them to
     * get their ban.
     * 
     * @return
     */
    public Map<Integer, Integer> getBlockPointMap();
    
    public String getPotMsg();
    public String getPotReason();
    public String getPotSender();
    public int getToolId();

    public boolean getKickFlag();
    public boolean getBanFlag();
    public boolean getLocFlag();
    public boolean getLogFlag();
    public boolean getShoutFlag();
    public String getLogPath();
    
    public boolean isGlobalBan();
}
