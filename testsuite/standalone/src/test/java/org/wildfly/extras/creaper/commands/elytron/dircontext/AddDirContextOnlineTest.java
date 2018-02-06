package org.wildfly.extras.creaper.commands.elytron.dircontext;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.commands.elytron.tls.AddServerSSLContext;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.commands.elytron.CredentialRef;
import org.wildfly.extras.creaper.commands.elytron.authenticationclient.AddAuthenticationContext;

@RunWith(Arquillian.class)
public class AddDirContextOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_DIR_CONTEXT_NAME = "CreaperTestDirContext";
    private static final Address TEST_DIR_CONTEXT_ADDRESS = SUBSYSTEM_ADDRESS
            .and("dir-context", TEST_DIR_CONTEXT_NAME);
    private static final String TEST_DIR_CONTEXT_NAME2 = "CreaperTestDirContext2";
    private static final Address TEST_DIR_CONTEXT_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("dir-context", TEST_DIR_CONTEXT_NAME2);

    private static final String TEST_SERVER_SSL_CONTEXT = "CreaperTestSslContext";
    private static final Address TEST_SERVER_SSL_CONTEXT_ADDRESS = SUBSYSTEM_ADDRESS
            .and("server-ssl-context", TEST_SERVER_SSL_CONTEXT);

    private static final String TEST_AUTHENTICATION_CONTEXT_NAME = "CreaperTestAuthenticationContext";
    private static final Address TEST_AUTHENTICATION_CONTEXT_ADDRESS = SUBSYSTEM_ADDRESS
            .and("authentication-context", TEST_AUTHENTICATION_CONTEXT_NAME);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_DIR_CONTEXT_ADDRESS);
        ops.removeIfExists(TEST_DIR_CONTEXT_ADDRESS2);
        ops.removeIfExists(TEST_SERVER_SSL_CONTEXT_ADDRESS);
        ops.removeIfExists(TEST_AUTHENTICATION_CONTEXT_ADDRESS);
        administration.reloadIfRequired();
    }

    @Test
    public void addDirContext() throws Exception {
        AddDirContext addDirContext = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("localhost")
                .build();
        client.apply(addDirContext);

        assertTrue("Dir context should be created", ops.exists(TEST_DIR_CONTEXT_ADDRESS));
    }

    @Test
    public void addDirContexts() throws Exception {
        AddDirContext addDirContext = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("localhost")
                .build();

        AddDirContext addDirContext2 = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME2)
                .url("localhost")
                .build();

        client.apply(addDirContext);
        client.apply(addDirContext2);

        assertTrue("Dir context should be created", ops.exists(TEST_DIR_CONTEXT_ADDRESS));
        assertTrue("Second dir context should be created", ops.exists(TEST_DIR_CONTEXT_ADDRESS2));
    }

    @Test
    public void addFullDirContext() throws Exception {
        AddServerSSLContext addServerSSLContext = new AddServerSSLContext.Builder(TEST_SERVER_SSL_CONTEXT)
                .build();
        client.apply(addServerSSLContext);
        AddAuthenticationContext addAuthenticationContext
                = new AddAuthenticationContext.Builder(TEST_AUTHENTICATION_CONTEXT_NAME)
                .build();
        client.apply(addAuthenticationContext);

        AddDirContext addDirContext = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("localhost")
                .authenticationLevel(AddDirContext.AuthenticationLevel.STRONG)
                .enableConnectionPooling(false)
                .principal("test-principal")
                .referralMode(AddDirContext.ReferralMode.THROW)
                .authenticationContext(TEST_AUTHENTICATION_CONTEXT_NAME)
                .connectionTimeout(10)
                .readTimeout(20)
                .sslContext(TEST_SERVER_SSL_CONTEXT)
                .credentialReference(new CredentialRef.CredentialRefBuilder()
                        .clearText("somePassword")
                        .build())
                .addMechanismProperties(new AddDirContext.Property("property1", "value1"),
                        new AddDirContext.Property("property2", "value2"))
                .build();

        client.apply(addDirContext);
        assertTrue("Dir context should be created", ops.exists(TEST_DIR_CONTEXT_ADDRESS));

        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "url", "localhost");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "authentication-level", "STRONG");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "enable-connection-pooling", "false");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "principal", "test-principal");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "referral-mode", "THROW");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "authentication-context", TEST_AUTHENTICATION_CONTEXT_NAME);
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "connection-timeout", "10");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "read-timeout", "20");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "ssl-context", TEST_SERVER_SSL_CONTEXT);
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "credential-reference.clear-text", "somePassword");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "properties.property1", "value1");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "properties.property2", "value2");
    }

    @Test(expected = CommandFailedException.class)
    public void addDirContextNotAllowed() throws Exception {
        AddDirContext addDirContext = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("localhost")
                .build();

        AddDirContext addDirContext2 = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("localhost")
                .build();

        client.apply(addDirContext);
        assertTrue("Dir context should be created", ops.exists(TEST_DIR_CONTEXT_ADDRESS));
        client.apply(addDirContext2);
        fail("Dir Context CreaperTestDirContext already exists in configuration, exception should be thrown");
    }

    @Test
    public void addDirContextAllowed() throws Exception {
        AddDirContext addDirContext = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("localhost")
                .build();

        AddDirContext addDirContext2 = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("http://www.example.com/")
                .replaceExisting()
                .build();

        client.apply(addDirContext);
        assertTrue("Dir context should be created", ops.exists(TEST_DIR_CONTEXT_ADDRESS));
        client.apply(addDirContext2);
        assertTrue("Dir context should be created", ops.exists(TEST_DIR_CONTEXT_ADDRESS));
        // check whether it was really rewritten
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "url", "http://www.example.com/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDirContext_nullName() throws Exception {
        new AddDirContext.Builder(null)
                .url("localhost")
                .build();
        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDirContext_emptyName() throws Exception {
        new AddDirContext.Builder("")
                .url("localhost")
                .build();
        fail("Creating command with empty name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDirContext_nullUrl() throws Exception {
        new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url(null)
                .build();
        fail("Creating command with null url should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDirContext_emptyUrl() throws Exception {
        new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("")
                .build();
        fail("Creating command with empty url should throw exception");
    }

}
