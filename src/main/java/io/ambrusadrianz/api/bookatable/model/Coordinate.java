package io.ambrusadrianz.api.bookatable.model;

import org.immutables.value.Value;

@Value.Immutable
public interface Coordinate {
    Double getLatitude();

    Double getLongitude();
}
