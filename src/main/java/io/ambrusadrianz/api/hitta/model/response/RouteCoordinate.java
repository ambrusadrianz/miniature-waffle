package io.ambrusadrianz.api.hitta.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableRouteCoordinate.class)
@JsonDeserialize(as = ImmutableRouteCoordinate.class)
public interface RouteCoordinate {
    @JsonProperty("type")
    String getType();

    @JsonProperty("coordinates")
    List<Double> getCoordinates();

    default Double getLatitude() {
        return getCoordinates().get(1);
    }

    default Double getLongitude() {
        return getCoordinates().get(0);
    }
}
