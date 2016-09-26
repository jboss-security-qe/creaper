package org.wildfly.extras.creaper.commands.elytron.mapper;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.wildfly.extras.creaper.commands.elytron.mapper.AbstractAddNameRewriterOnlineTest.TEST_CONSTANT_NAME_REWRITER_NAME;

@RunWith(Arquillian.class)
public class AddChainedNameRewriterOnlineTest extends AbstractAddNameRewriterOnlineTest {

    private static final String TEST_CHAINED_NAME_REWRITER_NAME = "CreaperTestChainedNameRewriter";
    private static final Address TEST_CHAINED_NAME_REWRITER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("chained-name-rewriter", TEST_CHAINED_NAME_REWRITER_NAME);
    private static final String TEST_CHAINED_NAME_REWRITER_NAME2 = "CreaperTestChainedNameRewriter2";
    private static final Address TEST_CHAINED_NAME_REWRITER_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("chained-name-rewriter", TEST_CHAINED_NAME_REWRITER_NAME2);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_CHAINED_NAME_REWRITER_ADDRESS);
        ops.removeIfExists(TEST_CHAINED_NAME_REWRITER_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addChainedNameRewriter() throws Exception {
        AddChainedNameRewriter addChainedNameRewriter
                = new AddChainedNameRewriter.Builder(TEST_CHAINED_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2)
                .build();

        client.apply(addChainedNameRewriter);

        assertTrue("Chained-name-rewriter should be created", ops.exists(TEST_CHAINED_NAME_REWRITER_ADDRESS));
        checkAttribute(TEST_CHAINED_NAME_REWRITER_ADDRESS, "name-rewriters", NAME_REWRITERS_1_AND_2);
    }

    @Test
    public void addTwoChainedNameRewriters() throws Exception {
        AddChainedNameRewriter addChainedNameRewriter
                = new AddChainedNameRewriter.Builder(TEST_CHAINED_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2)
                .build();

        AddChainedNameRewriter addChainedNameRewriter2
                = new AddChainedNameRewriter.Builder(TEST_CHAINED_NAME_REWRITER_NAME2)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME2, TEST_DIFFERENT_NAME_REWRITER_NAME)
                .build();

        client.apply(addChainedNameRewriter);
        client.apply(addChainedNameRewriter2);

        assertTrue("Chained-name-rewriter should be created", ops.exists(TEST_CHAINED_NAME_REWRITER_ADDRESS));
        assertTrue("Chained-name-rewriter should be created", ops.exists(TEST_CHAINED_NAME_REWRITER_ADDRESS2));
    }

    @Test(expected = CommandFailedException.class)
    public void addExistChainedNameRewritersNotAllowed() throws Exception {
        AddChainedNameRewriter addChainedNameRewriter
                = new AddChainedNameRewriter.Builder(TEST_CHAINED_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2)
                .build();

        AddChainedNameRewriter addChainedNameRewriter2
                = new AddChainedNameRewriter.Builder(TEST_CHAINED_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME2, TEST_DIFFERENT_NAME_REWRITER_NAME)
                .build();

        client.apply(addChainedNameRewriter);
        assertTrue("Chained-name-rewriter should be created", ops.exists(TEST_CHAINED_NAME_REWRITER_ADDRESS));

        client.apply(addChainedNameRewriter2);
        fail("Chained-name-rewriter CreaperTestChainedNameRewriter already exists in configuration, exception should be thrown");
    }

    @Test
    public void addExistChainedNameRewritersAllowed() throws Exception {
        AddChainedNameRewriter addChainedNameRewriter
                = new AddChainedNameRewriter.Builder(TEST_CHAINED_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2)
                .build();

        AddChainedNameRewriter addChainedNameRewriter2
                = new AddChainedNameRewriter.Builder(TEST_CHAINED_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME2, TEST_DIFFERENT_NAME_REWRITER_NAME)
                .replaceExisting()
                .build();

        client.apply(addChainedNameRewriter);
        assertTrue("Chained-name-rewriter should be created", ops.exists(TEST_CHAINED_NAME_REWRITER_ADDRESS));

        client.apply(addChainedNameRewriter2);
        assertTrue("Chained-name-rewriter should be created", ops.exists(TEST_CHAINED_NAME_REWRITER_ADDRESS));
        checkAttribute(TEST_CHAINED_NAME_REWRITER_ADDRESS, "name-rewriters", NAME_REWRITERS_2_AND_DIFFERENT);
    }

    @Test(expected = CommandFailedException.class)
    public void addChainedNameRewriterWithoutConfiguredNameRewriters() throws Exception {
        AddChainedNameRewriter addChainedNameRewriter
                = new AddChainedNameRewriter.Builder(TEST_CHAINED_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, "NotConfiguredNameRewriter")
                .build();

        client.apply(addChainedNameRewriter);
        fail("Chained-name-rewriter shouldn't be added when using unconfigured name rewriter");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChainedNameRewriter_nullName() throws Exception {
        new AddChainedNameRewriter.Builder(null)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2)
                .build();
        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChainedNameRewriter_emptyName() throws Exception {
        new AddChainedNameRewriter.Builder("")
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2)
                .build();
        fail("Creating command with empty name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChainedNameRewriter_nullNameRewriters() throws Exception {
        new AddChainedNameRewriter.Builder(TEST_CHAINED_NAME_REWRITER_NAME)
                .nameRewriters(null)
                .build();
        fail("Creating command with null name-rewriters should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChainedNameRewriter_emptyNameRewriters() throws Exception {
        new AddChainedNameRewriter.Builder(TEST_CHAINED_NAME_REWRITER_NAME)
                .nameRewriters("")
                .build();
        fail("Creating command with empty name-rewriters should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChainedNameRewriter_oneNameRewriter() throws Exception {
        new AddChainedNameRewriter.Builder(TEST_CHAINED_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME)
                .build();
        fail("Creating command with only one name-rewriter should throw exception");
    }

}
