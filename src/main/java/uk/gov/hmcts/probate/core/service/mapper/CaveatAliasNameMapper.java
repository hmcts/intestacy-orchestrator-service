package uk.gov.hmcts.probate.core.service.mapper;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.probate.core.service.mapper.qualifiers.FromCaveatCollectionMember;
import uk.gov.hmcts.probate.core.service.mapper.qualifiers.ToCaveatCollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.FullAliasName;
import uk.gov.hmcts.reform.probate.model.forms.AliasOtherNames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CaveatAliasNameMapper {

    @ToCaveatCollectionMember
    public List<CollectionMember<FullAliasName>> toCaveatCollectionMember(Map<String, AliasOtherNames> otherNames) {
        if (CollectionUtils.isEmpty(otherNames)) {
            return null;//NOSONAR
        }
        return otherNames.values()
            .stream()
            .map(aliasOtherName -> aliasOtherName.getFirstName() + " ".concat(aliasOtherName.getLastName()))
                .map(aliasName -> FullAliasName.builder().fullAliasName(aliasName).build())
                .map(fullAliasName -> CollectionMember.<FullAliasName>builder().value(fullAliasName).build())
            .collect(Collectors.toList());
    }

    @FromCaveatCollectionMember
    public Map<String, AliasOtherNames> fromCaveatCollectionMember(List<CollectionMember<FullAliasName>> collectionMembers) {
        if (CollectionUtils.isEmpty(collectionMembers)) {
            return null;//NOSONAR
        }
        List<AliasOtherNames> aliasOtherNamesList = collectionMembers
                .stream()
                .map(CollectionMember::getValue)
                .map(this::createAliasOtherName)
                .collect(Collectors.toList());
        Map<String, AliasOtherNames> aliasOtherNamesMap = new HashMap<>();
        int count = 0;
        for (AliasOtherNames aliasOtherNames : aliasOtherNamesList) {
            aliasOtherNamesMap.put("name_" + count, aliasOtherNames);
            count++;
        }
        return aliasOtherNamesMap;
    }

    private AliasOtherNames createAliasOtherName(FullAliasName fullAliasName) {
        if (fullAliasName == null) {
            return null;//NOSONAR
        }
        int separation = fullAliasName.getFullAliasName().lastIndexOf(' ' );
        String value = fullAliasName.getFullAliasName();
        String firstName = value.substring(0, separation);
        String lastName = value.substring(separation + 1, value.length());
        return AliasOtherNames.builder().firstName(firstName).lastName(lastName).build();
    }
}