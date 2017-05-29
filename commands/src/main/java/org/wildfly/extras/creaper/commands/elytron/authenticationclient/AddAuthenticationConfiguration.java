package org.wildfly.extras.creaper.commands.elytron.authenticationclient;

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

public final class AddAuthenticationConfiguration implements OnlineCommand {

    private final String name;
    private final List<Property> mechanismProperties;
    private final CredentialRef credentialReference;
    private final Boolean allowAllMechanisms;
    private final List<String> allowSaslMechanisms;
    private final Boolean anonymous;
    private final String authenticationName;
    private final String authorizationName;
    private final String extend;
    private final List<String> forbidSaslMechanisms;
    private final String host;
    private final Integer port;
    private final String protocol;
    private final String realm;
    private final String securityDomain;
    private final String saslMechanismSelector;
    private final String kerberosSecurityFactory;
    private final boolean replaceExisting;

    private AddAuthenticationConfiguration(Builder builder) {
        this.name = builder.name;
        this.mechanismProperties = builder.mechanismProperties;
        this.credentialReference = builder.credentialReference;
        this.allowAllMechanisms = builder.allowAllMechanisms;
        this.allowSaslMechanisms = builder.allowSaslMechanisms;
        this.anonymous = builder.anonymous;
        this.authenticationName = builder.authenticationName;
        this.authorizationName = builder.authorizationName;
        this.extend = builder.extend;
        this.forbidSaslMechanisms = builder.forbidSaslMechanisms;
        this.host = builder.host;
        this.port = builder.port;
        this.protocol = builder.protocol;
        this.realm = builder.realm;
        this.securityDomain = builder.securityDomain;
        this.saslMechanismSelector = builder.saslMechanismSelector;
        this.kerberosSecurityFactory = builder.kerberosSecurityFactory;
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address realmAddress = Address.subsystem("elytron").and("authentication-configuration", name);
        if (replaceExisting) {
            ops.removeIfExists(realmAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ModelNode mechanismPropertiesNode = null;
        if (mechanismProperties != null && !mechanismProperties.isEmpty()) {
            mechanismPropertiesNode = new ModelNode();
            for (Property property : mechanismProperties) {
                mechanismPropertiesNode.add(property.getKey(), property.getValue());
            }
            mechanismPropertiesNode = mechanismPropertiesNode.asObject();
        }

        Values credentialReferenceValues = credentialReference != null ? credentialReference.toValues() : null;

        ops.add(realmAddress, Values.empty()
                .andOptional("extends", extend)
                .andOptional("anonymous", anonymous)
                .andOptional("authentication-name", authenticationName)
                .andOptional("authorization-name", authorizationName)
                .andOptional("host", host)
                .andOptional("protocol", protocol)
                .andOptional("port", port)
                .andOptional("realm", realm)
                .andOptional("security-domain", securityDomain)
                .andOptional("allow-all-mechanisms", allowAllMechanisms)
                .andOptional("mechanism-properties", mechanismPropertiesNode)
                .andOptional("sasl-mechanism-selector", saslMechanismSelector)
                .andOptional("kerberos-security-factory", kerberosSecurityFactory)
                .andObjectOptional("credential-reference", credentialReferenceValues)
                .andListOptional(String.class, "allow-sasl-mechanisms", allowSaslMechanisms)
                .andListOptional(String.class, "forbid-sasl-mechanisms", forbidSaslMechanisms));
    }

    public static final class Builder {

        private String name;
        private List<Property> mechanismProperties = new ArrayList<Property>();
        private CredentialRef credentialReference;
        private Boolean allowAllMechanisms;
        private List<String> allowSaslMechanisms;
        private Boolean anonymous;
        private String authenticationName;
        private String authorizationName;
        private String extend;
        private List<String> forbidSaslMechanisms;
        private String host;
        private Integer port;
        private String protocol;
        private String realm;
        private String securityDomain;
        private String saslMechanismSelector;
        private String kerberosSecurityFactory;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the authentication-configuration must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the authentication-configuration must not be empty value");
            }
            this.name = name;
        }

        public Builder addMechanismProperties(Property... mechanismProperties) {
            if (mechanismProperties == null) {
                throw new IllegalArgumentException("MechanismProperties added to authentication-configuration must not be null");
            }
            Collections.addAll(this.mechanismProperties, mechanismProperties);
            return this;
        }

        public Builder credentialReference(CredentialRef credentialReference) {
            this.credentialReference = credentialReference;
            return this;
        }

        public Builder allowAllMechanisms(Boolean allowAllMechanisms) {
            this.allowAllMechanisms = allowAllMechanisms;
            return this;
        }

        public Builder addAllowSaslMechanisms(String... allowSaslMechanisms) {
            if (allowSaslMechanisms == null) {
                throw new IllegalArgumentException("AllowSaslMechanisms added to authentication-configuration must not be null");
            }
            if (this.allowSaslMechanisms == null) {
                this.allowSaslMechanisms = new ArrayList<String>();
            }
            Collections.addAll(this.allowSaslMechanisms, allowSaslMechanisms);
            return this;
        }

        public Builder anonymous(Boolean anonymous) {
            this.anonymous = anonymous;
            return this;
        }

        public Builder authenticationName(String authenticationName) {
            this.authenticationName = authenticationName;
            return this;
        }

        public Builder authorizationName(String authorizationName) {
            this.authorizationName = authorizationName;
            return this;
        }

        public Builder extend(String extend) {
            this.extend = extend;
            return this;
        }

        public Builder addForbidSaslMechanisms(String... forbidSaslMechanisms) {
            if (forbidSaslMechanisms == null) {
                throw new IllegalArgumentException("ForbidSaslMechanisms added to authentication-configuration must not be null");
            }
            if (this.forbidSaslMechanisms == null) {
                this.forbidSaslMechanisms = new ArrayList<String>();
            }
            Collections.addAll(this.forbidSaslMechanisms, forbidSaslMechanisms);
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder realm(String realm) {
            this.realm = realm;
            return this;
        }

        public Builder securityDomain(String securityDomain) {
            this.securityDomain = securityDomain;
            return this;
        }

        public Builder saslMechanismSelector(String saslMechanismSelector) {
            this.saslMechanismSelector = saslMechanismSelector;
            return this;
        }

        public Builder kerberosSecurityFactory(String kerberosSecurityFactory) {
            this.kerberosSecurityFactory = kerberosSecurityFactory;
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddAuthenticationConfiguration build() {
            if (allowAllMechanisms != null && (allowSaslMechanisms != null && !allowSaslMechanisms.isEmpty())) {
                throw new IllegalArgumentException("Only one of allow-all-mechanisms and allow-sasl-mechanisms can be set.");
            }

            int authCounter = 0;
            if (authenticationName != null) {
                authCounter++;
            }
            if (anonymous != null) {
                authCounter++;
            }
            if (securityDomain != null) {
                authCounter++;
            }
            if (kerberosSecurityFactory != null) {
                authCounter++;
            }
            if (authCounter > 1) {
                throw new IllegalArgumentException("Only one of authentication-name, anonymous, security-domain and kerberos-security-factory can be set.");
            }
            return new AddAuthenticationConfiguration(this);
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
}
