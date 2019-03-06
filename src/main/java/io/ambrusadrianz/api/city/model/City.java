package io.ambrusadrianz.api.city.model;

import org.immutables.value.Value;

@Value.Immutable
public interface City {
    String getName();

    String getCountry();
}
