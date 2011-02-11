package org.angelsl.bukkit.jxpl;

import java.io.File;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import org.bukkit.plugin.PluginDescription;
import org.bukkit.plugin.PluginLoader;

public final class ScriptDescription extends PluginDescription {

    private final ScriptEngineFactory factory;
    private ScriptEngine engine = null;

    protected ScriptDescription(PluginLoader loader, File file, ScriptEngineFactory factory) {
        super(loader, file);
        this.factory = factory;

        // Name the plugin after the filename
        String filename = file.getName();
        int index = file.getName().lastIndexOf(".");
        if (index != -1) {
            name = filename.substring(0, index);
        }
        else {
            name = filename;
        }
    }

    public ScriptEngine getScriptEngine() {
        if (engine == null) {
            engine = factory.getScriptEngine();
        }
        return engine;
    }
}
