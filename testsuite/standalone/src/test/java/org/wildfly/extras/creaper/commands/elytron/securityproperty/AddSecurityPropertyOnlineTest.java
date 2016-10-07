package org.wildfly.extras.creaper.commands.elytron.securityproperty;

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
public class AddSecurityPropertyOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_SECURITY_PROPERTY_NAME = "CreaperTestSecurityProperty";
    private static final Address TEST_SECURITY_PROPERTY_ADDRESS = SUBSYSTEM_ADDRESS
            .and("security-property", TEST_SECURITY_PROPERTY_NAME);
    private static final String TEST_SECURITY_PROPERTY_NAME2 = "CreaperTestSecurityProperty2";
    private static final Address TEST_SECURITY_PROPERTY_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("security-property", TEST_SECURITY_PROPERTY_NAME2);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_SECURITY_PROPERTY_ADDRESS);
        ops.removeIfExists(TEST_SECURITY_PROPERTY_ADDRESS2);
    }

    @Test
    public void addSecurityProperty() throws Exception {
        AddSecurityProperty addSecurityProperty = new AddSecurityProperty.Builder(TEST_SECURITY_PROPERTY_NAME)
                .value("someSecretValue")
                .build();
        client.apply(addSecurityProperty);

        assertTrue("Security property should be created", ops.exists(TEST_SECURITY_PROPERTY_ADDRESS));
        checkAttribute(TEST_SECURITY_PROPERTY_ADDRESS, "value", "someSecretValue");
    }

    @Test
    public void addSecurityProperties() throws Exception {
        AddSecurityProperty addSecurityProperty = new AddSecurityProperty.Builder(TEST_SECURITY_PROPERTY_NAME)
                .value("someSecretValue")
                .build();

        AddSecurityProperty addSecurityProperty2 = new AddSecurityProperty.Builder(TEST_SECURITY_PROPERTY_NAME2)
                .value("someSecretValue")
                .build();

        client.apply(addSecurityProperty);
        client.apply(addSecurityProperty2);

        assertTrue("Security property should be created", ops.exists(TEST_SECURITY_PROPERTY_ADDRESS));
        assertTrue("Second dir context should be created", ops.exists(TEST_SECURITY_PROPERTY_ADDRESS2));
    }

    @Test(expected = CommandFailedException.class)
    public void addSecurityPropertyNotAllowed() throws Exception {
        AddSecurityProperty addSecurityProperty = new AddSecurityProperty.Builder(TEST_SECURITY_PROPERTY_NAME)
                .value("someSecretValue")
                .build();

        AddSecurityProperty addSecurityProperty2 = new AddSecurityProperty.Builder(TEST_SECURITY_PROPERTY_NAME)
                .value("someSecretValue")
                .build();

        client.apply(addSecurityProperty);
        assertTrue("Security property should be created", ops.exists(TEST_SECURITY_PROPERTY_ADDRESS));
        client.apply(addSecurityProperty2);
        fail("Security property CreaperTestSecurityProperty already exists in configuration, exception should be thrown");
    }

    @Test
    public void addSecurityPropertyAllowed() throws Exception {
        AddSecurityProperty addSecurityProperty = new AddSecurityProperty.Builder(TEST_SECURITY_PROPERTY_NAME)
                .value("someSecretValue")
                .build();

        AddSecurityProperty addSecurityProperty2 = new AddSecurityProperty.Builder(TEST_SECURITY_PROPERTY_NAME)
                .value("differentSecretValue")
                .replaceExisting()
                .build();

        client.apply(addSecurityProperty);
        assertTrue("Security property should be created", ops.exists(TEST_SECURITY_PROPERTY_ADDRESS));
        client.apply(addSecurityProperty2);
        assertTrue("Security property should be created", ops.exists(TEST_SECURITY_PROPERTY_ADDRESS));
        // check whether it was really rewritten
        checkAttribute(TEST_SECURITY_PROPERTY_ADDRESS, "value", "differentSecretValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSecurityProperty_nullKey() throws Exception {
        new AddSecurityProperty.Builder(null)
                .value("someSecretValue")
                .build();
        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSecurityProperty_emptyKey() throws Exception {
        new AddSecurityProperty.Builder("")
                .value("someSecretValue")
                .build();
        fail("Creating command with empty name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSecurityProperty_nullValue() throws Exception {
        new AddSecurityProperty.Builder(TEST_SECURITY_PROPERTY_NAME)
                .value(null)
                .build();
        fail("Creating command with null value should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSecurityProperty_emptyValue() throws Exception {
        new AddSecurityProperty.Builder(TEST_SECURITY_PROPERTY_NAME)
                .value("")
                .build();
        fail("Creating command with empty value should throw exception");
    }

}
