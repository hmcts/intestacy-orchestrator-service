package uk.gov.hmcts.probate.core.service.mapper;

import org.apache.commons.lang3.BooleanUtils;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.core.service.mapper.qualifiers.ToCaseAddress;
import uk.gov.hmcts.probate.core.service.mapper.qualifiers.ToFormAddress;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorApplying;
import uk.gov.hmcts.reform.probate.model.forms.pa.Executor;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {BooleanUtils.class})
public interface ExecutorApplyingMapper {

    @Mapping(target = "value.applyingExecutorName", expression = "java(ExecutorNamesMapper.getFullname(executor))")
    @Mapping(target = "value.applyingExecutorPhoneNumber", source = "mobile")
    @Mapping(target = "value.applyingExecutorEmail", source = "email")
    @Mapping(target = "value.applyingExecutorAddress", source = "address", qualifiedBy = {ToCaseAddress.class})
    @Mapping(target = "value.applyingExecutorOtherNames", expression = "java(BooleanUtils.isTrue(executor.getHasOtherName()) ? executor.getCurrentName() : null)")
    @Mapping(target = "value.applyingExecutorOtherNamesReason", expression = "java(BooleanUtils.isTrue(executor.getHasOtherName()) ? executor.getCurrentNameReason() : null)")
    @Mapping(target = "value.applyingExecutorOtherReason", expression = "java(BooleanUtils.isTrue(executor.getHasOtherName()) ? executor.getOtherReason() : null)")
    @Mapping(target = "value.applyingExecutorInvitationId", source = "inviteId")
    @Mapping( target ="value.applyingExecutorLeadName", source ="leadExecutorName")
    @Mapping( target ="value.applyingExecutorAgreed", source ="executorAgreed")
    CollectionMember<ExecutorApplying> toExecutorApplying(Executor executor);


    @Mapping(target = "hasOtherName", expression = "java(executorApplyingCollectionMember.getValue().getApplyingExecutorOtherNames() != null)")
    @Mapping(target = "currentName", source = "value.applyingExecutorOtherNames")
    @Mapping(target = "currentNameReason", source = "value.applyingExecutorOtherNamesReason")
    @Mapping(target = "address", source = "value.applyingExecutorAddress", qualifiedBy = {ToFormAddress.class})
    @Mapping(target = "otherReason", source = "value.applyingExecutorOtherReason")
    @Mapping(target = "isApplying", expression = "java(true)")
    @Mapping(target = "fullName", source = "value.applyingExecutorName")
    @InheritInverseConfiguration
    Executor fromExecutorApplying(CollectionMember<ExecutorApplying> executorApplyingCollectionMember);
}
