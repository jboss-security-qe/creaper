package org.wildfly.extras.creaper.commands.elytron.credentialstore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.commands.elytron.CredentialRef;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.operations.Address;

@RunWith(Arquillian.class)
public class RemoveCredentialStoreAliasOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_CREDENTIAL_STORE_NAME = "CreaperTestCredentialStore";
    private static final Address TEST_CREDENTIAL_STORE_ADDRESS = SUBSYSTEM_ADDRESS
            .and("credential-store", TEST_CREDENTIAL_STORE_NAME);
    private static final String TEST_CREDENTIAL_STORE_ALIAS_NAME = "creapertestcredentialstorealias";
    private static final Address TEST_CREDENTIAL_STORE_ALIAS_ADDRESS = TEST_CREDENTIAL_STORE_ADDRESS
            .and("alias", TEST_CREDENTIAL_STORE_ALIAS_NAME);

    @Before
    public void createCredentialStore() throws Exception {
        AddCredentialStore addCredentialStore = new AddCredentialStore.Builder(TEST_CREDENTIAL_STORE_NAME)
                .uri("cr-store://testUri?create=true")
                .credentialReference(new CredentialRef.CredentialRefBuilder()
                        .clearText("somePassword")
                        .build())
                .build();

        client.apply(addCredentialStore);
    }

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_CREDENTIAL_STORE_ADDRESS);
        ops.removeIfExists(TEST_CREDENTIAL_STORE_ALIAS_ADDRESS);
        administration.reloadIfRequired();
    }

    @Ignore("https://issues.jboss.org/browse/JBEAP-6630")
    @Test
    public void removeCredentialStore() throws Exception {
        AddCredentialStoreAlias addCredentialStoreAlias
                = new AddCredentialStoreAlias.Builder(TEST_CREDENTIAL_STORE_ALIAS_NAME)
                .credentialStore(TEST_CREDENTIAL_STORE_NAME)
                .secretValue("someSecretValue")
                .build();
        client.apply(addCredentialStoreAlias);
        assertTrue("Credential store alias should be created", ops.exists(TEST_CREDENTIAL_STORE_ALIAS_ADDRESS));

        RemoveCredentialStoreAlias removeCredentialStoreAlias
                = new RemoveCredentialStoreAlias(TEST_CREDENTIAL_STORE_NAME, TEST_CREDENTIAL_STORE_ALIAS_NAME);

        client.apply(removeCredentialStoreAlias);
        assertFalse("Credential store alias should be removed", ops.exists(TEST_CREDENTIAL_STORE_ALIAS_ADDRESS));
    }

    @Test(expected = CommandFailedException.class)
    public void removeNonExistingCredentialStore() throws Exception {
        RemoveCredentialStoreAlias removeCredentialStoreAlias
                = new RemoveCredentialStoreAlias(TEST_CREDENTIAL_STORE_NAME, TEST_CREDENTIAL_STORE_ALIAS_NAME);
        client.apply(removeCredentialStoreAlias);

        fail("Specified credential store alias does not exist in configuration, an exception should be thrown");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeCredentialStore_nullCredentialStore() throws Exception {
        new RemoveCredentialStoreAlias(null, TEST_CREDENTIAL_STORE_ALIAS_NAME);
        fail("Creating command with null credential store name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeCredentialStore_emptyCredentialStore() throws Exception {
        new RemoveCredentialStoreAlias("", TEST_CREDENTIAL_STORE_ALIAS_NAME);
        fail("Creating command with empty credential store name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeCredentialStore_nullCredentialStoreAlias() throws Exception {
        new RemoveCredentialStoreAlias(TEST_CREDENTIAL_STORE_NAME, null);
        fail("Creating command with null credential store alias name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeCredentialStore_emptyCredentialStoreAlias() throws Exception {
        new RemoveCredentialStoreAlias(TEST_CREDENTIAL_STORE_NAME, "");
        fail("Creating command with empty credential store alias name should throw exception");
    }
}
