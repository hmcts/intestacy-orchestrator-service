package uk.gov.hmcts.probate.core.service.mapper;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.TestUtils;
import uk.gov.hmcts.reform.probate.model.ProbateType;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;
import uk.gov.hmcts.reform.probate.model.forms.Copies;
import uk.gov.hmcts.reform.probate.model.forms.InheritanceTax;
import uk.gov.hmcts.reform.probate.model.forms.Registry;
import uk.gov.hmcts.reform.probate.model.forms.Will;
import uk.gov.hmcts.reform.probate.model.forms.caveat.CaveatForm;
import uk.gov.hmcts.reform.probate.model.forms.pa.Executors;
import uk.gov.hmcts.reform.probate.model.forms.pa.PaApplicant;
import uk.gov.hmcts.reform.probate.model.forms.pa.PaAssets;
import uk.gov.hmcts.reform.probate.model.forms.pa.PaDeceased;
import uk.gov.hmcts.reform.probate.model.forms.pa.PaDeclaration;
import uk.gov.hmcts.reform.probate.model.forms.pa.PaForm;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaMapperTest {

    @Autowired
    private PaMapper mapper;

    private PaForm paForm;
    private GrantOfRepresentationData grantOfRepresentation;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        paForm = PaTestDataCreator.createPaForm();
        grantOfRepresentation = PaTestDataCreator.createGrantOfRepresentation();
    }

    @Test
    public void shouldJason() throws Exception {
        String jsonString  = TestUtils.getJSONFromFile("addressForm.json");
        PaForm paForm = objectMapper.readValue(jsonString, PaForm.class);
    }

    @Test
    public void shouldMapPaFormToGrantOfRepresentation() {
        GrantOfRepresentationData actualGrantOfRepresentation = mapper.toCaseData(paForm);
        assertThat(actualGrantOfRepresentation).isEqualToComparingFieldByFieldRecursively(grantOfRepresentation);
    }

    @Test
    public void shouldMapGrantOfRepresentationToPaForm() {
        PaForm actualPaForm = mapper.fromCaseData(grantOfRepresentation);
        assertThat(actualPaForm).isEqualToComparingFieldByFieldRecursively(paForm);
    }

    @Test
    public void shouldMapNullPaFormToGrantOfRepresentation() {
        GrantOfRepresentationData actualGrantOfRepresentation = mapper.toCaseData(null);
        Assert.assertThat(actualGrantOfRepresentation, is(nullValue()));
    }

    @Test
    public void shouldMapNullGrantOfRepresentationToGrantOfPaForm() {
        PaForm actualPaForm = mapper.fromCaseData(null);
        Assert.assertThat(actualPaForm, is(nullValue()));
    }

    @Test
    public void shouldMapEmptyPaFormToGrantOfRepresentation() {
        GrantOfRepresentationData expectedGrantOfRepresentation = new GrantOfRepresentationData();
        expectedGrantOfRepresentation.setApplicationType(ApplicationType.PERSONAL);
        expectedGrantOfRepresentation.setGrantType(GrantType.GRANT_OF_PROBATE);
        expectedGrantOfRepresentation.setApplicationSubmittedDate(LocalDate.now());
        expectedGrantOfRepresentation.setNumberOfApplicants(0L);
        expectedGrantOfRepresentation.setIhtReferenceNumber("Not applicable");
        GrantOfRepresentationData actualGrantOfRepresentation = mapper.toCaseData(new PaForm());
        Assert.assertThat(actualGrantOfRepresentation, equalTo(expectedGrantOfRepresentation));
        assertThat(actualGrantOfRepresentation).isEqualToComparingFieldByFieldRecursively(expectedGrantOfRepresentation);

    }

    @Test
    public void shouldMapEmptyGrantOfRepresentationToGrantOfPaForm() {
        PaForm expectedPaForm = new PaForm();
        expectedPaForm.setType(ProbateType.PA);
        expectedPaForm.setCopies(new Copies());
        PaAssets paAssets = new PaAssets();
        paAssets.setAssetsoverseas(false);
        expectedPaForm.setAssets(paAssets);
        expectedPaForm.setIht(new InheritanceTax());
        expectedPaForm.setRegistry(new Registry());
        expectedPaForm.setApplicant(new PaApplicant());
        expectedPaForm.setDeceased(new PaDeceased());
        expectedPaForm.setWill(new Will());
        expectedPaForm.setExecutors(new Executors());
        expectedPaForm.setDeclaration(new PaDeclaration());
        PaForm actualPaForm = mapper.fromCaseData(new GrantOfRepresentationData());
        assertThat(actualPaForm).isEqualToComparingFieldByFieldRecursively(expectedPaForm);
    }
}