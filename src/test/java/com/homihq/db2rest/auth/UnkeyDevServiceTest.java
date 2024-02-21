package com.homihq.db2rest.auth;

import com.homihq.db2rest.MySQLBaseIntegrationTest;
import com.homihq.db2rest.auth.service.UnkeyDevService;
import com.homihq.db2rest.auth.to.AuthInfo;
import com.homihq.db2rest.auth.to.VerifyKeyResponse;
import com.ibm.db2.cmx.annotation.Required;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisabledIfEnvironmentVariable(named = "testUnkeyDevKey", matches = "")
public class UnkeyDevServiceTest extends MySQLBaseIntegrationTest {

    @Autowired
    private UnkeyDevService unkeyDevService;

    @Test
    @DisplayName("Test api key verification")
    public void testAPIKeyVerify() {
        String apiKey = System.getenv("testUnkeyDevKey");
        VerifyKeyResponse response = unkeyDevService.verifyApiKey(apiKey);
        assert response.enabled && response.valid;
    }

}
