package io.ambrusadrianz.api.bookatable.model;

import org.immutables.value.Value;

@Value.Immutable
public interface RestaurantListingRequest {
    String getCity();

    String getCountry();
}
