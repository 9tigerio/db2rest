package com.homihq.db2rest.auth;


import com.homihq.db2rest.auth.unkey.service.UnKeyAuthService;
import com.homihq.db2rest.auth.unkey.to.UnKeyVerifyResponse;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

@DisabledIfEnvironmentVariable(named = "testUnkeyDevKey", matches = "")
public class UnkeyDevServiceTest  {
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
