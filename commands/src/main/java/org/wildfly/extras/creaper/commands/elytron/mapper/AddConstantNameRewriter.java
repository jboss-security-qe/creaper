package org.wildfly.extras.creaper.commands.elytron.mapper;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddConstantNameRewriter implements OnlineCommand {

    private final String name;
    private final String constant;
    private final boolean replaceExisting;

    private AddConstantNameRewriter(Builder builder) {
        this.name = builder.name;
        this.constant = builder.constant;
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address constantNameRewriterAddress = Address.subsystem("elytron").and("constant-name-rewriter", name);
        if (replaceExisting) {
            ops.removeIfExists(constantNameRewriterAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(constantNameRewriterAddress, Values.empty()
                .and("constant", constant));
    }

    public static final class Builder {

        private final String name;
        private String constant;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the constant-name-rewriter must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the constant-name-rewriter must not be empty value");
            }
            this.name = name;
        }

        public Builder constant(String constant) {
            this.constant = constant;
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddConstantNameRewriter build() {
            if (constant == null || constant.isEmpty()) {
                throw new IllegalArgumentException("Constant must not be null and must have a minimum length of 1 character");
            }
            return new AddConstantNameRewriter(this);
        }
    }

}
