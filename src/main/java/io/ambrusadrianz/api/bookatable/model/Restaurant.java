package io.ambrusadrianz.api.bookatable.model;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface Restaurant {
    String getId();

    String getName();

    String getCuisine();

    Optional<String> getAddress();

    Optional<Integer> getPostalCode();

    Coordinate getLocation();

    String getImageUrl();

    String getCity();

    Optional<RestaurantDetails> getDetails();
}
