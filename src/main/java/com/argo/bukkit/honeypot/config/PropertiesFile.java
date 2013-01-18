package com.argo.bukkit.honeypot.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import com.argo.util.PropertyHandler;

public class PropertiesFile implements Config {
    private static final String propertiesPath = "plugins/Honeypot/honeypot.properties";

    private int toolID = defaultToolID;
    private int offenseCount = 1;
    private boolean doLoc = false;  //shall we insert into reason last loc. of player (only mcbans and EB)?
    private boolean doLog = true;
    private boolean doKick = true;
    private boolean doBan = false;
    private boolean doShout = true;
    private String honeypotMsg = defaultHoneypotMsg;
    private String honeypotBanReason = defaultHoneypotBanReason;
    private String kickBanSender = defaultKickBanSender;
    private String logPath = defaultLogPath;

    public void load(JavaPlugin plugin) throws Exception {
	PropertyHandler props = new PropertyHandler();

	    if(!new File(propertiesPath).exists()) {
		new File(propertiesPath).createNewFile();

		props.setInt("tool-id", defaultToolID);
		props.setInt("offenseCount", offenseCount);
		props.setString("honeypot-kick-msg", defaultHoneypotMsg);
                props.setString("honeypot-ban-reason", defaultHoneypotBanReason);
                props.setString("sender-of-kickban", defaultKickBanSender); 
                props.setBoolean("reason-with-loc", doLoc);
		props.setBoolean("log-to-file", doLog);
                props.setBoolean("kick", doKick);
		props.setBoolean("ban", doBan);
		props.setBoolean("notify-online-players", doShout);
		props.setString("logpath", defaultLogPath);

		props.store(new FileOutputStream(propertiesPath), null);
	    } else {
		props.load(new FileInputStream(propertiesPath));
		toolID = props.getInt("tool-id", defaultToolID);
		offenseCount = props.getInt("offenseCount", offenseCount);
		honeypotMsg = props.getString("honeypot-kick-msg", defaultHoneypotMsg);
                honeypotBanReason = props.getString("honeypot-ban-reason", defaultHoneypotBanReason);
                kickBanSender = props.getString("sender-of-kickban", defaultKickBanSender);
                doLoc = props.getBoolean("reason-with-loc", doLoc);
		doLog = props.getBoolean("log-to-file", doLog);
		doKick = props.getBoolean("kick", doKick);
		doBan = props.getBoolean("ban", doBan);
		doShout = props.getBoolean("notify-online-players", doShout);
		logPath = props.getString("logpath", defaultLogPath);
	    }
    }

	@Override
	public void save() throws Exception {
		throw new Exception("Not implemented");
	}
	
    public int getOffenseCount() {
    	return offenseCount;
    }

    public String getPotMsg() {
	return honeypotMsg;
    }
    
    public String getPotReason() {
	return honeypotBanReason;
    }
    
    public String getPotSender() {
	return kickBanSender;
    }

    public int getToolId() {
	return toolID;
    }

    public boolean getKickFlag() {
	return doKick;
    }

    public boolean getBanFlag() {
	return doBan;
    }
    
    public boolean getLocFlag() {
	return doLoc;
    }

    public boolean getLogFlag() {
	return doLog;
    }

    public boolean getShoutFlag() {
	return doShout;
    }
    
    public String getLogPath() {
    	return logPath;
    }
    
    public boolean isGlobalBan() {
    	return false;		// NOT SUPPORTED IN PROPERTIES FILE
    }

    /*	NOT SUPPORTED IN PROPERTIES FILE.
     * 
     * (non-Javadoc)
     * @see com.argo.bukkit.honeypot.config.Config#getOffensePoints()
     */
	@Override
	public int getOffensePoints() {
		return 0;
	}

    /*	NOT SUPPORTED IN PROPERTIES FILE.
     */
	@Override
	public Map<Integer, Integer> getBlockPointMap() {
		return null;
	}

    /*  NOT SUPPORTED IN PROPERTIES FILE.
     */
	@Override
	public String getBanSystem() {
	    return null;
	}
	
    /*  NOT SUPPORTED IN PROPERTIES FILE.
     */
    @Override
	public String[] getCustomBanCommands() { return null; }

    /*  NOT SUPPORTED IN PROPERTIES FILE.
     */
    @Override
    public String[] getCustomKickCommands() { return null; }
}
