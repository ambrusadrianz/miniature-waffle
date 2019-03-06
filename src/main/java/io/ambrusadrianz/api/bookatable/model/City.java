package io.ambrusadrianz.api.bookatable.model;

import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public interface City {
    @Nullable
    String getCity();

    String getCountry();

    String getName();

    Integer getType();

    String getUrl();

    Integer getRestaurantCount();
}
