package org.wildfly.extras.creaper.commands.elytron;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.wildfly.extras.creaper.commands.elytron.realm.AddExtensionAndSubsystem;
import org.wildfly.extras.creaper.commands.elytron.realm.RemoveExtensionAndSubsystem;
import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.OnlineOptions;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public abstract class AbstractElytronOnlineTest {

    protected static OnlineManagementClient client;
    protected static Operations ops;
    protected static Administration administration;
    private static boolean removeSubsystemAtTheEnd = false;

    protected static final Address SUBSYSTEM_ADDRESS = Address.subsystem("elytron");

    @BeforeClass
    public static void addSubsystem() throws Exception {
        client = ManagementClient.online(OnlineOptions.standalone().localDefault().build());
        ops = new Operations(client);
        administration = new Administration(client);
        if (!ops.exists(SUBSYSTEM_ADDRESS)) {
            AddExtensionAndSubsystem addExtensionAndSubsystem = new AddExtensionAndSubsystem();
            client.apply(addExtensionAndSubsystem);
            removeSubsystemAtTheEnd = true;
        }
        assertTrue("The Elytron subsystem must exist.", ops.exists(SUBSYSTEM_ADDRESS));
    }

    @AfterClass
    public static void removeSubsystem() throws Exception {
        try {
            if (removeSubsystemAtTheEnd && ops.exists(SUBSYSTEM_ADDRESS)) {
                RemoveExtensionAndSubsystem removeExtensionAndSubsystem = new RemoveExtensionAndSubsystem();
                client.apply(removeExtensionAndSubsystem);
                administration.reloadIfRequired();
                assertFalse("The Elytron was no removed.", ops.exists(SUBSYSTEM_ADDRESS));
            }
        } finally {
            removeSubsystemAtTheEnd = false;
            client.close();
        }
    }
}
