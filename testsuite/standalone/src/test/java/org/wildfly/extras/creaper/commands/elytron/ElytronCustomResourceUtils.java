package org.wildfly.extras.creaper.commands.elytron;

import java.io.IOException;
import org.wildfly.extras.creaper.commands.modules.RemoveModule;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

public final class ElytronCustomResourceUtils {

    private ElytronCustomResourceUtils() {
    }

    public static void removeCustomModuleIfExists(OnlineManagementClient client, String moduleName) throws IOException,
            CommandFailedException {
        Operations operations = new Operations(client);
        ModelNodeResult result = operations.invoke("list-resource-loader-paths", Address.coreService("module-loading"),
                Values.empty().and("module", moduleName));
        if (result.isSuccess()) {
            RemoveModule removeModule = new RemoveModule(moduleName);
            client.apply(removeModule);
        }
    }

}
