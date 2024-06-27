package io.ffroliva.keycloak.token.exchange;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@Testcontainers
@SpringBootTest
@Import(AbstractKeycloakTestContainer.KeycloakConfigTestConfiguration.class)
public class AbstractKeycloakTestContainer {

    @Container
    static KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:25.0.1")
            .withContextPath("/auth")
            .withAdminPassword("admin")
            .withAdminPassword("admin")
            .withFeaturesEnabled("token-exchange", "admin-fine-grained-authz")
            .withExposedPorts(8080);

    static Keycloak keycloak;

    static {
        keycloakContainer.start();
        keycloak = keycloakContainer.getKeycloakAdminClient();
        log.info("Keycloak URL: {}", keycloakContainer.getAuthServerUrl());
    }

    @TestConfiguration
    public static class KeycloakConfigTestConfiguration {
        @Bean
        @Primary
        public Keycloak keycloak() {
            return keycloak;
        }

        @Bean
        @Primary
        public RestClient restClient() {
            return RestClient.builder()
                    .baseUrl(keycloakContainer.getAuthServerUrl())
                    .build();
        }
    }
}

