package com.homihq.db2rest.auth;

import com.homihq.db2rest.MySQLBaseIntegrationTest;
import com.homihq.db2rest.auth.unkey.service.UnKeyAuthService;
import com.homihq.db2rest.auth.unkey.to.UnKeyVerifyResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

@DisabledIfEnvironmentVariable(named = "testUnkeyDevKey", matches = "")
public class UnkeyDevServiceTest extends MySQLBaseIntegrationTest {
    //TODO - mock
    /*
    @Autowired
    private UnKeyAuthService unKeyAuthService;

    @Test
    @DisplayName("Test api key verification")
    public void testAPIKeyVerify() {
        String apiKey = System.getenv("testUnkeyDevKey");
        UnKeyVerifyResponse response = unKeyAuthService.verifyApiKey(apiKey);
        assert response.enabled && response.valid;
    }

     */

}
