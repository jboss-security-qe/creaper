package org.wildfly.extras.creaper.commands.elytron.tls;

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

public final class AddLdapKeyStore implements OnlineCommand {

    private final String name;
    private final String dirContext;
    private final String searchPath;
    private final Boolean searchRecursive;
    private final Integer searchTimeLimit;
    private final String filterAlias;
    private final String filterCertificate;
    private final String filterIterate;
    private final LdapMapping ldapMapping;
    private final NewItemTemplate newItemTemplate;
    private final boolean replaceExisting;

    private AddLdapKeyStore(Builder builder) {
        this.name = builder.name;
        this.dirContext = builder.dirContext;
        this.searchPath = builder.searchPath;
        this.searchRecursive = builder.searchRecursive;
        this.searchTimeLimit = builder.searchTimeLimit;
        this.filterAlias = builder.filterAlias;
        this.filterCertificate = builder.filterCertificate;
        this.filterIterate = builder.filterIterate;
        this.ldapMapping = builder.ldapMapping;
        this.newItemTemplate = builder.newItemTemplate;
        // Replace existing
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address keyStoreAddress = Address.subsystem("elytron").and("ldap-key-store", name);
        if (replaceExisting) {
            ops.removeIfExists(keyStoreAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        Values keyStoreValues = Values.empty()
            .and("name", name)
            .and("dir-context", dirContext)
            .and("search-path", searchPath)
            .andOptional("search-recursive", searchRecursive)
            .andOptional("search-time-limit", searchTimeLimit)
            .andOptional("filterAlias", filterAlias)
            .andOptional("filter-certificate", filterCertificate)
            .andOptional("filter-iterate", filterIterate);
        // LdapMapping
        if (ldapMapping != null) {
            keyStoreValues = keyStoreValues
            .andOptional("alias-attribute", ldapMapping.getAliasAttribute())
            .andOptional("certificate-attribute", ldapMapping.getCertificateAttribute())
            .andOptional("certificate-type", ldapMapping.getCertificateType())
            .andOptional("certificate-chain-attribute", ldapMapping.getCertificateChainAttribute())
            .andOptional("certificate-chain-encoding", ldapMapping.getCertificateChainEncoding());
        }

        if (newItemTemplate != null) {
            keyStoreValues = keyStoreValues
            .andOptional("new-item-path", newItemTemplate.getNewItemPath())
            .andOptional("new-item-rdn", newItemTemplate.getNewItemRdn());
            if (newItemTemplate.getNewItemAttributes() != null && !newItemTemplate.getNewItemAttributes().isEmpty()) {
                List<ModelNode> newItemAttributesNodeList = new ArrayList<ModelNode>();
                for (NewItemAttribute newItemAttribute : newItemTemplate.getNewItemAttributes()) {
                    ModelNode attributeNode = new ModelNode();

                    if (newItemAttribute.getName() != null && !newItemAttribute.getName().isEmpty()) {
                        attributeNode.add("name", newItemAttribute.getName());
                    }

                    ModelNode valuesList = new ModelNode().setEmptyList();
                    for (String value : newItemAttribute.getValues()) {
                        valuesList.add(value);
                    }
                    attributeNode.add("value", valuesList);

                    attributeNode = attributeNode.asObject();
                    newItemAttributesNodeList.add(attributeNode);
                }
                ModelNode newIdentityAttributesNode = new ModelNode();
                newIdentityAttributesNode.set(newItemAttributesNodeList);
                keyStoreValues = keyStoreValues
                        .and("new-item-attributes", newIdentityAttributesNode);
            }
        }

        if (ldapMapping != null) {
            keyStoreValues = keyStoreValues
            .andOptional("alias-attribute", ldapMapping.getAliasAttribute())
            .andOptional("certificate-attribute", ldapMapping.getCertificateAttribute())
            .andOptional("certificate-type", ldapMapping.getCertificateType())
            .andOptional("certificate-chain-attribute", ldapMapping.getCertificateChainAttribute())
            .andOptional("certificate-chain-encoding", ldapMapping.getCertificateChainEncoding());
        }

        ops.add(keyStoreAddress, keyStoreValues);

    }

    public static final class Builder {

        private final String name;
        private String dirContext;
        private String searchPath;
        private Boolean searchRecursive;
        private Integer searchTimeLimit;
        private String filterAlias;
        private String filterCertificate;
        private String filterIterate;
        private LdapMapping ldapMapping;
        private NewItemTemplate newItemTemplate;
        private boolean replaceExisting;

        public Builder(String name) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Name of the ldap-key-store must be specified as non empty value");
            }
            this.name = name;
        }

        public Builder dirContext(String dirContext) {
            this.dirContext = dirContext;
            return this;
        }

        public Builder searchPath(String searchPath) {
            this.searchPath = searchPath;
            return this;
        }

        public Builder searchRecursive(Boolean searchRecursive) {
            this.searchRecursive = searchRecursive;
            return this;
        }

        public Builder searchTimeLimit(Integer searchTimeLimit) {
            this.searchTimeLimit = searchTimeLimit;
            return this;
        }

        public Builder filterAlias(String filterAlias) {
            this.filterAlias = filterAlias;
            return this;
        }

        public Builder filterCertificate(String filterCertificate) {
            this.filterCertificate = filterCertificate;
            return this;
        }

        public Builder filterIterate(String filterIterate) {
            this.filterIterate = filterIterate;
            return this;
        }

        public Builder ldapMapping(LdapMapping ldapMapping) {
            this.ldapMapping = ldapMapping;
            return this;
        }

        public Builder newItemTemplate(NewItemTemplate newItemTemplate) {
            this.newItemTemplate = newItemTemplate;
            return this;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }


        public AddLdapKeyStore build() {

            if (dirContext == null || dirContext.isEmpty()) {
                throw new IllegalArgumentException("Dir context of the ldap-key-store must be specified as non empty value");
            }
            if (searchPath == null || searchPath.isEmpty()) {
                throw new IllegalArgumentException("Search path of the ldap-key-store must be specified as non empty value");
            }

            return new AddLdapKeyStore(this);
        }
    }

