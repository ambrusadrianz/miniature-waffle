package io.ambrusadrianz.data.city.model;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface CityRecord {
    UUID getId();

    String getName();

    String getCountry();
}
