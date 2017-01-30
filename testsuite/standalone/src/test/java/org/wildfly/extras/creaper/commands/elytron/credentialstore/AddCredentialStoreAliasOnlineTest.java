package org.wildfly.extras.creaper.commands.elytron.credentialstore;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.commands.elytron.CredentialRef;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
public class AddCredentialStoreAliasOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_CREDENTIAL_STORE_NAME = "CreaperTestCredentialStore";
    private static final Address TEST_CREDENTIAL_STORE_ADDRESS = SUBSYSTEM_ADDRESS
            .and("credential-store", TEST_CREDENTIAL_STORE_NAME);

    private static final String TEST_CREDENTIAL_STORE_ALIAS_NAME = "creapertestcredentialstorealias";
    private static final Address TEST_CREDENTIAL_STORE_ALIAS_ADDRESS = TEST_CREDENTIAL_STORE_ADDRESS
            .and("alias", TEST_CREDENTIAL_STORE_ALIAS_NAME);
    private static final String TEST_CREDENTIAL_STORE_ALIAS_NAME2 = "creapertestcredentialstorealias2";
    private static final Address TEST_CREDENTIAL_STORE_ALIAS_ADDRESS2 = TEST_CREDENTIAL_STORE_ADDRESS
            .and("alias", TEST_CREDENTIAL_STORE_ALIAS_NAME2);

    @ClassRule
    public static TemporaryFolder tmp = new TemporaryFolder();

    @BeforeClass
    public static void createTmpPath() throws Exception {
        try (OnlineManagementClient client = createManagementClient()) {
            AddTmpDirectoryToPath addTargetToPath = new AddTmpDirectoryToPath();
            client.apply(addTargetToPath);
        }
    }

    @Before
    public void createCredentialStore() throws Exception {
        AddCredentialStore addCredentialStore = new AddCredentialStore.Builder(TEST_CREDENTIAL_STORE_NAME)
                .uri("cr-store://test.testCs")
                .credentialReference(new CredentialRef.CredentialRefBuilder()
                        .clearText("somePassword")
                        .build())
                .relativeTo("tmp")
                .build();

        client.apply(addCredentialStore);
    }

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_CREDENTIAL_STORE_ALIAS_ADDRESS);
        ops.removeIfExists(TEST_CREDENTIAL_STORE_ALIAS_ADDRESS2);
        ops.removeIfExists(TEST_CREDENTIAL_STORE_ADDRESS);
        administration.reloadIfRequired();
    }

    @Test
    public void addSimpleCredentialStoreAlias() throws Exception {
        AddCredentialStoreAlias addCredentialStoreAlias
                = new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME)
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue("someSecretValue")
                .build();

        client.apply(addCredentialStoreAlias);

        assertTrue("Credential store alias should be created", ops.exists(TEST_CREDENTIAL_STORE_ALIAS_ADDRESS));
    }

    @Ignore("https://issues.jboss.org/browse/JBEAP-6611")
    @Test
    public void addFullCredentialStoreAlias() throws Exception {
        AddCredentialStoreAlias addCredentialStoreAlias
                = new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME)
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue("someSecretValue")
                .entryType(AddCredentialStoreAlias.EntryType.PASSWORD_CREDENTIAL)
                .build();

        client.apply(addCredentialStoreAlias);
        assertTrue("Credential store alias should be created", ops.exists(TEST_CREDENTIAL_STORE_ALIAS_ADDRESS));
    }

    @Test
    public void addTwoCredentialStoreAliases() throws Exception {
        AddCredentialStoreAlias addCredentialStoreAlias
                = new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME)
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue("someSecretValue")
                .build();

        AddCredentialStoreAlias addCredentialStoreAlias2
                = new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME2)
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue("someOtherValue")
                .build();

        client.apply(addCredentialStoreAlias);
        client.apply(addCredentialStoreAlias2);

        assertTrue("Credential store alias should be created", ops.exists(TEST_CREDENTIAL_STORE_ALIAS_ADDRESS));
        assertTrue("Second credential store alias should be created", ops.exists(TEST_CREDENTIAL_STORE_ALIAS_ADDRESS2));
    }

    @Test(expected = CommandFailedException.class)
    public void addExistCredentialStoreAliasNotAllowed() throws Exception {
        AddCredentialStoreAlias addCredentialStoreAlias
                = new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME)
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue("someSecretValue")
                .build();

        AddCredentialStoreAlias addCredentialStoreAlias2
                = new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME)
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue("someOtherValue")
                .build();

        client.apply(addCredentialStoreAlias);
        assertTrue("Credential store alias should be created", ops.exists(TEST_CREDENTIAL_STORE_ADDRESS));
        client.apply(addCredentialStoreAlias2);
        fail("Credential store alias creapertestcredentialstorealias already exists in configuration, exception should be thrown");
    }

    @Ignore("https://issues.jboss.org/browse/JBEAP-6630")
    @Test
    public void addExistCredentialStoreAliasAllowed() throws Exception {
        AddCredentialStoreAlias addCredentialStoreAlias
                = new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME)
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue("someSecretValue")
                .build();

        AddCredentialStoreAlias addCredentialStoreAlias2
                = new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME)
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue("someOtherValue")
                .replaceExisting()
                .build();

        client.apply(addCredentialStoreAlias);
        assertTrue("Credential store alias should be created", ops.exists(TEST_CREDENTIAL_STORE_ADDRESS));
        client.apply(addCredentialStoreAlias2);
        assertTrue("Credential store alias should be created", ops.exists(TEST_CREDENTIAL_STORE_ADDRESS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCredentialStoreAlias_nullName() throws Exception {
        new AddCredentialStoreAlias.Builder(null)
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue("someSecretValue")
                .build();

        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCredentialStoreAlias_emptyName() throws Exception {
        new AddCredentialStoreAlias.Builder("")
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue("someSecretValue")
                .build();

        fail("Creating command with empty name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCredentialStoreAlias_nullCredentialStore() throws Exception {
        new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME)
                .credentialStore(null)
                .secretValue("someSecretValue")
                .build();

        fail("Creating command with null credential-store should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCredentialStoreAlias_emptyCredentialStore() throws Exception {
        new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME)
                .credentialStore("")
                .secretValue("someSecretValue")
                .build();

        fail("Creating command with empty credential-store should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCredentialStoreAlias_nullSecretValue() throws Exception {
        new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME)
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue(null)
                .build();

        fail("Creating command with null secret-value should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCredentialStoreAlias_emptySecretValue() throws Exception {
        new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME)
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue("")
                .build();

        fail("Creating command with empty secret-value should throw exception");
    }

    private static final class AddTmpDirectoryToPath implements OnlineCommand {

        @Override
        public void apply(OnlineCommandContext ctx) throws Exception {
            Operations ops = new Operations(ctx.client);
            Address pathAddress = Address.root()
                    .and("path", "tmp");

            ops.add(pathAddress, Values.empty()
                    .and("path", tmp.getRoot().getAbsolutePath()));
        }
    }

}
