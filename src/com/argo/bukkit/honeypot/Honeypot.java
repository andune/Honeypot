package com.argo.bukkit.honeypot;

import java.io.File;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.argo.bukkit.util.BansHandler;

public class Honeypot extends JavaPlugin {
	private final HoneypotBlockListener blockListener = new HoneypotBlockListener(this);
	private final HoneypotPlayerListener playerListener = new HoneypotPlayerListener(this);
	
	private static Honeypot instance;

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
		
		createDirs();

		if(!Settings.load()) {
			System.out.println("Honeypot: an error occured while trying to load the properties file.");
		}
		
		Honeyfarm.setLogPath(Settings.getLogPath());
		
		if(!Honeyfarm.refreshData()) {
			System.out.println("Honeypot: an error occured while trying to load the honeypot list.");
		}
		if(!HoneypotPermissionsHandler.setupPermissions(this)) {
			System.out.println("Honeypot: Permissions plugin not found, using default.");
		} else {
			System.out.println("Honeypot: Permissions plugin found, using that.");
		}

		switch(BansHandler.setupbanHandler(this)) {
		case VANILLA:
			System.out.println("Honeypot: Didn't find ban plugin, using vanilla.");
			break;
		case MCBANS:
			System.out.println("Honeypot: MCBans plugin found, using that.");
                        break;
                case EASYBAN:
			System.out.println("Honeypot: EasyBan plugin found, using that.");
			break;
		case SIMPLEBAN:
			System.out.println("Honeypot: SimpleBan plugin found, using that.");
			break;
		default:
			System.out.println("Honeypot: Didn't find ban plugin, using vanilla.");
			break;
		}

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
		getCommand("honeypot").setExecutor(new CmdHoneypot(this));

//		HoneypotManager.getInstance().setHoneypot(this);
		
		PluginDescriptionFile pdf = this.getDescription();
		System.out.println(pdf.getName() + " revision " + pdf.getVersion() + " by " + pdf.getAuthors().get(0) + " succesfully loaded.");
	}

	public void onDisable() {
		if(!Honeyfarm.saveData()) {
			System.out.println("Honeypot: an error occured while trying to save the honeypot list.");
		}

		PluginDescriptionFile pdf = this.getDescription();
		System.out.println(pdf.getName() + " revision " + pdf.getVersion() + " by " + pdf.getAuthors().get(0) + " succesfully disabled.");
	}

	public void createDirs() {
		new File("plugins/Honeypot").mkdir();
	}

	public String getLogPath() { return Settings.getLogPath(); }
}
