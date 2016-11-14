package org.wildfly.extras.creaper.commands.elytron.tls;

import org.wildfly.extras.creaper.commands.elytron.CredentialRef;
import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

/**
 * credential-reference is mandatory! https://issues.jboss.org/browse/JBEAP-6757
 *
 *
 */
public final class AddKeyManager implements OnlineCommand {

    private final String name;
    private final String algorithm;
    private final String keyStore;
    private final CredentialRef credentialReference;
    private final String provider;
    private final String providerLoader;
    private final boolean replaceExisting;

    private AddKeyManager(Builder builder) {
        this.name = builder.name;
        this.algorithm = builder.algorithm;
        this.keyStore = builder.keyStore;
        this.provider = builder.provider;
        this.providerLoader = builder.providerLoader;
        this.credentialReference = builder.credentialReference;
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
            .andObject("credential-reference", credentialReference.toValues())
            .andOptional("provider", provider)
            .andOptional("provider-loader", providerLoader));
    }

    public static final class Builder {

        private final String name;
        private String algorithm;
        private String keyStore;
        private CredentialRef credentialReference;
        private String provider;
        private String providerLoader;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Name of the key-manager must be specified as non empty value");
            }
            this.name = name;
        }

        public Builder algorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder keyStore(String keyStore) {
            this.keyStore = keyStore;
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

        public Builder credentialReference(CredentialRef credentialReference) {
            this.credentialReference = credentialReference;
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddKeyManager build() {
            if (algorithm == null || algorithm.isEmpty()) {
                throw new IllegalArgumentException("Algorithm of the key-manager must be specified as non empty value");
            }
            if (credentialReference == null) {
                throw new IllegalArgumentException("Credential reference of the key-manager must be specified");
            }
            return new AddKeyManager(this);
        }
    }
}
