package uk.gov.hmcts.probate.core.service.mapper.migration;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.core.service.mapper.AliasNameMapper;
import uk.gov.hmcts.probate.core.service.mapper.IhtMethodConverter;
import uk.gov.hmcts.probate.core.service.mapper.LegalStatementMapper;
import uk.gov.hmcts.probate.core.service.mapper.LocalDateTimeMapper;
import uk.gov.hmcts.probate.core.service.mapper.MapConverter;
import uk.gov.hmcts.probate.core.service.mapper.PaPaymentMapper;
import uk.gov.hmcts.probate.core.service.mapper.PaymentsMapper;
import uk.gov.hmcts.probate.core.service.mapper.PoundsConverter;
import uk.gov.hmcts.probate.core.service.mapper.RegistryLocationMapper;
import uk.gov.hmcts.probate.core.service.mapper.qualifiers.FromIhtMethod;
import uk.gov.hmcts.probate.core.service.mapper.qualifiers.FromMap;
import uk.gov.hmcts.probate.core.service.mapper.qualifiers.ToCaseAddress;
import uk.gov.hmcts.probate.core.service.mapper.qualifiers.ToCollectionMember;
import uk.gov.hmcts.probate.core.service.mapper.qualifiers.ToLocalDate;
import uk.gov.hmcts.probate.core.service.mapper.qualifiers.ToPennies;
import uk.gov.hmcts.probate.core.service.mapper.qualifiers.ToRegistryLocation;
import uk.gov.hmcts.probate.model.persistence.LegacyForm;
import uk.gov.hmcts.reform.probate.model.ProbateType;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;
import uk.gov.hmcts.reform.probate.model.forms.IhtMethod;

@Mapper(componentModel = "spring", uses = {PaPaymentMapper.class, PaymentsMapper.class, AliasNameMapper.class, RegistryLocationMapper.class, PoundsConverter.class,
    IhtMethodConverter.class, MapConverter.class, LegalStatementMapper.class, LocalDateTimeMapper.class},
    imports = {ApplicationType.class, GrantType.class, ProbateType.class, IhtMethod.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface LegacyIntestacyMapper extends LegacyFormMapper{

    @Mapping(target = "applicationType", expression = "java(ApplicationType.PERSONAL)")
    @Mapping(target = "grantType", expression = "java(GrantType.INTESTACY)")
    @Mapping(target = "primaryApplicantForenames", source = "applicant.firstName")
    @Mapping(target = "primaryApplicantSurname", source = "applicant.lastName")
    @Mapping(target = "primaryApplicantRelationshipToDeceased", source = "applicant.relationshipToDeceased")
    @Mapping(target = "primaryApplicantAdoptionInEnglandOrWales", source = "applicant.adoptionInEnglandOrWales")
    @Mapping(target = "primaryApplicantPhoneNumber", source = "applicant.phoneNumber")
    @Mapping(target = "primaryApplicantEmailAddress",
        expression = "java(form.getApplicantEmail() != null ? form.getApplicantEmail().toLowerCase() : null)")
    @Mapping(target = "primaryApplicantAddress", source = "applicant.address", qualifiedBy = {ToCaseAddress.class})
    @Mapping(target = "primaryApplicantAddresses", source = "applicant.addresses", qualifiedBy = {FromMap.class})
    @Mapping(target = "primaryApplicantPostCode", source = "applicant.postCode")
    @Mapping(target = "deceasedAddress", source = "deceased.address", qualifiedBy = {ToCaseAddress.class})
    @Mapping(target = "deceasedPostCode", source = "deceased.postCode")
    @Mapping(target = "deceasedAddresses", source = "deceased.addresses", qualifiedBy = {FromMap.class})
    @Mapping(target = "deceasedSurname", source = "deceased.lastName")
    @Mapping(target = "deceasedForenames", source = "deceased.firstName")
    @Mapping(target = "deceasedDateOfBirth", source = "deceased.dateOfBirth", qualifiedBy = {ToLocalDate.class})
    @Mapping(target = "deceasedDateOfDeath", source = "deceased.dateOfDeath", qualifiedBy = {ToLocalDate.class})
    @Mapping(target = "deceasedAnyOtherNames", source = "deceased.alias")
    @Mapping(target = "deceasedMartialStatus", source = "deceased.maritalStatus")
    @Mapping(target = "deceasedDivorcedInEnglandOrWales", source = "deceased.divorcedInEnglandOrWales")
    @Mapping(target = "deceasedOtherChildren", source = "deceased.otherChildren")
    @Mapping(target = "declarationCheckbox", source = "declaration.declarationCheckbox")
    @Mapping(target = "childrenOverEighteenSurvived", source = "deceased.allDeceasedChildrenOverEighteen")
    @Mapping(target = "childrenDied", source = "deceased.anyDeceasedChildrenDieBeforeDeceased")
    @Mapping(target = "grandChildrenSurvivedUnderEighteen",
        source = "deceased.anyDeceasedGrandchildrenUnderEighteen")
    @Mapping(target = "deceasedSpouseNotApplyingReason", source = "deceased.spouseNotApplyingReason")
    @Mapping(target = "deceasedAnyChildren", source = "deceased.anyChildren")
    @Mapping(target = "deceasedAliasNameList", source = "deceased.otherNames",
        qualifiedBy = {ToCollectionMember.class})
    @Mapping(target = "outsideUkGrantCopies", source = "copies.overseas")
    @Mapping(target = "extraCopiesOfGrant", source = "copies.uk")
    @Mapping(target = "deceasedHasAssetsOutsideUK", source = "iht.assetsOutside")
    @Mapping(target = "ihtReferenceNumber", source = "iht.identifier")
    @Mapping(target = "ihtFormId", source = "iht.ihtFormId")
    @Mapping(target = "assetsOutside", source = "iht.assetsOutside")
    @Mapping(target = "ihtFormCompletedOnline", source = "iht.method", qualifiedBy = {FromIhtMethod.class})
    @Mapping(target = "ihtNetValue", source = "iht.netValue", qualifiedBy = {ToPennies.class})
    @Mapping(target = "ihtGrossValue", source = "iht.grossValue", qualifiedBy = {ToPennies.class})
    @Mapping(target = "registryLocation", source = "registry.name", qualifiedBy = {ToRegistryLocation.class})
    @Mapping(target = "assetsOutsideNetValue", source = "iht.assetsOutsideNetValue",
        qualifiedBy = {ToPennies.class})
    @Mapping(target = "legalDeclarationJson", source = "legalDeclaration", qualifiedBy = {FromMap.class})
    @Mapping(target = "checkAnswersSummaryJson", source = "checkAnswersSummary", qualifiedBy = {FromMap.class})
    @Mapping(target = "payments", source = "payment")
    GrantOfRepresentationData toCaseData(LegacyForm form);
}
