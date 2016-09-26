package org.wildfly.extras.creaper.commands.elytron.mapper;

import java.util.Arrays;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public class AbstractAddNameRewriterOnlineTest extends AbstractElytronOnlineTest {

    protected static final String TEST_CONSTANT_NAME_REWRITER_NAME = "CreaperTestConstantNameRewriter";
    protected static final Address TEST_CONSTANT_NAME_REWRITER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("constant-name-rewriter", TEST_CONSTANT_NAME_REWRITER_NAME);
    protected static final String TEST_CONSTANT_NAME_REWRITER_NAME2 = "CreaperTestConstantNameRewriter2";
    protected static final Address TEST_CONSTANT_NAME_REWRITER_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("constant-name-rewriter", TEST_CONSTANT_NAME_REWRITER_NAME2);
    protected static final String TEST_DIFFERENT_NAME_REWRITER_NAME = "CreaperTestConstantNameRewriter3";
    protected static final Address TEST_DIFFERENT_NAME_REWRITER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("constant-name-rewriter", TEST_DIFFERENT_NAME_REWRITER_NAME);

    protected static final List<String> NAME_REWRITERS_1_AND_2
            = Arrays.asList(TEST_CONSTANT_NAME_REWRITER_NAME, TEST_CONSTANT_NAME_REWRITER_NAME2);
    protected static final List<String> NAME_REWRITERS_2_AND_DIFFERENT
            = Arrays.asList(TEST_CONSTANT_NAME_REWRITER_NAME2, TEST_DIFFERENT_NAME_REWRITER_NAME);

    @BeforeClass
    public static void addNameRewriters() throws Exception {
        try (OnlineManagementClient client = createManagementClient()) {
            AddConstantNameRewriter addConstantNameRewriter
                    = new AddConstantNameRewriter.Builder(TEST_CONSTANT_NAME_REWRITER_NAME)
                    .constant("name1")
                    .build();
            AddConstantNameRewriter addConstantNameRewriter2
                    = new AddConstantNameRewriter.Builder(TEST_CONSTANT_NAME_REWRITER_NAME2)
                    .constant("name2")
                    .build();
            AddConstantNameRewriter addConstantNameRewriter3
                    = new AddConstantNameRewriter.Builder(TEST_DIFFERENT_NAME_REWRITER_NAME)
                    .constant("name3")
                    .build();
            client.apply(addConstantNameRewriter);
            client.apply(addConstantNameRewriter2);
            client.apply(addConstantNameRewriter3);
        }
    }

    @AfterClass
    public static void removeNameRewriters() throws Exception {
        try (OnlineManagementClient client = createManagementClient()) {
            Operations ops = new Operations(client);
            ops.removeIfExists(TEST_CONSTANT_NAME_REWRITER_ADDRESS);
            ops.removeIfExists(TEST_CONSTANT_NAME_REWRITER_ADDRESS2);
            ops.removeIfExists(TEST_DIFFERENT_NAME_REWRITER_ADDRESS);
            Administration administration = new Administration(client);
            administration.reloadIfRequired();
        }
    }
}
