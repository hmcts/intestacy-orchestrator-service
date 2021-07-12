package uk.gov.hmcts.probate.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.client.IdamClientApi;
import uk.gov.hmcts.probate.model.exception.InvalidTokenException;
import uk.gov.hmcts.probate.model.idam.AuthenticateUserResponse;
import uk.gov.hmcts.probate.model.idam.TokenExchangeResponse;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;

import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityUtils {

    private static final String BASIC = "Basic ";
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String CODE = "code";
    private final AuthTokenGenerator authTokenGenerator;
    private final AuthTokenValidator authTokenValidator;
    private final List<String> allowedToUpdateDetails;
    private final IdamClientApi idamClient;
    @Value("${auth.idam.redirectUrl}")
    private String authRedirectUrl;

    @Value("${auth.idam.clientId}")
    private String authClientId;

    @Value("${auth.idam.clientSecret}")
    private String authClientSecret;

    @Value("${auth.idam.caseworker.username}")
    private String caseworkerUserName;

    @Value("${auth.idam.caseworker.password}")
    private String caseworkerPassword;

    @Value("${auth.idam.scheduler.username}")
    private String schedulerUserName;

    @Value("${auth.idam.scheduler.password}")
    private String schedulerPassword;

    @Autowired
    public SecurityUtils(IdamClientApi idamClient,
                         AuthTokenValidator authTokenValidator,
                         AuthTokenGenerator authTokenGenerator,
                         @Value("${auth.idam.s2s-auth.services-allowed-to-payment-update}")
                             List<String> allowedToUpdateDetails) {
        this.authTokenGenerator = authTokenGenerator;
        this.authTokenValidator = authTokenValidator;
        this.allowedToUpdateDetails = allowedToUpdateDetails;
        this.idamClient = idamClient;
    }

    public String getServiceAuthorisation() {
        return authTokenGenerator.generate();
    }

    public String getAuthorisation() {
        return (String) SecurityContextHolder.getContext()
            .getAuthentication()
            .getCredentials();
    }

    public void setSecurityContextUserAsCaseworker() {
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(caseworkerUserName, getCaseworkerToken()));
    }

    public void setSecurityContextUserAsScheduler() {
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(schedulerUserName, getSchedulerToken()));
    }

    private String getCaseworkerToken() {
        return getIdamOauth2Token(caseworkerUserName, caseworkerPassword);
    }

    private String getSchedulerToken() {
        return getIdamOauth2Token(schedulerUserName, schedulerPassword);
    }

    private String getIdamOauth2Token(String username, String password) {
        String basicAuthHeader = getBasicAuthHeader(username, password);

        AuthenticateUserResponse authenticateUserResponse = idamClient.authenticateUser(
            basicAuthHeader,
            CODE,
            authClientId,
            authRedirectUrl
        );

        log.info("Authenticated. Exchanging...");
        TokenExchangeResponse tokenExchangeResponse = idamClient.exchangeCode(
            authenticateUserResponse.getCode(),
            AUTHORIZATION_CODE,
            authRedirectUrl,
            authClientId,
            authClientSecret
        );

        log.info("Getting AccessToken...");
        return tokenExchangeResponse.getAccessToken();
    }

    private String getBasicAuthHeader(String username, String password) {
        String authorisation = username + ":" + password;
        return BASIC + Base64.getEncoder().encodeToString(authorisation.getBytes());
    }

    public Boolean checkIfServiceIsAllowed(String token) throws InvalidTokenException {
        String serviceName = this.authenticate(token);
        if (Objects.nonNull(serviceName)) {
            return allowedToUpdateDetails.contains(serviceName);
        } else {
            log.info("Service name from token is null");
            return Boolean.FALSE;
        }
    }

    public String getBearerToken(String token) {
        if (StringUtils.isBlank(token)) {
            return token;
        }

        return token.startsWith(BEARER) ? token : BEARER.concat(token);
    }

    public String authenticate(String authHeader) throws InvalidTokenException {
        if (isBlank(authHeader)) {
            throw new InvalidTokenException("Provided S2S token is missing or invalid");
        }
        String bearerAuthToken = getBearerToken(authHeader);
        log.info("S2S token found in the request");

        return authTokenValidator.getServiceName(bearerAuthToken);
    }
}
