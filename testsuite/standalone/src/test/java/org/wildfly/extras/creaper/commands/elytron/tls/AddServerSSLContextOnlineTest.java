package org.wildfly.extras.creaper.commands.elytron.tls;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.operations.Address;

@RunWith(Arquillian.class)
public class AddServerSSLContextOnlineTest extends AbstractAddSSLContextOnlineTest {

    private static final String SERVER_SSL_CONTEXT_PROTOCOL = "TLSv1.2";
    private static final String SERVER_SSL_CONTEXT_NAME = "CreaperTestServerSSLContext";
    private static final String SERVER_SSL_CONTEXT_NAME2 = "CreaperTestServerSSLContext2";
    private static final Address SERVER_SSL_CONTEXT_ADDRESS = SUBSYSTEM_ADDRESS.and("server-ssl-context",
            SERVER_SSL_CONTEXT_NAME);
    private static final Address SERVER_SSL_CONTEXT_ADDRESS2 = SUBSYSTEM_ADDRESS.and("server-ssl-context",
            SERVER_SSL_CONTEXT_NAME2);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(SERVER_SSL_CONTEXT_ADDRESS);
        ops.removeIfExists(SERVER_SSL_CONTEXT_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addSimpleServerSSLContext() throws Exception {
        AddServerSSLContext addServerSSLContext = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
                .keyManager(TEST_KEY_MNGR_NAME)
                .build();
        assertFalse("The server ssl context should not exist", ops.exists(SERVER_SSL_CONTEXT_ADDRESS));
        client.apply(addServerSSLContext);
        assertTrue("Server ssl context should be created", ops.exists(SERVER_SSL_CONTEXT_ADDRESS));
    }

    @Test
    public void addTwoSimpleServerSSLContexts() throws Exception {
        AddServerSSLContext addServerSSLContext = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
                .keyManager(TEST_KEY_MNGR_NAME)
                .build();
        AddServerSSLContext addServerSSLContext2 = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME2)
                .keyManager(TEST_KEY_MNGR_NAME)
                .build();

        assertFalse("The server ssl context should not exist", ops.exists(SERVER_SSL_CONTEXT_ADDRESS));
        assertFalse("The server ssl context should not exist", ops.exists(SERVER_SSL_CONTEXT_ADDRESS2));

        client.apply(addServerSSLContext);
        client.apply(addServerSSLContext2);

        assertTrue("Server SSL context should be created", ops.exists(SERVER_SSL_CONTEXT_ADDRESS));
        assertTrue("Server SSL context should be created", ops.exists(SERVER_SSL_CONTEXT_ADDRESS2));
    }

    @Test(expected = CommandFailedException.class)
    public void addDuplicateServerSSLContextNotAllowed() throws Exception {
        AddServerSSLContext addServerSSLContext = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
                .keyManager(TEST_KEY_MNGR_NAME)
                .build();
        AddServerSSLContext addServerSSLContext2 = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
                .keyManager(TEST_KEY_MNGR_NAME)
                .build();

        client.apply(addServerSSLContext);
        assertTrue("The server ssl context should be created", ops.exists(SERVER_SSL_CONTEXT_ADDRESS));

        client.apply(addServerSSLContext2);
        fail("Server ssl context is already configured, exception should be thrown");
    }

    @Test
    public void addDuplicateKeyManagerAllowed() throws Exception {
        AddServerSSLContext addServerSSLContext = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
                .keyManager(TEST_KEY_MNGR_NAME)
                .build();
        AddServerSSLContext addServerSSLContext2 = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
                .keyManager(TEST_KEY_MNGR_NAME)
                .sessionTimeout(5)
                .replaceExisting()
                .build();

        client.apply(addServerSSLContext);
        assertTrue("The server ssl context should be created", ops.exists(SERVER_SSL_CONTEXT_ADDRESS));

        client.apply(addServerSSLContext2);
        assertTrue("The server ssl context should be created", ops.exists(SERVER_SSL_CONTEXT_ADDRESS));
        // check whether it was really rewritten
        checkAttribute(SERVER_SSL_CONTEXT_ADDRESS, "session-timeout", "5");
    }

    @Test
    public void addFullServerSSLContext() throws Exception {
        AddServerSSLContext addServerSSLContext = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
                .cipherSuiteFilter("ALL")
                .keyManager(TEST_KEY_MNGR_NAME)
                .trustManager(TRUST_MNGR_NAME)
                .maximumSessionCacheSize(0)
                .sessionTimeout(0)
                .protocols(SERVER_SSL_CONTEXT_PROTOCOL)
                .needClientAuth(true)
                .wantClientAuth(true)
                .authenticationOptional(true)
//                .securityDomain("security-domain")
                .build();
        client.apply(addServerSSLContext);
        assertTrue("The server ssl context should be created", ops.exists(SERVER_SSL_CONTEXT_ADDRESS));

        checkAttribute("cipher-suite-filter", "ALL");
        checkAttribute("key-manager", TEST_KEY_MNGR_NAME);
        checkAttribute("trust-manager", TRUST_MNGR_NAME);
        checkAttribute("maximum-session-cache-size", "0");
        checkAttribute("session-timeout", "0");
        checkAttribute("protocols", Arrays.asList(SERVER_SSL_CONTEXT_PROTOCOL));
        checkAttribute("need-client-auth", "true");
        checkAttribute("want-client-auth", "true");
        checkAttribute("authentication-optional", "true");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServerSSLContext_nullName() throws Exception {
        new AddServerSSLContext.Builder(null)
            .keyManager(TEST_KEY_MNGR_NAME)
            .build();
        fail("Creating command with null server SSL context name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServerSSLContext_emptyName() throws Exception {
        new AddServerSSLContext.Builder("")
            .keyManager(TEST_KEY_MNGR_NAME)
            .build();
        fail("Creating command with empty server ssl context name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addKeyManager_nullKeyManager() throws Exception {
        new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
            .keyManager(null)
            .build();
        fail("Creating command with null key manager should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addKeyManager_emptyAlgorithm() throws Exception {
        new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
            .keyManager("")
            .build();
        fail("Creating command with empty key manager should throw exception");
    }

    private void checkAttribute(String attribute, String expectedValue) throws IOException {
        checkAttribute(SERVER_SSL_CONTEXT_ADDRESS, attribute, expectedValue);
    }

    private void checkAttribute(String attribute, List<String> expected) throws IOException {
        checkAttribute(SERVER_SSL_CONTEXT_ADDRESS, attribute, expected);
    }

}
