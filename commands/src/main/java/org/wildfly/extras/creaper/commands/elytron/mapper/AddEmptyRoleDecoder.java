package org.wildfly.extras.creaper.commands.elytron.mapper;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddEmptyRoleDecoder implements OnlineCommand {

    private final String name;
    private final boolean replaceExisting;

    public AddEmptyRoleDecoder(Builder builder) {
        this.name = builder.name;
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address emptyRoleDecoderAddress = Address.subsystem("elytron").and("empty-role-decoder", name);
        if (replaceExisting) {
            ops.removeIfExists(emptyRoleDecoderAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(emptyRoleDecoderAddress, Values.empty());
    }

    public static final class Builder {

        private final String name;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the empty-role-decoder must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the empty-role-decoder must not be empty value");
            }
            this.name = name;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddEmptyRoleDecoder build() {
            return new AddEmptyRoleDecoder(this);
        }
    }


}
