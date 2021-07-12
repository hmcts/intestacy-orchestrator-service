package uk.gov.hmcts.probate.functional.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.functional.TestContextConfiguration;
import uk.gov.hmcts.probate.functional.TestTokenGenerator;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@ContextConfiguration(classes = TestContextConfiguration.class)
@Component
public class TestUtils {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CITIZEN = "citizen";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${idam.citizen.username}")
    public String citizenEmail;
    @Value("${service.auth.provider.base.url}")
    public String authProviderUrl;
    @Autowired
    protected TestTokenGenerator testTokenGenerator;
    @Value("${idam.caseworker.username}")
    private String caseworkerEmail;
    private String serviceToken;

    @PostConstruct
    public void init() throws JsonProcessingException {
        serviceToken = testTokenGenerator.generateServiceAuthorisation();

        testTokenGenerator.createNewUser(citizenEmail, CITIZEN);
    }

    public String getJsonFromFile(String fileName) {
        try {
            File file = ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public File getFile(String fileName) {
        try {
            return ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Headers getCitizenHeaders() {
        return getHeaders(citizenEmail);
    }

    public Headers getCaseworkerHeaders() {
        return getHeaders(caseworkerEmail);
    }

    public Headers getHeaders(String email) {
        return Headers.headers(
                new Header("ServiceAuthorization", serviceToken),
                new Header(CONTENT_TYPE, ContentType.JSON.toString()),
                new Header(AUTHORIZATION, testTokenGenerator.generateAuthorisation(email)));


    }

    public String getPaymentToken() {

        String token = RestAssured.given().relaxedHTTPSValidation().baseUri(authProviderUrl).when()
            .body("{\"microservice\": \"payment_app\"}").contentType(MediaType.APPLICATION_JSON_VALUE)
            .post("/testing-support/lease").then()
            .statusCode(200).extract().asString();

        return token;
    }
}