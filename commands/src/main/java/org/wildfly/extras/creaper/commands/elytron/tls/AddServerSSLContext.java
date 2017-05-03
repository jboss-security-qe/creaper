package org.wildfly.extras.creaper.commands.elytron.tls;

import org.wildfly.extras.creaper.commands.foundation.offline.xml.GroovyXmlTransform;
import org.wildfly.extras.creaper.commands.foundation.offline.xml.Subtree;
import org.wildfly.extras.creaper.core.offline.OfflineCommandContext;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddServerSSLContext extends AbstractAddSSLContext {

    private final Boolean authenticationOptional;
    private final Boolean needClientAuth;
    private final Boolean wantClientAuth;
    private final String securityDomain;

    private AddServerSSLContext(Builder builder) {
        super(builder);
        this.authenticationOptional = builder.authenticationOptional;
        this.needClientAuth = builder.needClientAuth;
        this.wantClientAuth = builder.wantClientAuth;
        this.securityDomain = builder.securityDomain;

    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address serverSSLContextAddress = Address.subsystem("elytron").and("server-ssl-context", name);
        if (replaceExisting) {
            ops.removeIfExists(serverSSLContextAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(serverSSLContextAddress, Values.empty()
                .andOptional("cipher-suite-filter", cipherSuiteFilter)
                .andOptional("maximum-session-cache-size", maximumSessionCacheSize)
                .andOptional("session-timeout", sessionTimeout)
                .andOptional("key-managers", keyManagers)
                .andOptional("trust-managers", trustManagers)
                .andListOptional(String.class, "protocols", protocols)
                .andOptional("authentication-optional", authenticationOptional)
                .andOptional("need-client-auth", needClientAuth)
                .andOptional("want-client-auth", wantClientAuth)
                .andOptional("security-domain", securityDomain)
                .andOptional("providers", providers)
                .andOptional("provider-name", providerName));
    }

    @Override
    public void apply(OfflineCommandContext ctx) throws Exception {
        ctx.client.apply(GroovyXmlTransform.of(AddServerSSLContext.class)
                .subtree("elytronSubsystem", Subtree.subsystem("elytron"))
                .parameter("atrName", name)
                .parameter("atrCipherSuiteFilter", cipherSuiteFilter)
                .parameter("atrMaximumSessionCacheSize", maximumSessionCacheSize)
                .parameter("atrSessionTimeout", sessionTimeout)
                .parameter("atrKeyManagers", keyManagers)
                .parameter("atrTrustManagers", trustManagers)
                .parameter("atrProtocols", protocols != null ? String.join(" ", protocols) : null)
                .parameter("atrAuthenticationOptional", authenticationOptional)
                .parameter("atrNeedClientAuth", needClientAuth)
                .parameter("atrWantClientAuth", wantClientAuth)
                .parameter("atrSecurityDomain", securityDomain)
                .parameter("atrProviders", providers)
                .parameter("atrProviderName", providerName)
                .parameter("atrReplaceExisting", replaceExisting)
                .build());
    }

    public static final class Builder extends AbstractAddSSLContext.Builder<Builder> {

        private Boolean authenticationOptional;
        private Boolean needClientAuth;
        private Boolean wantClientAuth;
        private String securityDomain;

        public Builder(String name) {
            super(name);
        }

        public Builder authenticationOptional(Boolean authenticationOptional) {
            this.authenticationOptional = authenticationOptional;
            return this;
        }

        public Builder needClientAuth(Boolean needClientAuth) {
            this.needClientAuth = needClientAuth;
            return this;
        }

        public Builder wantClientAuth(Boolean wantClientAuth) {
            this.wantClientAuth = wantClientAuth;
            return this;
        }

        public Builder securityDomain(String securityDomain) {
            this.securityDomain = securityDomain;
            return this;
        }

        @Override
        public AddServerSSLContext build() {
            if (keyManagers == null || keyManagers.isEmpty()) {
                throw new IllegalArgumentException("Key-manager must be specified as non empty value");
            }
            return new AddServerSSLContext(this);
        }


    }

}
