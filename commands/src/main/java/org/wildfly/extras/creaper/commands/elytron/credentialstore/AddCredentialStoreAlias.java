package org.wildfly.extras.creaper.commands.elytron.credentialstore;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddCredentialStoreAlias implements OnlineCommand {

    private final String name;
    private final String credentialStore;
    private final EntryType entryType;
    private final String secretValue;
    private final boolean replaceExisting;

    private AddCredentialStoreAlias(Builder builder) {
        this.name = builder.name;
        this.credentialStore = builder.credentialStore;
        this.entryType = builder.entryType;
        this.secretValue = builder.secretValue;
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address credentialStoreAliasAddress = Address.subsystem("elytron")
                .and("credential-store", credentialStore)
                .and("alias", name);
        if (replaceExisting) {
            ops.removeIfExists(credentialStoreAliasAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(credentialStoreAliasAddress, Values.empty()
                .and("secret-value", secretValue)
                .andOptional("entry-type", entryType == null ? null : entryType.getEntryType()));
    }

    public static final class Builder {

        private final String name;
        private String credentialStore;
        private EntryType entryType;
        private String secretValue;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Name of the kerberos-security-factory must be specified as non empty value");
            }
            this.name = name;
        }

        public Builder credentialStore(String credentialStore) {
            this.credentialStore = credentialStore;
            return this;
        }

        public Builder entryType(EntryType entryType) {
            this.entryType = entryType;
            return this;
        }

        public Builder secretValue(String secretValue) {
            this.secretValue = secretValue;
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddCredentialStoreAlias build() {
            if (credentialStore == null || credentialStore.isEmpty()) {
                throw new IllegalArgumentException("credential-store must be specified as non empty value");
            }
            if (secretValue == null || secretValue.isEmpty()) {
                throw new IllegalArgumentException("secret-value must be specified as non empty value");
            }

            return new AddCredentialStoreAlias(this);
        }
    }

    public static enum EntryType {

        OTHER("Other"), PASSWORD_CREDENTIAL("org.wildfly.security.credential.PasswordCredential");

        private final String entryTypeName;

        private EntryType(String entryTypeName) {
            this.entryTypeName = entryTypeName;
        }

        public String getEntryType() {
            return entryTypeName;
        }
    }
}
