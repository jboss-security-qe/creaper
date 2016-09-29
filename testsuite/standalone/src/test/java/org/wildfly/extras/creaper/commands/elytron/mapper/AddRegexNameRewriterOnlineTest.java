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
public class AddRegexNameRewriterOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_REGEX_NAME_REWRITER_NAME = "CreaperTestRegexNameRewriter";
    private static final Address TEST_REGEX_NAME_REWRITER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("regex-name-rewriter", TEST_REGEX_NAME_REWRITER_NAME);
    private static final String TEST_REGEX_NAME_REWRITER_NAME2 = "CreaperTestRegexNameRewriter2";
    private static final Address TEST_REGEX_NAME_REWRITER_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("regex-name-rewriter", TEST_REGEX_NAME_REWRITER_NAME2);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_REGEX_NAME_REWRITER_ADDRESS);
        ops.removeIfExists(TEST_REGEX_NAME_REWRITER_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addRegexNameRewriter() throws Exception {
        AddRegexNameRewriter addRegexNameRewriter
                = new AddRegexNameRewriter.Builder(TEST_REGEX_NAME_REWRITER_NAME)
                .pattern("test-pattern")
                .replacement("test-replacement")
                .replaceAll(true)
                .build();

        client.apply(addRegexNameRewriter);

        assertTrue("Regex name rewriter should be created", ops.exists(TEST_REGEX_NAME_REWRITER_ADDRESS));
        checkRegexNameRewriterAttribute("pattern", "test-pattern");
        checkRegexNameRewriterAttribute("replacement", "test-replacement");
        checkRegexNameRewriterAttribute("replace-all", "true");
    }

    @Test
    public void addTwoRegexNameRewriters() throws Exception {
        AddRegexNameRewriter addRegexNameRewriter
                = new AddRegexNameRewriter.Builder(TEST_REGEX_NAME_REWRITER_NAME)
                .pattern("test-pattern")
                .replacement("test-replacement")
                .build();
        AddRegexNameRewriter addRegexNameRewriter2
                = new AddRegexNameRewriter.Builder(TEST_REGEX_NAME_REWRITER_NAME2)
                .pattern("test-pattern2")
                .replacement("test-replacement2")
                .build();

        client.apply(addRegexNameRewriter);
        client.apply(addRegexNameRewriter2);

        assertTrue("Regex name rewriter should be created",
                ops.exists(TEST_REGEX_NAME_REWRITER_ADDRESS));
        assertTrue("Regex name rewriter should be created",
                ops.exists(TEST_REGEX_NAME_REWRITER_ADDRESS2));
    }

    @Test(expected = CommandFailedException.class)
    public void addExistRegexNameRewriterNotAllowed() throws Exception {
        AddRegexNameRewriter addRegexNameRewriter
                = new AddRegexNameRewriter.Builder(TEST_REGEX_NAME_REWRITER_NAME)
                .pattern("test-pattern")
                .replacement("test-replacement")
                .build();
        AddRegexNameRewriter addRegexNameRewriter2
                = new AddRegexNameRewriter.Builder(TEST_REGEX_NAME_REWRITER_NAME)
                .pattern("test-pattern2")
                .replacement("test-replacement2")
                .build();

        client.apply(addRegexNameRewriter);
        assertTrue("Regex name rewriter should be created", ops.exists(TEST_REGEX_NAME_REWRITER_ADDRESS));

        client.apply(addRegexNameRewriter2);
        fail("Regex name rewriter CreaperTestRegexNameRewriter already exists in configuration, exception should be thrown");
    }

    @Test
    public void addExistRegexNameRewriterAllowed() throws Exception {
        AddRegexNameRewriter addRegexNameRewriter
                = new AddRegexNameRewriter.Builder(TEST_REGEX_NAME_REWRITER_NAME)
                .pattern("test-pattern")
                .replacement("test-replacement")
                .build();
        AddRegexNameRewriter addRegexNameRewriter2
                = new AddRegexNameRewriter.Builder(TEST_REGEX_NAME_REWRITER_NAME)
                .pattern("test-pattern2")
                .replacement("test-replacement2")
                .replaceExisting()
                .build();

        client.apply(addRegexNameRewriter);
        assertTrue("Regex name rewriter should be created", ops.exists(TEST_REGEX_NAME_REWRITER_ADDRESS));

        client.apply(addRegexNameRewriter2);
        assertTrue("Regex name rewriter should be created", ops.exists(TEST_REGEX_NAME_REWRITER_ADDRESS));
        checkRegexNameRewriterAttribute("pattern", "test-pattern2");
        checkRegexNameRewriterAttribute("replacement", "test-replacement2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRegexNameRewriter_nullName() throws Exception {
        new AddRegexNameRewriter.Builder(null)
                .pattern("test-pattern")
                .replacement("test-replacement")
                .build();
        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRegexNameRewriter_emptyName() throws Exception {
        new AddRegexNameRewriter.Builder("")
                .pattern("test-pattern")
                .replacement("test-replacement")
                .build();
        fail("Creating command with empty name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRegexNameRewriter_nullPattern() throws Exception {
        new AddRegexNameRewriter.Builder(TEST_REGEX_NAME_REWRITER_NAME)
                .pattern(null)
                .replacement("test-replacement")
                .build();
        fail("Creating command with null pattern should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRegexNameRewriter_emptyPattern() throws Exception {
        new AddRegexNameRewriter.Builder(TEST_REGEX_NAME_REWRITER_NAME)
                .pattern("")
                .replacement("test-replacement")
                .build();
        fail("Creating command with empty pattern should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRegexNameRewriter_nullReplacement() throws Exception {
        new AddRegexNameRewriter.Builder(TEST_REGEX_NAME_REWRITER_NAME)
                .pattern("test-pattern")
                .replacement(null)
                .build();
        fail("Creating command with null replacement should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRegexNameRewriter_emptyReplacement() throws Exception {
        new AddRegexNameRewriter.Builder(TEST_REGEX_NAME_REWRITER_NAME)
                .pattern("test-pattern")
                .replacement("")
                .build();
        fail("Creating command with empty replacement should throw exception");
    }

    private void checkRegexNameRewriterAttribute(String attr, String expected) throws IOException {
        checkAttribute(TEST_REGEX_NAME_REWRITER_ADDRESS, attr, expected);
    }
}
