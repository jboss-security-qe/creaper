package org.wildfly.extras.creaper.commands.elytron.mapper;

import java.util.HashMap;
import java.util.Map;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddCustomRoleMapper implements OnlineCommand {

    private final String name;
    private final String className;
    private final String module;
    private final Map<String, String> configuration;
    private final boolean replaceExisting;

    private AddCustomRoleMapper(Builder builder) {
        this.name = builder.name;
        this.className = builder.className;
        this.module = builder.module;
        this.configuration = builder.configuration;
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address securityRealmAddress = Address.subsystem("elytron").and("custom-role-mapper", name);
        if (replaceExisting) {
            ops.removeIfExists(securityRealmAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(securityRealmAddress, Values.empty()
            .and("class-name", className)
            .andOptional("module", module)
            .andObjectOptional("configuration", Values.fromMap(configuration)));
    }

    public static final class Builder {

        private final String name;
        private String className;
        private String module;
        private Map<String, String> configuration = new HashMap<String, String>();
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the custom-role-mapper must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the custom-role-mapper must not be empty value");
            }

            this.name = name;
        }

        public Builder className(String className) {
            this.className = className;
            return this;
        }

        public Builder module(String module) {
            this.module = module;
            return this;
        }

        public Builder addConfiguration(String name, String value) {
            configuration.put(name, value);
            return this;
        }

        public Builder addConfiguration(String name, boolean value) {
            configuration.put(name, Boolean.toString(value));
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddCustomRoleMapper build() {
            if (className == null || className.isEmpty()) {
                throw new IllegalArgumentException("className must not be null or empty string");
            }
            return new AddCustomRoleMapper(this);
        }
    }
}
