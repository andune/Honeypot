package com.argo.bukkit.honeypot;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.argo.bukkit.honeypot.config.Config;
import com.argo.bukkit.honeypot.config.PropertiesFile;
import com.argo.bukkit.honeypot.config.YMLFile;
import com.argo.bukkit.honeypot.listener.HoneypotBlockListener;
import com.argo.bukkit.honeypot.listener.HoneypotPlayerListener;
import com.argo.bukkit.util.BansHandler;
import com.argo.bukkit.util.JarUtils;
import com.argo.bukkit.util.PermissionSystem;

public class Honeypot extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Honeypot");
    public static final String logPrefix = "[Honeypot] ";

    private static Honeypot instance;
    private HoneyStack honeyStack;
    private Config config;
    private BansHandler bansHandler;
    private PermissionSystem perm;
    private JarUtils jarUtils;
	private int buildNumber = -1;

    public void log(final String message) {
    	log.info(logPrefix+message);
    }

    /** I think there's a correct "PluginManager" way to get plugin instances, but
     * I'm cheating and using a static instance ala the Singleton pattern for now.
     *
     * @return
     */
    public static Honeypot getCurrentInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        honeyStack = new HoneyStack();

        perm = new PermissionSystem(this, log, logPrefix);
        perm.setupPermissions();

        createDirs();

        loadConfig();
        Honeyfarm.setLogPath(config.getLogPath());

        if (!Honeyfarm.refreshData()) {
        	log("an error occured while trying to load the honeypot list.");
        }

        bansHandler = new BansHandler(this);
        switch (bansHandler.setupbanHandler(this)) {
            case VANILLA:
            	log("Didn't find ban plugin, using vanilla.");
                break;
            case MCBANS4:
                log("MCBans 4.0+ plugin found, using that.");
                break;
            case EASYBAN:
                log("EasyBan plugin found, using that.");
                break;
            case KABANS:
                log("KiwiAdmin plugin found, using that.");
                break;
            case UBAN:
                log("UltraBan plugin found, using that.");
                break;
            default:
                log("Didn't find ban plugin, using vanilla.");
                break;
        }

        getServer().getPluginManager().registerEvents(new HoneypotBlockListener(this), this);
        getServer().getPluginManager().registerEvents(new HoneypotPlayerListener(this), this);

        getCommand("honeypot").setExecutor(new CmdHoneypot(this));

        // schedule to run every minute (20 ticks * 60 seconds)
        getServer().getScheduler().scheduleSyncRepeatingTask(this, honeyStack, 1200, 1200);

    	jarUtils = new JarUtils(this, getFile(), log, logPrefix);
		buildNumber = jarUtils.getBuildNumber();
        PluginDescriptionFile pdf = this.getDescription();
		log("version "+pdf.getVersion()+", build "+buildNumber+" is enabled");
    }

    public void onDisable() {
        if (!Honeyfarm.saveData()) {
            log("an error occured while trying to save the honeypot list.");
        }

        honeyStack.rollBackAll();
        getServer().getScheduler().cancelTasks(this);

        PluginDescriptionFile pdf = this.getDescription();
		log("version "+pdf.getVersion()+", build "+buildNumber+" is disabled");
    }

    private void loadConfig() {
    	// bad code, we break the interface abstraction by looking for implementation-specific
    	// details, but I'm OK with this since this is intended simply as temporary a
    	// transition from the old properties file to new-style config.yml
    	File newFile = new File("plugins/Honeypot/config.yml");
    	File oldFile = new File("plugins/Honeypot/honeypot.properties");

    	if( newFile.exists() ) {		// new-style config.yml exists?  use it
    		config = new YMLFile();
    	}
    	else if( oldFile.exists() ) { 	// no new-style exists, but old-style does, use that instead.
    		config = new PropertiesFile();
    	}
    	else {							// neither exists yet (new installation), create and use new-style
    		this.saveDefaultConfig();
    		config = new YMLFile();
    	}

    	try {
    		config.load(this);
    	}
    	catch(Exception e) {
            log("an error occured while trying to load the config file.");
    		e.printStackTrace();
    	}
    }

    public boolean hasPermission(CommandSender sender, String permissionNode) {
    	return perm.has(sender, permissionNode);
    }

    public Config getHPConfig() { return config; }
    public BansHandler getBansHandler() { return bansHandler; }

    public void createDirs() {
        new File("plugins/Honeypot").mkdir();
    }

    public String getLogPath() {
        return config.getLogPath();
    }

    public HoneyStack getHoneyStack() {
        return honeyStack;
    }

    public static String prettyPrintLocation(Location l) {
        return "{world=" + l.getWorld().getName() + ", x=" + l.getBlockX() +
                ", y=" + l.getBlockY() + ", z=" + l.getBlockZ() + "}";
    }
}
