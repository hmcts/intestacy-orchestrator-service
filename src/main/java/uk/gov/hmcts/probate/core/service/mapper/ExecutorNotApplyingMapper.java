package uk.gov.hmcts.probate.core.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorNotApplying;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorNotApplyingReason;
import uk.gov.hmcts.reform.probate.model.forms.pa.Executor;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {ExecutorNotApplyingReason.class})
public interface ExecutorNotApplyingMapper {

    @Mapping(target = "value.notApplyingExecutorName", source = "fullName")
    @Mapping(target = "value.notApplyingExecutorReason",
        expression = "java(ExecutorNotApplyingReason.getExecutorNotApplyingReasonByValue(executor.getNotApplyingKey()))")
    CollectionMember<ExecutorNotApplying> toExecutorNotApplying(Executor executor);

    @Mapping(target = "notApplyingKey",
        expression = "java(executorNotApplyingCollectionMember.getValue().getNotApplyingExecutorReason().getOptionValue())")
    @InheritInverseConfiguration
    Executor fromExecutorNotApplying(CollectionMember<ExecutorNotApplying> executorNotApplyingCollectionMember);
}