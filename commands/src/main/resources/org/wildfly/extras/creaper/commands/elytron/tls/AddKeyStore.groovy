keyStoreAttrs = ['name': atrName]
if (atrType != null) keyStoreAttrs['type'] = atrType
if (atrAliasFilter != null) keyStoreAttrs['alias-filter'] = atrAliasFilter
if (atrProvider != null) keyStoreAttrs['provider'] = atrProvider
if (atrProviderLoader != null) keyStoreAttrs['provider-loader'] = atrProviderLoader

fileAttrs = [:]
if (atrPath != null) fileAttrs['path'] = atrPath
if (atrRelativeTo != null) fileAttrs['relative-to'] = atrRelativeTo
if (atrRequired != null) fileAttrs['required'] = atrRequired

credentialReferenceAttrs = [:]
if (atrCredentialRefAlias != null) credentialReferenceAttrs['alias'] = atrCredentialRefAlias
if (atrCredentialRefType != null) credentialReferenceAttrs['type'] = atrCredentialRefType
if (atrCredentialRefStore != null) credentialReferenceAttrs['store'] = atrCredentialRefStore
if (atrCredentialRefClearText != null) credentialReferenceAttrs['clear-text'] = atrCredentialRefClearText

def keyStoreDefinition = {
    'key-store'(keyStoreAttrs) {
        if (atrPath || atrRelativeTo || atrRequired) {
            'file'(fileAttrs)
        }
        'credential-reference'(credentialReferenceAttrs)
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

def existingKeyStore = elytronSubsystem.'tls'.'key-stores'.'key-store'.find { it.'@name' == atrName }
if (existingKeyStore && !atrReplaceExisting) {
    throw new IllegalStateException("KeyStore with name $atrName already exists in configuration. Use different name.")
} else {
    if (existingKeyStore) {
        existingKeyStore.replaceNode keyStoreDefinition
    } else {
        elytronSubsystem.'tls'.'key-stores'.appendNode keyStoreDefinition
    }
}
