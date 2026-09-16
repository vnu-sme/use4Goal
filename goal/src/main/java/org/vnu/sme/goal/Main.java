package org.vnu.sme.goal;

import org.tzi.use.runtime.IPlugin;
import org.tzi.use.runtime.IPluginRuntime;

/**
 * Conformance of Processes and Goals plugin entry point.
 * Required by USE plugin system (referenced in META-INF/MANIFEST.MF).
 */
public class Main implements IPlugin {

    private static final String NAME = "Conformance of Processes and Goals";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void run(IPluginRuntime runtime) throws Exception {
        // No initialisation needed — actions are registered via useplugin.xml
    }
}
