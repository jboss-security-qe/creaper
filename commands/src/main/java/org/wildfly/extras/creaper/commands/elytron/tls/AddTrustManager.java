package org.wildfly.extras.creaper.commands.elytron.tls;

import org.wildfly.extras.creaper.commands.foundation.offline.xml.GroovyXmlTransform;
import org.wildfly.extras.creaper.commands.foundation.offline.xml.Subtree;
import org.wildfly.extras.creaper.core.offline.OfflineCommand;
import org.wildfly.extras.creaper.core.offline.OfflineCommandContext;
import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddTrustManager implements OnlineCommand, OfflineCommand {

    private final String name;
    private final String algorithm;
    private final String aliasFilter;
    private final String keyStore;
    private final String providerName;
    private final String providers;
    private final boolean replaceExisting;

    private AddTrustManager(Builder builder) {
        this.name = builder.name;
        this.algorithm = builder.algorithm;
        this.aliasFilter = builder.aliasFilter;
        this.keyStore = builder.keyStore;
        this.providerName = builder.providerName;
        this.providers = builder.providers;
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address trustManagerAddress = Address.subsystem("elytron").and("trust-managers", name);
        if (replaceExisting) {
            ops.removeIfExists(trustManagerAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(trustManagerAddress, Values.empty()
                .and("name", name)
                .and("algorithm", algorithm)
                .and("key-store", keyStore)
                .andOptional("alias-filter", aliasFilter)
                .andOptional("provider-name", providerName)
                .andOptional("providers", providers));
    }

    @Override
    public void apply(OfflineCommandContext ctx) throws Exception {
        ctx.client.apply(GroovyXmlTransform.of(AddTrustManager.class)
                .subtree("elytronSubsystem", Subtree.subsystem("elytron"))
                .parameter("atrName", name)
                .parameter("atrAlgorithm", algorithm)
                .parameter("atrAliasFilter", aliasFilter)
                .parameter("atrKeyStore", keyStore)
                .parameter("atrProviderName", providerName)
                .parameter("atrProviders", providers)
                .parameter("atrReplaceExisting", replaceExisting)
                .build());
    }

    public static final class Builder {

        private final String name;
        private String algorithm;
        private String aliasFilter;
        private String keyStore;
        private String providerName;
        private String providers;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Name of the trust-manager must be specified as non empty value");
            }
            this.name = name;
        }

        public Builder algorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder aliasFilter(String aliasFilter) {
            this.aliasFilter = aliasFilter;
            return this;
        }

        public Builder keyStore(String keyStore) {
            this.keyStore = keyStore;
            return this;
        }

        public Builder providerName(String providerName) {
            this.providerName = providerName;
            return this;
        }

        public Builder providers(String providers) {
            this.providers = providers;
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddTrustManager build() {
            if (algorithm == null || algorithm.isEmpty()) {
                throw new IllegalArgumentException("Algorithm of the trust-manager must be specified as non empty value");
            }
            return new AddTrustManager(this);
        }
    }
}
