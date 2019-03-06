package io.ambrusadrianz.application.loader.model;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface LoaderCity {
    UUID getId();

    String getCity();

    String getCountry();

    Integer getRestaurantCount();
}
