package com.homihq.db2rest.auth.provider.jwt;

import com.homihq.db2rest.auth.AuthConfiguration;
import com.homihq.db2rest.auth.data.ApiExcludedResource;
import com.homihq.db2rest.auth.data.ResourceRole;
import com.homihq.db2rest.auth.data.UserDetail;
import com.homihq.db2rest.auth.datalookup.NoAuthdataLookup;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.AntPathMatcher;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthProviderTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    private static final NoAuthdataLookup AUTH_DATA = new NoAuthdataLookup() {
        @Override
        public List<ResourceRole> getApiResourceRoles() {
            return List.of(new ResourceRole("/v1/rdbms/db/film", "GET", List.of("reader")));
        }

        @Override
        public List<ApiExcludedResource> getExcludedResources() {
            return List.of(new ApiExcludedResource("/v1/rdbms/db/public/**", "GET"));
        }
    };

    @Test
    void readsRolesFromArrayClaim() throws Exception {
        UserDetail user = authenticate(provider("roles", List.of("at+jwt")),
                token("at+jwt", c -> c.claim("roles", List.of("reader", "writer"))));

        assertThat(user.principal()).isEqualTo("alice");
        assertThat(user.roles()).containsExactly("reader", "writer");
    }

    @Test
    void readsRolesFromNamespacedStringClaim() throws Exception {
        UserDetail user = authenticate(provider("https://example.com/roles", List.of("at+jwt")),
                token("at+jwt", c -> c.claim("https://example.com/roles", "reader")));

        assertThat(user.roles()).containsExactly("reader");
    }

    @Test
    void noRolesWhenClaimMissingOrNotConfigured() throws Exception {
        String jwt = token("at+jwt", c -> c.claim("roles", List.of("reader")));

        assertThat(authenticate(provider("groups", List.of("at+jwt")), jwt).roles()).isEmpty();
        assertThat(authenticate(provider(null, List.of("at+jwt")), jwt).roles()).isEmpty();
    }

    @Test
    void ignoresRolesClaimOfUnexpectedType() throws Exception {
        UserDetail user = authenticate(provider("roles", List.of("at+jwt")),
                token("at+jwt", c -> c.claim("roles", 42)));

        assertThat(user).isNotNull();
        assertThat(user.roles()).isEmpty();
    }

    @Test
    void rejectsJwtTypeUnlessConfigured() throws Exception {
        String jwt = token("JWT", c -> c.claim("roles", List.of("reader")));

        assertThat(authenticate(provider("roles", List.of("at+jwt")), jwt)).isNull();
        assertThat(authenticate(provider("roles", List.of("at+jwt", "JWT")), jwt).roles())
                .containsExactly("reader");
    }

    @Test
    void authorizesUsingRolesFromToken() throws Exception {
        JwtAuthProvider provider = provider("roles", List.of("at+jwt"));

        UserDetail reader = authenticate(provider, token("at+jwt", c -> c.claim("roles", List.of("reader"))));
        UserDetail other = authenticate(provider, token("at+jwt", c -> c.claim("roles", List.of("other"))));

        assertThat(provider.authorize(reader, "/v1/rdbms/db/film", "GET")).isTrue();
        assertThat(provider.authorize(other, "/v1/rdbms/db/film", "GET")).isFalse();
    }

    @Test
    void honoursExcludedResourcesAndDefaultWhitelist() throws Exception {
        JwtAuthProvider provider = provider("roles", List.of("at+jwt"));

        assertThat(provider.isExcluded("/v1/rdbms/db/public/x", "GET")).isTrue();
        assertThat(provider.isExcluded("/actuator/health", "GET")).isTrue();
        assertThat(provider.isExcluded("/v1/rdbms/db/film", "GET")).isFalse();
    }

    @Test
    void returnsNullWhenAuthorizationHeaderMissingOrNotBearer() throws Exception {
        JwtAuthProvider provider = provider("roles", List.of("at+jwt"));

        assertThat(provider.authenticate(new MockHttpServletRequest())).isNull();

        MockHttpServletRequest basic = new MockHttpServletRequest();
        basic.addHeader("Authorization", "Basic YWxpY2U6c2VjcmV0");
        assertThat(provider.authenticate(basic)).isNull();
    }

    private static JwtAuthProvider provider(String rolesClaim, List<String> tokenTypes) throws Exception {
        JwtProperties properties = new JwtProperties();
        properties.setAlgorithm("HS256");
        properties.setKey(SECRET);
        properties.setRolesNamespace(rolesClaim);
        properties.setTokenTypes(tokenTypes);

        return new JwtAuthProvider(AUTH_DATA, new AntPathMatcher(),
                new AuthConfiguration().jwtProcessor(properties), properties.getRolesNamespace());
    }

    private static String token(String type, Consumer<JWTClaimsSet.Builder> claims) throws Exception {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .subject("alice")
                .expirationTime(new Date(System.currentTimeMillis() + 60_000));
        claims.accept(builder);

        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.HS256).type(new JOSEObjectType(type)).build(),
                builder.build());
        jwt.sign(new MACSigner(SECRET.getBytes()));
        return jwt.serialize();
    }

    private static UserDetail authenticate(JwtAuthProvider provider, String jwt) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwt);
        return provider.authenticate(request);
    }
}
