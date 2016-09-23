package org.wildfly.extras.creaper.commands.elytron.mapper;

import java.io.IOException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
public class AddConstantNameRewriterOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_CONSTANT_NAME_REWRITER_NAME = "CreaperTestConstantNameRewriter";
    private static final Address TEST_CONSTANT_NAME_REWRITER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("constant-name-rewriter", TEST_CONSTANT_NAME_REWRITER_NAME);
    private static final String TEST_CONSTANT_NAME_REWRITER_NAME2 = "CreaperTestConstantNameRewriter2";
    private static final Address TEST_CONSTANT_NAME_REWRITER_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("constant-name-rewriter", TEST_CONSTANT_NAME_REWRITER_NAME2);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_CONSTANT_NAME_REWRITER_ADDRESS);
        ops.removeIfExists(TEST_CONSTANT_NAME_REWRITER_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addConstantNameRewriter() throws Exception {
        AddConstantNameRewriter addConstantNameRewriter
                = new AddConstantNameRewriter.Builder(TEST_CONSTANT_NAME_REWRITER_NAME)
                .constant("name1")
                .build();

        client.apply(addConstantNameRewriter);

        assertTrue("Constant name rewriter should be created", ops.exists(TEST_CONSTANT_NAME_REWRITER_ADDRESS));
        checkConstantNameRewriterConstant("name1");
    }

    @Test
    public void addTwoConstantNameRewriters() throws Exception {
        AddConstantNameRewriter addConstantNameRewriter
                = new AddConstantNameRewriter.Builder(TEST_CONSTANT_NAME_REWRITER_NAME)
                .constant("name1")
                .build();
        AddConstantNameRewriter addConstantNameRewriter2
                = new AddConstantNameRewriter.Builder(TEST_CONSTANT_NAME_REWRITER_NAME2)
                .constant("name2")
                .build();

        client.apply(addConstantNameRewriter);
        client.apply(addConstantNameRewriter2);

        assertTrue("Constant name rewriter should be created",
                ops.exists(TEST_CONSTANT_NAME_REWRITER_ADDRESS));
        assertTrue("Constant name rewriter should be created",
                ops.exists(TEST_CONSTANT_NAME_REWRITER_ADDRESS2));
    }

    @Test(expected = CommandFailedException.class)
    public void addExistConstantNameRewriterNotAllowed() throws Exception {
        AddConstantNameRewriter addConstantNameRewriter
                = new AddConstantNameRewriter.Builder(TEST_CONSTANT_NAME_REWRITER_NAME)
                .constant("name1")
                .build();
        AddConstantNameRewriter addConstantNameRewriter2
                = new AddConstantNameRewriter.Builder(TEST_CONSTANT_NAME_REWRITER_NAME)
                .constant("name1")
                .build();

        client.apply(addConstantNameRewriter);
        assertTrue("Constant name rewriter should be created", ops.exists(TEST_CONSTANT_NAME_REWRITER_ADDRESS));

        client.apply(addConstantNameRewriter2);
        fail("Constant name rewriter CreaperTestConstantNameRewriter already exists in configuration, exception should be thrown");
    }

    @Test
    public void addExistConstantNameRewriterAllowed() throws Exception {
        AddConstantNameRewriter addConstantNameRewriter
                = new AddConstantNameRewriter.Builder(TEST_CONSTANT_NAME_REWRITER_NAME)
                .constant("name1")
                .build();
        AddConstantNameRewriter addConstantNameRewriter2
                = new AddConstantNameRewriter.Builder(TEST_CONSTANT_NAME_REWRITER_NAME)
                .constant("name2")
                .replaceExisting()
                .build();

        client.apply(addConstantNameRewriter);
        assertTrue("Constant name rewriter should be created", ops.exists(TEST_CONSTANT_NAME_REWRITER_ADDRESS));
        checkConstantNameRewriterConstant("name1");

        client.apply(addConstantNameRewriter2);
        assertTrue("Constant name rewriter should be created", ops.exists(TEST_CONSTANT_NAME_REWRITER_ADDRESS));
        checkConstantNameRewriterConstant("name2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addConstantNameRewriter_nullName() throws Exception {
        new AddConstantNameRewriter.Builder(null)
                .constant("name1")
                .build();
        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addConstantNameRewriter_emptyName() throws Exception {
        new AddConstantNameRewriter.Builder("")
                .constant("name1")
                .build();
        fail("Creating command with empty name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addConstantNameRewriter_nullConstant() throws Exception {
        new AddConstantNameRewriter.Builder(TEST_CONSTANT_NAME_REWRITER_NAME)
                .constant(null)
                .build();
        fail("Creating command with null constant should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addConstantNameRewriter_emptyConstant() throws Exception {
        new AddConstantNameRewriter.Builder(TEST_CONSTANT_NAME_REWRITER_NAME)
                .constant("")
                .build();
        fail("Creating command with empty constant should throw exception");
    }

    private void checkConstantNameRewriterConstant(String expectedValue) throws IOException {
        checkAttribute(TEST_CONSTANT_NAME_REWRITER_ADDRESS, "constant", expectedValue);
    }
}
