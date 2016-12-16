keyStoreAttrs = ['name': atrName, 'dir-context': atrDirContext, 'search-path': atrSearchPath]
if (atrSearchRecursive != null) keyStoreAttrs['search-recursive'] = atrSearchRecursive
if (atrSearchTimeLimit != null) keyStoreAttrs['search-time-limit'] = atrSearchTimeLimit
if (atrFilterAlias != null) keyStoreAttrs['filter-alias'] = atrFilterAlias
if (atrFilterCertificate != null) keyStoreAttrs['filter-certificate'] = atrFilterCertificate
if (atrFilterIterate != null) keyStoreAttrs['filter-iterate'] = atrFilterIterate

ldapMappingAttrs = [:]
if (atrAliasAttribute != null) ldapMappingAttrs['alias-attribute'] = atrAliasAttribute
if (atrCertificateAttribute != null) ldapMappingAttrs['certificate-attribute'] = atrCertificateAttribute
if (atrCertificateType != null) ldapMappingAttrs['certificate-type'] = atrCertificateType
if (atrCertificateChainAttribute != null) ldapMappingAttrs['certificate-chain-attribute'] = atrCertificateChainAttribute
if (atrCertificateChainEncoding != null) ldapMappingAttrs['certificate-chain-encoding'] = atrCertificateChainEncoding

newItemTemplateAttrs = [:]
if (atrNewItemPath != null) newItemTemplateAttrs['new-item-path'] = atrNewItemPath
if (atrNewItemRdn != null) newItemTemplateAttrs['new-item-rdn'] = atrNewItemRdn

def keyStoreDefinition = {
    'ldap-key-store'(keyStoreAttrs) {
        if (!ldapMappingAttrs.isEmpty()) {
            'ldap-mapping'(ldapMappingAttrs)
        }
        if (!newItemTemplateAttrs.isEmpty()) {
            'new-item-template'(newItemTemplateAttrs) {
                if (atrNewItemAttributes != null && !atrNewItemAttributes.isEmpty()) {
                    for (newItemAttribute in atrNewItemAttributes) {
                        'attribute'(['name': newItemAttribute.name, 'value': String.join(' ', newItemAttribute.values)])
                    }
                }
            }
        }
    }
}

def isExistingTls = elytronSubsystem.'tls'.any { it.name() == 'tls' }
if (! isExistingTls) {
    elytronSubsystem.appendNode { 'tls' { 'key-stores' keyStoreDefinition } }
    return
}

def isExistingKeyStores = elytronSubsystem.'tls'.'key-stores'.any { it.name() == 'key-stores' }
if (! isExistingKeyStores) {
    elytronSubsystem.'tls'.appendNode { 'key-stores' keyStoreDefinition }
    return
}

def existingKeyStore = elytronSubsystem.'tls'.'key-stores'.'ldap-key-store'.find { it.'@name' == atrName }
if (existingKeyStore && !atrReplaceExisting) {
    throw new IllegalStateException("LdapKeyStore with name $atrName already exists in configuration. Use different name.")
} else {
    if (existingKeyStore) {
        existingKeyStore.replaceNode keyStoreDefinition
    } else {
        elytronSubsystem.'tls'.'key-stores'.appendNode keyStoreDefinition
    }
}
