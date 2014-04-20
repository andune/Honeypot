/**
 * 
 */
package com.argo.bukkit.util;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.logging.Logger;

import org.reflections.Reflections;
import org.reflections.util.FilterBuilder;

import com.andune.minecraft.commonlib.reflections.YamlSerializer;
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

//        this.reflections = Reflections.collect();
        this.reflections = Reflections.collect("META-INF/reflections",
                new FilterBuilder().include(".*-reflections.yml"),
                new YamlSerializer());
    }
    
    /**
     * Return true if the banSystem is the special "ANY" type.
     * 
     * @param banSystem
     * @return
     */
    private boolean isAny(String banSystem) {
        return banSystem.equalsIgnoreCase("any");
    }

    public BanHandler getBansHandler() {
        BanHandler handler = null;
//        boolean any = false;
        
        String banSystem = config.getBanSystem();
        Set<Class<? extends BanHandler>> set = reflections.getSubTypesOf(BanHandler.class);
        for(Class<? extends BanHandler> clazz : set) {
            log.fine("DEBUG: testing class "+clazz);
            // skip abstract classes
            if( Modifier.isAbstract(clazz.getModifiers()) )
                continue;
            
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
            if( isAny(banSystem) || banSystem.toString().equalsIgnoreCase(handlerName) ) {
                handler.init(plugin);
                
                if( handler.isSupported() ) {
                    if( isAny(banSystem) ) {
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

            // display a notice if the admin didn't explicitly choose Vanilla
            if( !banSystem.equals(handler.getHandlerName()) )
                log.info("Didn't find ban plugin, using vanilla.");
        }
        
        return handler;
    }
}
