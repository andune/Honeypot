package com.argo.bukkit.honeypot;

import com.argo.util.PropertyHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Settings {
    private static final String propertiesPath = "plugins/Honeypot/honeypot.properties";

    private static String honeypotMsg = "[Honeypot] You have been caught destroying a honeypot block.";
    private static String honeypotBanReason = "Destroyed honeypot block.";  //ban reason (all ban systems)
    private static String kickBanSender = "[Honeypot]"; //who will kick / ban when hp get destroyed? Only MCBANS, in other cases it will be Console !
    private static int toolID = 271;
    private static boolean doLoc = false;  //shall we insert into reason last loc. of player (only mcbans and EB)?
    private static boolean doLog = true;
    private static boolean doKick = true;
    private static boolean doBan = false;
    private static boolean doShout = true;
    private static String logPath = "plugins/Honeypot/honeypot.log";

    public static boolean load() {
	PropertyHandler props = new PropertyHandler();

	try {
	    if(!new File(propertiesPath).exists()) {
		new File(propertiesPath).createNewFile();

		props.setInt("tool-id", toolID);
		props.setString("honeypot-kick-msg", honeypotMsg);
                props.setString("honeypot-ban-reason", honeypotBanReason);
                props.setString("sender-of-kickban", kickBanSender); 
                props.setBoolean("reason-with-loc", doLoc);
		props.setBoolean("log-to-file", doLog);
                props.setBoolean("kick", doKick);
		props.setBoolean("ban", doBan);
		props.setBoolean("notify-online-players", doShout);
		props.setString("logpath", logPath);

		props.store(new FileOutputStream(propertiesPath), null);
	    } else {
		props.load(new FileInputStream(propertiesPath));
		toolID = props.getInt("tool-id", toolID);
		honeypotMsg = props.getString("honeypot-kick-msg", honeypotMsg);
                honeypotBanReason = props.getString("honeypot-ban-reason", honeypotBanReason);
                kickBanSender = props.getString("sender-of-kickban", kickBanSender);
                doLoc = props.getBoolean("reason-with-loc", doLoc);
		doLog = props.getBoolean("log-to-file", doLog);
		doKick = props.getBoolean("kick", doKick);
		doBan = props.getBoolean("ban", doBan);
		doShout = props.getBoolean("notify-online-players", doShout);
		logPath = props.getString("logpath", logPath);
	    }
	} catch (Exception ex) {
	    return false;
	}

	
	return true;
    }

    public static String getPotMsg() {
	return honeypotMsg;
    }
    
    public static String getPotRea() {
	return honeypotBanReason;
    }
    
    public static String getPotSend() {
	return kickBanSender;
    }

    public static int getToolId() {
	return toolID;
    }

    public static boolean getKickFlag() {
	return doKick;
    }

    public static boolean getBanFlag() {
	return doBan;
    }
    
    public static boolean getLocFlag() {
	return doLoc;
    }

    public static boolean getLogFlag() {
	return doLog;
    }

    public static boolean getShoutFlag() {
	return doShout;
    }
    
    public static String getLogPath() {
    	return logPath;
    }
}
