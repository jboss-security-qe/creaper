package org.wildfly.extras.creaper.commands.elytron.mapper;

import java.util.HashMap;
import java.util.Map;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

abstract class AbstractAddCustom implements OnlineCommand {

    private final String name;
    private final String className;
    private final String module;
    private final Map<String, String> configuration;
    private final boolean replaceExisting;

    protected AbstractAddCustom(Builder<? extends Builder> builder) {
        this.name = builder.name;
        this.className = builder.className;
        this.module = builder.module;
        this.configuration = builder.configuration;
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address securityRealmAddress = Address.subsystem("elytron").and(getCustomTypeName(), name);
        if (replaceExisting) {
            ops.removeIfExists(securityRealmAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(securityRealmAddress, Values.empty()
            .and("class-name", className)
            .andOptional("module", module)
            .andObjectOptional("configuration", Values.fromMap(configuration)));
    }

    protected abstract String getCustomTypeName();

    abstract static class Builder<T extends Builder> {

        private final String name;
        protected String className;
        private String module;
        private Map<String, String> configuration = new HashMap<String, String>();
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the custom-realm-mapper must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the custom-realm-mapper must not be empty value");
            }

            this.name = name;
        }

        public T className(String className) {
            this.className = className;
            return (T) this;
        }

        public T module(String module) {
            this.module = module;
            return (T) this;
        }

        public T addConfiguration(String name, String value) {
            configuration.put(name, value);
            return (T) this;
        }

        public T addConfiguration(String name, boolean value) {
            configuration.put(name, Boolean.toString(value));
            return (T) this;
        }

        public T replaceExisting() {
            this.replaceExisting = true;
            return (T) this;
        }
    }
}
