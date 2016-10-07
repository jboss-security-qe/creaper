package org.wildfly.extras.creaper.commands.elytron.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jboss.dmr.ModelNode;
import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddSecurityDomain implements OnlineCommand {

    private final String name;
    private final String defaultRealm;
    private final String preRealmNameRewriter;
    private final String postRealmNameRewriter;
    private final String principalDecoder;
    private final String realmMapper;
    private final String roleMapper;
    private final String permissionMapper;
    private final List<String> trustedSecurityDomains;
    private final List<Realm> realms;
    private final boolean replaceExisting;

    private AddSecurityDomain(Builder builder) {
        this.name = builder.name;
        this.defaultRealm = builder.defaultRealm;
        this.preRealmNameRewriter = builder.preRealmNameRewriter;
        this.postRealmNameRewriter = builder.postRealmNameRewriter;
        this.principalDecoder = builder.principalDecoder;
        this.realmMapper = builder.realmMapper;
        this.roleMapper = builder.roleMapper;
        this.permissionMapper = builder.permissionMapper;
        this.replaceExisting = builder.replaceExisting;
        this.realms = builder.realms;
        this.trustedSecurityDomains = builder.trustedSecurityDomains;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address securityDomainAddress = Address.subsystem("elytron").and("security-domain", name);
        if (replaceExisting) {
            ops.removeIfExists(securityDomainAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        List<ModelNode> realmsModelNodeList = new ArrayList<ModelNode>();
        for (Realm realm : realms) {
            ModelNode configNode = new ModelNode();
            configNode.add("realm", realm.getName());
            if (realm.getNameRewriter() != null && !realm.getNameRewriter().isEmpty()) {
                configNode.add("name-rewriter", realm.getNameRewriter());
            }
            if (realm.getRoleDecoder() != null && !realm.getRoleDecoder().isEmpty()) {
                configNode.add("role-decoder", realm.getRoleDecoder());
            }
            if (realm.getRoleMapper() != null && !realm.getRoleMapper().isEmpty()) {
                configNode.add("role-mapper", realm.getRoleMapper());
            }
            configNode = configNode.asObject();
            realmsModelNodeList.add(configNode);
        }

        ops.add(securityDomainAddress, Values.empty()
                .and("default-realm", defaultRealm)
                .andList(ModelNode.class, "realms", realmsModelNodeList)
                .andOptional("pre-realm-name-rewriter", preRealmNameRewriter)
                .andOptional("post-realm-name-rewriter", postRealmNameRewriter)
                .andOptional("principal-decoder", principalDecoder)
                .andOptional("realm-mapper", realmMapper)
                .andOptional("role-mapper", roleMapper)
                .andOptional("permission-mapper", permissionMapper)
                .andListOptional(String.class, "trusted-security-domains", trustedSecurityDomains));
    }

    public static final class Builder {

        private final String name;
        private String defaultRealm;
        private String preRealmNameRewriter;
        private String postRealmNameRewriter;
        private String principalDecoder;
        private String realmMapper;
        private String roleMapper;
        private String permissionMapper;
        private List<String> trustedSecurityDomains;
        private List<Realm> realms;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the security-domain must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the security-domain must not be empty value");
            }
            this.name = name;
        }

        public Builder defaultRealm(String defaultRealm) {
            this.defaultRealm = defaultRealm;
            return this;
        }

        public Builder preRealmNameRewriter(String preRealmNameRewriter) {
            this.preRealmNameRewriter = preRealmNameRewriter;
            return this;
        }

        public Builder postRealmNameRewriter(String postRealmNameRewriter) {
            this.postRealmNameRewriter = postRealmNameRewriter;
            return this;
        }

        public Builder principalDecoder(String principalDecoder) {
            this.principalDecoder = principalDecoder;
            return this;
        }

        public Builder realmMapper(String realmMapper) {
            this.realmMapper = realmMapper;
            return this;
        }

        public Builder roleMapper(String roleMapper) {
            this.roleMapper = roleMapper;
            return this;
        }

        public Builder permissionMapper(String permissionMapper) {
            this.permissionMapper = permissionMapper;
            return this;
        }

        public Builder trustedSecurityDomains(String... trustedSecurityDomains) {
            if (trustedSecurityDomains == null) {
                throw new IllegalArgumentException("Trusted Security Domains added to security-domain must not be null");
            }
            if (this.trustedSecurityDomains == null) {
                this.trustedSecurityDomains = new ArrayList<String>();
            }
            Collections.addAll(this.trustedSecurityDomains, trustedSecurityDomains);
            return this;
        }

        public Builder realms(Realm... realms) {
            if (realms == null) {
                throw new IllegalArgumentException("Realms added to security-domain must not be null");
            }
            if (this.realms == null) {
                this.realms = new ArrayList<Realm>();
            }
            Collections.addAll(this.realms, realms);
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddSecurityDomain build() {
            if (defaultRealm == null || defaultRealm.isEmpty()) {
                throw new IllegalArgumentException("default-realm must not be null or empty");
            }
            if (realms == null || realms.isEmpty()) {
                throw new IllegalArgumentException("realms must not be null and must include at least one entry");
            }
            return new AddSecurityDomain(this);
        }

    }

    public static final class Realm {

        private final String name;
        private final String nameRewriter;
        private final String roleDecoder;
        private final String roleMapper;

        private Realm(RealmBuilder builder) {
            this.name = builder.name;
            this.nameRewriter = builder.nameRewriter;
            this.roleDecoder = builder.roleDecoder;
            this.roleMapper = builder.roleMapper;
        }

        public String getName() {
            return name;
        }

        public String getNameRewriter() {
            return nameRewriter;
        }

        public String getRoleDecoder() {
            return roleDecoder;
        }

        public String getRoleMapper() {
            return roleMapper;
        }

    }

    public static final class RealmBuilder {

        private final String name;
        private String nameRewriter;
        private String roleDecoder;
        private String roleMapper;

        public RealmBuilder(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name of the realm in security-domain must be specified as non null value");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name of the realm in security-domain must not be empty value");
            }
            this.name = name;
        }

        public RealmBuilder nameRewriter(String nameRewriter) {
            this.nameRewriter = nameRewriter;
            return this;
        }

        public RealmBuilder roleDecoder(String roleDecoder) {
            this.roleDecoder = roleDecoder;
            return this;
        }

        public RealmBuilder roleMapper(String roleMapper) {
            this.roleMapper = roleMapper;
            return this;
        }

        public Realm build() {
            return new Realm(this);
        }

    }
}
