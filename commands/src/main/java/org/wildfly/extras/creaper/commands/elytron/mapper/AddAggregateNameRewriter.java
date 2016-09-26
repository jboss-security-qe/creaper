package org.wildfly.extras.creaper.commands.elytron.mapper;

import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public class AddAggregateNameRewriter extends AbstractAddNameRewriter {

    private AddAggregateNameRewriter(Builder builder) {
        super(builder);
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address aggregateNameRewriterAddress = Address.subsystem("elytron")
                .and("aggregate-name-rewriter", name);
        if (replaceExisting) {
            ops.removeIfExists(aggregateNameRewriterAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(aggregateNameRewriterAddress, Values.empty()
                .andList(String.class, "name-rewriters", nameRewriters));
    }

    public static final class Builder extends AbstractAddNameRewriter.Builder<Builder> {

        public Builder(String name) {
            super(name);
        }

        @Override
        public AddAggregateNameRewriter build() {
            if (nameRewriters.size() < 2) {
                throw new IllegalArgumentException("There must be at least two name-rewriters");
            }
            return new AddAggregateNameRewriter(this);
        }

    }
}
