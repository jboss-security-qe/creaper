package org.wildfly.extras.creaper.commands.elytron.credentialstore;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

public final class RemoveCredentialStoreAlias implements OnlineCommand {

    private final String credentialStoreName;
    private final String credentialStoreAlias;

    public RemoveCredentialStoreAlias(String credentialStoreName, String credentialStoreAlias) {
        if (credentialStoreName == null) {
            throw new IllegalArgumentException("Name of the credential-store must be specified as non null value");
        }
        if (credentialStoreName.isEmpty()) {
            throw new IllegalArgumentException("Name of the credential-store must not be empty value");
        }
        if (credentialStoreAlias == null) {
            throw new IllegalArgumentException("Alias of the credential-store must be specified as non null value");
        }
        if (credentialStoreAlias.isEmpty()) {
            throw new IllegalArgumentException("Alias of the credential-store must not be empty value");
        }

        this.credentialStoreName = credentialStoreName;
        this.credentialStoreAlias = credentialStoreAlias;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Address credentialStoreAliasAddress = Address.subsystem("elytron")
                .and("credential-store", credentialStoreName)
                .and("alias", credentialStoreAlias);

        Operations ops = new Operations(ctx.client);
        ops.remove(credentialStoreAliasAddress);
    }

}
