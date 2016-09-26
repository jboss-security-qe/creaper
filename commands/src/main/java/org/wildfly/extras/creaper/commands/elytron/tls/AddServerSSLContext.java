package org.wildfly.extras.creaper.commands.elytron.tls;

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
                .andOptional("security-domain", securityDomain));
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
            return new AddServerSSLContext(this);
        }


    }

}
