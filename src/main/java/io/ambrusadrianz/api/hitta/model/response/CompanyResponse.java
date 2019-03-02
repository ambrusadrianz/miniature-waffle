package io.ambrusadrianz.api.hitta.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableCompanyResponse.class)
@JsonDeserialize(as = ImmutableCompanyResponse.class)
public interface CompanyResponse {
    @JsonProperty("total")
    Long getTotal();

    @JsonProperty("included")
    Long getIncluded();

    @JsonProperty("company")
    List<Company> getCompany();

    @JsonProperty("attribute")
    List<Attribute> getAttribute();
}