    public static final class LdapMapping {

        private String aliasAttribute;
        private String certificateAttribute;
        private String certificateType;
        private String certificateChainAttribute;
        private String certificateChainEncoding;

        private LdapMapping(LdapMappingBuilder builder) {
            this.aliasAttribute = builder.aliasAttribute;
            this.certificateAttribute = builder.certificateAttribute;
            this.certificateType = builder.certificateType;
            this.certificateChainAttribute = builder.certificateChainAttribute;
            this.certificateChainEncoding = builder.certificateChainEncoding;
        }

        public String getAliasAttribute() {
            return aliasAttribute;
        }

        public String getCertificateAttribute() {
            return certificateAttribute;
        }

        public String getCertificateType() {
            return certificateType;
        }

        public String getCertificateChainAttribute() {
            return certificateChainAttribute;
        }

        public String getCertificateChainEncoding() {
            return certificateChainEncoding;
        }

    }

    public static final class LdapMappingBuilder {

        private String aliasAttribute;
        private String certificateAttribute;
        private String certificateType;
        private String certificateChainAttribute;
        private String certificateChainEncoding;

        public LdapMappingBuilder aliasAttribute(String aliasAttribute) {
            this.aliasAttribute = aliasAttribute;
            return this;
        }

        public LdapMappingBuilder certificateAttribute(String certificateAttribute) {
            this.certificateAttribute = certificateAttribute;
            return this;
        }

        public LdapMappingBuilder certificateType(String certificateType) {
            this.certificateType = certificateType;
            return this;
        }

        public LdapMappingBuilder certificateChainAttribute(String certificateChainAttribute) {
            this.certificateChainAttribute = certificateChainAttribute;
            return this;
        }

        public LdapMappingBuilder certificateChainEncoding(String certificateChainEncoding) {
            this.certificateChainEncoding = certificateChainEncoding;
            return this;
        }

        public LdapMapping build() {
            return new LdapMapping(this);
        }
    }

    public static final class NewItemTemplate {

        private final List<NewItemAttribute> newItemAttributes;
        private final String newItemPath;
        private final String newItemRdn;


        private NewItemTemplate(NewItemTemplateBuilder builder) {
            this.newItemPath = builder.newItemPath;
            this.newItemRdn = builder.newItemRdn;
            this.newItemAttributes = builder.newItemAttributes;
        }

        public String getNewItemPath() {
            return newItemPath;
        }

        public String getNewItemRdn() {
            return newItemRdn;
        }

        public List<NewItemAttribute> getNewItemAttributes() {
            return newItemAttributes;
        }

    }

    public static final class NewItemTemplateBuilder {

        private String newItemPath;
        private String newItemRdn;
        private List<NewItemAttribute> newItemAttributes  = new ArrayList<NewItemAttribute>();

        public NewItemTemplateBuilder newItemPath(String newItemPath) {
            this.newItemPath = newItemPath;
            return this;
        }

        public NewItemTemplateBuilder newItemRdn(String newItemRdn) {
            this.newItemRdn = newItemRdn;
            return this;
        }

        public NewItemTemplateBuilder addNewItemAttributes(NewItemAttribute... newItemAttributes) {
            if (newItemAttributes == null) {
                throw new IllegalArgumentException("NewItemAttributes added to ldap-key-store must not be null");
            }
            Collections.addAll(this.newItemAttributes, newItemAttributes);
            return this;
        }

        public NewItemTemplate build() {
            return new NewItemTemplate(this);
        }
    }

    public static final class NewItemAttribute {

        private final String name;
        private List<String> values;

        private NewItemAttribute(NewItemAttributeBuilder builder) {
            this.name = builder.name;
            this.values = builder.values;
        }

        public String getName() {
            return name;
        }

        public List<String> getValues() {
            return values;
        }

    }

    public static final class NewItemAttributeBuilder {

        private String name;
        private List<String> values = new ArrayList<String>();

        public NewItemAttributeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public NewItemAttributeBuilder addValues(String... values) {
            if (values == null) {
                throw new IllegalArgumentException("Values added to NewIdentityAttributesBuilder for ldap-key-store must not be null");
            }
            Collections.addAll(this.values, values);
            return this;
        }

        public NewItemAttribute build() {
            if (values == null || values.isEmpty()) {
                throw new IllegalArgumentException("values must not be null and must include at least one entry");
            }
            return new NewItemAttribute(this);
        }
    }

}
