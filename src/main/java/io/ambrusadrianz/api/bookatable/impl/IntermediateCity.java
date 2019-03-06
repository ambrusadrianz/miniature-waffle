package io.ambrusadrianz.api.bookatable.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableIntermediateCity.class)
@JsonDeserialize(as = ImmutableIntermediateCity.class)
public interface IntermediateCity {
    @Nullable
    @JsonProperty("City")
    String getCity();

    @JsonProperty("Country")
    String getCountry();

    @JsonProperty("Name")
    String getName();

    @JsonProperty("Type")
    Integer getType();

    @JsonProperty("Url")
    String getUrl();
}
