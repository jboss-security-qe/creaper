package org.wildfly.extras.creaper.commands.elytron.mapper;

import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.core.online.operations.Address;

@RunWith(Arquillian.class)
public class AddLogicalRoleMapperOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_LOGICAL_ROLE_MAPPER_NAME = "CreaperTestLogicalRoleMapper";
    private static final Address TEST_LOGICAL_ROLE_MAPPER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("logical-role-mapper", TEST_LOGICAL_ROLE_MAPPER_NAME);
    private static final String TEST_LOGICAL_ROLE_MAPPER_NAME2 = "CreaperTestLogicalRoleMapper2";
    private static final Address TEST_LOGICAL_ROLE_MAPPER_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("logical-role-mapper", TEST_LOGICAL_ROLE_MAPPER_NAME2);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_LOGICAL_ROLE_MAPPER_ADDRESS);
        ops.removeIfExists(TEST_LOGICAL_ROLE_MAPPER_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addLogicalRoleMapper() throws Exception {
        AddLogicalRoleMapper addLogicalRoleMapper = new AddLogicalRoleMapper.Builder(TEST_LOGICAL_ROLE_MAPPER_NAME)
                .logicalOperation(AddLogicalRoleMapper.LogicalOperation.OR)
                .build();

        client.apply(addLogicalRoleMapper);

        assertTrue("Logical role mapper should be created", ops.exists(TEST_LOGICAL_ROLE_MAPPER_ADDRESS));
    }

    @Test
    public void addLogicalRoleMappers() throws Exception {
        AddLogicalRoleMapper addLogicalRoleMapper = new AddLogicalRoleMapper.Builder(TEST_LOGICAL_ROLE_MAPPER_NAME)
                .logicalOperation(AddLogicalRoleMapper.LogicalOperation.OR)
                .build();

        AddLogicalRoleMapper addLogicalRoleMapper2 = new AddLogicalRoleMapper.Builder(TEST_LOGICAL_ROLE_MAPPER_NAME2)
                .logicalOperation(AddLogicalRoleMapper.LogicalOperation.OR)
                .build();

        client.apply(addLogicalRoleMapper);
        client.apply(addLogicalRoleMapper2);

        assertTrue("Logical role mapper should be created", ops.exists(TEST_LOGICAL_ROLE_MAPPER_ADDRESS));
        assertTrue("Second logical role mapper should be created", ops.exists(TEST_LOGICAL_ROLE_MAPPER_ADDRESS2));
    }

}
