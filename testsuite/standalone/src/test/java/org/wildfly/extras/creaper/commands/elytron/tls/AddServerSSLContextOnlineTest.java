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
                .build();
        assertFalse("The server ssl context should not exist", ops.exists(SERVER_SSL_CONTEXT_ADDRESS));
        client.apply(addServerSSLContext);
        assertTrue("Server ssl context should be created", ops.exists(SERVER_SSL_CONTEXT_ADDRESS));
    }

    @Test
    public void addTwoSimpleServerSSLContexts() throws Exception {
        AddServerSSLContext addServerSSLContext = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
                .build();
        AddServerSSLContext addServerSSLContext2 = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME2)
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
                .build();
        AddServerSSLContext addServerSSLContext2 = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
                .build();

        client.apply(addServerSSLContext);
        assertTrue("The server ssl context should be created", ops.exists(SERVER_SSL_CONTEXT_ADDRESS));

        client.apply(addServerSSLContext2);
        fail("Server ssl context is already configured, exception should be thrown");
    }

    @Test
    public void addDuplicateKeyManagerAllowed() throws Exception {
        AddServerSSLContext addServerSSLContext = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
                .build();
        AddServerSSLContext addServerSSLContext2 = new AddServerSSLContext.Builder(SERVER_SSL_CONTEXT_NAME)
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
                .keyManagers(TEST_KEY_MNGR_NAME)
                .trustManagers(TRUST_MNGR_NAME)
                .maximumSessionCacheSize(0)
                .sessionTimeout(0)
                .protocols("TLSv1_2")
                .needClientAuth(true)
                .wantClientAuth(true)
                .authenticationOptional(true)
//                .securityDomain("security-domain")
                .build();
        client.apply(addServerSSLContext);
        assertTrue("The server ssl context should be created", ops.exists(SERVER_SSL_CONTEXT_ADDRESS));

        checkAttribute("cipher-suite-filter", "ALL");
        checkAttribute("key-managers", TEST_KEY_MNGR_NAME);
        checkAttribute("trust-managers", TRUST_MNGR_NAME);
        checkAttribute("maximum-session-cache-size", "0");
        checkAttribute("session-timeout", "0");
        checkAttribute("protocols", Arrays.asList("TLSv1_2"));
        checkAttribute("need-client-auth", "true");
        checkAttribute("want-client-auth", "true");
        checkAttribute("authentication-optional", "true");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServerSSLContext_nullName() throws Exception {
        new AddServerSSLContext.Builder(null)
            .build();
        fail("Creating command with null server SSL context name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServerSSLContext_emptyName() throws Exception {
        new AddServerSSLContext.Builder("")
            .build();
        fail("Creating command with empty server ssl context name should throw exception");
    }

    private void checkAttribute(String attribute, String expectedValue) throws IOException {
        checkAttribute(SERVER_SSL_CONTEXT_ADDRESS, attribute, expectedValue);
    }

    private void checkAttribute(String attribute, List<String> expected) throws IOException {
        checkAttribute(SERVER_SSL_CONTEXT_ADDRESS, attribute, expected);
    }

}
