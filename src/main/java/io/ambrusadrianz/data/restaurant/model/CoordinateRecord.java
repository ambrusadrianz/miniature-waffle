package io.ambrusadrianz.data.restaurant.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableCoordinateRecord.class)
@JsonDeserialize(as = ImmutableCoordinateRecord.class)
public interface CoordinateRecord {
    Double getLatitude();

    Double getLongitude();
}
