package org.wildfly.extras.creaper.commands.elytron.realm;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.elytron.AbstractElytronOnlineTest;
import org.wildfly.extras.creaper.commands.elytron.dircontext.AddDirContext;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.operations.Address;

@RunWith(Arquillian.class)
public final class AddLdapRealmOnlineTest extends AbstractElytronOnlineTest {

    private static final String TEST_LDAP_REALM_NAME = "CreaperTestLdapRealm";
    private static final Address TEST_LDAP_REALM_ADDRESS = SUBSYSTEM_ADDRESS
            .and("ldap-realm", TEST_LDAP_REALM_NAME);
    private static final String TEST_LDAP_REALM_NAME2 = "CreaperTestLdapRealm2";
    private static final Address TEST_LDAP_REALM_ADDRESS2 = SUBSYSTEM_ADDRESS
            .and("ldap-realm", TEST_LDAP_REALM_NAME2);

    private static final String TEST_DIR_CONTEXT_NAME = "CreaperTestDirContext";
    private static final Address TEST_DIR_CONTEXT_ADDRESS = SUBSYSTEM_ADDRESS
            .and("dir-context", TEST_DIR_CONTEXT_NAME);
    private final AddDirContext addDirContext = new AddDirContext.Builder(TEST_DIR_CONTEXT_NAME)
            .url("localhost")
            .authenticationLevel(AddDirContext.AuthenticationLevel.NONE)
            .build();

    @After
    public void cleanup() throws Exception {
        ops.removeIfExists(TEST_LDAP_REALM_ADDRESS);
        ops.removeIfExists(TEST_LDAP_REALM_ADDRESS2);
        ops.removeIfExists(TEST_DIR_CONTEXT_ADDRESS);
        administration.reloadIfRequired();
    }

    @Test
    public void addSimpleLdapRealm() throws Exception {
        client.apply(addDirContext);

        AddLdapRealm addLdapRealm = new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .build())
                .build();

        client.apply(addLdapRealm);

