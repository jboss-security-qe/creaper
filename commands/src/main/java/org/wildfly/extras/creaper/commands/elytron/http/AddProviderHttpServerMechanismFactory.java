package org.wildfly.extras.creaper.commands.elytron.http;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddProviderHttpServerMechanismFactory implements OnlineCommand {

    private final String name;
    private final String providerLoader;
    private final boolean replaceExisting;

    private AddProviderHttpServerMechanismFactory(Builder builder) {
        this.name = builder.name;
        this.providerLoader = builder.providerLoader;
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address securityRealmAddress = Address.subsystem("elytron")
                .and("provider-http-server-mechanism-factory", name);
        if (replaceExisting) {
            ops.removeIfExists(securityRealmAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(securityRealmAddress, Values.empty()
                .andOptional("provider-loader", providerLoader));
    }

    public static final class Builder {

        private final String name;
        private String providerLoader;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the provider-http-server-mechanism-factory must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the provider-http-server-mechanism-factory must not be empty value");
            }
            this.name = name;
        }

        public Builder providerLoader(String providerLoader) {
            this.providerLoader = providerLoader;
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddProviderHttpServerMechanismFactory build() {
            return new AddProviderHttpServerMechanismFactory(this);
        }

    }
}
