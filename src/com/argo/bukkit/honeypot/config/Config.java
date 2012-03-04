/**
 * 
 */
package com.argo.bukkit.honeypot.config;

import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

/** Interface for configuration implementations to use.  Created to allow a seamless code transition
 * between the old properties file and native Bukkit "config.yml" configurations.
 * 
 * @author morganm, dwdcweb
 *
 */
public interface Config {
    public static String defaultHoneypotMsg = "[CandyBag] You have been caught destroying a block in a candy bag.";
    public static String defaultCandybagMsg = "[CandyBag] You have been caught placing a block in a candy bag.";
    public static String defaultHoneypotBanReason = "Destroyed block in a candy bag.";  //ban reason (all ban systems)
    public static String defaultCandybagBanReason = "Placed a block in a candy bag.";  //ban reason (all ban systems)
    public static Object defaultKickBanSender = "[CandyBag]"; //who will kick / ban when hp get destroyed? Only MCBANS, in other cases it will be Console !
    public static String defaultLogPath = "plugins/CandyBag/candybag.log";
    public static int defaultToolID = 271;
    
	public void load(JavaPlugin plugin) throws Exception;
	public void save() throws Exception;
	
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
    public String getBagMsg();
    public String getPotReason();
    public String getBagReason();
    public Object getPotSender();
    public int getToolId();

    public boolean getKickFlag();
    public boolean getBanFlag();
    public boolean getLocFlag();
    public boolean getLogFlag();
    public boolean getShoutFlag();
    public String getLogPath();
    
    public boolean isGlobalBan();
}
