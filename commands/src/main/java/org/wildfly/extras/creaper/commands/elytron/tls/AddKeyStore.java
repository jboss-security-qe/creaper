package org.wildfly.extras.creaper.commands.elytron.tls;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddKeyStore implements OnlineCommand {

    private final String name;
    private final String type;
    private final String provider;
    private final String providerLoader;
    private final String password;
    private final String aliasFilter;
    private final String path;
    private final String relativeTo;
    private final Boolean required;
    private final boolean replaceExisting;

    private AddKeyStore(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.provider = builder.provider;
        this.providerLoader = builder.providerLoader;
        this.password = builder.password;
        this.aliasFilter = builder.aliasFilter;
        // File
        this.path = builder.path;
        this.relativeTo = builder.relativeTo;
        this.required = builder.required;
        // Replace existing
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address keyStoreAddress = Address.subsystem("elytron").and("key-store", name);
        if (replaceExisting) {
            ops.removeIfExists(keyStoreAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(keyStoreAddress, Values.empty()
            .and("name", name)
            .and("type", type)
            .andOptional("provider", provider)
            .andOptional("provider-loader", providerLoader)
            .andOptional("password", password)
            .andOptional("alias-filter", aliasFilter)
            .andOptional("path", path)
            .andOptional("relative-to", relativeTo)
            .andOptional("required", required));
    }

    public static final class Builder {

        private final String name;
        private String type;
        private String provider;
        private String providerLoader;
        private String password;
        private String aliasFilter;
        private String path;
        private String relativeTo;
        private Boolean required;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Name of the key-store must be specified as non empty value");
            }
            this.name = name;
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

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder aliasFilter(String aliasFilter) {
            this.aliasFilter = aliasFilter;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder relativeTo(String relativeTo) {
            this.relativeTo = relativeTo;
            return this;
        }

        public Builder required(Boolean required) {
            this.required = required;
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddKeyStore build() {
            if (type == null || type.isEmpty()) {
                throw new IllegalArgumentException("Type of the key-store must be specified as non empty value");
            }
            return new AddKeyStore(this);
        }
    }
}
