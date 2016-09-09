package org.wildfly.extras.creaper.commands.elytron.mapper;

import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.core.online.operations.Address;

@RunWith(Arquillian.class)
public class AddConstantRoleMapperOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_CONSTANT_ROLE_MAPPER_NAME = "CreaperTestConstantRoleMapper";
    private static final Address TEST_CONSTANT_ROLE_MAPPER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("constant-role-mapper", TEST_CONSTANT_ROLE_MAPPER_NAME);
    private static final String TEST_CONSTANT_ROLE_MAPPER_NAME2 = "CreaperTestConstantRoleMapper2";
    private static final Address TEST_CONSTANT_ROLE_MAPPER_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("constant-role-mapper", TEST_CONSTANT_ROLE_MAPPER_NAME2);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_CONSTANT_ROLE_MAPPER_ADDRESS);
        ops.removeIfExists(TEST_CONSTANT_ROLE_MAPPER_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addConstantRoleMapper() throws Exception {
        AddConstantRoleMapper addConstantRoleMapper = new AddConstantRoleMapper.Builder(TEST_CONSTANT_ROLE_MAPPER_NAME)
                .addRole("AnyRole")
                .build();

        client.apply(addConstantRoleMapper);

        assertTrue("Constant role mapper should be created", ops.exists(TEST_CONSTANT_ROLE_MAPPER_ADDRESS));
    }

    @Test
    public void addConstantRoleMappers() throws Exception {
        AddConstantRoleMapper addConstantRoleMapper = new AddConstantRoleMapper.Builder(TEST_CONSTANT_ROLE_MAPPER_NAME)
                .addRole("AnyRole")
                .build();

        AddConstantRoleMapper addConstantRoleMapper2
                = new AddConstantRoleMapper.Builder(TEST_CONSTANT_ROLE_MAPPER_NAME2)
                .addRole("AnyRole")
                .build();

        client.apply(addConstantRoleMapper);
        client.apply(addConstantRoleMapper2);

        assertTrue("Constant role mapper should be created", ops.exists(TEST_CONSTANT_ROLE_MAPPER_ADDRESS));
        assertTrue("Second constant role mapper should be created", ops.exists(TEST_CONSTANT_ROLE_MAPPER_ADDRESS2));
    }
}
