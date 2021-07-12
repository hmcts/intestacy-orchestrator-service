package uk.gov.hmcts.probate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.core.service.SecurityUtils;
import uk.gov.hmcts.probate.model.exception.InvalidTokenException;
import uk.gov.hmcts.probate.service.PaymentUpdateService;
import uk.gov.hmcts.reform.probate.model.payments.PaymentDto;

@Api(tags = {"Payment Update Controller"})
@SwaggerDefinition(tags = {@Tag(name = "PaymentUpdateController", description = "Payment Update API")})
@RestController
@Slf4j
public class PaymentUpdateController {

    public static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";
    private static final String PAYMENT_UPDATES = "/payment-updates";
    private final PaymentUpdateService paymentUpdateService;
    @Autowired
    private SecurityUtils authS2sUtil;

    @Autowired
    private PaymentUpdateController(PaymentUpdateService paymentUpdateService) {
        this.paymentUpdateService = paymentUpdateService;
    }

    @ApiOperation(value = "Update payment", notes = "Update payment")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Updated payment successfully"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Provided S2S token is missing or invalid"),
        @ApiResponse(code = 403, message = "Calling service is not authorised to use the endpoint"),
        @ApiResponse(code = 500, message = "Internal Server Error")})
    @PutMapping(path = PAYMENT_UPDATES, consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> updatePayment(
        @RequestHeader(value = SERVICE_AUTHORIZATION_HEADER) String s2sAuthToken,
        @RequestBody PaymentDto paymentDto) {

        try {
            Boolean isServiceAllowed = authS2sUtil.checkIfServiceIsAllowed(s2sAuthToken);
            if (Boolean.TRUE.equals(isServiceAllowed)) {
                paymentUpdateService.paymentUpdate(paymentDto);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                log.info("Calling Service is not authorised to use the endpoint");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (InvalidTokenException e) {
            log.error(e.getMessage());
            log.info("Provided s2s token is missing or invalid");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
