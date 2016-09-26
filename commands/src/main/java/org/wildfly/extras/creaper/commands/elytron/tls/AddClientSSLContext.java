package org.wildfly.extras.creaper.commands.elytron.tls;

import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddClientSSLContext extends AbstractAddSSLContext {

    private AddClientSSLContext(Builder builder) {
        super(builder);
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address clientSSLContextAddress = Address.subsystem("elytron").and("client-ssl-context", name);
        if (replaceExisting) {
            ops.removeIfExists(clientSSLContextAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(clientSSLContextAddress, Values.empty()
                .andOptional("cipher-suite-filter", cipherSuiteFilter)
                .andOptional("maximum-session-cache-size", maximumSessionCacheSize)
                .andOptional("session-timeout", sessionTimeout)
                .andOptional("key-managers", keyManagers)
                .andOptional("trust-managers", trustManagers)
                .andListOptional(String.class, "protocols", protocols));
    }

    public static final class Builder extends AbstractAddSSLContext.Builder<Builder> {

        public Builder(String name) {
            super(name);
        }

        @Override
        public AddClientSSLContext build() {
            return new AddClientSSLContext(this);
        }

    }

}
