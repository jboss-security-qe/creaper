trustManagerAttrs = ['name': atrName]
if (atrAlgorithm != null) trustManagerAttrs['algorithm'] = atrAlgorithm
if (atrKeyStore != null) trustManagerAttrs['key-store'] = atrKeyStore
if (atrProvider != null) trustManagerAttrs['provider'] = atrProvider
if (atrProviderLoader != null) trustManagerAttrs['provider-loader'] = atrProviderLoader

def trustManagerDefinition = {
    'trust-manager'(trustManagerAttrs)
}

def isExistingTls = elytronSubsystem.'tls'.any { it.name() == 'tls' }
if (! isExistingTls) {
    elytronSubsystem.appendNode { 'tls' { 'trust-managers' trustManagerDefinition } }
    return
}

def isExistingTrustManagers = elytronSubsystem.'tls'.'trust-managers'.any { it.name() == 'trust-managers' }
if (! isExistingTrustManagers) {
    elytronSubsystem.'tls'.appendNode { 'trust-managers' trustManagerDefinition }
    return
}

def existingTrustManager = elytronSubsystem.'tls'.'trust-managers'.'trust-manager'.find { it.'@name' == atrName }
if (existingTrustManager && !atrReplaceExisting) {
    throw new IllegalStateException("TrustManager with name $atrName already exists in configuration. Use different name.")
} else {
    if (existingTrustManager) {
        existingTrustManager.replaceNode trustManagerDefinition
    } else {
        elytronSubsystem.'tls'.'trust-managers'.appendNode trustManagerDefinition
    }
}
