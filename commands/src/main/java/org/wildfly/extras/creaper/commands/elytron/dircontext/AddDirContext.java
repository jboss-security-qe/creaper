package org.wildfly.extras.creaper.commands.elytron.dircontext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jboss.dmr.ModelNode;
import org.wildfly.extras.creaper.commands.elytron.CredentialRef;
import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddDirContext implements OnlineCommand {

    private final String name;
    private final String url;
    private final AuthenticationLevel authenticationLevel;
    private final String principal;
    private final Boolean enableConnectionPooling;
    private final String sslContext;
    private final ReferralMode referralMode;
    private final List<Property> properties;
    private final CredentialRef credentialReference;
    private final boolean replaceExisting;

    private AddDirContext(Builder builder) {
        this.name = builder.name;
        this.url = builder.url;
        this.authenticationLevel = builder.authenticationLevel;
        this.principal = builder.principal;
        this.enableConnectionPooling = builder.enableConnectionPooling;
        this.sslContext = builder.sslContext;
        this.referralMode = builder.referralMode;
        this.replaceExisting = builder.replaceExisting;
        this.properties = builder.properties;
        this.credentialReference = builder.credentialReference;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address dirContextAddress = Address.subsystem("elytron")
                .and("dir-context", name);
        if (replaceExisting) {
            ops.removeIfExists(dirContextAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ModelNode propertiesNode = null;
        if (properties != null && !properties.isEmpty()) {
            propertiesNode = new ModelNode();
            for (Property property : properties) {
                propertiesNode.add(property.getKey(), property.getValue());
            }
            propertiesNode = propertiesNode.asObject();
        }

        String authenticationModeValue = authenticationLevel == null ? null : authenticationLevel.name();
        String referralModeValue = referralMode == null ? null : referralMode.name();
        Values credentialReferenceValues = credentialReference != null ? credentialReference.toValues() : null;

        ops.add(dirContextAddress, Values.empty()
                .and("url", url)
                .andOptional("authentication-level", authenticationModeValue)
                .andOptional("principal", principal)
                .andOptional("enable-connection-pooling", enableConnectionPooling)
                .andOptional("ssl-context", sslContext)
                .andOptional("referral-mode", referralModeValue)
                .andOptional("properties", propertiesNode)
                .andObjectOptional("credential-reference", credentialReferenceValues));
    }

    /**
     * If SIMPLE or STRONG authentication level is used then you must also define principal and credential.
     *
     */
    public static final class Builder {

        private final String name;
        private String url;
        private AuthenticationLevel authenticationLevel;
        private String principal;
        private Boolean enableConnectionPooling;
        private String sslContext;
        private ReferralMode referralMode;
        private List<Property> properties = new ArrayList<Property>();
        private CredentialRef credentialReference;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the dir-context must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the dir-context must not be empty value");
            }
            this.name = name;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder authenticationLevel(AuthenticationLevel authenticationLevel) {
            this.authenticationLevel = authenticationLevel;
            return this;
        }

        public Builder principal(String principal) {
            this.principal = principal;
            return this;
        }

        public Builder enableConnectionPooling(boolean enableConnectionPooling) {
            this.enableConnectionPooling = enableConnectionPooling;
            return this;
        }

        public Builder sslContext(String sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        public Builder referralMode(ReferralMode referralMode) {
            this.referralMode = referralMode;
            return this;
        }

        public Builder addMechanismProperties(Property... properties) {
            if (properties == null) {
                throw new IllegalArgumentException("Properties added to authentication-configuration must not be null");
            }
            Collections.addAll(this.properties, properties);
            return this;
        }

        public Builder credentialReference(CredentialRef credentialReference) {
            this.credentialReference = credentialReference;
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddDirContext build() {
            if (url == null || url.isEmpty()) {
                throw new IllegalArgumentException("url must not be null or empty");
            }
            return new AddDirContext(this);
        }
    }

    public static final class Property {

        private final String key;
        private final String value;

        public Property(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

    }

    public static enum AuthenticationLevel {

        NONE, SIMPLE, STRONG
    }

    public static enum ReferralMode {

        FOLLOW, IGNORE, THROW
    }
}