        assertTrue("Ldap realm should be created", ops.exists(TEST_LDAP_REALM_ADDRESS));
    }

    @Test
    public void addTwoSimpleLdapRealms() throws Exception {
        client.apply(addDirContext);

        AddLdapRealm addLdapRealm = new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .build())
                .build();

        AddLdapRealm addLdapRealm2 = new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME2)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .build())
                .build();

        client.apply(addLdapRealm);
        client.apply(addLdapRealm2);

        assertTrue("Ldap realm should be created", ops.exists(TEST_LDAP_REALM_ADDRESS));
        assertTrue("Second ldap realm should be created", ops.exists(TEST_LDAP_REALM_ADDRESS2));
    }

    @Test
    public void addFullLdapRealm() throws Exception {
        client.apply(addDirContext);

        AddLdapRealm addLdapRealm = new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .directVerification(false)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .searchBaseDn("someSearchBaseDn")
                        .useRecursiveSearch(true)
                        .iteratorFilter("someIteratorFilter")
                        .newIdentityParentDn("DN=someDn")
                        .addAttributeMappings(new AddLdapRealm.AttributeMappingBuilder()
                                .from("someAttributeFrom")
                                .to("someAttributeTo")
                                .filter("someAttributeFilter")
                                .filterBaseDn("someAttributeFilterBaseDn")
                                .asRdn("someAttributeAsRdn")
                                .build(),
                                new AddLdapRealm.AttributeMappingBuilder()
                                .from("someAttributeFrom2")
                                .to("someAttributeTo2")
                                .filter("someAttributeFilter2")
                                .filterBaseDn("someAttributeFilterBaseDn2")
                                .asRdn("someAttributeAsRdn2")
                                .build())
                        .userPasswordMapper(new AddLdapRealm.UserPasswordMapperBuilder()
                                .from("someUserPasswordFrom")
                                .writable(true)
                                .verifiable(true)
                                .build())
                        .otpCredentialMapper(new AddLdapRealm.OtpCredentialMapperBuilder()
                                .algorithmFrom("someAlgorithmFrom")
                                .hashFrom("someHashFrom")
                                .seedFrom("someSeedFrom")
                                .sequenceFrom("someSequenceFrom")
                                .build())
                        .x509CredentialMapper(new AddLdapRealm.X509CredentialMapperBuilder()
                                .digestFrom("someDigestFrom")
                                .digestAlgorithm("someDigestAlgorithm")
                                .certificateFrom("someCertificateFrom")
                                .serialNumberFrom("someSerialNumberFrom")
                                .subjectDnFrom("someSubjectDnFrom")
                                .build())
                        .addNewIdentityAttributes(new AddLdapRealm.NewIdentityAttributesBuilder()
                                .name("someName")
                                .addValues("someValue1", "someValue2")
                                .build(),
                                new AddLdapRealm.NewIdentityAttributesBuilder()
                                .name("someName2")
                                .addValues("someValue3")
                                .build())
                        .build())
                .build();

        client.apply(addLdapRealm);

        assertTrue("Ldap realm should be created", ops.exists(TEST_LDAP_REALM_ADDRESS));

        checkAttribute("dir-context", TEST_DIR_CONTEXT_NAME);
        checkAttribute("direct-verification", "false");
        checkAttribute("identity-mapping.rdn-identifier", "someId");
        checkAttribute("identity-mapping.search-base-dn", "someSearchBaseDn");
        checkAttribute("identity-mapping.use-recursive-search", "true");
        checkAttribute("identity-mapping.iterator-filter", "someIteratorFilter");
        checkAttribute("identity-mapping.new-identity-parent-dn", "DN=someDn");

        checkAttribute("identity-mapping.attribute-mapping[0].from", "someAttributeFrom");
        checkAttribute("identity-mapping.attribute-mapping[0].to", "someAttributeTo");
        checkAttribute("identity-mapping.attribute-mapping[0].filter", "someAttributeFilter");
        checkAttribute("identity-mapping.attribute-mapping[0].filter-base-dn", "someAttributeFilterBaseDn");
        checkAttribute("identity-mapping.attribute-mapping[0].as-rdn", "someAttributeAsRdn");
        checkAttribute("identity-mapping.attribute-mapping[1].from", "someAttributeFrom2");
        checkAttribute("identity-mapping.attribute-mapping[1].to", "someAttributeTo2");
        checkAttribute("identity-mapping.attribute-mapping[1].filter", "someAttributeFilter2");
        checkAttribute("identity-mapping.attribute-mapping[1].filter-base-dn", "someAttributeFilterBaseDn2");
        checkAttribute("identity-mapping.attribute-mapping[1].as-rdn", "someAttributeAsRdn2");

        checkAttribute("identity-mapping.user-password-mapper.from", "someUserPasswordFrom");
        checkAttribute("identity-mapping.user-password-mapper.writable", "true");
        checkAttribute("identity-mapping.user-password-mapper.verifiable", "true");

        checkAttribute("identity-mapping.otp-credential-mapper.algorithm-from", "someAlgorithmFrom");
        checkAttribute("identity-mapping.otp-credential-mapper.hash-from", "someHashFrom");
        checkAttribute("identity-mapping.otp-credential-mapper.seed-from", "someSeedFrom");
        checkAttribute("identity-mapping.otp-credential-mapper.sequence-from", "someSequenceFrom");

        checkAttribute("identity-mapping.x509-credential-mapper.digest-from", "someDigestFrom");
        checkAttribute("identity-mapping.x509-credential-mapper.digest-algorithm", "someDigestAlgorithm");
        checkAttribute("identity-mapping.x509-credential-mapper.certificate-from", "someCertificateFrom");
        checkAttribute("identity-mapping.x509-credential-mapper.serial-number-from", "someSerialNumberFrom");
        checkAttribute("identity-mapping.x509-credential-mapper.subject-dn-from", "someSubjectDnFrom");

        checkAttribute("identity-mapping.new-identity-attributes[0].name", "someName");
        checkAttribute("identity-mapping.new-identity-attributes[0].value[0]", "someValue1");
        checkAttribute("identity-mapping.new-identity-attributes[0].value[1]", "someValue2");
        checkAttribute("identity-mapping.new-identity-attributes[1].name", "someName2");
        checkAttribute("identity-mapping.new-identity-attributes[1].value[0]", "someValue3");
    }

    @Test(expected = CommandFailedException.class)
    public void addExistLdapRealmNotAllowed() throws Exception {
        client.apply(addDirContext);

        AddLdapRealm addLdapRealm = new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .build())
                .build();

        AddLdapRealm addLdapRealm2 = new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId2")
                        .build())
                .build();

        client.apply(addLdapRealm);
        assertTrue("Ldap realm should be created", ops.exists(TEST_LDAP_REALM_ADDRESS));
        client.apply(addLdapRealm2);
        fail("Ldap realm CreaperTestLdapRealm already exists in configuration, exception should be thrown");

    }

    @Test
    public void addExistLdapRealmAllowed() throws Exception {
        client.apply(addDirContext);

        AddLdapRealm addLdapRealm = new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .build())
                .build();

        AddLdapRealm addLdapRealm2 = new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId2")
                        .build())
                .replaceExisting()
                .build();

        client.apply(addLdapRealm);
        assertTrue("Ldap realm should be created", ops.exists(TEST_LDAP_REALM_ADDRESS));
        client.apply(addLdapRealm2);
        assertTrue("Ldap realm should be created", ops.exists(TEST_LDAP_REALM_ADDRESS));
        // check whether it was really rewritten
        checkAttribute("identity-mapping.rdn-identifier", "someId2");
    }

    @Test(expected = IllegalArgumentException.class)
     public void addLdapRealm_nullName() throws Exception {
        new AddLdapRealm.Builder(null)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .build())
                .build();
        fail("Creating command with null name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLdapRealm_emptyName() throws Exception {
        new AddLdapRealm.Builder("")
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .build())
                .build();
        fail("Creating command with empty name should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLdapRealm_nullDirContext() throws Exception {
        new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(null)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .build())
                .build();
        fail("Creating command with null dir-context should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLdapRealm_emptyDirContext() throws Exception {
        new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext("")
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .build())
                .build();
        fail("Creating command with empty dir-context should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLdapRealm_nullIdentityMapping() throws Exception {
        new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(null)
                .build();
        fail("Creating command with null identity-mapping should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLdapRealm_nullRdnIdentifier_identityMapping() throws Exception {
        new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier(null)
                        .build())
                .build();
        fail("Creating command with null rdn-identifier of identity-mapping should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLdapRealm_emptyRdnIdentifier_identityMapping() throws Exception {
        new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("")
                        .build())
                .build();
        fail("Creating command with empty rdn-identifier of identity-mapping should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLdapRealm_nullFrom_attributeMapping() throws Exception {
        new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .addAttributeMappings(new AddLdapRealm.AttributeMappingBuilder()
                                .from(null)
                                .build())
                        .build())
                .build();
        fail("Creating command with null from of attribute-mapping should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLdapRealm_emptyFrom_attributeMapping() throws Exception {
        new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .addAttributeMappings(new AddLdapRealm.AttributeMappingBuilder()
                                .from("")
                                .build())
                        .build())
                .build();
        fail("Creating command with empty from of attribute-mapping should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLdapRealm_nullValue_newIdentityAttributesBuilder() throws Exception {
        new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .addNewIdentityAttributes(new AddLdapRealm.NewIdentityAttributesBuilder()
                                .addValues(null)
                                .build())
                        .build())
                .build();
        fail("Creating command with null values of new-identity-attributes should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLdapRealm_noValue_newIdentityAttributesBuilder() throws Exception {
        new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .addNewIdentityAttributes(new AddLdapRealm.NewIdentityAttributesBuilder()
                                .build())
                        .build())
                .build();
        fail("Creating command with no values of new-identity-attributes should throw exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLdapRealm_emptyValue_newIdentityAttributesBuilder() throws Exception {
        new AddLdapRealm.Builder(TEST_LDAP_REALM_NAME)
                .dirContext(TEST_DIR_CONTEXT_NAME)
                .identityMapping(new AddLdapRealm.IdentityMappingBuilder()
                        .rdnIdentifier("someId")
                        .addNewIdentityAttributes(new AddLdapRealm.NewIdentityAttributesBuilder()
                                .addValues()
                                .build())
                        .build())
                .build();
        fail("Creating command with empty values of new-identity-attributes should throw exception");
    }

    private void checkAttribute(String attribute, String expectedValue) throws IOException {
        checkAttribute(TEST_LDAP_REALM_ADDRESS, attribute, expectedValue);
    }

}
