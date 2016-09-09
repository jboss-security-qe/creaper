package org.wildfly.extras.creaper.commands.elytron.mapper;

import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.core.online.operations.Address;

@RunWith(Arquillian.class)
public class AddSimplePermissionMapperOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_SIMPLE_PERMISSION_MAPPER_NAME = "CreaperTestSimplePermissionMapper";
    private static final Address TEST_SIMPLE_PERMISSION_MAPPER_ADDRESS = SUBSYSTEM_ADDRESS
            .and("simple-permission-mapper", TEST_SIMPLE_PERMISSION_MAPPER_NAME);
    private static final String TEST_SIMPLE_PERMISSION_MAPPER_NAME2 = "CreaperTestSimplePermissionMapper2";
    private static final Address TEST_SIMPLE_PERMISSION_MAPPER_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("simple-permission-mapper", TEST_SIMPLE_PERMISSION_MAPPER_NAME2);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_SIMPLE_PERMISSION_MAPPER_ADDRESS);
        ops.removeIfExists(TEST_SIMPLE_PERMISSION_MAPPER_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addSimplePermissionMapper() throws Exception {
        AddSimplePermissionMapper addSimplePermissionMapper
                = new AddSimplePermissionMapper.Builder(TEST_SIMPLE_PERMISSION_MAPPER_NAME)
                .addPermissionMapping(new AddSimplePermissionMapper.PermissionMappingBuilder()
                        .addPermission(new AddSimplePermissionMapper.PermissionBuilder()
                                .className("org.wildfly.security.auth.permission.LoginPermission")
                                .build())
                        .build())
                .build();

        client.apply(addSimplePermissionMapper);

        assertTrue("Simple permission mapper should be created", ops.exists(TEST_SIMPLE_PERMISSION_MAPPER_ADDRESS));
    }

    @Test
    public void addSimplePermissionMappers() throws Exception {
        AddSimplePermissionMapper addSimplePermissionMapper
                = new AddSimplePermissionMapper.Builder(TEST_SIMPLE_PERMISSION_MAPPER_NAME)
                .addPermissionMapping(new AddSimplePermissionMapper.PermissionMappingBuilder()
                        .addPermission(new AddSimplePermissionMapper.PermissionBuilder()
                                .className("org.wildfly.security.auth.permission.LoginPermission")
                                .build())
                        .build())
                .build();

        AddSimplePermissionMapper addSimplePermissionMapper2
                = new AddSimplePermissionMapper.Builder(TEST_SIMPLE_PERMISSION_MAPPER_NAME2)
                .addPermissionMapping(new AddSimplePermissionMapper.PermissionMappingBuilder()
                        .addPermission(new AddSimplePermissionMapper.PermissionBuilder()
                                .className("org.wildfly.security.auth.permission.LoginPermission")
                                .build())
                        .build())
                .build();

        client.apply(addSimplePermissionMapper);
        client.apply(addSimplePermissionMapper2);

        assertTrue("Simple permission mapper should be created", ops.exists(TEST_SIMPLE_PERMISSION_MAPPER_ADDRESS));
        assertTrue("Second Simple permission mapper should be created",
                ops.exists(TEST_SIMPLE_PERMISSION_MAPPER_ADDRESS2));
    }

}
