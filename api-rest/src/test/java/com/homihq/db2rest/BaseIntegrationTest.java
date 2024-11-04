package com.homihq.db2rest;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Paths;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class})
@Slf4j
public abstract class BaseIntegrationTest {

	@Autowired
	public MockMvc mockMvc;

	@Autowired
	protected ApplicationContext applicationContext;

	@Autowired
	protected Db2RestConfigProperties db2RestConfigProperties;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext,
	           RestDocumentationContextProvider restDocumentation) {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.apply(documentationConfiguration(restDocumentation)
						.snippets().withTemplateFormat(TemplateFormats.markdown())
				)
				.build();
		setupEnv();
	}

	void setupEnv() {
		var templatesLocation = db2RestConfigProperties.getTemplates();
		if (templatesLocation.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX)) {
			try {
				Resource resource = applicationContext.getResource(templatesLocation);
				var resolvedTemplatesLocation = String.valueOf(Paths.get(resource.getFile().getAbsolutePath()));
				db2RestConfigProperties.setTemplates(resolvedTemplatesLocation);
			} catch (IOException ioe) {
				log.debug("Error while resolve _sql templates location for testing", ioe);
			}
		}
	}
}
