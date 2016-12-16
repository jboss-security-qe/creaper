package org.wildfly.extras.creaper.commands.elytron.tls;

import java.util.Arrays;
import java.util.List;
import org.wildfly.extras.creaper.core.offline.OfflineCommand;

import org.wildfly.extras.creaper.core.online.OnlineCommand;

abstract class AbstractAddSSLContext implements OnlineCommand, OfflineCommand {

    protected final String name;
    protected final String cipherSuiteFilter;
    protected final List<String> protocols;
    protected final Integer maximumSessionCacheSize;
    protected final Integer sessionTimeout;
    protected final String keyManagers;
    protected final String trustManagers;
    protected final boolean replaceExisting;

    protected AbstractAddSSLContext(Builder builder) {
        this.name = builder.name;
        this.cipherSuiteFilter = builder.cipherSuiteFilter;
        this.protocols = builder.protocols;
        this.maximumSessionCacheSize = builder.maximumSessionCacheSize;
        this.sessionTimeout = builder.sessionTimeout;
        this.keyManagers = builder.keyManagers;
        this.trustManagers = builder.trustManagers;
        this.replaceExisting = builder.replaceExisting;
    }

    abstract static class Builder<THIS extends Builder> {

        protected final String name;
        protected String cipherSuiteFilter;
        protected List<String> protocols;
        protected Integer maximumSessionCacheSize;
        protected Integer sessionTimeout;
        protected String keyManagers;
        protected String trustManagers;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the ssl-context must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the ssl-context must not be empty value");
            }
            this.name = name;
        }

        public final THIS protocols(String... protocols) {
            if (protocols != null && protocols.length > 0) {
                this.protocols = Arrays.asList(protocols);
            }
            return (THIS) this;
        }

        public final THIS cipherSuiteFilter(String cipherSuiteFilter) {
            this.cipherSuiteFilter = cipherSuiteFilter;
            return (THIS) this;
        }

        public final THIS maximumSessionCacheSize(Integer maximumSessionCacheSize) {
            this.maximumSessionCacheSize = maximumSessionCacheSize;
            return (THIS) this;
        }

        public final THIS sessionTimeout(Integer sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
            return (THIS) this;
        }

        public final THIS keyManagers(String keyManagers) {
            this.keyManagers = keyManagers;
            return (THIS) this;
        }

        public final THIS trustManagers(String trustManagers) {
            this.trustManagers = trustManagers;
            return (THIS) this;
        }

        public final THIS replaceExisting() {
            this.replaceExisting = true;
            return (THIS) this;
        }

        public abstract AbstractAddSSLContext build();
    }

}
