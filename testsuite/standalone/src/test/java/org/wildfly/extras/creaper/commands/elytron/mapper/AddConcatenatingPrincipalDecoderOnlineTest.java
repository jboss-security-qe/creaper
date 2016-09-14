package org.wildfly.extras.creaper.commands.elytron.mapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
public class AddConcatenatingPrincipalDecoderOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_CONCATENATING_PRINCIPAL_DECODER_NAME = "CreaperTestConcatenatingPrincipalDecoder";
    private static final Address TEST_CONCATENATING_PRINCIPAL_DECODER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("concatenating-principal-decoder", TEST_CONCATENATING_PRINCIPAL_DECODER_NAME);
    private static final String TEST_CONCATENATING_PRINCIPAL_DECODER_NAME2 = "CreaperTestConcatenatingPrincipalDecoder2";
    private static final Address TEST_CONCATENATING_PRINCIPAL_DECODER_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("concatenating-principal-decoder", TEST_CONCATENATING_PRINCIPAL_DECODER_NAME2);

    private static final String TEST_CONSTANT_PRINCIPAL_DECODER_NAME = "CreaperTestConstantPrincipalDecoder";
    private static final Address TEST_CONSTANT_PRINCIPAL_DECODER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("constant-principal-decoder", TEST_CONSTANT_PRINCIPAL_DECODER_NAME);
    private static final String TEST_CONSTANT_PRINCIPAL_DECODER_NAME2 = "CreaperTestConstantPrincipalDecoder2";
    private static final Address TEST_CONSTANT_PRINCIPAL_DECODER_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("constant-principal-decoder", TEST_CONSTANT_PRINCIPAL_DECODER_NAME2);

    private static final List<String> EXPECTED_PRINCIPAL_DECODERS
            = Arrays.asList(TEST_CONSTANT_PRINCIPAL_DECODER_NAME, TEST_CONSTANT_PRINCIPAL_DECODER_NAME2);

    @Before
    public void addPrincipalDecoders() throws Exception {
        AddConstantPrincipalDecoder addConstantPrincipalDecoder
                = new AddConstantPrincipalDecoder.Builder(TEST_CONSTANT_PRINCIPAL_DECODER_NAME)
                .constant("role1")
                .build();
        AddConstantPrincipalDecoder addConstantPrincipalDecoder2
                = new AddConstantPrincipalDecoder.Builder(TEST_CONSTANT_PRINCIPAL_DECODER_NAME2)
                .constant("role2")
                .build();
        client.apply(addConstantPrincipalDecoder);
        client.apply(addConstantPrincipalDecoder2);
    }

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_CONCATENATING_PRINCIPAL_DECODER_ADDRESS);
        ops.removeIfExists(TEST_CONCATENATING_PRINCIPAL_DECODER_ADDRESS2);
        ops.removeIfExists(TEST_CONSTANT_PRINCIPAL_DECODER_ADDRESS);
        ops.removeIfExists(TEST_CONSTANT_PRINCIPAL_DECODER_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addConcatenatingPrincipalDecoder() throws Exception {
        AddConcatenatingPrincipalDecoder addConcatenatingPricipalDecoder
                = new AddConcatenatingPrincipalDecoder.Builder(TEST_CONCATENATING_PRINCIPAL_DECODER_NAME)
                .joiner(".")
                .principalDecoders(TEST_CONSTANT_PRINCIPAL_DECODER_NAME, TEST_CONSTANT_PRINCIPAL_DECODER_NAME2)
                .build();

        client.apply(addConcatenatingPricipalDecoder);

        assertTrue("Concatenating-principal-decoder should be created",
                ops.exists(TEST_CONCATENATING_PRINCIPAL_DECODER_ADDRESS));
        checkConcatenatingPrincipalDecoderAttribute("joiner", ".");
        checkConcatenatingPrincipalDecoderAttribute("principal-decoders", EXPECTED_PRINCIPAL_DECODERS);
    }

    @Test
    public void addTwoConcatenatingPrincipalDecoders() throws Exception {
        AddConcatenatingPrincipalDecoder addConcatenatingPricipalDecoder
                = new AddConcatenatingPrincipalDecoder.Builder(TEST_CONCATENATING_PRINCIPAL_DECODER_NAME)
                .principalDecoders(TEST_CONSTANT_PRINCIPAL_DECODER_NAME, TEST_CONSTANT_PRINCIPAL_DECODER_NAME2)
                .build();

        AddConcatenatingPrincipalDecoder addConcatenatingPricipalDecoder2
                = new AddConcatenatingPrincipalDecoder.Builder(TEST_CONCATENATING_PRINCIPAL_DECODER_NAME2)
                .principalDecoders(TEST_CONSTANT_PRINCIPAL_DECODER_NAME, TEST_CONSTANT_PRINCIPAL_DECODER_NAME2)
                .build();

        client.apply(addConcatenatingPricipalDecoder);
        client.apply(addConcatenatingPricipalDecoder2);

        assertTrue("Concatenating-principal-decoder should be created",
                ops.exists(TEST_CONCATENATING_PRINCIPAL_DECODER_ADDRESS));
        assertTrue("Concatenating-principal-decoder should be created",
                ops.exists(TEST_CONCATENATING_PRINCIPAL_DECODER_ADDRESS2));
    }

    @Test(expected = CommandFailedException.class)
    public void addExistConcatenatingPrincipalDecodersNotAllowed() throws Exception {
        AddConcatenatingPrincipalDecoder addConcatenatingPricipalDecoder
                = new AddConcatenatingPrincipalDecoder.Builder(TEST_CONCATENATING_PRINCIPAL_DECODER_NAME)
                .principalDecoders(TEST_CONSTANT_PRINCIPAL_DECODER_NAME, TEST_CONSTANT_PRINCIPAL_DECODER_NAME2)
                .build();

        AddConcatenatingPrincipalDecoder addConcatenatingPricipalDecoder2
                = new AddConcatenatingPrincipalDecoder.Builder(TEST_CONCATENATING_PRINCIPAL_DECODER_NAME)
                .principalDecoders(TEST_CONSTANT_PRINCIPAL_DECODER_NAME, TEST_CONSTANT_PRINCIPAL_DECODER_NAME2)
                .build();

        client.apply(addConcatenatingPricipalDecoder);
        assertTrue("Concatenating-principal-decoder should be created",
                ops.exists(TEST_CONCATENATING_PRINCIPAL_DECODER_ADDRESS));

        client.apply(addConcatenatingPricipalDecoder2);
        fail("Concatenating-principal-decoder CreaperTestConcatenatingPrincipalDecoder already exists in configuration, exception should be thrown");
    }

    @Test
    public void addExistConcatenatingPrincipalDecodersAllowed() throws Exception {
        AddConcatenatingPrincipalDecoder addConcatenatingPricipalDecoder
                = new AddConcatenatingPrincipalDecoder.Builder(TEST_CONCATENATING_PRINCIPAL_DECODER_NAME)
                .principalDecoders(TEST_CONSTANT_PRINCIPAL_DECODER_NAME, TEST_CONSTANT_PRINCIPAL_DECODER_NAME2)
                .build();

        AddConcatenatingPrincipalDecoder addConcatenatingPricipalDecoder2
                = new AddConcatenatingPrincipalDecoder.Builder(TEST_CONCATENATING_PRINCIPAL_DECODER_NAME)
                .joiner("::")
                .principalDecoders(TEST_CONSTANT_PRINCIPAL_DECODER_NAME, TEST_CONSTANT_PRINCIPAL_DECODER_NAME2)
                .replaceExisting()
                .build();

        client.apply(addConcatenatingPricipalDecoder);
        assertTrue("Concatenating-principal-decoder should be created",
                ops.exists(TEST_CONCATENATING_PRINCIPAL_DECODER_ADDRESS));

        client.apply(addConcatenatingPricipalDecoder2);
        assertTrue("Concatenating-principal-decoder should be created",
                ops.exists(TEST_CONCATENATING_PRINCIPAL_DECODER_ADDRESS));
        checkConcatenatingPrincipalDecoderAttribute("joiner", "::");
    }

    @Test(expected = CommandFailedException.class)
    public void addConcatenatingPrincipalDecoderWithoutConfiguredPrincipalsDecoders() throws Exception {
        AddConcatenatingPrincipalDecoder addConcatenatingPricipalDecoder
                = new AddConcatenatingPrincipalDecoder.Builder(TEST_CONCATENATING_PRINCIPAL_DECODER_NAME)
                .joiner(".")
                .principalDecoders(TEST_CONSTANT_PRINCIPAL_DECODER_NAME, "NotConfiguredPrincipalDecoder")
                .build();

        client.apply(addConcatenatingPricipalDecoder);
        fail("Concatenating-principal-decoder shouldn't be added when using unconfigured principal decoder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addConcatenatingPrincipalDecoder_nullName() throws Exception {
        new AddConcatenatingPrincipalDecoder.Builder(null)
            .principalDecoders(TEST_CONSTANT_PRINCIPAL_DECODER_NAME, TEST_CONSTANT_PRINCIPAL_DECODER_NAME2)
            .build();
        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addConcatenatingPrincipalDecoder_emptyName() throws Exception {
        new AddConcatenatingPrincipalDecoder.Builder("")
            .principalDecoders(TEST_CONSTANT_PRINCIPAL_DECODER_NAME, TEST_CONSTANT_PRINCIPAL_DECODER_NAME2)
            .build();
        fail("Creating command with empty name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addConcatenatingPrincipalDecoder_nullPrincipalDecoders() throws Exception {
        new AddConcatenatingPrincipalDecoder.Builder(TEST_CONCATENATING_PRINCIPAL_DECODER_NAME)
            .principalDecoders(null)
            .build();
        fail("Creating command with null principal-decoders should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addConcatenatingPrincipalDecoder_emptyPrincipalDecoders() throws Exception {
        new AddConcatenatingPrincipalDecoder.Builder(TEST_CONCATENATING_PRINCIPAL_DECODER_NAME)
            .principalDecoders("")
            .build();
        fail("Creating command with empty principal-decoders should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addConcatenatingPrincipalDecoder_onePrincipalDecoder() throws Exception {
        new AddConcatenatingPrincipalDecoder.Builder(TEST_CONCATENATING_PRINCIPAL_DECODER_NAME)
            .principalDecoders(TEST_CONSTANT_PRINCIPAL_DECODER_NAME)
            .build();
        fail("Creating command with only one principal-decoder should throw exception");
    }

    private void checkConcatenatingPrincipalDecoderAttribute(String attr, String expected) throws IOException {
        ModelNodeResult readAttribute = ops.readAttribute(TEST_CONCATENATING_PRINCIPAL_DECODER_ADDRESS, attr);
        readAttribute.assertSuccess("Read operation for " + attr + " failed");
        assertEquals("Read operation for " + attr + " return unexpected value", expected,
                readAttribute.stringValue());
    }

    private void checkConcatenatingPrincipalDecoderAttribute(String attr, List<String> expected) throws IOException {
        ModelNodeResult readAttribute = ops.readAttribute(TEST_CONCATENATING_PRINCIPAL_DECODER_ADDRESS, attr);
        readAttribute.assertSuccess("Read operation for " + attr + " failed");
        assertEquals("Read operation for " + attr + " return unexpected value", expected,
                readAttribute.stringListValue());
    }
}
