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
public class AddAggregateNameRewriterOnlineTest extends AbstractAddNameRewriterOnlineTest {

    private static final String TEST_AGGREGATE_NAME_REWRITER_NAME = "CreaperTestAggregateNameRewriter";
    private static final Address TEST_AGGREGATE_NAME_REWRITER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("aggregate-name-rewriter", TEST_AGGREGATE_NAME_REWRITER_NAME);
    private static final String TEST_AGGREGATE_NAME_REWRITER_NAME2 = "CreaperTestAggregateNameRewriter2";
    private static final Address TEST_AGGREGATE_NAME_REWRITER_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("aggregate-name-rewriter", TEST_AGGREGATE_NAME_REWRITER_NAME2);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_AGGREGATE_NAME_REWRITER_ADDRESS);
        ops.removeIfExists(TEST_AGGREGATE_NAME_REWRITER_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addAggregateNameRewriter() throws Exception {
        AddAggregateNameRewriter addAggregateNameRewriter
                = new AddAggregateNameRewriter.Builder(TEST_AGGREGATE_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2)
                .build();

        client.apply(addAggregateNameRewriter);

        assertTrue("Aggregate-name-rewriter should be created", ops.exists(TEST_AGGREGATE_NAME_REWRITER_ADDRESS));
        checkAttribute(TEST_AGGREGATE_NAME_REWRITER_ADDRESS, "name-rewriters", NAME_REWRITERS_1_AND_2);
    }

    @Test
    public void addTwoAggregateNameRewriters() throws Exception {
        AddAggregateNameRewriter addAggregateNameRewriter
                = new AddAggregateNameRewriter.Builder(TEST_AGGREGATE_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2)
                .build();

        AddAggregateNameRewriter addAggregateNameRewriter2
                = new AddAggregateNameRewriter.Builder(TEST_AGGREGATE_NAME_REWRITER_NAME2)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME2, TEST_DIFFERENT_NAME_REWRITER_NAME)
                .build();

        client.apply(addAggregateNameRewriter);
        client.apply(addAggregateNameRewriter2);

        assertTrue("Aggregate-name-rewriter should be created", ops.exists(TEST_AGGREGATE_NAME_REWRITER_ADDRESS));
        assertTrue("Aggregate-name-rewriter should be created", ops.exists(TEST_AGGREGATE_NAME_REWRITER_ADDRESS2));
    }

    @Test(expected = CommandFailedException.class)
    public void addExistAggregateNameRewritersNotAllowed() throws Exception {
        AddAggregateNameRewriter addAggregateNameRewriter
                = new AddAggregateNameRewriter.Builder(TEST_AGGREGATE_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2)
                .build();

        AddAggregateNameRewriter addAggregateNameRewriter2
                = new AddAggregateNameRewriter.Builder(TEST_AGGREGATE_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME2, TEST_DIFFERENT_NAME_REWRITER_NAME)
                .build();

        client.apply(addAggregateNameRewriter);
        assertTrue("Aggregate-name-rewriter should be created", ops.exists(TEST_AGGREGATE_NAME_REWRITER_ADDRESS));

        client.apply(addAggregateNameRewriter2);
        fail("Aggregate-name-rewriter CreaperTestAggregateNameRewriter already exists in configuration, exception should be thrown");
    }

    @Test
    public void addExistAggregateNameRewritersAllowed() throws Exception {
        AddAggregateNameRewriter addAggregateNameRewriter
                = new AddAggregateNameRewriter.Builder(TEST_AGGREGATE_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2)
                .build();

        AddAggregateNameRewriter addAggregateNameRewriter2
                = new AddAggregateNameRewriter.Builder(TEST_AGGREGATE_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME2, TEST_DIFFERENT_NAME_REWRITER_NAME)
                .replaceExisting()
                .build();

        client.apply(addAggregateNameRewriter);
        assertTrue("Aggregate-name-rewriter should be created", ops.exists(TEST_AGGREGATE_NAME_REWRITER_ADDRESS));

        client.apply(addAggregateNameRewriter2);
        assertTrue("Aggregate-name-rewriter should be created", ops.exists(TEST_AGGREGATE_NAME_REWRITER_ADDRESS));
        checkAttribute(TEST_AGGREGATE_NAME_REWRITER_ADDRESS, "name-rewriters", NAME_REWRITERS_2_AND_DIFFERENT);
    }

    @Test(expected = CommandFailedException.class)
    public void addAggregateNameRewriterWithoutConfiguredNameRewriters() throws Exception {
        AddAggregateNameRewriter addAggregateNameRewriter
                = new AddAggregateNameRewriter.Builder(TEST_AGGREGATE_NAME_REWRITER_NAME)
                .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, "NotConfiguredNameRewriter")
                .build();

        client.apply(addAggregateNameRewriter);
        fail("Aggregate-name-rewriter shouldn't be added when using unconfigured name rewriter");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAggregateNameRewriter_nullName() throws Exception {
        new AddAggregateNameRewriter.Builder(null)
            .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2)
            .build();
        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAggregateNameRewriter_emptyName() throws Exception {
        new AddAggregateNameRewriter.Builder("")
            .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2)
            .build();
        fail("Creating command with empty name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAggregateNameRewriter_nullNameRewriters() throws Exception {
        new AddAggregateNameRewriter.Builder(TEST_AGGREGATE_NAME_REWRITER_NAME)
            .nameRewriters(null)
            .build();
        fail("Creating command with null name-rewriters should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAggregateNameRewriter_emptyNameRewriters() throws Exception {
        new AddAggregateNameRewriter.Builder(TEST_AGGREGATE_NAME_REWRITER_NAME)
            .nameRewriters("")
            .build();
        fail("Creating command with empty name-rewriters should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAggregateNameRewriter_oneNameRewriter() throws Exception {
        new AddAggregateNameRewriter.Builder(TEST_AGGREGATE_NAME_REWRITER_NAME)
            .nameRewriters(TEST_CONSTANT_NAME_REWRITER_NAME)
            .build();
        fail("Creating command with only one name-rewriter should throw exception");
    }

}
