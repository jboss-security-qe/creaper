package org.wildfly.extras.creaper.commands.elytron.audit;

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
public class AddSyslogAuditLogOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_SYSLOG_AUDIT_LOG_NAME = "CreaperTestSyslogAuditLog";
    private static final Address TEST_SYSLOG_AUDIT_LOG_ADDRESS = SUBSYSTEM_ADDRESS
            .and("syslog-audit-log", TEST_SYSLOG_AUDIT_LOG_NAME);
    private static final String TEST_SYSLOG_AUDIT_LOG_NAME2 = "CreaperTestSyslogAuditLog2";
    private static final Address TEST_SYSLOG_AUDIT_LOG_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("syslog-audit-log", TEST_SYSLOG_AUDIT_LOG_NAME2);

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_SYSLOG_AUDIT_LOG_ADDRESS);
        ops.removeIfExists(TEST_SYSLOG_AUDIT_LOG_ADDRESS2);
        administration.reloadIfRequired();
    }

    @Test
    public void addSimpleSyslogAuditLog() throws Exception {
        AddSyslogAuditLog addSyslogAuditLog = new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME)
                .serverAddress("localhost")
                .port(9898)
                .hostName("Elytron-audit")
                .build();

        client.apply(addSyslogAuditLog);

        assertTrue("Syslog audit log should be created", ops.exists(TEST_SYSLOG_AUDIT_LOG_ADDRESS));
    }

    @Test
    public void addTwoSyslogAuditLogs() throws Exception {
        AddSyslogAuditLog addSyslogAuditLog = new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME)
                .serverAddress("localhost")
                .port(9898)
                .hostName("Elytron-audit")
                .build();
        AddSyslogAuditLog addSyslogAuditLog2 = new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME2)
                .serverAddress("localhost")
                .port(9898)
                .hostName("Elytron-audit")
                .build();

        client.apply(addSyslogAuditLog);
        client.apply(addSyslogAuditLog2);

        assertTrue("Syslog audit log should be created", ops.exists(TEST_SYSLOG_AUDIT_LOG_ADDRESS));
        assertTrue("Second syslog audit log should be created", ops.exists(TEST_SYSLOG_AUDIT_LOG_ADDRESS2));
    }

    @Test
    public void addFullSyslogAuditLog() throws Exception {
        AddSyslogAuditLog addSyslogAuditLog = new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME)
                .serverAddress("localhost")
                .port(9898)
                .hostName("Elytron-audit")
                .format(AuditFormat.JSON)
                .transport(AddSyslogAuditLog.TransportProtocolType.UDP)
                .build();

        client.apply(addSyslogAuditLog);

        assertTrue("Syslog audit log should be created", ops.exists(TEST_SYSLOG_AUDIT_LOG_ADDRESS));
        checkAttribute(TEST_SYSLOG_AUDIT_LOG_ADDRESS, "server-address", "localhost");
        checkAttribute(TEST_SYSLOG_AUDIT_LOG_ADDRESS, "port", "9898");
        checkAttribute(TEST_SYSLOG_AUDIT_LOG_ADDRESS, "host-name", "Elytron-audit");
        checkAttribute(TEST_SYSLOG_AUDIT_LOG_ADDRESS, "format", "JSON");
        checkAttribute(TEST_SYSLOG_AUDIT_LOG_ADDRESS, "transport", "UDP");
    }

    @Test(expected = CommandFailedException.class)
    public void addExistSyslogAuditLogNotAllowed() throws Exception {
        AddSyslogAuditLog addSyslogAuditLog = new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME)
                .serverAddress("localhost")
                .port(9898)
                .hostName("Elytron-audit")
                .build();
        AddSyslogAuditLog addSyslogAuditLog2 = new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME)
                .serverAddress("localhost")
                .port(9898)
                .hostName("Elytron-audit")
                .build();

        client.apply(addSyslogAuditLog);
        assertTrue("Syslog audit log should be created", ops.exists(TEST_SYSLOG_AUDIT_LOG_ADDRESS));
        client.apply(addSyslogAuditLog2);
        fail("Syslog audit log CreaperTestSyslogAuditLog already exists in configuration, exception should be thrown");
    }

    @Test
    public void addExistSyslogAuditLogAllowed() throws Exception {
        AddSyslogAuditLog addSyslogAuditLog = new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME)
                .serverAddress("localhost")
                .port(9898)
                .hostName("Elytron-audit")
                .build();
        AddSyslogAuditLog addSyslogAuditLog2 = new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME)
                .serverAddress("localhost")
                .port(9898)
                .hostName("another-hostname")
                .replaceExisting()
                .build();

        client.apply(addSyslogAuditLog);
        assertTrue("Syslog audit log should be created", ops.exists(TEST_SYSLOG_AUDIT_LOG_ADDRESS));
        client.apply(addSyslogAuditLog2);

        // check whether it was really rewritten
        checkAttribute(TEST_SYSLOG_AUDIT_LOG_ADDRESS, "host-name", "another-hostname");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSyslogAuditLog_nullName() throws Exception {
        new AddSyslogAuditLog.Builder(null)
                .serverAddress("localhost")
                .port(9898)
                .hostName("Elytron-audit")
                .build();

        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSyslogAuditLog_emptyName() throws Exception {
        new AddSyslogAuditLog.Builder("")
                .serverAddress("localhost")
                .port(9898)
                .hostName("Elytron-audit")
                .build();

        fail("Creating command with empty name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSyslogAuditLog_nullServerAddress() throws Exception {
        new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME)
                .serverAddress(null)
                .port(9898)
                .hostName("Elytron-audit")
                .build();

        fail("Creating command with null server-address should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSyslogAuditLog_emptyServerAddress() throws Exception {
        new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME)
                .serverAddress("")
                .port(9898)
                .hostName("Elytron-audit")
                .build();

        fail("Creating command with empty server-address should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSyslogAuditLog_undefinedPort() throws Exception {
        new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME)
                .serverAddress("localhost")
                .hostName("Elytron-audit")
                .build();

        fail("Creating command with undefined port should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSyslogAuditLog_nullHostName() throws Exception {
        new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME)
                .serverAddress("localhost")
                .port(9898)
                .hostName(null)
                .build();

        fail("Creating command with null host-name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSyslogAuditLog_emptyHostName() throws Exception {
        new AddSyslogAuditLog.Builder(TEST_SYSLOG_AUDIT_LOG_NAME)
                .serverAddress("localhost")
                .port(9898)
                .hostName("")
                .build();

        fail("Creating command with empty host-name should throw exception");
    }
}
