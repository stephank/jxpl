package org.angelsl.bukkit.jxpl;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescription;

import javax.script.Invocable;
import javax.script.ScriptEngine;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ScriptPlugin implements Plugin {

    static Logger l = Logger.getLogger("Minecraft.JxplPlugin");

    private final Server server;
    private final ScriptDescription description;
    private final Invocable invocable;

    public ScriptPlugin(Server server, ScriptDescription description) {
        final ScriptEngine engine = description.getScriptEngine();
        this.server = server;
        this.description = description;
        this.invocable = (Invocable)description.getScriptEngine();
        engine.put("plugin", this);
    }

    public PluginDescription getDescription() {
        return description;
    }

    public Server getServer() {
        return server;
    }

    public Object tryInvoke(String funcName, Object... params)
    {
        try {
            return invocable.invokeFunction(funcName, params);
        } catch (NoSuchMethodException ex) {
            // Do nothing, the script doesn't want to handle this.
        } catch (Exception e) {
            l.log(Level.WARNING, "Error while running " + funcName + " of script " + description.getName() + ".", e);
        }
        return null;
    }

    private final class ScriptExecutor implements EventExecutor {
        private final Type type;
        private final String functionName;

        public ScriptExecutor(final Event.Type type, final String functionName) {
            this.type = type;
            this.functionName = functionName;
        }

        public void execute(Event event) {
            tryInvoke(functionName, type, event);
        }
    }

    public void registerEvent(Event.Type type, Event.Priority priority, String functionName)
    {
        server.getPluginManager().registerEvent(this, type, priority, new ScriptExecutor(type, functionName));
    }
    
    public void log(Level l, String message)
    {
        ScriptPlugin.l.log(l, message);
    }
}
