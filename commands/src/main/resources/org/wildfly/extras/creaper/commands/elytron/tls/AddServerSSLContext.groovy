sslContextAttrs = ['name': atrName]
if (atrCipherSuiteFilter != null) sslContextAttrs['cipher-suite-filter'] = atrCipherSuiteFilter
if (atrMaximumSessionCacheSize != null) sslContextAttrs['maximum-session-cache-size'] = atrMaximumSessionCacheSize
if (atrSessionTimeout != null) sslContextAttrs['session-timeout'] = atrSessionTimeout
if (atrKeyManagers != null) sslContextAttrs['key-managers'] = atrKeyManagers
if (atrTrustManagers != null) sslContextAttrs['trust-managers'] = atrTrustManagers
if (atrProtocols != null) sslContextAttrs['protocols'] = atrProtocols
if (atrAuthenticationOptional != null) sslContextAttrs['authentication-optional'] = atrAuthenticationOptional
if (atrNeedClientAuth != null) sslContextAttrs['need-client-auth'] = atrNeedClientAuth
if (atrWantClientAuth != null) sslContextAttrs['want-client-auth'] = atrWantClientAuth
if (atrSecurityDomain != null) sslContextAttrs['security-domain'] = atrSecurityDomain

def sslContextDefinition = {
    'server-ssl-context'(sslContextAttrs)
}

def isExistingTls = elytronSubsystem.'tls'.any { it.name() == 'tls' }
if (! isExistingTls) {
    elytronSubsystem.appendNode { 'tls' { 'server-ssl-contexts' sslContextDefinition } }
    return
}

def isExistingServerSslContexts = elytronSubsystem.'tls'.'server-ssl-contexts'.any { it.name() == 'server-ssl-contexts' }
if (! isExistingServerSslContexts) {
    elytronSubsystem.'tls'.appendNode { 'server-ssl-contexts' sslContextDefinition }
    return
}

def existingServerSslContext = elytronSubsystem.'tls'.'server-ssl-contexts'.'server-ssl-context'.find { it.'@name' == atrName }
if (existingServerSslContext && !atrReplaceExisting) {
    throw new IllegalStateException("Server SSL context with name $atrName already exists in configuration. Use different name.")
} else {
    if (existingServerSslContext) {
        existingServerSslContext.replaceNode sslContextDefinition
    } else {
        elytronSubsystem.'tls'.'server-ssl-contexts'.appendNode sslContextDefinition
    }
}
