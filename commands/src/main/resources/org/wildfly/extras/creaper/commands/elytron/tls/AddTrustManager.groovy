trustManagerAttrs = ['name': atrName]
if (atrAlgorithm != null) trustManagerAttrs['algorithm'] = atrAlgorithm
if (atrAliasFilter != null) trustManagerAttrs['alias-filter'] = atrAliasFilter
if (atrKeyStore != null) trustManagerAttrs['key-store'] = atrKeyStore
if (atrProviderName != null) trustManagerAttrs['provider-name'] = atrProviderName
if (atrProviders != null) trustManagerAttrs['providers'] = atrProviders

if (atrCrl) {
    crlAttrs = [:]
    if (atrCrlPath != null) crlAttrs['path'] = atrCrlPath
    if (atrCrlRelativeTo != null) crlAttrs['relative-to'] = atrCrlRelativeTo
    if (atrCrlMaximumCertPath != null) crlAttrs['maximum-cert-path'] = atrCrlMaximumCertPath
}

def trustManagerDefinition = {
    if (atrCrl) {
        'trust-manager'(trustManagerAttrs) { 'certificate-revocation-list'(crlAttrs) }
    } else {
        'trust-manager'(trustManagerAttrs)
    }
}

def isExistingTls = elytronSubsystem.'tls'.any { it.name() == 'tls' }
if (! isExistingTls) {
    elytronSubsystem.appendNode { 'tls' { 'trust-manager' trustManagerDefinition } }
    return
}

def isExistingTrustManager = elytronSubsystem.'tls'.'trust-manager'.any { it.name() == 'trust-manager' }
if (! isExistingTrustManager) {
    elytronSubsystem.'tls'.appendNode { 'trust-manager' trustManagerDefinition }
    return
}

def existingTrustManager = elytronSubsystem.'tls'.'trust-manager'.'trust-manager'.find { it.'@name' == atrName }
if (existingTrustManager && !atrReplaceExisting) {
    throw new IllegalStateException("TrustManager with name $atrName already exists in configuration. Use different name.")
} else {
    if (existingTrustManager) {
        existingTrustManager.replaceNode trustManagerDefinition
    } else {
        elytronSubsystem.'tls'.'trust-manager'.appendNode trustManagerDefinition
    }
}
