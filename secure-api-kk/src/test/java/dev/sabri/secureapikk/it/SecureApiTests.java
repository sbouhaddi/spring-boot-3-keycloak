package dev.sabri.secureapikk.it;

import dev.sabri.secureapikk.config.AbstractTestcontainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecureApiTests extends AbstractTestcontainers {

    private static final String SECURE_API_URL = "/api/test";
    private static final String SECURE_API_URL_ANONYMOUS = SECURE_API_URL + "/anonymous";
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void testGetAnonymousUserUnauthorized() {
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(SECURE_API_URL_ANONYMOUS, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void testCreateBookInformingInvalidToken() {

        HttpHeaders headers = authBearerHeaders("abcdef");
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(SECURE_API_URL_ANONYMOUS, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void testCreateBookInformingValidToken() {

        String accessToken = keycloakClient.tokenManager().grantToken().getToken();

        HttpHeaders headers = authBearerHeaders(accessToken);
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(SECURE_API_URL_ANONYMOUS, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotEmpty();
    }

    private HttpHeaders authBearerHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

}
