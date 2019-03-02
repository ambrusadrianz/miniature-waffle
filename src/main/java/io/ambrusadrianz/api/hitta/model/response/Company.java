package io.ambrusadrianz.api.hitta.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Value.Immutable
@JsonSerialize(as = ImmutableCompany.class)
@JsonDeserialize(as = ImmutableCompany.class)
public interface Company {
    @JsonProperty("id")
    String getId();

    @JsonProperty("displayName")
    String getDisplayName();

    @Nullable
    @JsonProperty("legalName")
    String getLegalName();

    @Nullable
    @JsonProperty("orgNo")
    Long getOrgNo();

    @JsonProperty("address")
    List<Address> getAddress();

    @JsonProperty("phone")
    List<Phone> getPhone();

    @JsonProperty("trade")
    List<Trade> getTrade();

    @Nullable
    @JsonProperty("metadata")
    Map<String, String> getMetadata();

    @Nullable
    @JsonProperty("attribute")
    List<Attribute> getAttribute();
}
