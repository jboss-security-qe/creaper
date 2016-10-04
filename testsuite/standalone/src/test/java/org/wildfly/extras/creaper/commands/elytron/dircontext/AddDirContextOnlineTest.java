package org.wildfly.extras.creaper.commands.elytron.dircontext;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.commands.elytron.tls.AddServerSSLContext;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_DIR_CONTEXT_ADDRESS);
        ops.removeIfExists(TEST_DIR_CONTEXT_ADDRESS2);
        ops.removeIfExists(TEST_SERVER_SSL_CONTEXT_ADDRESS);
        administration.reloadIfRequired();
    }

    @Test
    public void addDirContext() throws Exception {
        AddDirContext addDirContext = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("localhost")
                .authenticationLevel(AddDirContext.AuthenticationLevel.NONE)
                .build();
        client.apply(addDirContext);

        assertTrue("Dir context should be created", ops.exists(TEST_DIR_CONTEXT_ADDRESS));
    }

    @Test
    public void addDirContexts() throws Exception {
        AddDirContext addDirContext = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("localhost")
                .authenticationLevel(AddDirContext.AuthenticationLevel.NONE)
                .build();

        AddDirContext addDirContext2 = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME2)
                .url("localhost")
                .authenticationLevel(AddDirContext.AuthenticationLevel.NONE)
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

        AddDirContext addDirContext = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("localhost")
                .authenticationLevel(AddDirContext.AuthenticationLevel.STRONG)
                .credential("test-credential")
                .enableConnectionPooling(false)
                .principal("test-principal")
                .referralMode(AddDirContext.ReferralMode.THROW)
                .sslContext(TEST_SERVER_SSL_CONTEXT)
                .build();

        client.apply(addDirContext);
        assertTrue("Dir context should be created", ops.exists(TEST_DIR_CONTEXT_ADDRESS));

        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "url", "localhost");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "authentication-level", "STRONG");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "credential", "test-credential");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "enable-connection-pooling", "false");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "principal", "test-principal");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "referral-mode", "THROW");
        checkAttribute(TEST_DIR_CONTEXT_ADDRESS, "ssl-context", TEST_SERVER_SSL_CONTEXT);
    }

    @Test(expected = CommandFailedException.class)
    public void addDirContextNotAllowed() throws Exception {
        AddDirContext addDirContext = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("localhost")
                .authenticationLevel(AddDirContext.AuthenticationLevel.NONE)
                .build();

        AddDirContext addDirContext2 = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("localhost")
                .authenticationLevel(AddDirContext.AuthenticationLevel.NONE)
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
                .authenticationLevel(AddDirContext.AuthenticationLevel.NONE)
                .build();

        AddDirContext addDirContext2 = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("http://www.example.com/")
                .authenticationLevel(AddDirContext.AuthenticationLevel.NONE)
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
                .authenticationLevel(AddDirContext.AuthenticationLevel.NONE)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDirContext_emptyName() throws Exception {
        new AddDirContext.Builder("")
                .url("localhost")
                .authenticationLevel(AddDirContext.AuthenticationLevel.NONE)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDirContext_nullUrl() throws Exception {
        new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url(null)
                .authenticationLevel(AddDirContext.AuthenticationLevel.NONE)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDirContext_emptyUrl() throws Exception {
        new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url("")
                .authenticationLevel(AddDirContext.AuthenticationLevel.NONE)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDirContext_undefinedCredential() throws Exception {
        new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url(null)
                .authenticationLevel(AddDirContext.AuthenticationLevel.SIMPLE)
                .principal("test-principal")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDirContext_undefinedPrincipal() throws Exception {
        new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
                .url(null)
                .authenticationLevel(AddDirContext.AuthenticationLevel.SIMPLE)
                .credential("test-credential")
                .build();
    }
}
