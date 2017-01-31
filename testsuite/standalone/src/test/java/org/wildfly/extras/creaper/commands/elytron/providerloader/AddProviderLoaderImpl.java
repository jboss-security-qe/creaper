package org.wildfly.extras.creaper.commands.elytron.providerloader;

import java.security.Provider;

public class AddProviderLoaderImpl extends Provider {

    public AddProviderLoaderImpl() {
        super("name", 1.0, "some info");
    }

}
