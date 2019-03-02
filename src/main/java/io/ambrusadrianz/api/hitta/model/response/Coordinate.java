package io.ambrusadrianz.api.hitta.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableCoordinate.class)
@JsonDeserialize(as = ImmutableCoordinate.class)
public interface Coordinate {
    @JsonProperty("north")
    Double getNorth();

    @JsonProperty("east")
    Double getEast();

    @JsonProperty("system")
    String getSystem();
}
