package com.homihq.db2rest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.auth.data.UserDetail;
import com.homihq.db2rest.auth.provider.AbstractAuthProvider;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthFilterTest {

    private final AbstractAuthProvider provider = mock(AbstractAuthProvider.class);
    private final AuthFilter filter = new AuthFilter(provider, new ObjectMapper().findAndRegisterModules());

    @Test
    void returns401WhenAuthenticationFails() throws Exception {
        when(provider.isExcluded(anyString(), anyString())).thenReturn(false);
        when(provider.authenticate(any())).thenReturn(null);

        MockHttpServletResponse response = run();

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("\"status\":401", "Authentication failure.");
    }

    @Test
    void returns403WhenAuthenticatedButNotAuthorized() throws Exception {
        UserDetail user = new UserDetail("alice", List.of("guest"));
        when(provider.isExcluded(anyString(), anyString())).thenReturn(false);
        when(provider.authenticate(any())).thenReturn(user);
        when(provider.authorize(any(), anyString(), anyString())).thenReturn(false);

        MockHttpServletResponse response = run();

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString())
                .contains("\"status\":403", "Authorization failure.", "https://db2rest/forbidden");
    }

    @Test
    void passesRequestThroughWhenAuthorized() throws Exception {
        UserDetail user = new UserDetail("alice", List.of("reader"));
        when(provider.isExcluded(anyString(), anyString())).thenReturn(false);
        when(provider.authenticate(any())).thenReturn(user);
        when(provider.authorize(any(), anyString(), anyString())).thenReturn(true);

        MockFilterChain chain = new MockFilterChain();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/v1/rdbms/db/film"), response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(chain.getRequest()).isNotNull();
    }

    private MockHttpServletResponse run() throws Exception {
        MockFilterChain chain = new MockFilterChain();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/v1/rdbms/db/film"), response, chain);
        assertThat(chain.getRequest()).isNull();
        return response;
    }
}
