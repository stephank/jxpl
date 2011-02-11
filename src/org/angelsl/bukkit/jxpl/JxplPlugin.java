package org.angelsl.bukkit.jxpl;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginDescription;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JxplPlugin extends JavaPlugin {

    private static Logger l = Logger.getLogger("Minecraft.JxplPlugin");

    public JxplPlugin(Server server, JavaPluginDescription description) {
        super(server, description);
    }

    public void onEnable() {
        l.log(Level.INFO, "Initialising jxpl...");
        File scriptsDir = getDescription().getDataFolder();
        if (!scriptsDir.exists()) scriptsDir.mkdir();
        ScriptLoader loader = new ScriptLoader(this); 
        getServer().getPluginManager().registerInterface(loader);
    }

}
