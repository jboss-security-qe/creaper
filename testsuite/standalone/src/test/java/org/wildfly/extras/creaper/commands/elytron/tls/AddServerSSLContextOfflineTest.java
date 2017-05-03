package org.wildfly.extras.creaper.commands.elytron.tls;

import static org.junit.Assert.fail;
import static org.wildfly.extras.creaper.XmlAssert.assertXmlIdentical;

import java.io.File;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.offline.OfflineManagementClient;
import org.wildfly.extras.creaper.core.offline.OfflineOptions;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class AddServerSSLContextOfflineTest {

    private static final String SUBSYSTEM_EMPTY = ""
            + "<server xmlns=\"urn:jboss:domain:1.7\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:wildfly:elytron:1.0\">\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String SUBSYSTEM_TLS_EMPTY = ""
            + "<server xmlns=\"urn:jboss:domain:1.7\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:wildfly:elytron:1.0\">\n"
            + "            <tls>\n"
            + "            </tls>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String SUBSYSTEM_SERVER_SSL_CONTEXTS_EMPTY = ""
            + "<server xmlns=\"urn:jboss:domain:1.7\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:wildfly:elytron:1.0\">\n"
            + "            <tls>\n"
            + "                <server-ssl-contexts>\n"
            + "                </server-ssl-contexts>\n"
            + "            </tls>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String SUBSYSTEM_SIMPLE = ""
            + "<server xmlns=\"urn:jboss:domain:1.7\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:wildfly:elytron:1.0\">\n"
            + "            <tls>\n"
            + "                <server-ssl-contexts>\n"
            + "                    <server-ssl-context name=\"serverSslContext\" key-managers=\"keyManagers\"/>\n"
            + "                </server-ssl-contexts>\n"
            + "            </tls>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String SUBSYSTEM_EXPECTED_REPLACE = ""
            + "<server xmlns=\"urn:jboss:domain:1.7\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:wildfly:elytron:1.0\">\n"
            + "            <tls>\n"
            + "                <server-ssl-contexts>\n"
            + "                    <server-ssl-context name=\"serverSslContext\" key-managers=\"keyManagers\" cipher-suite-filter=\"ALL\"/>\n"
            + "                </server-ssl-contexts>\n"
            + "            </tls>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String SUBSYSTEM_SECOND_SERVER_SSL_CONTEXT = ""
            + "<server xmlns=\"urn:jboss:domain:1.7\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:wildfly:elytron:1.0\">\n"
            + "            <tls>\n"
            + "                <server-ssl-contexts>\n"
            + "                    <server-ssl-context name=\"serverSslContext\" key-managers=\"keyManagers\"/>\n"
            + "                    <server-ssl-context name=\"serverSslContext2\" key-managers=\"keyManagers\"/>\n"
            + "                </server-ssl-contexts>\n"
            + "            </tls>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String SUBSYSTEM_FULL = ""
            + "<server xmlns=\"urn:jboss:domain:1.7\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:wildfly:elytron:1.0\">\n"
            + "            <tls>\n"
            + "                <server-ssl-contexts>\n"
            + "                    <server-ssl-context name=\"serverSslContext\" cipher-suite-filter=\"ALL\" "
            + "                                        maximum-session-cache-size=\"40\" session-timeout=\"30\" "
            + "                                        key-managers=\"keyManagers\" trust-managers=\"trustManagers\" "
            + "                                        protocols=\"TLSv1.2 TLSv1.1\" authentication-optional=\"true\" "
            + "                                        need-client-auth=\"false\" want-client-auth=\"true\" "
            + "                                        security-domain=\"securityDomain\" provider-name=\"ksProvider\" providers=\"ksProviderLoader\"/>\n"
            + "                </server-ssl-contexts>\n"
            + "            </tls>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Before
    public void setUp() {
        XMLUnit.setNormalizeWhitespace(true);
    }

    @Test
    public void addSimpleToEmpty() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_EMPTY, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddServerSSLContext addServerSslContext = new AddServerSSLContext.Builder("serverSslContext")
                .keyManagers("keyManagers")
                .build();

        assertXmlIdentical(SUBSYSTEM_EMPTY, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addServerSslContext);
        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void addSimpleToTlsEmpty() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_TLS_EMPTY, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddServerSSLContext addServerSslContext = new AddServerSSLContext.Builder("serverSslContext")
                .keyManagers("keyManagers")
                .build();

        assertXmlIdentical(SUBSYSTEM_TLS_EMPTY, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addServerSslContext);
        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void addSimpleToServerSslContextsEmpty() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_SERVER_SSL_CONTEXTS_EMPTY, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddServerSSLContext addServerSslContext = new AddServerSSLContext.Builder("serverSslContext")
                .keyManagers("keyManagers")
                .build();

        assertXmlIdentical(SUBSYSTEM_SERVER_SSL_CONTEXTS_EMPTY, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addServerSslContext);
        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test(expected = CommandFailedException.class)
    public void existing() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_SIMPLE, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddServerSSLContext addServerSslContext = new AddServerSSLContext.Builder("serverSslContext")
                .keyManagers("keyManagers")
                .build();

        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addServerSslContext);

        fail("Server SSL context serverSslContext already exists in configuration, exception should be thrown");
    }

    @Test
    public void overrideExisting() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_SIMPLE, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddServerSSLContext addServerSslContext = new AddServerSSLContext.Builder("serverSslContext")
                .keyManagers("keyManagers")
                .cipherSuiteFilter("ALL")
                .replaceExisting()
                .build();

        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addServerSslContext);
        assertXmlIdentical(SUBSYSTEM_EXPECTED_REPLACE, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void overrideNonExisting() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_SIMPLE, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddServerSSLContext addServerSslContext = new AddServerSSLContext.Builder("serverSslContext2")
                .keyManagers("keyManagers")
                .replaceExisting()
                .build();

        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addServerSslContext);
        assertXmlIdentical(SUBSYSTEM_SECOND_SERVER_SSL_CONTEXT, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void addSecondServerSslContext() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_SIMPLE, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddServerSSLContext addServerSslContext = new AddServerSSLContext.Builder("serverSslContext2")
                .keyManagers("keyManagers")
                .replaceExisting()
                .build();

        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addServerSslContext);
        assertXmlIdentical(SUBSYSTEM_SECOND_SERVER_SSL_CONTEXT, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void addFullToEmpty() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_EMPTY, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddServerSSLContext addServerSslContext = new AddServerSSLContext.Builder("serverSslContext")
                .cipherSuiteFilter("ALL")
                .maximumSessionCacheSize(40)
                .sessionTimeout(30)
                .keyManagers("keyManagers")
                .trustManagers("trustManagers")
                .protocols("TLSv1.2", "TLSv1.1")
                .authenticationOptional(true)
                .needClientAuth(false)
                .wantClientAuth(true)
                .securityDomain("securityDomain")
                .providerName("ksProvider")
                .providers("ksProviderLoader")
                .build();

        assertXmlIdentical(SUBSYSTEM_EMPTY, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addServerSslContext);
        assertXmlIdentical(SUBSYSTEM_FULL, Files.toString(cfg, Charsets.UTF_8));
    }
}
