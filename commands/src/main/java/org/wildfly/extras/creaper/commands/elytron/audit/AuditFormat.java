package org.wildfly.extras.creaper.commands.elytron.audit;

public enum AuditFormat {

    SIMPLE("SIMPLE"),
    JSON("JSON");

    private final String value;

    AuditFormat(String value) {
        this.value = value;
    }

    String value() {
        return value;
    }
}
