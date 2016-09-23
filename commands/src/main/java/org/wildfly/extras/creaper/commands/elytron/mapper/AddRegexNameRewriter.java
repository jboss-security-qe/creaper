package org.wildfly.extras.creaper.commands.elytron.mapper;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddRegexNameRewriter implements OnlineCommand {

    private final String name;
    private final String pattern;
    private final String replacement;
    private final Boolean replaceAll;
    private final boolean replaceExisting;

    private AddRegexNameRewriter(Builder builder) {
        this.name = builder.name;
        this.pattern = builder.pattern;
        this.replacement = builder.replacement;
        this.replaceAll = builder.replaceAll;
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address regexNameRewriterAddress = Address.subsystem("elytron").and("regex-name-rewriter", name);
        if (replaceExisting) {
            ops.removeIfExists(regexNameRewriterAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(regexNameRewriterAddress, Values.empty()
                .and("pattern", pattern)
                .and("replacement", replacement)
                .andOptional("replace-all", replaceAll));
    }

    public static final class Builder {

        private final String name;
        private String pattern;
        private String replacement;
        private Boolean replaceAll;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the regex-name-rewriter must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the regex-name-rewriter must not be empty value");
            }
            this.name = name;
        }

        public Builder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder replacement(String replacement) {
            this.replacement = replacement;
            return this;
        }

        public Builder replaceAll(boolean replaceAll) {
            this.replaceAll = replaceAll;
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddRegexNameRewriter build() {
            if (pattern == null || pattern.isEmpty()) {
                throw new IllegalArgumentException("Pattern must not be null and must have a minimum length of 1 character");
            }
            if (replacement == null || replacement.isEmpty()) {
                throw new IllegalArgumentException("Replacement must not be null and must have a minimum length of 1 character");
            }
            return new AddRegexNameRewriter(this);
        }
    }
}
