package org.wildfly.extras.creaper.commands.elytron;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
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

    protected OnlineManagementClient client;
    protected Operations ops;
    protected Administration administration;
    private static boolean removeSubsystemAtTheEnd = false;

    protected static final Address SUBSYSTEM_ADDRESS = Address.subsystem("elytron");

    @BeforeClass
    public static void addSubsystem() throws Exception {
        try (OnlineManagementClient client = createManagementClient()) {
            Operations ops = new Operations(client);
            if (!ops.exists(SUBSYSTEM_ADDRESS)) {
                AddExtensionAndSubsystem addExtensionAndSubsystem = new AddExtensionAndSubsystem();
                client.apply(addExtensionAndSubsystem);
                removeSubsystemAtTheEnd = true;
            }
            assertTrue("The Elytron subsystem must exist.", ops.exists(SUBSYSTEM_ADDRESS));
        }
    }

    @AfterClass
    public static void removeSubsystem() throws Exception {
        try (OnlineManagementClient client = createManagementClient()) {
            Operations ops = new Operations(client);
            Administration administration = new Administration(client);
            if (removeSubsystemAtTheEnd && ops.exists(SUBSYSTEM_ADDRESS)) {
                RemoveExtensionAndSubsystem removeExtensionAndSubsystem = new RemoveExtensionAndSubsystem();
                client.apply(removeExtensionAndSubsystem);
                administration.reload();
                assertFalse("The Elytron subsystem should not be present anymore.",
                        ops.exists(SUBSYSTEM_ADDRESS));
            }
        }
    }

    @Before
    public void setupCreaperForTest() throws IOException {
        client = createManagementClient();
        ops = new Operations(client);
        administration = new Administration(client);
    }

    @After
    public void tearDownCreaperForTest() throws IOException {
        client.close();
    }

    protected static OnlineManagementClient createManagementClient() throws IOException {
        return ManagementClient.online(OnlineOptions.standalone().localDefault().build());
    }
}
