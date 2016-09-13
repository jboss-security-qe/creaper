package org.wildfly.extras.creaper.commands.elytron.mapper;

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
public class AddEmptyRoleDecoderOnlineTest extends AbstractElytronOnlineTest {
    private static final String TEST_EMPTY_ROLE_DECODER_NAME = "CreaperTestEmptyRoleDecoder";
    private static final Address TEST_EMPTY_ROLE_DECODER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("empty-role-decoder", TEST_EMPTY_ROLE_DECODER_NAME);
    private static final String TEST_EMPTY_ROLE_DECODER_NAME2 = "CreaperTestEmptyRoleDecoder2";
    private static final Address TEST_EMPTY_ROLE_DECODER_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("empty-role-decoder", TEST_EMPTY_ROLE_DECODER_NAME2);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_EMPTY_ROLE_DECODER_ADDRESS);
        ops.removeIfExists(TEST_EMPTY_ROLE_DECODER_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addEmptyRoleDecoder() throws Exception {
        AddEmptyRoleDecoder addEmptyRoleDecoder = new AddEmptyRoleDecoder.Builder(TEST_EMPTY_ROLE_DECODER_NAME)
                .build();

        client.apply(addEmptyRoleDecoder);

        assertTrue("Empty role decoder should be created", ops.exists(TEST_EMPTY_ROLE_DECODER_ADDRESS));
    }

    @Test
    public void addTwoEmptyRoleDecoders() throws Exception {
        AddEmptyRoleDecoder addEmptyRoleDecoder = new AddEmptyRoleDecoder.Builder(TEST_EMPTY_ROLE_DECODER_NAME)
                .build();
        AddEmptyRoleDecoder addEmptyRoleDecoder2 = new AddEmptyRoleDecoder.Builder(TEST_EMPTY_ROLE_DECODER_NAME2)
                .build();

        client.apply(addEmptyRoleDecoder);
        client.apply(addEmptyRoleDecoder2);

        assertTrue("Empty role decoder should be created", ops.exists(TEST_EMPTY_ROLE_DECODER_ADDRESS));
        assertTrue("Empty role decoder should be created", ops.exists(TEST_EMPTY_ROLE_DECODER_ADDRESS2));
    }

    @Test(expected = CommandFailedException.class)
    public void addExistEmptyRoleDecoderNotAllowed() throws Exception {
        AddEmptyRoleDecoder addEmptyRoleDecoder = new AddEmptyRoleDecoder.Builder(TEST_EMPTY_ROLE_DECODER_NAME)
                .build();
        AddEmptyRoleDecoder addEmptyRoleDecoder2 = new AddEmptyRoleDecoder.Builder(TEST_EMPTY_ROLE_DECODER_NAME)
                .build();

        client.apply(addEmptyRoleDecoder);
        assertTrue("Empty role decoder should be created", ops.exists(TEST_EMPTY_ROLE_DECODER_ADDRESS));

        client.apply(addEmptyRoleDecoder2);
        fail("Empty role decoder CreaperTestEmptyRoleDecoder already exists in configuration, exception should be thrown");
    }

    @Test
    public void addExistEmptyRoleDecoderAllowed() throws Exception {
        AddEmptyRoleDecoder addEmptyRoleDecoder = new AddEmptyRoleDecoder.Builder(TEST_EMPTY_ROLE_DECODER_NAME)
                .build();
        AddEmptyRoleDecoder addEmptyRoleDecoder2 = new AddEmptyRoleDecoder.Builder(TEST_EMPTY_ROLE_DECODER_NAME)
                .replaceExisting()
                .build();

        client.apply(addEmptyRoleDecoder);
        assertTrue("Empty role decoder should be created", ops.exists(TEST_EMPTY_ROLE_DECODER_ADDRESS));

        client.apply(addEmptyRoleDecoder2);
        assertTrue("Empty role decoder should be created", ops.exists(TEST_EMPTY_ROLE_DECODER_ADDRESS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addEmptyRoleDecoder_nullName() throws Exception {
        new AddEmptyRoleDecoder.Builder(null).build();
        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addEmptyRoleDecoder_emptyName() throws Exception {
        new AddEmptyRoleDecoder.Builder("").build();
        fail("Creating command with empty name should throw exception");
    }
}
