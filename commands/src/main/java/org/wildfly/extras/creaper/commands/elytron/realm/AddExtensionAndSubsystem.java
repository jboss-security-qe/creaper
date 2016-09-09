package org.wildfly.extras.creaper.commands.elytron.realm;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddExtensionAndSubsystem implements OnlineCommand {

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        ops.add(Address.extension("org.wildfly.extension.elytron"));
        ops.add(Address.subsystem("elytron"));
        new Administration(ctx.client).reloadIfRequired();
    }

}
