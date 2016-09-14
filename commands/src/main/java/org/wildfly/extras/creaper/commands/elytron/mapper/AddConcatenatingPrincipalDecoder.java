package org.wildfly.extras.creaper.commands.elytron.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddConcatenatingPrincipalDecoder implements OnlineCommand {

    private final String name;
    private final String joiner;
    private final List<String> principalDecoders;
    private final boolean replaceExisting;

    public AddConcatenatingPrincipalDecoder(Builder builder) {
        this.name = builder.name;
        this.joiner = builder.joiner;
        this.principalDecoders = builder.principalDecoders;
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address constantPrincipalDecoderAddress = Address.subsystem("elytron")
                .and("concatenating-principal-decoder", name);
        if (replaceExisting) {
            ops.removeIfExists(constantPrincipalDecoderAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(constantPrincipalDecoderAddress, Values.empty()
                .andList(String.class, "principal-decoders", principalDecoders)
                .andOptional("joiner", joiner));
    }

    public static final class Builder {

        private final String name;
        private String joiner;
        private final List<String> principalDecoders = new ArrayList<String>();
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the concatenating-principal-decoder must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the concatenating-principal-decoder must not be empty value");
            }
            this.name = name;
        }

        public Builder joiner(String joiner) {
            if (joiner == null || joiner.isEmpty()) {
                throw new IllegalArgumentException("Joiner must not be null and must have a minimum length of 1 character");
            }
            this.joiner = joiner;
            return this;
        }

        /**
         * Sets principal decoders that should be concatenated. At least 2 principal decoders must be defined.
         * It is possible to use following types as a principal decoder:
         * <ul>
         * <li>customPrincipalDecoderType</li>
         * <li>aggregate-principal-decoder</li>
         * <li>concatenating-principal-decoder</li>
         * <li>constant-principal-decoder</li>
         * <li>x500-attribute-principal-decoder</li>
         * </ul>
         *
         * @param principalDecoders previously defined principal-decoder
         * @return builder
         */
        public Builder principalDecoders(String... principalDecoders) {
            if (principalDecoders == null) {
                throw new IllegalArgumentException("Principal decoder added to concatenating-principal-decoder must not be null");
            }
            Collections.addAll(this.principalDecoders, principalDecoders);
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddConcatenatingPrincipalDecoder build() {
            if (principalDecoders.size() < 2) {
                throw new IllegalArgumentException("There must be at least two principal-decoders");
            }
            return new AddConcatenatingPrincipalDecoder(this);
        }
    }

}
