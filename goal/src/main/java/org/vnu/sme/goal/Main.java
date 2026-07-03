package org.vnu.sme.goal;

import org.tzi.use.runtime.IPlugin;
import org.tzi.use.runtime.IPluginRuntime;

/**
 * MAGoalTax plugin entry point.
 * Required by USE plugin system (referenced in META-INF/MANIFEST.MF).
 */
public class Main implements IPlugin {

    private static final String NAME = "MAGoalTax — Goal Decomposition Plugin";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void run(IPluginRuntime runtime) throws Exception {
        // No initialisation needed — actions are registered via useplugin.xml
    }
}
