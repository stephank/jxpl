package org.angelsl.bukkit.jxpl;

import org.bukkit.Server;
import org.bukkit.plugin.*;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptLoader implements PluginLoader {

    static Logger l = Logger.getLogger("Minecraft.JxplPlugin");

    private final Plugin containing;
    private final Server server;
    private final File scriptsDir;
    private final HashMap<Pattern, ScriptEngineFactory> fileFilters;
    private final ScriptEngineManager manager = new ScriptEngineManager();

    public ScriptLoader(Plugin containing) {
        this.containing = containing;
        this.server = containing.getServer();
        this.scriptsDir = containing.getDescription().getDataFolder();

        fileFilters = new HashMap<Pattern, ScriptEngineFactory>();
        for (ScriptEngineFactory sef : manager.getEngineFactories()) {
            try {
                @SuppressWarnings("unused")
                Invocable t = (Invocable)sef.getScriptEngine();
            } catch (ClassCastException t) {
                // engine does not support invocable. pass.
                continue;
            }
            for (String ext : sef.getExtensions()) {
                l.log(Level.INFO, "Adding file extension \"." + ext + "\" for scripting engine \"" + sef.getEngineName() + "\".");
                fileFilters.put(Pattern.compile(Pattern.quote("." + ext) + "$"), sef);
            }
        }
    }

    public Plugin getContainingPlugin() {
        return containing;
    }

    public void discoverPlugins() {
        PluginManager pm = server.getPluginManager();
        File[] files = scriptsDir.listFiles();
        for (File file : files) {
            for (Map.Entry<Pattern, ScriptEngineFactory> entry : fileFilters.entrySet()) {
                Matcher matcher = entry.getKey().matcher(file.getName());
                if (matcher.find()) {
                    ScriptDescription description = new ScriptDescription(this, file, entry.getValue());
                    pm.register(description);
                    break;
                }
            }
        }
    }

    public Plugin enablePlugin(PluginDescription abstractDescription) throws InvalidPluginException {
        ScriptDescription description = (ScriptDescription)abstractDescription;
        ScriptPlugin plugin = new ScriptPlugin(server, description);

        ScriptEngine engine = description.getScriptEngine();
        try {
            FileReader reader = new FileReader(description.getFile());
            engine.eval(reader);
            reader.close();
        }
        catch (Exception ex) {
            throw new InvalidPluginException(ex);
        }

        l.log(Level.INFO, "Loaded script " + description.getName());
        plugin.tryInvoke("onEnable");
        return plugin;
    }

    public void disablePlugin(Plugin abstractPlugin) {
        ScriptPlugin plugin = (ScriptPlugin)abstractPlugin;
        plugin.tryInvoke("onDisable");
    }

}
