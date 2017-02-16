package org.wildfly.extras.creaper.commands.elytron.tls;

import static org.junit.Assert.fail;
import static org.wildfly.extras.creaper.XmlAssert.assertXmlIdentical;

import java.io.File;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.wildfly.extras.creaper.commands.elytron.CredentialRef.CredentialRefBuilder;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.offline.OfflineManagementClient;
import org.wildfly.extras.creaper.core.offline.OfflineOptions;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class AddKeyStoreOfflineTest {

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

    private static final String SUBSYSTEM_KEY_STORES_EMPTY = ""
            + "<server xmlns=\"urn:jboss:domain:1.7\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:wildfly:elytron:1.0\">\n"
            + "            <tls>\n"
            + "                <key-stores>\n"
            + "                </key-stores>\n"
            + "            </tls>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String SUBSYSTEM_SIMPLE = ""
            + "<server xmlns=\"urn:jboss:domain:1.7\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:wildfly:elytron:1.0\">\n"
            + "            <tls>\n"
            + "                <key-stores>\n"
            + "                    <key-store name=\"creaperKeyStore\" type=\"jks\">\n"
            + "                        <credential-reference clear-text=\"secret\"/>\n"
            + "                    </key-store>\n"
            + "                </key-stores>\n"
            + "            </tls>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String SUBSYSTEM_EXPECTED_REPLACE = ""
            + "<server xmlns=\"urn:jboss:domain:1.7\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:wildfly:elytron:1.0\">\n"
            + "            <tls>\n"
            + "                <key-stores>\n"
            + "                    <key-store name=\"creaperKeyStore\" type=\"jks\">\n"
            + "                        <file path=\"/tmp/keystore.jks\"/>\n"
            + "                        <credential-reference clear-text=\"secret\"/>\n"
            + "                    </key-store>\n"
            + "                </key-stores>\n"
            + "            </tls>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String SUBSYSTEM_SECOND_KEY_STORE = ""
            + "<server xmlns=\"urn:jboss:domain:1.7\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:wildfly:elytron:1.0\">\n"
            + "            <tls>\n"
            + "                <key-stores>\n"
            + "                    <key-store name=\"creaperKeyStore\" type=\"jks\">\n"
            + "                        <credential-reference clear-text=\"secret\"/>\n"
            + "                    </key-store>\n"
            + "                    <key-store name=\"creaperKeyStore2\" type=\"jks\">\n"
            + "                        <credential-reference clear-text=\"secret\"/>\n"
            + "                    </key-store>\n"
            + "                </key-stores>\n"
            + "            </tls>\n"
            + "        </subsystem>\n"
            + "    </profile>\n"
            + "</server>";

    private static final String SUBSYSTEM_FULL = ""
            + "<server xmlns=\"urn:jboss:domain:1.7\">\n"
            + "    <profile>\n"
            + "        <subsystem xmlns=\"urn:wildfly:elytron:1.0\">\n"
            + "            <tls>\n"
            + "                <key-stores>\n"
            + "                    <key-store name=\"creaperKeyStore\" type=\"jks\" provider-name=\"ksProvider\" "
            + "                               providers=\"ksProviderLoader\" alias-filter=\"aliasInFilter\">\n"
            + "                        <file path=\"/tmp/keystore.jks\" relative-to=\"relativeToDir\" required=\"true\"/>\n"
            + "                        <credential-reference alias=\"crAlias\" type=\"crType\" store=\"crStore\" clear-text=\"secret\" />\n"
            + "                    </key-store>\n"
            + "                </key-stores>\n"
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

        AddKeyStore addKeyStore = new AddKeyStore.Builder("creaperKeyStore")
                .type("jks")
                .credentialReference(new CredentialRefBuilder().clearText("secret").build())
                .build();

        assertXmlIdentical(SUBSYSTEM_EMPTY, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addKeyStore);
        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void addSimpleToTlsEmpty() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_TLS_EMPTY, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddKeyStore addKeyStore = new AddKeyStore.Builder("creaperKeyStore")
                .type("jks")
                .credentialReference(new CredentialRefBuilder().clearText("secret").build())
                .build();

        assertXmlIdentical(SUBSYSTEM_TLS_EMPTY, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addKeyStore);
        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void addSimpleToKeyStoresEmpty() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_KEY_STORES_EMPTY, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddKeyStore addKeyStore = new AddKeyStore.Builder("creaperKeyStore")
                .type("jks")
                .credentialReference(new CredentialRefBuilder().clearText("secret").build())
                .build();

        assertXmlIdentical(SUBSYSTEM_KEY_STORES_EMPTY, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addKeyStore);
        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test(expected = CommandFailedException.class)
    public void existing() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_SIMPLE, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddKeyStore addKeyStore = new AddKeyStore.Builder("creaperKeyStore")
                .type("jks")
                .credentialReference(new CredentialRefBuilder().clearText("secret").build())
                .build();

        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addKeyStore);

        fail("Key store creaperKeyStore already exists in configuration, exception should be thrown");
    }

    @Test
    public void overrideExisting() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_SIMPLE, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddKeyStore addKeyStore = new AddKeyStore.Builder("creaperKeyStore")
                .type("jks")
                .path("/tmp/keystore.jks")
                .credentialReference(new CredentialRefBuilder().clearText("secret").build())
                .replaceExisting()
                .build();

        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addKeyStore);
        assertXmlIdentical(SUBSYSTEM_EXPECTED_REPLACE, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void overrideNonExisting() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_SIMPLE, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddKeyStore addKeyStore = new AddKeyStore.Builder("creaperKeyStore2")
                .type("jks")
                .credentialReference(new CredentialRefBuilder().clearText("secret").build())
                .replaceExisting()
                .build();

        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addKeyStore);
        assertXmlIdentical(SUBSYSTEM_SECOND_KEY_STORE, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void addSecondKeyStore() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_SIMPLE, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddKeyStore addKeyStore = new AddKeyStore.Builder("creaperKeyStore2")
                .type("jks")
                .credentialReference(new CredentialRefBuilder().clearText("secret").build())
                .build();

        assertXmlIdentical(SUBSYSTEM_SIMPLE, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addKeyStore);
        assertXmlIdentical(SUBSYSTEM_SECOND_KEY_STORE, Files.toString(cfg, Charsets.UTF_8));
    }

    @Test
    public void addFullToEmpty() throws Exception {
        File cfg = tmp.newFile("xmlTransform.xml");
        Files.write(SUBSYSTEM_EMPTY, cfg, Charsets.UTF_8);

        OfflineManagementClient client = ManagementClient.offline(
                OfflineOptions.standalone().configurationFile(cfg).build());

        AddKeyStore addKeyStore = new AddKeyStore.Builder("creaperKeyStore")
                .type("jks")
                .providerName("ksProvider")
                .providers("ksProviderLoader")
                .aliasFilter("aliasInFilter")
                .path("/tmp/keystore.jks")
                .relativeTo("relativeToDir")
                .required(true)
                .credentialReference(new CredentialRefBuilder()
                        .alias("crAlias")
                        .type("crType")
                        .store("crStore")
                        .clearText("secret")
                        .build())
                .build();

        assertXmlIdentical(SUBSYSTEM_EMPTY, Files.toString(cfg, Charsets.UTF_8));
        client.apply(addKeyStore);
        assertXmlIdentical(SUBSYSTEM_FULL, Files.toString(cfg, Charsets.UTF_8));
    }
}
