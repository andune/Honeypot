/**
 * 
 */
package com.argo.bukkit.util;

import java.util.Set;
import java.util.logging.Logger;

import org.reflections.Reflections;

import com.argo.bukkit.honeypot.Honeypot;
import com.argo.bukkit.honeypot.config.Config;


/**
 * @author andune
 *
 */
public class BanHandlerFactory {
    private final Honeypot plugin;
    private final Config config;
    private final Logger log;
    private final Reflections reflections;
    
    public BanHandlerFactory(Honeypot plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
        this.log = plugin.getLogger();
        
        this.reflections = Reflections.collect();
    }

    public BanHandler getBansHandler() {
        BanHandler handler = null;
//        boolean any = false;
        
        BanMethod banSystem = config.getBanSystem();
        System.out.println("getBansHandler, banSystem = "+banSystem);
        Set<Class<? extends BanHandler>> set = reflections.getSubTypesOf(BanHandler.class);
        for(Class<? extends BanHandler> clazz : set) {
            System.out.println("teseting class "+clazz);
            // skip Vanilla class, it is handled separate as the last-ditch option
            if( clazz.equals(Vanilla.class) )
                continue;

            try {
                handler = clazz.newInstance();
            } catch (Exception e) {
                log.warning("Error loading class "+clazz+": "+e.getMessage());
                continue;
            }

            String handlerName = handler.getHandlerName();
            System.out.println("getBansHandler, handlerName = "+handlerName);
            if( banSystem.equals(BanMethod.ANY) || banSystem.toString().equalsIgnoreCase(handlerName) ) {
                handler.init(plugin);
                
                if( handler.isSupported() ) {
                    if( banSystem.equals(BanMethod.ANY) ) {
                        log.info(handler.getHandlerName() + " " + handler.getVersion() + " found, using it");
                    }
                    
                    // we found an installed ban system, use it
                    break;
                }
                else if( banSystem.toString().equalsIgnoreCase(handlerName) ) {
                    log.info(banSystem+" selected, but Honeypot cannot connect to it. "+banSystem+" detected version = "+handler.getVersion());
                    break;
                }
            }
            else
                handler = null;
        }
        
        if( handler == null || !handler.isSupported() ) {
            handler = new Vanilla();
            handler.init(plugin);

            if( !banSystem.equals(BanMethod.VANILLA) )
                log.info("Didn't find ban plugin, using vanilla.");
        }
        
        return handler;
    }
        
        /*
        BanMethod banSystem = config.getBanSystem();
        System.out.println("getBansHandler, banSystem = "+banSystem);
        switch(banSystem){
        case ANY:
        default:
            System.out.println("hit ANY/default");
            // if ANY is defined or we hit default, then we flip on the any
            // flag and just fall through each section sequentially until
            // we find a working implementation.
            any = true;
            
        case MCBANS:
            System.out.println("hit mcbans");
            handler = new MCBans4(plugin, config);

            // if not in ANY mode, then admin has specifically selected this
            // ban system, so break immediately and return this handler
            if( !any )
                break;

            // if we're in ANY mode and this ban plugin is supported on
            // this server, then print a message and return it. Otherwise,
            // we fall through to the next option.
            if( any && handler.isSupported() ) {
                log.info("MCBans 4.0+ plugin found, using that.");
                break;
            }
            
        case EASYBAN:
            System.out.println("hit easyban");
            handler = new EasyBan(plugin.getServer());

            // if not in ANY mode, then admin has specifically selected this
            // ban system, so break immediately and return this handler
            if( !any )
                break;

            // if we're in ANY mode and this ban plugin is supported on
            // this server, then print a message and return it. Otherwise,
            // we fall through to the next option.
            if( any && handler.isSupported() ) {
                log.info("EasyBan plugin found, using that.");
                break;
            }

        // last option in ANY or default mode, this one is selected if
        // none of the other Ban handlers are active on the server
        case VANILLA:
            System.out.println("hit vanilla");
            if( any )
                log.info("Didn't find ban plugin, using vanilla.");
            handler = new Vanilla(plugin.getServer(), config);
            break;
        }
        
        handler.init();
        
        if( !handler.isSupported() )
            log.info(banSystem+" selected, but Honeypot cannot connect to it. "+banSystem+" detected version = "+handler.getVersion());

        return handler;
    }
    */
}
