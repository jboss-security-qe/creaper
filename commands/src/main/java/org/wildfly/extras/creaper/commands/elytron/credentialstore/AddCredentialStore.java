package org.wildfly.extras.creaper.commands.elytron.credentialstore;

import org.wildfly.extras.creaper.commands.elytron.CredentialRef;
import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddCredentialStore implements OnlineCommand {

    private final String name;
    private final String type;
    private final String provider;
    private final String providerLoader;
    private final String relativeTo;
    private final String uri;
    private final CredentialRef credentialReference;
    private final boolean replaceExisting;

    private AddCredentialStore(Builder builder) {
        this.name = builder.name;
        this.uri = builder.uri;
        this.type = builder.type;
        this.provider = builder.provider;
        this.providerLoader = builder.providerLoader;
        this.relativeTo = builder.relativeTo;
        this.credentialReference = builder.credentialReference;
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address credentialStoreAddress = Address.subsystem("elytron").and("credential-store", name);
        if (replaceExisting) {
            ops.removeIfExists(credentialStoreAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(credentialStoreAddress, Values.empty()
                .and("uri", uri)
                .andObject("credential-reference", credentialReference.toValues())
                .andOptional("type", type)
                .andOptional("provider", provider)
                .andOptional("provider-loader", providerLoader)
                .andOptional("relative-to", relativeTo));
    }

    public static final class Builder {

        private final String name;
        private String type;
        private String provider;
        private String providerLoader;
        private String relativeTo;
        private String uri;
        private CredentialRef credentialReference;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Name of the kerberos-security-factory must be specified as non empty value");
            }
            this.name = name;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
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

        public Builder relativeTo(String relativeTo) {
            this.relativeTo = relativeTo;
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

        public AddCredentialStore build() {
            if (uri == null || uri.isEmpty()) {
                throw new IllegalArgumentException("URI of the credential-store must be specified as non empty value");
            }
            if (credentialReference == null) {
                throw new IllegalArgumentException("Credential-reference of the credential-store must be specified");
            }

            return new AddCredentialStore(this);
        }
    }
}
