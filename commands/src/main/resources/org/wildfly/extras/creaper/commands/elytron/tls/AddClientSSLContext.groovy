sslContextAttrs = ['name': atrName]
if (atrCipherSuiteFilter != null) sslContextAttrs['cipher-suite-filter'] = atrCipherSuiteFilter
if (atrMaximumSessionCacheSize != null) sslContextAttrs['maximum-session-cache-size'] = atrMaximumSessionCacheSize
if (atrSessionTimeout != null) sslContextAttrs['session-timeout'] = atrSessionTimeout
if (atrKeyManagers != null) sslContextAttrs['key-managers'] = atrKeyManagers
if (atrTrustManagers != null) sslContextAttrs['trust-managers'] = atrTrustManagers
if (atrProtocols != null) sslContextAttrs['protocols'] = atrProtocols

def sslContextDefinition = {
    'client-ssl-context'(sslContextAttrs)
}

def isExistingTls = elytronSubsystem.'tls'.any { it.name() == 'tls' }
if (! isExistingTls) {
    elytronSubsystem.appendNode { 'tls' { 'client-ssl-contexts' sslContextDefinition } }
    return
}

def isExistingClientSslContexts = elytronSubsystem.'tls'.'client-ssl-contexts'.any { it.name() == 'client-ssl-contexts' }
if (! isExistingClientSslContexts) {
    elytronSubsystem.'tls'.appendNode { 'client-ssl-contexts' sslContextDefinition }
    return
}

def existingClientSslContext = elytronSubsystem.'tls'.'client-ssl-contexts'.'client-ssl-context'.find { it.'@name' == atrName }
if (existingClientSslContext && !atrReplaceExisting) {
    throw new IllegalStateException("Client SSL context with name $atrName already exists in configuration. Use different name.")
} else {
    if (existingClientSslContext) {
        existingClientSslContext.replaceNode sslContextDefinition
    } else {
        elytronSubsystem.'tls'.'client-ssl-contexts'.appendNode sslContextDefinition
    }
}
