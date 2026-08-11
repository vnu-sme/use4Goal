package org.vnu.sme.tocl;

import org.tzi.use.runtime.IPlugin;
import org.tzi.use.runtime.IPluginRuntime;

public class Main implements IPlugin {
    protected final String PLUGIN_ID = "TemporalOCL";
    
    @Override
    public String getName() {
        return PLUGIN_ID;
    }
    
    @Override
    public void run(IPluginRuntime pluginRuntime) throws Exception {
    
    }
}
