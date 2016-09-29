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
public class AddRegexNameValidatingRewriterOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_REGEX_NAME_VALIDATING_REWRITER_NAME = "CreaperTestRegexNameValidatingRewriter";
    private static final Address TEST_REGEX_NAME_VALIDATING_REWRITER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("regex-name-validating-rewriter", TEST_REGEX_NAME_VALIDATING_REWRITER_NAME);
    private static final String TEST_REGEX_NAME_VALIDATING_REWRITER_NAME2 = "CreaperTestRegexNameValidatingRewriter2";
    private static final Address TEST_REGEX_NAME_VALIDATING_REWRITER_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("regex-name-validating-rewriter", TEST_REGEX_NAME_VALIDATING_REWRITER_NAME2);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_REGEX_NAME_VALIDATING_REWRITER_ADDRESS);
        ops.removeIfExists(TEST_REGEX_NAME_VALIDATING_REWRITER_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addRegexNameValidatingRewriter() throws Exception {
        AddRegexNameValidatingRewriter addRegexNameValidatingRewriter
                = new AddRegexNameValidatingRewriter.Builder(TEST_REGEX_NAME_VALIDATING_REWRITER_NAME)
                .pattern("test-pattern")
                .match(true)
                .build();

        client.apply(addRegexNameValidatingRewriter);

        assertTrue("Regex name validating rewriter should be created",
                ops.exists(TEST_REGEX_NAME_VALIDATING_REWRITER_ADDRESS));
        checkRegexNameValidatingRewriterAttribute("pattern", "test-pattern");
        checkRegexNameValidatingRewriterAttribute("match", "true");
    }

    @Test
    public void addTwoRegexNameValidatingRewriters() throws Exception {
        AddRegexNameValidatingRewriter addRegexNameValidatingRewriter
                = new AddRegexNameValidatingRewriter.Builder(TEST_REGEX_NAME_VALIDATING_REWRITER_NAME)
                .pattern("test-pattern")
                .match(true)
                .build();
        AddRegexNameValidatingRewriter addRegexNameValidatingRewriter2
                = new AddRegexNameValidatingRewriter.Builder(TEST_REGEX_NAME_VALIDATING_REWRITER_NAME2)
                .pattern("test-pattern2")
                .match(false)
                .build();

        client.apply(addRegexNameValidatingRewriter);
        client.apply(addRegexNameValidatingRewriter2);

        assertTrue("Regex name validating rewriter should be created",
                ops.exists(TEST_REGEX_NAME_VALIDATING_REWRITER_ADDRESS));
        assertTrue("Regex name validating rewriter should be created",
                ops.exists(TEST_REGEX_NAME_VALIDATING_REWRITER_ADDRESS2));
    }

    @Test(expected = CommandFailedException.class)
    public void addExistRegexNameValidatingRewriterNotAllowed() throws Exception {
        AddRegexNameValidatingRewriter addRegexNameValidatingRewriter
                = new AddRegexNameValidatingRewriter.Builder(TEST_REGEX_NAME_VALIDATING_REWRITER_NAME)
                .pattern("test-pattern")
                .match(true)
                .build();
        AddRegexNameValidatingRewriter addRegexNameValidatingRewriter2
                = new AddRegexNameValidatingRewriter.Builder(TEST_REGEX_NAME_VALIDATING_REWRITER_NAME)
                .pattern("test-pattern2")
                .match(false)
                .build();

        client.apply(addRegexNameValidatingRewriter);
        assertTrue("Regex name validating rewriter should be created",
                ops.exists(TEST_REGEX_NAME_VALIDATING_REWRITER_ADDRESS));

        client.apply(addRegexNameValidatingRewriter2);
        fail("Regex name validating rewriter CreaperTestRegexNameValidatingRewriter already exists in configuration, exception should be thrown");
    }

    @Test
    public void addExistRegexNameValidatingRewriterAllowed() throws Exception {
        AddRegexNameValidatingRewriter addRegexNameValidatingRewriter
                = new AddRegexNameValidatingRewriter.Builder(TEST_REGEX_NAME_VALIDATING_REWRITER_NAME)
                .pattern("test-pattern")
                .match(true)
                .build();
        AddRegexNameValidatingRewriter addRegexNameValidatingRewriter2
                = new AddRegexNameValidatingRewriter.Builder(TEST_REGEX_NAME_VALIDATING_REWRITER_NAME)
                .pattern("test-pattern2")
                .match(false)
                .replaceExisting()
                .build();

        client.apply(addRegexNameValidatingRewriter);
        assertTrue("Regex name validating rewriter should be created",
                ops.exists(TEST_REGEX_NAME_VALIDATING_REWRITER_ADDRESS));

        client.apply(addRegexNameValidatingRewriter2);
        assertTrue("Regex name validating rewriter should be created",
                ops.exists(TEST_REGEX_NAME_VALIDATING_REWRITER_ADDRESS));
        checkRegexNameValidatingRewriterAttribute("pattern", "test-pattern2");
        checkRegexNameValidatingRewriterAttribute("match", "false");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRegexNameValidatingRewriter_nullName() throws Exception {
        new AddRegexNameValidatingRewriter.Builder(null)
                .pattern("test-pattern")
                .match(true)
                .build();
        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRegexNameValidatingRewriter_emptyName() throws Exception {
        new AddRegexNameValidatingRewriter.Builder("")
                .pattern("test-pattern")
                .match(true)
                .build();
        fail("Creating command with empty name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRegexNameValidatingRewriter_nullPattern() throws Exception {
        new AddRegexNameValidatingRewriter.Builder(TEST_REGEX_NAME_VALIDATING_REWRITER_NAME)
                .pattern(null)
                .match(true)
                .build();
        fail("Creating command with null pattern should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRegexNameValidatingRewriter_emptyPattern() throws Exception {
        new AddRegexNameValidatingRewriter.Builder(TEST_REGEX_NAME_VALIDATING_REWRITER_NAME)
                .pattern("")
                .match(true)
                .build();
        fail("Creating command with empty pattern should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRegexNameValidatingRewriter_matchUnspecified() throws Exception {
        new AddRegexNameValidatingRewriter.Builder(TEST_REGEX_NAME_VALIDATING_REWRITER_NAME)
                .pattern("test-pattern")
                .build();
        fail("Creating command without defined match should throw exception");
    }

    private void checkRegexNameValidatingRewriterAttribute(String attr, String expected) throws IOException {
        checkAttribute(TEST_REGEX_NAME_VALIDATING_REWRITER_ADDRESS, attr, expected);
    }
}
