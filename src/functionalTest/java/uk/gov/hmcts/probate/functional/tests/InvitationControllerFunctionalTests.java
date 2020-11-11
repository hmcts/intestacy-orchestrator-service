package uk.gov.hmcts.probate.functional.tests;

import io.restassured.RestAssured;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;


@RunWith(SpringIntegrationSerenityRunner.class)
public class InvitationControllerFunctionalTests extends FormsFunctionalTests {
    private static final String INVITE_URL = "/invite";
    private static final String INVITE_AGREED_URL = "/invite/agreed/";
    private static final String INVITE_AGREED_ALL_URL = "/invite/allAgreed/";
    private static final String INVITE_BILINGUAL_URL = "/invite/bilingual";
    private static final String INVITE_CONTACT_DETAILS_URL = "/invite/contactdetails/";
    private static final String INVITE_DATA_URL = "/invite/data/";
    private static final String DELETE_INVITE_URL = "/invite/delete/";
    private static final String INVITE_PIN_URL = "/invite/pin";
    private static final String INVITE_PIN_BILINGUAL_URL = "/invite/pin/bilingual";
    private static final String INVITE_RESET_AGREED_FLAGS_URL = "/invite/resetAgreed/";
    private static final String GET_ALL_INVITE_URL = "/invites/";
    private static final String FORMDATA_ID_PLACEHOLDER = "XXXXX";
    private static final String INVITE_ID_PLACEHOLDER = "YYYYY";
    private static final String INVALID_FORM_DATA_ID = "1604925395199999";
    private String inviteId;
    private long caseId;
    private boolean setUp;

    @Before
    public void init() throws IOException {
        if (!setUp) {
            setUpANewCase();
            shouldSaveFormSuccessfully();
            caseId = geCaseId();
            setUp = true;
        }
    }

    @Test
    public void generateInvitation() throws IOException {
        String validInvitationJsonStr = utils.getJsonFromFile("validInvitation.json");
        validInvitationJsonStr = validInvitationJsonStr.replace(FORMDATA_ID_PLACEHOLDER, String.valueOf(caseId));
        inviteId = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getCitizenHeaders())
                .header("Session-Id", "ses123")
                .body(validInvitationJsonStr)
                .when()
                .post(INVITE_URL)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().response().path("invitations[0].inviteId");
    }

    @Test
    public void inviteAgreed() throws IOException {
        generateInvitation();
        String validInvitationJsonStr = utils.getJsonFromFile("validInvitationWithInviteId.json");
        validInvitationJsonStr = validInvitationJsonStr.replace(FORMDATA_ID_PLACEHOLDER, String.valueOf(caseId));
        validInvitationJsonStr = validInvitationJsonStr.replace(INVITE_ID_PLACEHOLDER, inviteId);
        String response = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getCaseworkerHeaders())
                .header("Session-Id", "ses123")
                .body(validInvitationJsonStr)
                .when()
                .post(INVITE_AGREED_URL + caseId)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().jsonPath().prettify();
    }

    @Test
    public void getInviteAllAgreedForValidFormDataId() throws IOException {
        String response = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getCitizenHeaders())
                .when()
                .get(INVITE_AGREED_ALL_URL + caseId)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().response().getBody().prettyPrint();
        Assert.assertEquals("false", response);

    }

    @Test
    public void getInviteAllAgreedForInValidFormDataId() throws IOException {
        RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getCitizenHeaders())
                .when()
                .get(INVITE_AGREED_ALL_URL + INVALID_FORM_DATA_ID)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void inviteBilingual() throws IOException {
        generateInvitation();
        String validInvitationJsonStr = utils.getJsonFromFile("validInvitationWithInviteId.json");
        validInvitationJsonStr = validInvitationJsonStr.replace(FORMDATA_ID_PLACEHOLDER, String.valueOf(caseId));
        validInvitationJsonStr = validInvitationJsonStr.replace(INVITE_ID_PLACEHOLDER, inviteId);
        validInvitationJsonStr = "[" + validInvitationJsonStr + "]";
        System.out.println("caseId string value of:" + String.valueOf(caseId));
        System.out.println("validInvitationJsonStr:" + validInvitationJsonStr);
        String response = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getCitizenHeaders())
                .header("Session-Id", "ses123")
                .body(validInvitationJsonStr)
                .when()
                .post(INVITE_BILINGUAL_URL)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().jsonPath().prettify();
    }
}
