package com.homihq.db2rest.interceptor;

import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.multidb.DatabaseContextHolder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DatabaseContextRequestInterceptorTest {

    private final DatabaseContextRequestInterceptor interceptor = new DatabaseContextRequestInterceptor();
    private final TestController controller = new TestController();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        DatabaseContextHolder.clear();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).addInterceptors(interceptor).build();
    }

    @AfterEach
    void cleanUp() {
        DatabaseContextHolder.clear();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/error", "/swagger-ui/index.html", "/v3/api-docs/test",
            "/actuator/health", "/admin/test", VERSION + "/$dbs"})
    void whiteListedRequestsCannotInheritAStaleDatabase(String uri) {
        DatabaseContextHolder.setCurrentDbId("previous-request");
        assertTrue(interceptor.preHandle(new MockHttpServletRequest("GET", uri),
                new MockHttpServletResponse(), new Object()));
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @Test
    void invalidRouteClearsStaleContextEvenWhenPreHandleThrows() {
        DatabaseContextHolder.setCurrentDbId("previous-request");
        assertThrows(GenericDataAccessException.class, () -> interceptor.preHandle(
                new MockHttpServletRequest("GET", VERSION + "/missing"),
                new MockHttpServletResponse(), new Object()));
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void blankDatabaseIdDoesNotLeaveStaleContext(String dbId) {
        MockHttpServletRequest request = routedRequest(dbId);
        DatabaseContextHolder.setCurrentDbId("previous-request");
        assertThrows(GenericDataAccessException.class, () -> interceptor.preHandle(request,
                new MockHttpServletResponse(), new Object()));
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @Test
    void successfulRequestSelectsItsOwnDatabaseAndClearsItAfterCompletion() throws Exception {
        DatabaseContextHolder.setCurrentDbId("previous-request");
        mockMvc.perform(get(VERSION + "/alpha/ok"))
                .andExpect(status().isOk()).andExpect(content().string("alpha"));
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @Test
    void contextRemainsAvailableUntilCompletionRatherThanOnlyUntilPostHandle() throws Exception {
        MockHttpServletRequest request = routedRequest("alpha");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Object handler = new Object();
        interceptor.preHandle(request, response, handler);
        interceptor.postHandle(request, response, handler, null);
        assertEquals("alpha", DatabaseContextHolder.getCurrentDbId());
        interceptor.afterCompletion(request, response, handler, null);
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @Test
    void handledControllerExceptionClearsContext() throws Exception {
        mockMvc.perform(get(VERSION + "/alpha/failure")).andExpect(status().isInternalServerError());
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @Test
    void unhandledControllerExceptionClearsContext() {
        assertThrows(ServletException.class, () -> mockMvc.perform(get(VERSION + "/alpha/unhandled")));
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @Test
    void failingExceptionHandlerStillClearsContext() {
        assertThrows(ServletException.class, () -> mockMvc.perform(get(VERSION + "/alpha/error-handler-failure")));
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @Test
    void laterInterceptorFailureStillClearsContext() throws Exception {
        HandlerInterceptor failingInterceptor = new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                throw new IllegalStateException("interceptor failure");
            }
        };
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(interceptor, failingInterceptor).build();
        mvc.perform(get(VERSION + "/alpha/ok")).andExpect(status().isInternalServerError());
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @Test
    void reusedThreadDoesNotRouteTheNextRequestToAFailedRequestsDatabase() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(() -> {
                try {
                    mockMvc.perform(get(VERSION + "/alpha/failure"))
                            .andExpect(status().isInternalServerError());
                    assertNull(DatabaseContextHolder.getCurrentDbId());
                    mockMvc.perform(get("/admin/test"))
                            .andExpect(status().isOk()).andExpect(content().string("none"));
                    mockMvc.perform(get(VERSION + "/beta/ok"))
                            .andExpect(status().isOk()).andExpect(content().string("beta"));
                    assertNull(DatabaseContextHolder.getCurrentDbId());
                } finally {
                    DatabaseContextHolder.clear();
                }
                return null;
            }).get(10, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void asyncHandoffClearsServletThreadAndWorkerUsesExplicitDatabaseId() throws Exception {
        MvcResult result = mockMvc.perform(get(VERSION + "/alpha/callable"))
                .andExpect(request().asyncStarted()).andReturn();
        assertNull(DatabaseContextHolder.getCurrentDbId());
        assertEquals("alpha:none", result.getAsyncResult(5000));
        mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk())
                .andExpect(content().string("alpha:none"));
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @Test
    void asyncTimeoutClearsContextAtHandoffAndAfterErrorRedispatch() throws Exception {
        MvcResult result = mockMvc.perform(get(VERSION + "/alpha/deferred"))
                .andExpect(request().asyncStarted()).andReturn();
        assertNull(DatabaseContextHolder.getCurrentDbId());
        controller.deferred.setErrorResult(new AsyncRequestTimeoutException());
        mockMvc.perform(asyncDispatch(result)).andExpect(status().isServiceUnavailable());
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @Test
    void asyncFailureClearsContextAfterErrorRedispatch() throws Exception {
        MvcResult result = mockMvc.perform(get(VERSION + "/alpha/deferred"))
                .andExpect(request().asyncStarted()).andReturn();
        assertNull(DatabaseContextHolder.getCurrentDbId());
        controller.deferred.setErrorResult(new IllegalStateException("async failure"));
        mockMvc.perform(asyncDispatch(result)).andExpect(status().isInternalServerError());
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    private MockHttpServletRequest routedRequest(String dbId) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", VERSION + "/" + dbId + "/ok");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("dbId", dbId));
        return request;
    }

    @RestController
    static class TestController {

        private DeferredResult<String> deferred;

        @GetMapping(VERSION + "/{dbId}/ok")
        String ok() {
            return DatabaseContextHolder.getCurrentDbId();
        }

        @GetMapping("/admin/test")
        String admin() {
            return currentDatabaseOrNone();
        }

        @GetMapping(VERSION + "/{dbId}/failure")
        String failure() {
            throw new IllegalStateException("controller failure");
        }

        @GetMapping(VERSION + "/{dbId}/unhandled")
        String unhandled() {
            throw new UnsupportedOperationException("unhandled controller failure");
        }

        @GetMapping(VERSION + "/{dbId}/error-handler-failure")
        String errorHandlerFailure() {
            throw new IllegalArgumentException("rendering failure");
        }

        @GetMapping(VERSION + "/{dbId}/callable")
        Callable<String> callable(@PathVariable("dbId") String dbId) {
            return () -> dbId + ":" + currentDatabaseOrNone();
        }

        @GetMapping(VERSION + "/{dbId}/deferred")
        DeferredResult<String> deferred() {
            deferred = new DeferredResult<>();
            return deferred;
        }

        @ExceptionHandler(IllegalStateException.class)
        ResponseEntity<String> handleFailure() {
            return ResponseEntity.internalServerError().body("failure");
        }

        @ExceptionHandler(IllegalArgumentException.class)
        ResponseEntity<String> failingErrorHandler() {
            throw new UnsupportedOperationException("error handler failed");
        }

        private static String currentDatabaseOrNone() {
            String dbId = DatabaseContextHolder.getCurrentDbId();
            return dbId == null ? "none" : dbId;
        }
    }
}
