package org.wildfly.extras.creaper.commands.elytron.tls;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.net.ssl.TrustManagerFactory;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

@RunWith(Arquillian.class)
public class AddTrustManagerOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_KEY_STORE_NAME = "CreaperTestKeyStore";
    private static final String TEST_KEY_STORE_NAME2 = "CreaperTestKeyStore2";
    private static final Address TEST_KEY_STORE_ADDRESS = SUBSYSTEM_ADDRESS.and("key-store", TEST_KEY_STORE_NAME);
    private static final Address TEST_KEY_STORE_ADDRESS2 = SUBSYSTEM_ADDRESS.and("key-store", TEST_KEY_STORE_NAME2);
    private static final String TEST_KEY_STORE_TYPE = "JKS";

    private static final String TRUST_MNGR_NAME = "CreaperTestTrustManager";
    private static final String TRUST_MNGR_NAME2 = "CreaperTestTrustManager2";
    private static final Address TRUST_MNGR_ADDRESS = SUBSYSTEM_ADDRESS.and("trust-managers", TRUST_MNGR_NAME);
    private static final Address TRUST_MANAGER_ADDRESS2 = SUBSYSTEM_ADDRESS.and("trust-managers", TRUST_MNGR_NAME2);
    private static final String TEST_TRUST_MANAGER_ALGORITHM = TrustManagerFactory.getDefaultAlgorithm();

    @BeforeClass
    public static void addKeyStores() throws Exception {
        try (OnlineManagementClient client = createManagementClient()) {
            AddKeyStore addKeyStore = new AddKeyStore.Builder(TEST_KEY_STORE_NAME)
                    .type(TEST_KEY_STORE_TYPE)
                    .build();
            AddKeyStore addKeyStore2 = new AddKeyStore.Builder(TEST_KEY_STORE_NAME2)
                    .type(TEST_KEY_STORE_TYPE)
                    .build();

            client.apply(addKeyStore);
            client.apply(addKeyStore2);
        }
    }

    @AfterClass
    public static void removeKeyStores() throws Exception {
        try (OnlineManagementClient client = createManagementClient()) {
            Operations ops = new Operations(client);
            Administration administration = new Administration(client);
            ops.removeIfExists(TEST_KEY_STORE_ADDRESS);
            ops.removeIfExists(TEST_KEY_STORE_ADDRESS2);
            administration.reloadIfRequired();
        }
    }

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TRUST_MNGR_ADDRESS);
        ops.removeIfExists(TRUST_MANAGER_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addSimpleTrustManager() throws Exception {
        AddTrustManager addTrustManager = new AddTrustManager.Builder(TRUST_MNGR_NAME)
                .algorithm(TEST_TRUST_MANAGER_ALGORITHM)
                .keyStore(TEST_KEY_STORE_NAME)
                .build();
        assertFalse("The trust manager should not exist", ops.exists(TRUST_MNGR_ADDRESS));
        client.apply(addTrustManager);
        assertTrue("Trust manager should be created", ops.exists(TRUST_MNGR_ADDRESS));
    }

    @Test
    public void addTwoSimpleTrustManagers() throws Exception {
        AddTrustManager addTrustManager = new AddTrustManager.Builder(TRUST_MNGR_NAME)
                .algorithm(TEST_TRUST_MANAGER_ALGORITHM)
                .keyStore(TEST_KEY_STORE_NAME)
                .build();
        AddTrustManager addTrustManager2 = new AddTrustManager.Builder(TRUST_MNGR_NAME2)
                .algorithm(TEST_TRUST_MANAGER_ALGORITHM)
                .keyStore(TEST_KEY_STORE_NAME2)
                .build();

        assertFalse("The trust manager should not exist", ops.exists(TRUST_MNGR_ADDRESS));
        assertFalse("The trust manager should not exist", ops.exists(TRUST_MANAGER_ADDRESS2));

        client.apply(addTrustManager);
        client.apply(addTrustManager2);

        assertTrue("Trust manager should be created", ops.exists(TRUST_MNGR_ADDRESS));
        assertTrue("Trust manager should be created", ops.exists(TRUST_MANAGER_ADDRESS2));
    }

    @Test(expected = CommandFailedException.class)
    public void addDuplicateTrustManagersNotAllowed() throws Exception {
        AddTrustManager addTrustManager = new AddTrustManager.Builder(TRUST_MNGR_NAME)
                .algorithm(TEST_TRUST_MANAGER_ALGORITHM)
                .keyStore(TEST_KEY_STORE_NAME)
                .build();
        AddTrustManager addTrustManager2 = new AddTrustManager.Builder(TRUST_MNGR_NAME)
                .algorithm(TEST_TRUST_MANAGER_ALGORITHM)
                .keyStore(TEST_KEY_STORE_NAME)
                .build();

        client.apply(addTrustManager);
        assertTrue("The trust manager should be created", ops.exists(TRUST_MNGR_ADDRESS));

        client.apply(addTrustManager2);
        fail("Trust manager is already configured, exception should be thrown");
    }

    @Test
    public void addDuplicateTrustManagerAllowed() throws Exception {
        AddTrustManager addTrustManager = new AddTrustManager.Builder(TRUST_MNGR_NAME)
                .algorithm(TEST_TRUST_MANAGER_ALGORITHM)
                .keyStore(TEST_KEY_STORE_NAME)
                .build();
        AddTrustManager addTrustManager2 = new AddTrustManager.Builder(TRUST_MNGR_NAME)
                .algorithm(TEST_TRUST_MANAGER_ALGORITHM)
                .keyStore(TEST_KEY_STORE_NAME)
                .replaceExisting()
                .build();

        client.apply(addTrustManager);
        assertTrue("The trust manager should be created", ops.exists(TRUST_MNGR_ADDRESS));

        client.apply(addTrustManager2);
        assertTrue("The trust manager should be created", ops.exists(TRUST_MNGR_ADDRESS));
    }

    @Test
    public void addFullTrustManager() throws Exception {
        AddTrustManager addTrustManager = new AddTrustManager.Builder(TRUST_MNGR_NAME)
                .algorithm(TEST_TRUST_MANAGER_ALGORITHM)
                .keyStore(TEST_KEY_STORE_NAME)
                .build();
        client.apply(addTrustManager);
        assertTrue("Trust manager should be created", ops.exists(TRUST_MNGR_ADDRESS));

        checkAttribute("algorithm", TEST_TRUST_MANAGER_ALGORITHM);
        checkAttribute("key-store", TEST_KEY_STORE_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTrustManager_nullName() throws Exception {
        new AddTrustManager.Builder(null)
            .algorithm(TEST_TRUST_MANAGER_ALGORITHM)
            .build();
        fail("Creating command with null trust manager name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTrustManager_emptyName() throws Exception {
        new AddTrustManager.Builder("")
            .algorithm(TEST_TRUST_MANAGER_ALGORITHM)
            .build();
        fail("Creating command with empty trust manager name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTrustManager_nullAlgorithm() throws Exception {
        new AddTrustManager.Builder(TRUST_MNGR_NAME)
            .algorithm(null)
            .build();
        fail("Creating command with null trust manager algorithm should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addTrustManager_emptyAlgorithm() throws Exception {
        new AddTrustManager.Builder(TRUST_MNGR_NAME)
            .algorithm("")
            .build();
        fail("Creating command with empty trust manager algorithm should throw exception");
    }

    private void checkAttribute(String attribute, String expectedValue) throws IOException {
        checkAttribute(TRUST_MNGR_ADDRESS, attribute, expectedValue);
    }
}
