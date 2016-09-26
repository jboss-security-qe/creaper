package org.wildfly.extras.creaper.commands.elytron.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.wildfly.extras.creaper.core.online.OnlineCommand;

abstract class AbstractAddNameRewriter implements OnlineCommand {
    protected final String name;
    protected final List<String> nameRewriters;
    protected final boolean replaceExisting;

    protected AbstractAddNameRewriter(Builder builder) {
        this.name = builder.name;
        this.nameRewriters = builder.nameRewriters;
        this.replaceExisting = builder.replaceExisting;
    }

    abstract static class Builder<THIS extends Builder> {

        protected final String name;
        protected final List<String> nameRewriters = new ArrayList<String>();
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the name-decoder must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the name-decoder must not be empty value");
            }
            this.name = name;
        }

        /**
         * Sets name rewriters that should be aggregated/chained. At least 2 name rewriters
         * must be defined. It is possible to use following types as a name rewriter:
         * <ul>
         * <li>aggregate-name-rewriter</li>
         * <li>chained-name-rewriter</li>
         * <li>constant-name-rewriter</li>
         * <li>custom-name-rewriter</li>
         * <li>regex-name-rewriter</li>
         * <li>regex-name-validating-rewriter</li>
         * </ul>
         *
         * @param nameRewriters previously defined principal-decoder
         * @return builder
         */
        public final THIS nameRewriters(String... nameRewriters) {
            if (nameRewriters == null) {
                throw new IllegalArgumentException("Principal decoder added to aggregate-principal-decoder must not be null");
            }
            Collections.addAll(this.nameRewriters, nameRewriters);
            return (THIS) this;
        }

        public final THIS replaceExisting() {
            this.replaceExisting = true;
            return (THIS) this;
        }

        public abstract AbstractAddNameRewriter build();
    }
}
