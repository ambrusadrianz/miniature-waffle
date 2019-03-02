package io.ambrusadrianz.api.hitta.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableStreetView.class)
@JsonDeserialize(as = ImmutableStreetView.class)
public interface StreetView {
    @JsonProperty("row")
    Long getRow();

    @JsonProperty("col")
    Long getCol();

    @JsonProperty("type")
    String getType();

    @JsonProperty("uri")
    String getUri();

    @JsonProperty("angle")
    Double getAngle();

    @JsonProperty("coordinate")
    Coordinate getCoordinate();
}
