package uk.gov.hmcts.probate.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.reform.probate.model.documents.BulkScanCoverSheet;
import uk.gov.hmcts.reform.probate.model.documents.CheckAnswersSummary;
import uk.gov.hmcts.reform.probate.model.documents.LegalDeclaration;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@FeignClient(
        name = "business-service-api",
        url = "${business.service.api.url}",
        configuration = BusinessServiceConfiguration.class
)
public interface BusinessServiceApi {

    @PostMapping(
            value = "/businessDocument/generateCheckAnswersSummaryPDF",
            headers = {
                    CONTENT_TYPE + "=" + APPLICATION_JSON_VALUE,
                    ACCEPT + "=" + APPLICATION_OCTET_STREAM_VALUE
            }
    )
    byte[] generateCheckAnswersSummaryPdf(
            @RequestHeader(AUTHORIZATION) String authorization,
            @RequestHeader(BusinessServiceConfiguration.SERVICE_AUTHORIZATION) String serviceAuthorization,
            @RequestBody CheckAnswersSummary checkAnswersSummary
    );

    @PostMapping(
            value = "/businessDocument/generateLegalDeclarationPDF",
            headers = {
                    CONTENT_TYPE + "=" + APPLICATION_JSON_VALUE,
                    ACCEPT + "=" + APPLICATION_OCTET_STREAM_VALUE
            }
    )
    byte[] generateLegalDeclarationPDF(
            @RequestHeader(AUTHORIZATION) String authorization,
            @RequestHeader(BusinessServiceConfiguration.SERVICE_AUTHORIZATION) String serviceAuthorization,
            @RequestBody LegalDeclaration legalDeclaration
    );

    @PostMapping(
            value = "/businessDocument/generateBulkScanCoverSheetPDF",
            headers = {
                    CONTENT_TYPE + "=" + APPLICATION_JSON_VALUE,
                    ACCEPT + "=" + APPLICATION_OCTET_STREAM_VALUE
            }
    )
    byte[] generateBulkScanCoverSheetPDF(
            @RequestHeader(AUTHORIZATION) String authorization,
            @RequestHeader(BusinessServiceConfiguration.SERVICE_AUTHORIZATION) String serviceAuthorization,
            @RequestBody BulkScanCoverSheet bulkScanCoverSheet
    );

}
