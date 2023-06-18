package dev.sabri.secureapikk.config;


import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Testcontainers
public class AbstractTestcontainers {

    private static final PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:latest");
    private static final GenericContainer<?> keycloakContainer = new GenericContainer<>("quay.io/keycloak/keycloak:21.1.1");
    protected static Keycloak keycloakClient;

    @DynamicPropertySource
    private static void dynamicProperties(DynamicPropertyRegistry registry) {
        keycloakContainer.withExposedPorts(8080)
                .withEnv("KEYCLOAK_ADMIN", "admin")
                .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
                .withEnv("KC_DB", "dev-mem")
                .withCommand("start-dev")
                .waitingFor(Wait.forHttp("/admin").forPort(8080).withStartupTimeout(Duration.ofMinutes(2)))
                .start();

        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        String keycloakHost = keycloakContainer.getHost();
        Integer keycloakPort = keycloakContainer.getMappedPort(8080);

        String issuerUri = "http://%s:%s/realms/oauth-kk".formatted(keycloakHost, keycloakPort);
        String jwtSetUri = "%s/protocol/openid-connect/certs".formatted(issuerUri);
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> issuerUri);
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> jwtSetUri);

        if (keycloakClient == null) {
            String keycloakServerUrl = "http://%s:%s".formatted(keycloakHost, keycloakPort);
            setupKeycloak(keycloakServerUrl);
        }
    }

    private static void setupKeycloak(String keycloakServerUrl) {
        Keycloak keycloakAdmin = KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm("master")
                .username("admin")
                .password("admin")
                .clientId("admin-cli")
                .build();

        // Realm
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(REALM_NAME);
        realmRepresentation.setEnabled(true);

        // Client
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setId(CLIENT_ID);
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setSecret(CLIENT_SECRET);
        realmRepresentation.setClients(Collections.singletonList(clientRepresentation));

        // Client roles
        Map<String, List<String>> clientRoles = new HashMap<>();
        clientRoles.put(CLIENT_ID, SERVICE_ROLES);

        // Credentials
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(USER_PASSWORD);

        // User
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(USER_USERNAME);
        userRepresentation.setEnabled(true);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        userRepresentation.setClientRoles(clientRoles);
        userRepresentation.setRealmRoles(SERVICE_ROLES);
        realmRepresentation.setUsers(Collections.singletonList(userRepresentation));

        keycloakAdmin.realms().create(realmRepresentation);

        keycloakClient = KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(REALM_NAME)
                .username(USER_USERNAME)
                .password(USER_PASSWORD)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .build();
    }

    private static final String REALM_NAME = "oauth-kk";
    private static final String CLIENT_ID = "oauth-kk-client";
    private static final String CLIENT_SECRET = "abc123";
    private static final List<String> SERVICE_ROLES = Collections.singletonList("user");
    private static final String USER_USERNAME = "user1";
    private static final String USER_PASSWORD = "password";
}
