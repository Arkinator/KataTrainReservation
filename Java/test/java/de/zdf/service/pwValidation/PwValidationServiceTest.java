package de.zdf.service.pwValidation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PwValidationServiceTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void validPassword_shouldGive200Response() {
        final ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("/checkPassword?password=Pa$$w0rd", "", String.class);
        JSONAssert.assertEquals("{\"pwValid\":true}", stringResponseEntity.getBody(), false);
    }

    @Test
    public void invalidPassword_shouldGive4xx() {
        final ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("/checkPassword?password=falschesPasswort", "", String.class);
        JSONAssert.assertEquals("{\"pwValid\":false}", stringResponseEntity.getBody(), false);
    }

    @Test
    public void add2plus2_shouldGive4() {
        final ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("/add", "{\"number\":[2,3]}", String.class);
        assertThat(stringResponseEntity.getStatusCode()).isEqualTo(200);
        JSONAssert.assertEquals("{\"result\":5}", stringResponseEntity.getBody(), false);
    }
}
