package org.wildfly.extras.creaper.commands.elytron.mapper;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.commands.modules.AddModule;
import org.wildfly.extras.creaper.commands.modules.RemoveModule;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;


@RunWith(Arquillian.class)
public class AddCustomNameRewriterOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_ADD_CUSTOM_NAME_REWRITER_NAME = "CreaperTestAddCustomNameRewriter";
    private static final Address TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS = SUBSYSTEM_ADDRESS.and("custom-name-rewriter",
        TEST_ADD_CUSTOM_NAME_REWRITER_NAME);
    private static final String TEST_ADD_CUSTOM_NAME_REWRITER_NAME2 = "CreaperTestAddCustomNameRewriter2";
    private static final Address TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS2 = SUBSYSTEM_ADDRESS.and("custom-name-rewriter",
        TEST_ADD_CUSTOM_NAME_REWRITER_NAME2);
    private static final String CUSTOM_NAME_REWRITER_MODULE_NAME = "org.jboss.customnamerewriterimpl";

    @BeforeClass
    public static void setUp() throws IOException, CommandFailedException, InterruptedException, TimeoutException {
        try (OnlineManagementClient client = createManagementClient()) {
            File testJar1 = createJar("testJar", AddCustomNameRewriterImpl.class);
            AddModule addModule = new AddModule.Builder(CUSTOM_NAME_REWRITER_MODULE_NAME)
                    .resource(testJar1)
                    .resourceDelimiter(":")
                .dependency("org.wildfly.security.elytron")
                .dependency("org.wildfly.extension.elytron")
                    .build();
            client.apply(addModule);
        }
    }

    @AfterClass
    public static void afterClass() throws IOException, CommandFailedException, InterruptedException, TimeoutException {
        try (OnlineManagementClient client = createManagementClient()) {
            RemoveModule removeModule = new RemoveModule(CUSTOM_NAME_REWRITER_MODULE_NAME);
            client.apply(removeModule);
        }
    }

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS);
        ops.removeIfExists(TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addCustomNameRewriter() throws Exception {
        AddCustomNameRewriter addAddCustomNameRewriter =
            new AddCustomNameRewriter.Builder(TEST_ADD_CUSTOM_NAME_REWRITER_NAME)
            .className(AddCustomNameRewriterImpl.class.getName())
            .module(CUSTOM_NAME_REWRITER_MODULE_NAME)
            .addConfiguration("param", "parameterValue")
            .build();

        client.apply(addAddCustomNameRewriter);

        assertTrue("Add custom name rewriter should be created",
            ops.exists(TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS));
    }

    @Test
    public void addCustomNameRewriters() throws Exception {
        AddCustomNameRewriter addAddCustomNameRewriter =
            new AddCustomNameRewriter.Builder(TEST_ADD_CUSTOM_NAME_REWRITER_NAME)
            .className(AddCustomNameRewriterImpl.class.getName())
            .module(CUSTOM_NAME_REWRITER_MODULE_NAME)
            .build();

        AddCustomNameRewriter addAddCustomNameRewriter2 =
            new AddCustomNameRewriter.Builder(TEST_ADD_CUSTOM_NAME_REWRITER_NAME2)
            .className(AddCustomNameRewriterImpl.class.getName())
            .module(CUSTOM_NAME_REWRITER_MODULE_NAME)
            .build();

        client.apply(addAddCustomNameRewriter);
        client.apply(addAddCustomNameRewriter2);

        assertTrue("Add custom name rewriter should be created",
            ops.exists(TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS));
        assertTrue("Second add custom name rewriter should be created",
            ops.exists(TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS2));

        checkAttribute(TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS, "class-name",
            AddCustomNameRewriterImpl.class.getName());
        checkAttribute(TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS2, "class-name",
            AddCustomNameRewriterImpl.class.getName());

        administration.reload();

        checkAttribute(TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS, "class-name",
            AddCustomNameRewriterImpl.class.getName());
        checkAttribute(TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS2, "class-name",
            AddCustomNameRewriterImpl.class.getName());
    }

    @Test(expected = CommandFailedException.class)
    public void addDuplicateCustomNameRewriterNotAllowed() throws Exception {
        AddCustomNameRewriter addAddCustomNameRewriter =
            new AddCustomNameRewriter.Builder(TEST_ADD_CUSTOM_NAME_REWRITER_NAME)
            .className(AddCustomNameRewriterImpl.class.getName())
            .module(CUSTOM_NAME_REWRITER_MODULE_NAME)
            .build();

        client.apply(addAddCustomNameRewriter);
        assertTrue("Add custom name rewriter should be created",
            ops.exists(TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS));
        client.apply(addAddCustomNameRewriter);
        fail("Add custom name rewriter " + TEST_ADD_CUSTOM_NAME_REWRITER_NAME
            + " already exists in configuration, exception should be thrown");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCustomNameRewriter_nullName() throws Exception {
        new AddCustomNameRewriter.Builder(null);
        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAddCustomNameRewriter_emptyName() throws Exception {
        new AddCustomNameRewriter.Builder("");
        fail("Creating command with empty name should throw exception");
    }

    @Test(expected = CommandFailedException.class)
    public void addCustomNameRewriter_noModule() throws Exception {
        AddCustomNameRewriter addAddCustomNameRewriter =
            new AddCustomNameRewriter.Builder(TEST_ADD_CUSTOM_NAME_REWRITER_NAME)
            .className(AddCustomNameRewriterImpl.class.getName())
            .build();

        client.apply(addAddCustomNameRewriter);

        assertTrue("Add custom name rewriter should be created",
            ops.exists(TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCustomNameRewriter_noClassName() throws Exception {
        new AddCustomNameRewriter.Builder(TEST_ADD_CUSTOM_NAME_REWRITER_NAME).build();
        fail("Creating command with no custom should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCustomNameRewriter_emptyClassName() throws Exception {
        new AddCustomNameRewriter.Builder(TEST_ADD_CUSTOM_NAME_REWRITER_NAME)
            .className("")
            .build();

        fail("Creating command with empty classname should throw exception");
    }

    @Test(expected = CommandFailedException.class)
    public void addCustomNameRewriter_wrongModule() throws Exception {
        AddCustomNameRewriter addAddCustomNameRewriter =
            new AddCustomNameRewriter.Builder(TEST_ADD_CUSTOM_NAME_REWRITER_NAME)
            .className(AddCustomNameRewriterImpl.class.getName())
            .module("wrongModule")
            .build();

        client.apply(addAddCustomNameRewriter);

        assertTrue("Add custom name rewriter should be created",
            ops.exists(TEST_ADD_CUSTOM_NAME_REWRITER_ADDRESS));
    }

    @Test(expected = CommandFailedException.class)
    public void addCustomNameRewriter_configurationWithException() throws Exception {
        AddCustomNameRewriter addAddCustomNameRewriter =
            new AddCustomNameRewriter.Builder(TEST_ADD_CUSTOM_NAME_REWRITER_NAME)
            .className(AddCustomNameRewriterImpl.class.getName())
            .module(CUSTOM_NAME_REWRITER_MODULE_NAME)
            .addConfiguration("throwException", "parameterValue")
            .build();

        client.apply(addAddCustomNameRewriter);

        fail("Creating command with test configuration should throw exception");
    }
}
