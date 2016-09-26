package org.wildfly.extras.creaper.commands.elytron.tls;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddKeyManager implements OnlineCommand {

    private final String name;
    private final String algorithm;
    private final String keyStore;
    private final String password;
    private final String provider;
    private final String providerLoader;
    private final boolean replaceExisting;

    private AddKeyManager(Builder builder) {
        this.name = builder.name;
        this.algorithm = builder.algorithm;
        this.keyStore = builder.keyStore;
        this.password = builder.password;
        this.provider = builder.provider;
        this.providerLoader = builder.providerLoader;
        // Replace existing
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address keyManagerAddress = Address.subsystem("elytron").and("key-managers", name);
        if (replaceExisting) {
            ops.removeIfExists(keyManagerAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(keyManagerAddress, Values.empty()
            .and("name", name)
            .and("algorithm", algorithm)
            .and("key-store", keyStore)
            .andOptional("password", password)
            .andOptional("provider", provider)
            .andOptional("provider-loader", providerLoader));
    }

    public static final class Builder {

        private String name;
        private String algorithm;
        private String keyStore;
        private String password;
        private String provider;
        private String providerLoader;
        private boolean replaceExisting;

        public Builder(String name, String algorithm) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Name of the key-manager must be specified as non empty value");
            }
            if (algorithm == null || algorithm.isEmpty()) {
                throw new IllegalArgumentException("Algorithm of the key-manager must be specified as non empty value");
            }
            this.name = name;
            this.algorithm = algorithm;
        }

        public Builder keyStore(String keyStore) {
            this.keyStore = keyStore;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public Builder providerLoader(String providerLoader) {
            this.providerLoader = providerLoader;
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddKeyManager build() {
            return new AddKeyManager(this);
        }
    }
}
