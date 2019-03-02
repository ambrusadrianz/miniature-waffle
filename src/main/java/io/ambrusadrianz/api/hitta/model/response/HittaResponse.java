package io.ambrusadrianz.api.hitta.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableHittaResponse.class)
@JsonDeserialize(as = ImmutableHittaResponse.class)
public interface HittaResponse {
    @JsonProperty("companies")
    CompanyResponse getCompanies();

    @Nullable
    @JsonProperty("persons")
    Object getPersons(); // TODO address these later

    @Nullable
    @JsonProperty("locations")
    Object getLocations(); // TODO address these later

    @Nullable
    @JsonProperty("ads")
    Object getAds(); // TODO address these later
}
